package cc.prism.module.impl.movement;

import cc.prism.event.EventTarget;
import cc.prism.event.impl.TickEvent;
import cc.prism.module.Category;
import cc.prism.module.Module;
import cc.prism.property.ModeProperty;
import cc.prism.property.NumberProperty;
import cc.prism.util.MoveUtil;

public class Speed extends Module {
    private final ModeProperty mode  = addProperty(new ModeProperty("Mode", "Strafe", "Strafe", "BHop"));
    private final NumberProperty multiplier = addProperty(new NumberProperty("Multiplier", 1.3, 1.0, 3.0, 0.05));

    public Speed() {
        super("Speed", "Increases movement speed", Category.MOVEMENT);
    }

    @EventTarget
    public void onTick(TickEvent e) {
        if (mc.player == null) return;
        if (!MoveUtil.isInputtingMovement()) return;
        setSuffix(mode.getValue());

        if (mode.is("Strafe")) {
            if (mc.player.isOnGround()) {
                MoveUtil.setSpeed(0.2873 * multiplier.getFloat());
                mc.player.jump();
            }
        } else if (mode.is("BHop")) {
            if (mc.player.isOnGround()) {
                mc.player.jump();
            }
            if (MoveUtil.isMoving()) {
                MoveUtil.setSpeed(Math.max(MoveUtil.getSpeed(), 0.2873));
            }
        }
    }
}
