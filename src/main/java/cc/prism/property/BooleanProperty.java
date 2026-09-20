package cc.prism.property;

public class BooleanProperty extends Property<Boolean> {

    public BooleanProperty(String name, boolean defaultValue) {
        super(name, defaultValue);
    }

    public void toggle() {
        value = !value;
    }

    @Override
    public String getDisplayValue() {
        return value ? "On" : "Off";
    }
}






