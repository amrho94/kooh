package cc.prism.module.impl.client;

import cc.prism.module.Category;
import cc.prism.module.Module;
import cc.prism.property.BooleanProperty;
import cc.prism.property.ColorProperty;

/**
 * Controls global HUD and visual-interface settings.
 * <p>
 * Properties are declared {@code public static} so that HUD renderers can
 * read them without having to look up the module instance each frame.
 */
public class InterfaceModule extends Module {

    public static final ColorProperty  accentColor  = new ColorProperty("Accent Color", 0xFF0F58E5);
    public static final BooleanProperty arrayList   = new BooleanProperty("ArrayList",  true);
    public static final BooleanProperty watermark   = new BooleanProperty("Watermark",  true);
    public static final BooleanProperty rainbowMode = new BooleanProperty("Rainbow",    false);

    public InterfaceModule() {
        super("Interface", "Controls HUD and visual interface", Category.CLIENT);
        addProperty(accentColor);
        addProperty(arrayList);
        addProperty(watermark);
        addProperty(rainbowMode);
        // Enabled by default so the HUD is visible on first launch
        setEnabled(true);
    }
}






