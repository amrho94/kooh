package cc.prism.module.impl.visual;

import cc.prism.event.EventTarget;
import cc.prism.event.impl.TickEvent;
import cc.prism.module.Category;
import cc.prism.module.Module;

public class FullBright extends Module {

    /** The gamma value that was active before FullBright was enabled. */
    private double savedGamma = 1.0;

    public FullBright() {
        super("FullBright", "Maximises in-game brightness", Category.VISUAL);
    }

    @Override
    public void onEnable() {
        if (mc.options == null) return;
        savedGamma = mc.options.getGamma().getValue();
        mc.options.getGamma().setValue(100.0);
    }

    @Override
    public void onDisable() {
        if (mc.options == null) return;
        mc.options.getGamma().setValue(savedGamma);
    }

    /**
     * Re-applies the gamma each tick so that nothing else (e.g. a resource
     * pack or another mod) can quietly reset it while we're enabled.
     */
    @EventTarget
    public void onTick(TickEvent event) {
        if (mc.options == null) return;
        double current = mc.options.getGamma().getValue();
        if (current != 100.0) {
            mc.options.getGamma().setValue(100.0);
        }
    }
}






