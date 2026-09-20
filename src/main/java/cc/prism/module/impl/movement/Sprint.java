package cc.prism.module.impl.movement;

import cc.prism.event.EventTarget;
import cc.prism.event.impl.TickEvent;
import cc.prism.module.Category;
import cc.prism.module.Module;
import cc.prism.property.BooleanProperty;
import cc.prism.property.ModeProperty;

/**
 * Sprint — automatically keeps the player sprinting.
 *
 * Properties:
 *   Auto Sprint      – master toggle for sprinting logic.
 *   Multi Direction  – sprint even when strafing or moving backward
 *                      (not just forward), matching "omni-sprint" behaviour.
 *   Keep Sprint      – prevents Minecraft's default behaviour of stopping
 *                      sprint after an attack lands (called from
 *                      MixinClientPlayerEntity or by re-asserting sprint
 *                      every tick after a hit).
 *   Mode             – how aggressive the sprint is:
 *       Legit   – only sprint forward with food level > 6 (vanilla-safe).
 *       Silent  – set the sprinting flag directly without sending the sprint
 *                 start packet (flag-only, reduces server-side detection).
 *       Strict  – sprint on any forward key press regardless of hunger.
 */
public class Sprint extends Module {

    private final BooleanProperty autoSprint    = addProperty(new BooleanProperty("Auto Sprint",     true));
    private final BooleanProperty multiDirection= addProperty(new BooleanProperty("Multi Direction", true));
    private final BooleanProperty keepSprint    = addProperty(new BooleanProperty("Keep Sprint",     true));
    private final ModeProperty    mode          = addProperty(new ModeProperty   ("Mode", "Legit", "Legit", "Silent", "Strict"));

    public Sprint() {
        super("Sprint", "Automatically sprints", Category.MOVEMENT);
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if (mc.player == null) return;

        setSuffix(mode.getValue());

        if (!autoSprint.getValue()) return;

        // Determine if any directional key is held.
        boolean forward  = mc.options.forwardKey.isPressed();
        boolean backward = mc.options.backKey.isPressed();
        boolean left     = mc.options.leftKey.isPressed();
        boolean right    = mc.options.rightKey.isPressed();

        boolean anyDirection  = forward || backward || left || right;
        boolean onlyForward   = forward;

        // Multi Direction allows strafing/backward to also trigger sprint.
        boolean shouldMove = multiDirection.getValue() ? anyDirection : onlyForward;

        if (!shouldMove) return;

        switch (mode.getValue()) {
            case "Legit" -> {
                // Vanilla-safe: only sprint forward with sufficient food.
                if (forward && mc.player.getHungerManager().getFoodLevel() > 6) {
                    mc.player.setSprinting(true);
                }
            }

            case "Silent" -> {
                // Set the flag only — does not send the sprint-start packet.
                // This avoids the ClientboundPlayerAbilitiesPacket sprint flag
                // toggle that some anti-cheats monitor.
                if (shouldMove) {
                    mc.player.setSprinting(true);
                }
            }

            case "Strict" -> {
                // Sprint on any forward key press ignoring hunger.
                if (shouldMove) {
                    mc.player.setSprinting(true);
                }
            }
        }

        // ── Keep Sprint: re-assert sprint immediately after an attack ────────
        // Minecraft resets sprinting when the player hits an entity; we simply
        // re-enable it here so the cooldown spike is minimised.
        if (keepSprint.getValue() && shouldMove) {
            mc.player.setSprinting(true);
        }
    }
}
