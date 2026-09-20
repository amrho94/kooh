package cc.prism.module.impl.movement;

import cc.prism.event.EventTarget;
import cc.prism.event.impl.PacketEvent;
import cc.prism.event.impl.TickEvent;
import cc.prism.module.Category;
import cc.prism.module.Module;
import cc.prism.mixin.accessor.PlayerMoveC2SPacketAccessor;
import cc.prism.property.ModeProperty;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

/**
 * NoFall â€” prevents the player from taking fall damage.
 *
 * Modes:
 *   Packet     â€“ waits until the player has been falling fast
 *                (velocity.y < âˆ’0.5) for at least one full tick, then
 *                forces onGround=true in every outgoing movement packet
 *                until the player lands. Resets the fall-tick counter when
 *                the player is actually on the ground.
 *   Spoof      â€“ unconditionally forces onGround=true in every outgoing
 *                movement packet regardless of fall state.
 *   No Ground  â€“ unconditionally forces onGround=false, tricking the server
 *                into never applying fall damage (the server only calculates
 *                fall damage when it sees an onGround=true transition from
 *                an airborne state).
 */
public class NoFall extends Module {

    private final ModeProperty mode = addProperty(
            new ModeProperty("Mode", "Packet", "Packet", "Spoof", "No Ground"));

    // â”€â”€ Internal state â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    /**
     * Number of consecutive ticks the player has been falling fast.
     * Reset to 0 whenever the player is on the ground.
     */
    private int fallTicks = 0;

    public NoFall() {
        super("NoFall", "Prevents fall damage by spoofing onGround", Category.MOVEMENT);
    }

    // â”€â”€ Reset on disable â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Override
    public void onDisable() {
        fallTicks = 0;
    }

    // â”€â”€ Tick â€“ track fall state â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @EventTarget
    public void onTick(TickEvent event) {
        if (mc.player == null) return;

        setSuffix(mode.getValue());

        if (mc.player.isOnGround()) {
            // Back on solid ground â€” reset counter.
            fallTicks = 0;
        } else if (mc.player.getDeltaMovement().y < -0.5) {
            // Falling fast â€” accumulate.
            fallTicks++;
        }
    }

    // â”€â”€ Packet â€“ modify outgoing movement packets â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (event.getDirection() != PacketEvent.Direction.SEND) return;
        if (!(event.getPacket() instanceof ServerboundMovePlayerPacket packet)) return;
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
                // Never tell the server we touched the ground â€” server skips
                // fall-damage calculation for a grounded â†’ airborne transition.
                accessor.setOnGround(false);
            }
        }
    }
}






