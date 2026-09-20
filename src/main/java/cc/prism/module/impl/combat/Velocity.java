package cc.prism.module.impl.combat;

import cc.prism.event.EventTarget;
import cc.prism.event.impl.PacketEvent;
import cc.prism.event.impl.TickEvent;
import cc.prism.module.Category;
import cc.prism.module.Module;
import cc.prism.property.ModeProperty;
import cc.prism.property.NumberProperty;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;

/**
 * Velocity â€” reduces or negates incoming knockback.
 *
 * Modes:
 *   Standard    â€“ cancel the velocity packet entirely for the local player.
 *   Jump Reset  â€“ let the packet through but immediately jump if the player is
 *                 on the ground when hit (hurtTime == 9), optionally after a
 *                 configurable delay in ticks.
 *   Hypixel NCP â€“ cancel only the horizontal components; keep vertical (Y)
 *                 velocity so the server anti-cheat doesn't flag the player.
 */
public class Velocity extends Module {

    private final ModeProperty mode = addProperty(
            new ModeProperty("Mode", "Standard", "Standard", "Jump Reset", "Hypixel NCP"));

    /** Delay (ticks) before jumping in Jump Reset mode. */
    private final NumberProperty jumpDelay = addProperty(
            new NumberProperty("Jump Delay", 0, 0, 10, 1));

    // â”€â”€ Internal state â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    /** Whether a velocity packet was received this cycle (Jump Reset mode). */
    private boolean receivedVelocity = false;

    /** Ticks elapsed since the velocity packet was received. */
    private int delayTicks = 0;

    // â”€â”€ Stored velocity components for Hypixel NCP mode â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    private double pendingVelX = 0;
    private double pendingVelY = 0;
    private double pendingVelZ = 0;

    public Velocity() {
        super("Velocity", "Reduces or cancels incoming knockback", Category.COMBAT);
    }

    // â”€â”€ Enable / Disable cleanup â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Override
    public void onDisable() {
        receivedVelocity = false;
        delayTicks = 0;
    }

    // â”€â”€ Packet interception â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (event.getDirection() != PacketEvent.Direction.RECEIVE) return;
        if (!(event.getPacket() instanceof ClientboundSetEntityMotionPacket packet)) return;
        if (mc.player == null) return;
        if (packet.getEntityId() != mc.player.getId()) return;

        setSuffix(mode.getValue());

        switch (mode.getValue()) {
            case "Standard" -> {
                // Drop the packet completely â€” no knockback applied at all.
                event.setCancelled(true);
            }

            case "Jump Reset" -> {
                // Allow the velocity packet but flag that we need to jump.
                receivedVelocity = true;
                delayTicks = 0;
            }

            case "Hypixel NCP" -> {
                // Cancel the packet, but we will re-apply only the Y component
                // on the next tick so NCP sees a vertical motion delta.
                pendingVelX = packet.getVelocityX() / 8000.0;
                pendingVelY = packet.getVelocityY() / 8000.0;
                pendingVelZ = packet.getVelocityZ() / 8000.0;
                event.setCancelled(true);
            }
        }
    }

    // â”€â”€ Tick logic â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @EventTarget
    public void onTick(TickEvent event) {
        if (mc.player == null) return;

        setSuffix(mode.getValue());

        switch (mode.getValue()) {
            case "Jump Reset" -> handleJumpReset();
            case "Hypixel NCP" -> handleHypixelNCP();
        }
    }

    // â”€â”€ Mode helpers â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    /**
     * Jump Reset: when the server sends a velocity packet (hurtTime spikes to 9)
     * and the player is on the ground, perform a jump after {@code jumpDelay} ticks.
     */
    private void handleJumpReset() {
        if (!receivedVelocity) return;

        // hurtTime peaks at 10 on the first hurt tick in 1.21.x; wait for 9 so we
        // catch it the tick it is decremented, which is the most common check point.
        boolean isHurt = mc.player.hurtTime >= 9;
        if (!isHurt) {
            // Packet was received but hurtTime hasn't confirmed it yet â€” keep waiting.
            return;
        }

        if (delayTicks < jumpDelay.getInt()) {
            delayTicks++;
            return;
        }

        if (mc.player.isOnGround()) {
            mc.player.jump();
        }

        receivedVelocity = false;
        delayTicks = 0;
    }

    /**
     * Hypixel NCP: re-apply only the vertical (Y) component of the cancelled
     * velocity so the server anti-cheat observes a legitimate upward knock.
     */
    private void handleHypixelNCP() {
        if (pendingVelY == 0) return;

        mc.player.setDeltaMovement(
                mc.player.getDeltaMovement().x,
                pendingVelY,
                mc.player.getDeltaMovement().z
        );

        // Zero out so we only apply once per packet.
        pendingVelX = 0;
        pendingVelY = 0;
        pendingVelZ = 0;
    }
}






