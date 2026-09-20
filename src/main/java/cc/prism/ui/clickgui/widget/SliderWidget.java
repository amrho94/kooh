package cc.prism.ui.clickgui.widget;

import cc.prism.module.impl.client.InterfaceModule;
import cc.prism.property.NumberProperty;
import cc.prism.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;

public class SliderWidget extends AbstractWidget {
    private static final int TRACK_H  = 3;
    private static final int PADDING  = 4;

    private final NumberProperty property;
    private boolean dragging = false;

    public SliderWidget(NumberProperty property) {
        this.property = property;
    }

    @Override
    public int getHeight() { return 20; }

    @Override
    public void render(DrawContext ctx, int x, int y, int w, int mx, int my) {
        int accent = InterfaceModule.accentColor.getColor();

        // Background
        RenderUtil.fillRect(ctx, x, y, x + w, y + getHeight(), 0xBB161616);

        // Label + value
        String label = " " + property.getName() + " §8" + property.getDisplayValue();
        RenderUtil.drawText(ctx, label, x + PADDING, y + 2, 0xFFCCCCCC, false);

        // Track
        int trackX = x + PADDING;
        int trackW = w - PADDING * 2;
        int trackY = y + getHeight() - TRACK_H - 3;
        RenderUtil.fillRect(ctx, trackX, trackY, trackX + trackW, trackY + TRACK_H, 0xFF333333);

        // Fill
        double pct = (property.getValue() - property.getMin()) / (property.getMax() - property.getMin());
        int fillW = (int) (trackW * pct);
        RenderUtil.fillRect(ctx, trackX, trackY, trackX + fillW, trackY + TRACK_H, accent);

        // Thumb
        int thumbX = trackX + fillW - 2;
        RenderUtil.fillRect(ctx, thumbX, trackY - 1, thumbX + 4, trackY + TRACK_H + 1, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(int mx, int my, int button, int x, int y, int w) {
        if (button == 0 && isHovering(mx, my, x, y, w)) {
            dragging = true;
            updateValue(mx, x, w);
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(int mx, int my, int button, int x, int y, int w) {
        if (button == 0) dragging = false;
    }

    @Override
    public void mouseDragged(int mx, int my, int button, int x, int y, int w) {
        if (dragging) updateValue(mx, x, w);
    }

    private void updateValue(int mx, int x, int w) {
        int trackX = x + 4;
        int trackW = w - 8;
        double pct = Math.max(0, Math.min(1, (double)(mx - trackX) / trackW));
        double raw = property.getMin() + pct * (property.getMax() - property.getMin());
        // Snap to increment
        double inc = property.getIncrement();
        raw = Math.round(raw / inc) * inc;
        property.setValue(raw);
    }
}
