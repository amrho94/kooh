package cc.prism.module.impl.movement;

import cc.prism.module.Category;
import cc.prism.module.Module;

public class NoJumpDelay extends Module {

    public NoJumpDelay() {
        super("NoJumpDelay", "Removes the delay between jumps", Category.MOVEMENT);
    }

    // Logic applied in MixinClientPlayerEntity - checks isEnabled() there
}
