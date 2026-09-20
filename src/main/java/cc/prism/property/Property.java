package cc.prism.property;

public abstract class Property<T> {
    private final String name;
    protected T value;

    public Property(String name, T defaultValue) {
        this.name = name;
        this.value = defaultValue;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public abstract String getDisplayValue();
}






