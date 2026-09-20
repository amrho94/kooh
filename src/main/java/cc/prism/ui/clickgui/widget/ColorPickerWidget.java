package cc.prism.ui.clickgui.widget;

import cc.prism.property.ColorProperty;
import cc.prism.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;

import java.awt.Color;

/**
 * Color picker widget — shows a hue strip + saturation/brightness square + hex preview.
 * Expands when clicked.
 */
public class ColorPickerWidget extends AbstractWidget {
    private static final int PICKER_H = 60;
    private static final int HUE_H    = 8;
    private static final int GAP      = 2;

    private final ColorProperty property;
    private boolean open = false;
    private boolean draggingSV = false;
    private boolean draggingHue = false;

    // HSB state
    private float hue = 0, sat = 1, bri = 1;

    public ColorPickerWidget(ColorProperty property) {
        this.property = property;
        updateHSBFromColor();
    }

    private void updateHSBFromColor() {
        int c = property.getColor();
        float[] hsb = Color.RGBtoHSB(
                (c >> 16) & 0xFF,
                (c >> 8)  & 0xFF,
                c         & 0xFF, null);
        hue = hsb[0]; sat = hsb[1]; bri = hsb[2];
    }

    private void applyHSB() {
        int rgb = Color.HSBtoRGB(hue, sat, bri);
        property.setValue((property.getAlpha() << 24) | (rgb & 0x00FFFFFF));
    }

    @Override
    public int getHeight() {
        return AbstractWidget.HEIGHT + (open ? GAP + PICKER_H + GAP + HUE_H + GAP : 0);
    }

    @Override
    public void render(DrawContext ctx, int x, int y, int w, int mx, int my) {
        // Header row
        RenderUtil.fillRect(ctx, x, y, x + w, y + AbstractWidget.HEIGHT, 0xBB161616);
        RenderUtil.drawText(ctx, " " + property.getName(), x + 4, y + 2, 0xFFCCCCCC, false);

        // Color swatch on the right
        int swatchX = x + w - 22;
        RenderUtil.fillRect(ctx, swatchX, y + 2, swatchX + 18, y + AbstractWidget.HEIGHT - 2, 0xFF000000);
        RenderUtil.fillRect(ctx, swatchX + 1, y + 3, swatchX + 17, y + AbstractWidget.HEIGHT - 3, property.getColor() | 0xFF000000);
        RenderUtil.drawText(ctx, property.toHex(), x + w - 56, y + 2, 0xFF888888, false);

        if (!open) return;

        int py = y + AbstractWidget.HEIGHT + GAP;

        // SV square (approximate with steps)
        int sqW = w - 8;
        int sqX = x + 4;
        renderSVSquare(ctx, sqX, py, sqW, PICKER_H);

        // SV cursor
        int curX = sqX + (int)(sat * sqW);
        int curY = py + (int)((1f - bri) * PICKER_H);
        RenderUtil.fillRect(ctx, curX - 2, curY - 2, curX + 2, curY + 2, 0xFFFFFFFF);

        // Hue bar
        int hueY = py + PICKER_H + GAP;
        renderHueBar(ctx, sqX, hueY, sqW, HUE_H);

        // Hue cursor
        int hueCurX = sqX + (int)(hue * sqW);
        RenderUtil.fillRect(ctx, hueCurX - 1, hueY - 1, hueCurX + 1, hueY + HUE_H + 1, 0xFFFFFFFF);
    }

    private void renderSVSquare(DrawContext ctx, int x, int y, int w, int h) {
        // Approximate with 16 columns
        int steps = 16;
        int hueRgb = Color.HSBtoRGB(hue, 1f, 1f);
        for (int col = 0; col < steps; col++) {
            float s = (float) col / steps;
            int x1 = x + col * w / steps;
            int x2 = x + (col + 1) * w / steps;
            for (int row = 0; row < steps; row++) {
                float b = 1f - (float) row / steps;
                int y1 = y + row * h / steps;
                int y2 = y + (row + 1) * h / steps;
                int rgb = Color.HSBtoRGB(hue, s, b);
                RenderUtil.fillRect(ctx, x1, y1, x2, y2, rgb | 0xFF000000);
            }
        }
    }

    private void renderHueBar(DrawContext ctx, int x, int y, int w, int h) {
        int steps = 32;
        for (int i = 0; i < steps; i++) {
            float h1 = (float) i / steps;
            int x1 = x + i * w / steps;
            int x2 = x + (i + 1) * w / steps;
            int color = Color.HSBtoRGB(h1, 1f, 1f) | 0xFF000000;
            RenderUtil.fillRect(ctx, x1, y, x2, y + h, color);
        }
    }

    @Override
    public boolean mouseClicked(int mx, int my, int button, int x, int y, int w) {
        // Header click — toggle open
        if (my >= y && my <= y + AbstractWidget.HEIGHT && mx >= x && mx <= x + w) {
            if (button == 0) {
                open = !open;
                return true;
            }
        }
        if (!open) return false;

        int py = y + AbstractWidget.HEIGHT + GAP;
        int sqX = x + 4;
        int sqW = w - 8;
        int hueY = py + PICKER_H + GAP;

        // SV square
        if (mx >= sqX && mx <= sqX + sqW && my >= py && my <= py + PICKER_H) {
            draggingSV = true;
            updateSV(mx, my, sqX, py, sqW, PICKER_H);
            return true;
        }

        // Hue bar
        if (mx >= sqX && mx <= sqX + sqW && my >= hueY && my <= hueY + HUE_H) {
            draggingHue = true;
            hue = Math.max(0, Math.min(1, (float)(mx - sqX) / sqW));
            applyHSB();
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(int mx, int my, int button, int x, int y, int w) {
        draggingSV = false;
        draggingHue = false;
    }

    @Override
    public void mouseDragged(int mx, int my, int button, int x, int y, int w) {
        int py = y + AbstractWidget.HEIGHT + GAP;
        int sqX = x + 4;
        int sqW = w - 8;
        int hueY = py + PICKER_H + GAP;

        if (draggingSV) updateSV(mx, my, sqX, py, sqW, PICKER_H);
        if (draggingHue) {
            hue = Math.max(0, Math.min(1, (float)(mx - sqX) / sqW));
            applyHSB();
        }
    }

    private void updateSV(int mx, int my, int sqX, int sqY, int sqW, int sqH) {
        sat = Math.max(0, Math.min(1, (float)(mx - sqX) / sqW));
        bri = 1f - Math.max(0, Math.min(1, (float)(my - sqY) / sqH));
        applyHSB();
    }
}
