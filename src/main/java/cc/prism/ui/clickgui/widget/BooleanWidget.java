package cc.prism.ui.clickgui.widget;

import cc.prism.module.impl.client.InterfaceModule;
import cc.prism.property.BooleanProperty;
import cc.prism.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class BooleanWidget extends AbstractWidget {
    private final BooleanProperty property;

    public BooleanWidget(BooleanProperty property) {
        this.property = property;
    }

    @Override
    public void render(GuiGraphicsExtractor ctx, int x, int y, int w, int mx, int my) {
        boolean val = property.getValue();
        int accent = InterfaceModule.accentColor.getColor();

        // Row background
        RenderUtil.fillRect(ctx, x, y, x + w, y + HEIGHT, 0xBB161616);

        // Checkbox square on the right
        int cbX = x + w - 12;
        int cbY = y + 2;
        int cbSize = 8;
        RenderUtil.fillRect(ctx, cbX, cbY, cbX + cbSize, cbY + cbSize, val ? accent : 0xFF444444);
        if (val) {
            RenderUtil.drawText(ctx, "âœ”", cbX + 1, cbY, 0xFFFFFFFF, false);
        }

        // Property name
        RenderUtil.drawText(ctx, " " + property.getName(), x + 4, y + 2, 0xFFCCCCCC, false);
    }

    @Override
    public boolean mouseClicked(int mx, int my, int button, int x, int y, int w) {
        if (button == 0 && isHovering(mx, my, x, y, w)) {
            property.toggle();
            return true;
        }
        return false;
    }
}






