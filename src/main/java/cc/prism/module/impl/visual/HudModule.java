package cc.prism.module.impl.visual;

import cc.prism.module.Category;
import cc.prism.module.Module;

public class HudModule extends Module {

    public HudModule() {
        super("HUD", "Shows the HUD elements", Category.VISUAL);
        // Hidden from ArrayList; enabled state is checked directly by HUD renderers
        setHidden(true);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}






