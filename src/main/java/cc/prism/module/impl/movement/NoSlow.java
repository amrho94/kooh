package cc.prism.module.impl.movement;

import cc.prism.module.Category;
import cc.prism.module.Module;
import cc.prism.property.ModeProperty;

/**
 * NoSlow — removes the movement speed penalty applied while using items
 * (eating, drinking, blocking with a sword, drawing a bow, etc.).
 *
 * The actual speed-penalty bypass is injected via MixinClientPlayerEntity,
 * which calls {@link #isActive()} and {@link #isVanillaMode()} to decide
 * whether to suppress the {@code getAttributeValue} multiplier that Minecraft
 * applies when {@code player.isUsingItem()} is true.
 *
 * Modes:
 *   Vanilla      – suppress the item-use speed modifier while any item is in
 *                  use. Works on vanilla servers and most lightly-patched ones.
 *   Hypixel NCP  – same bypass but applies only on the client tick where the
 *                  item-use begins and periodically thereafter, making the
 *                  motion pattern harder for NCP's speed checks to flag.
 *
 * Design note:
 *   This class intentionally holds no @EventTarget methods. All interception
 *   happens at the mixin layer so that the bypass is applied as early as
 *   possible in the movement pipeline (before InputController reads speed).
 */
public class NoSlow extends Module {

    private final ModeProperty mode = addProperty(
            new ModeProperty("Mode", "Vanilla", "Vanilla", "Hypixel NCP"));

    public NoSlow() {
        super("NoSlow", "Removes slowness from blocking/eating", Category.MOVEMENT);
    }

    // ── Static helpers for use by MixinClientPlayerEntity ────────────────────

    /**
     * Returns {@code true} when NoSlow is loaded and enabled.
     * Safe to call from any mixin context; catches any initialisation errors.
     */
    public static boolean isActive() {
        try {
            Module m = cc.prism.Prism.MODULE_MANAGER.get(NoSlow.class);
            return m != null && m.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns {@code true} when the active mode is {@code "Vanilla"}.
     * The mixin uses this to decide whether to apply the full suppression or
     * the periodic/reduced Hypixel NCP variant.
     */
    public static boolean isVanillaMode() {
        try {
            Module m = cc.prism.Prism.MODULE_MANAGER.get(NoSlow.class);
            if (m instanceof NoSlow noSlow) {
                return noSlow.mode.is("Vanilla");
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
