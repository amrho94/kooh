package cc.prism.property;

import java.util.Arrays;
import java.util.List;

public class ModeProperty extends Property<String> {
    private final List<String> modes;

    public ModeProperty(String name, String defaultValue, String... modes) {
        super(name, defaultValue);
        this.modes = Arrays.asList(modes);
    }

    public List<String> getModes() {
        return modes;
    }

    public void cycle() {
        int idx = modes.indexOf(value);
        value = modes.get((idx + 1) % modes.size());
    }

    public boolean is(String mode) {
        return value.equalsIgnoreCase(mode);
    }

    @Override
    public String getDisplayValue() {
        return value;
    }
}
