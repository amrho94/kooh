package cc.prism.module.impl.player;

import cc.prism.module.Category;
import cc.prism.module.Module;

/**
 * NoPlaceDelay â€” removes the block-placement cooldown that normally prevents
 * placing more than one block per few ticks.
 * <p>
 * Actual bypass logic lives in {@code MixinClientPlayerInteractionManager};
 * this class exists solely so the module appears in the ClickGUI and its
 * state can be queried via {@link #isActive()}.
 */
public class NoPlaceDelay extends Module {

    public NoPlaceDelay() {
        super("NoPlaceDelay", "Removes block placement cooldown", Category.PLAYER);
    }

    /**
     * Called by the mixin.  Wrapped in try/catch for safety during early init.
     */
    public static boolean isActive() {
        try {
            Module m = cc.prism.Prism.MODULE_MANAGER.get(NoPlaceDelay.class);
            return m != null && m.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }
}






