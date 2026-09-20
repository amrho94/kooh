package cc.prism.ui.hud;

import cc.prism.Prism;
import cc.prism.module.Module;
import cc.prism.module.impl.client.InterfaceModule;
import cc.prism.util.ColorUtil;
import cc.prism.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

/**
 * Right-side ArrayList showing enabled modules sorted A→Z.
 * Each line gets a gradient color from purple→pink (matching screenshot 2).
 */
public class ArrayListHud extends HudElement {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final int PADDING_X = 4;
    private static final int LINE_HEIGHT = 10;

    public ArrayListHud() {
        super(0, 30); // x set dynamically from screen width
    }

    @Override
    public void render(DrawContext ctx, float delta) {
        if (!InterfaceModule.arrayList.getValue()) return;
        if (mc.currentScreen != null && !(mc.currentScreen instanceof cc.prism.ui.clickgui.ClickGuiScreen)) return;

        List<Module> enabled = Prism.MODULE_MANAGER.getEnabledSorted();
        if (enabled.isEmpty()) return;

        int screenW = mc.getWindow().getScaledWidth();
        int y = getY();
        int total = enabled.size();

        for (int i = 0; i < total; i++) {
            Module module = enabled.get(i);
            String displayName = module.getDisplayName();
            int textW = RenderUtil.textWidth(displayName);

            // Right-aligned x
            int x = screenW - textW - PADDING_X - 2;

            // Background bar
            RenderUtil.fillRect(ctx, x - 2, y, screenW, y + LINE_HEIGHT, 0xAA0D0D0D);

            // Color gradient: purple→pink based on index
            int color = InterfaceModule.rainbowMode.getValue()
                    ? ColorUtil.rainbow((float) i / total)
                    : ColorUtil.arrayListColor(i, total);

            // Draw text right-aligned, colored
            RenderUtil.drawText(ctx, displayName, x, y + 1, color, false);

            y += LINE_HEIGHT;
        }
    }
}
