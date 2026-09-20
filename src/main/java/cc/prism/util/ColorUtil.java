package cc.prism.util;

public class ColorUtil {

    /** Pack ARGB components into a single int. */
    public static int rgba(int r, int g, int b, int a) {
        return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
    }

    public static int rgb(int r, int g, int b) {
        return rgba(r, g, b, 255);
    }

    /** Linearly interpolate between two ARGB colors. */
    public static int lerp(int color1, int color2, float t) {
        int a1 = (color1 >> 24) & 0xFF, r1 = (color1 >> 16) & 0xFF,
            g1 = (color1 >> 8)  & 0xFF, b1 =  color1        & 0xFF;
        int a2 = (color2 >> 24) & 0xFF, r2 = (color2 >> 16) & 0xFF,
            g2 = (color2 >> 8)  & 0xFF, b2 =  color2        & 0xFF;
        return rgba(
            (int)(r1 + (r2 - r1) * t),
            (int)(g1 + (g2 - g1) * t),
            (int)(b1 + (b2 - b1) * t),
            (int)(a1 + (a2 - a1) * t)
        );
    }

    /** Apply an alpha multiplier (0.0â€“1.0) to an existing ARGB color. */
    public static int withAlpha(int color, float alpha) {
        return (color & 0x00FFFFFF) | ((int)(alpha * 255) << 24);
    }

    /** Rainbow color based on time offset (0â€“1). */
    public static int rainbow(float offset) {
        float hue = (System.currentTimeMillis() % 2000 / 2000f + offset) % 1f;
        return java.awt.Color.HSBtoRGB(hue, 0.6f, 1.0f) | 0xFF000000;
    }

    /**
     * Generates the purpleâ†’pink gradient used by the ArrayList HUD.
     * @param index  module index (0 = top)
     * @param total  total enabled modules
     */
    public static int arrayListColor(int index, int total) {
        float t = total <= 1 ? 0f : (float) index / (total - 1);
        // purple  #C16DE8  ->  pink  #E86DA2
        int purple = rgb(193, 109, 232);
        int pink   = rgb(232, 109, 162);
        return lerp(purple, pink, t);
    }
}






