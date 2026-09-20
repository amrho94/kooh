package cc.prism.property;

public class NumberProperty extends Property<Double> {
    private final double min;
    private final double max;
    private final double increment;

    public NumberProperty(String name, double defaultValue, double min, double max, double increment) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    @Override
    public void setValue(Double value) {
        this.value = Math.max(min, Math.min(max, value));
    }

    public double getMin() { return min; }
    public double getMax() { return max; }
    public double getIncrement() { return increment; }
    public float getFloat() { return value.floatValue(); }
    public int getInt() { return value.intValue(); }

    @Override
    public String getDisplayValue() {
        if (value == Math.floor(value)) return String.valueOf(value.intValue());
        return String.format("%.1f", value);
    }
}






