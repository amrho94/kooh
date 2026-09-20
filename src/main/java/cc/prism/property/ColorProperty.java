package cc.prism.property;

public class ColorProperty extends Property<Integer> {

    public ColorProperty(String name, int defaultColor) {
        super(name, defaultColor);
    }

    public int getColor() { return value; }
    public int getRed()   { return (value >> 16) & 0xFF; }
    public int getGreen() { return (value >> 8)  & 0xFF; }
    public int getBlue()  { return value         & 0xFF; }
    public int getAlpha() { return (value >> 24) & 0xFF; }

    public String toHex() {
        return String.format("#%06X", value & 0xFFFFFF);
    }

    @Override
    public String getDisplayValue() {
        return toHex();
    }
}






