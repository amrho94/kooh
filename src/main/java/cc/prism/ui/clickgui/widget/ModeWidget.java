package cc.prism.ui.clickgui.widget;

import cc.prism.module.impl.client.InterfaceModule;
import cc.prism.property.ModeProperty;
import cc.prism.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class ModeWidget extends AbstractWidget {
    private final ModeProperty property;

    public ModeWidget(ModeProperty property) {
        this.property = property;
    }

    @Override
    public void render(GuiGraphicsExtractor ctx, int x, int y, int w, int mx, int my) {
        int accent = InterfaceModule.accentColor.getColor();
        RenderUtil.fillRect(ctx, x, y, x + w, y + HEIGHT, 0xBB161616);
        RenderUtil.drawText(ctx, " " + property.getName(), x + 4, y + 2, 0xFFCCCCCC, false);

        // Current mode shown on the right
        String val = property.getValue();
        int valX = x + w - RenderUtil.textWidth(val) - 5;
        RenderUtil.drawText(ctx, val, valX, y + 2, accent, false);
    }

    @Override
    public boolean mouseClicked(int mx, int my, int button, int x, int y, int w) {
        if (!isHovering(mx, my, x, y, w)) return false;
        if (button == 0) { property.cycle(); return true; }
        if (button == 1) {
            // Cycle backwards
            int idx = property.getModes().indexOf(property.getValue());
            int prev = (idx - 1 + property.getModes().size()) % property.getModes().size();
            property.setValue(property.getModes().get(prev));
            return true;
        }
        return false;
    }
}






