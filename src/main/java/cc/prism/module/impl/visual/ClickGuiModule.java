package cc.prism.module.impl.visual;

import cc.prism.module.Category;
import cc.prism.module.Module;

public class ClickGuiModule extends Module {

    public ClickGuiModule() {
        super("ClickGui", "Opens the ClickGUI", Category.VISUAL);
        // Hidden so it never appears in the ArrayList HUD
        setHidden(true);
    }
}






