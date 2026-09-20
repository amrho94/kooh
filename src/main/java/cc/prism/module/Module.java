package cc.prism.module;

import cc.prism.Prism;
import cc.prism.property.Property;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    protected static final Minecraft mc = Minecraft.getInstance();

    private final String name;
    private final String description;
    private final Category category;
    private boolean enabled;
    private boolean hidden;
    private int keybind;
    private String suffix = "";

    private final List<Property<?>> properties = new ArrayList<>();

    public Module(String name, String description, Category category, int keybind) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.keybind = keybind;
    }

    public Module(String name, String description, Category category) {
        this(name, description, category, -1);
    }

    /** Register a setting so the ClickGUI picks it up. */
    protected <T extends Property<?>> T addProperty(T property) {
        properties.add(property);
        return property;
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            Prism.EVENT_BUS.register(this);
            onEnable();
        } else {
            Prism.EVENT_BUS.unregister(this);
            onDisable();
        }
    }

    public void onEnable()  {}
    public void onDisable() {}

    // â”€â”€ Getters / setters â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    public String   getName()        { return name; }
    public String   getDescription() { return description; }
    public Category getCategory()    { return category; }
    public boolean  isEnabled()      { return enabled; }
    public boolean  isHidden()       { return hidden; }
    public void     setHidden(boolean h) { this.hidden = h; }
    public int      getKeybind()     { return keybind; }
    public void     setKeybind(int k){ this.keybind = k; }
    public String   getSuffix()      { return suffix; }
    public void     setSuffix(String s){ this.suffix = s; }

    public List<Property<?>> getProperties() { return properties; }

    /** Display name shown in ArrayList (includes suffix if set). */
    public String getDisplayName() {
        if (suffix != null && !suffix.isEmpty())
            return name + " Â§7[" + suffix + "]";
        return name;
    }
}






