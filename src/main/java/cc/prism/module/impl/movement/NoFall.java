package cc.prism.module.impl.movement;

import cc.prism.event.EventTarget;
import cc.prism.event.impl.PacketEvent;
import cc.prism.event.impl.TickEvent;
import cc.prism.module.Category;
import cc.prism.module.Module;
import cc.prism.mixin.accessor.PlayerMoveC2SPacketAccessor;
import cc.prism.property.ModeProperty;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

/**
 * NoFall — prevents the player from taking fall damage.
 *
 * Modes:
 *   Packet     – waits until the player has been falling fast
 *                (velocity.y < −0.5) for at least one full tick, then
 *                forces onGround=true in every outgoing movement packet
 *                until the player lands. Resets the fall-tick counter when
 *                the player is actually on the ground.
 *   Spoof      – unconditionally forces onGround=true in every outgoing
 *                movement packet regardless of fall state.
 *   No Ground  – unconditionally forces onGround=false, tricking the server
 *                into never applying fall damage (the server only calculates
 *                fall damage when it sees an onGround=true transition from
 *                an airborne state).
 */
public class NoFall extends Module {

    private final ModeProperty mode = addProperty(
            new ModeProperty("Mode", "Packet", "Packet", "Spoof", "No Ground"));

    // ── Internal state ───────────────────────────────────────────────────────

    /**
     * Number of consecutive ticks the player has been falling fast.
     * Reset to 0 whenever the player is on the ground.
     */
    private int fallTicks = 0;

    public NoFall() {
        super("NoFall", "Prevents fall damage by spoofing onGround", Category.MOVEMENT);
    }

    // ── Reset on disable ─────────────────────────────────────────────────────

    @Override
    public void onDisable() {
        fallTicks = 0;
    }

    // ── Tick – track fall state ──────────────────────────────────────────────

    @EventTarget
    public void onTick(TickEvent event) {
        if (mc.player == null) return;

        setSuffix(mode.getValue());

        if (mc.player.isOnGround()) {
            // Back on solid ground — reset counter.
            fallTicks = 0;
        } else if (mc.player.getVelocity().y < -0.5) {
            // Falling fast — accumulate.
            fallTicks++;
        }
    }

    // ── Packet – modify outgoing movement packets ────────────────────────────

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (event.getDirection() != PacketEvent.Direction.SEND) return;
        if (!(event.getPacket() instanceof PlayerMoveC2SPacket packet)) return;
        if (mc.player == null) return;

        PlayerMoveC2SPacketAccessor accessor = (PlayerMoveC2SPacketAccessor) packet;

        switch (mode.getValue()) {
            case "Packet" -> {
                // Only spoof after at least 1 tick of fast falling so we don't
                // interfere with short hops.
                if (fallTicks >= 1) {
                    accessor.setOnGround(true);
                }
            }
            case "Spoof" -> {
                // Always tell the server we are on the ground.
                accessor.setOnGround(true);
            }
            case "No Ground" -> {
                // Never tell the server we touched the ground — server skips
                // fall-damage calculation for a grounded → airborne transition.
                accessor.setOnGround(false);
            }
        }
    }
}
