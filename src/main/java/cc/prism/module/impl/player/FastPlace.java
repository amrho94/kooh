package cc.prism.module.impl.player;

import cc.prism.module.Category;
import cc.prism.module.Module;

/**
 * FastPlace — removes the per-block placement delay so blocks can be placed
 * every tick without the normal cooldown.
 * <p>
 * The actual bypass is applied in {@code MixinClientPlayerInteractionManager}
 * which calls {@link #isActive()} to decide whether to skip the cooldown check.
 */
public class FastPlace extends Module {

    public FastPlace() {
        super("FastPlace", "Removes block placement delay", Category.PLAYER);
    }

    /**
     * Checked by the mixin.  Uses a safe try/catch so it never throws if the
     * module manager is not yet initialised (e.g. during early startup).
     */
    public static boolean isActive() {
        try {
            Module m = cc.prism.Prism.MODULE_MANAGER.get(FastPlace.class);
            return m != null && m.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }
}
