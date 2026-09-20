package cc.prism.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class RenderUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    // ── Filled rects ─────────────────────────────────────────────────────────

    public static void fill(DrawContext ctx, int x, int y, int w, int h, int color) {
        ctx.fill(x, y, x + w, y + h, color);
    }

    public static void fillRect(DrawContext ctx, int x1, int y1, int x2, int y2, int color) {
        ctx.fill(x1, y1, x2, y2, color);
    }

    // ── Borders ───────────────────────────────────────────────────────────────

    public static void drawBorder(DrawContext ctx, int x, int y, int w, int h, int thickness, int color) {
        ctx.fill(x, y, x + w, y + thickness, color);                    // top
        ctx.fill(x, y + h - thickness, x + w, y + h, color);            // bottom
        ctx.fill(x, y + thickness, x + thickness, y + h - thickness, color); // left
        ctx.fill(x + w - thickness, y + thickness, x + w, y + h - thickness, color); // right
    }

    // ── Horizontal gradient ──────────────────────────────────────────────────

    public static void fillGradientH(DrawContext ctx, int x, int y, int w, int h, int colorLeft, int colorRight) {
        ctx.fillGradient(x, y, x + w, y + h, colorLeft, colorRight);
    }

    // ── Text helpers ─────────────────────────────────────────────────────────

    public static void drawText(DrawContext ctx, String text, int x, int y, int color, boolean shadow) {
        ctx.drawText(mc.textRenderer, text, x, y, color, shadow);
    }

    public static int textWidth(String text) {
        return mc.textRenderer.getWidth(text);
    }

    public static int textHeight() {
        return mc.textRenderer.fontHeight;
    }

    // ── Texture helpers ──────────────────────────────────────────────────────

    public static Identifier id(String path) {
        return Identifier.of("prism", path);
    }

    public static void drawTexture(DrawContext ctx, Identifier texture, int x, int y, int w, int h) {
        ctx.drawTexture(net.minecraft.client.render.RenderLayer::getGuiTextured,
                texture, x, y, 0, 0, w, h, w, h);
    }
}
