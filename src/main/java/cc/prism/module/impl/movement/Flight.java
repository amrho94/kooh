package cc.prism.module.impl.movement;

import cc.prism.event.EventTarget;
import cc.prism.event.impl.TickEvent;
import cc.prism.module.Category;
import cc.prism.module.Module;
import cc.prism.property.ModeProperty;
import cc.prism.property.NumberProperty;

public class Flight extends Module {
    private final ModeProperty mode  = addProperty(new ModeProperty("Mode", "Creative", "Creative", "Vanilla"));
    private final NumberProperty speed = addProperty(new NumberProperty("Speed", 0.1, 0.01, 2.0, 0.01));

    public Flight() {
        super("Flight", "Allows you to fly", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        if (mc.player == null) return;
        if (mode.is("Creative")) {
            mc.player.getAbilities().allowFlying = true;
            mc.player.getAbilities().flying = true;
        }
    }

    @Override
    public void onDisable() {
        if (mc.player == null) return;
        if (!mc.player.isCreative()) {
            mc.player.getAbilities().allowFlying = false;
            mc.player.getAbilities().flying = false;
        }
    }

    @EventTarget
    public void onTick(TickEvent e) {
        if (mc.player == null) return;
        setSuffix(mode.getValue());
        if (mode.is("Vanilla")) {
            mc.player.getAbilities().allowFlying = true;
            mc.player.getAbilities().flying = true;
            mc.player.getAbilities().setFlySpeed(speed.getFloat());
        }
    }
}






