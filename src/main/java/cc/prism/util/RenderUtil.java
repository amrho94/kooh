package cc.prism.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Identifier;

public class RenderUtil {
    private static final Minecraft mc = Minecraft.getInstance();

    // â”€â”€ Filled rects â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    public static void fill(GuiGraphicsExtractor ctx, int x, int y, int w, int h, int color) {
        ctx.fill(x, y, x + w, y + h, color);
    }

    public static void fillRect(GuiGraphicsExtractor ctx, int x1, int y1, int x2, int y2, int color) {
        ctx.fill(x1, y1, x2, y2, color);
    }

    // â”€â”€ Borders â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    public static void drawBorder(GuiGraphicsExtractor ctx, int x, int y, int w, int h, int thickness, int color) {
        ctx.fill(x, y, x + w, y + thickness, color);                    // top
        ctx.fill(x, y + h - thickness, x + w, y + h, color);            // bottom
        ctx.fill(x, y + thickness, x + thickness, y + h - thickness, color); // left
        ctx.fill(x + w - thickness, y + thickness, x + w, y + h - thickness, color); // right
    }

    // â”€â”€ Horizontal gradient â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    public static void fillGradientH(GuiGraphicsExtractor ctx, int x, int y, int w, int h, int colorLeft, int colorRight) {
        ctx.fillGradient(x, y, x + w, y + h, colorLeft, colorRight);
    }

    // â”€â”€ Text helpers â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    public static void drawText(GuiGraphicsExtractor ctx, String text, int x, int y, int color, boolean shadow) {
        ctx.drawText(mc.font, text, x, y, color, shadow);
    }

    public static int textWidth(String text) {
        return mc.font.getWidth(text);
    }

    public static int textHeight() {
        return mc.font.lineHeight;
    }

    // â”€â”€ Texture helpers â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    public static Identifier id(String path) {
        return Identifier.of("prism", path);
    }

    public static void drawTexture(GuiGraphicsExtractor ctx, Identifier texture, int x, int y, int w, int h) {
        ctx.drawTexture(net.minecraft.client.renderer.RenderLayer::getGuiTextured,
                texture, x, y, 0, 0, w, h, w, h);
    }
}






