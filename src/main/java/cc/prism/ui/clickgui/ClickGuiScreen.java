package cc.prism.ui.clickgui;

import cc.prism.Prism;
import cc.prism.module.Category;
import cc.prism.module.Module;
import cc.prism.module.impl.client.InterfaceModule;
import cc.prism.util.ColorUtil;
import cc.prism.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ClickGuiScreen extends Screen {
    private final List<Panel> panels = new ArrayList<>();
    private Panel draggingPanel = null;
    private int dragOffsetX, dragOffsetY;

    // Open/close animation
    private float animationProgress = 0f;
    private long openTime;

    public ClickGuiScreen() {
        super(Text.literal("ClickGUI"));
    }

    @Override
    protected void init() {
        panels.clear();
        int x = 10;
        for (Category category : Category.values()) {
            Panel panel = new Panel(category, x, 40);
            panels.add(panel);
            x += Panel.WIDTH + 8;
        }
        openTime = System.currentTimeMillis();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Animate open (scale up from 0 to 1 over 150ms)
        animationProgress = Math.min(1f, (System.currentTimeMillis() - openTime) / 150f);

        // Dark overlay background
        context.fill(0, 0, width, height, 0x88000000);

        // Scissor-safe rendering — render panels back to front
        for (int i = panels.size() - 1; i >= 0; i--) {
            panels.get(i).render(context, mouseX, mouseY, animationProgress);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int mx = (int) mouseX, my = (int) mouseY;
        for (Panel panel : panels) {
            if (panel.mouseClicked(mx, my, button)) {
                if (button == 0 && panel.isHoveringHeader(mx, my)) {
                    draggingPanel = panel;
                    dragOffsetX = mx - panel.getX();
                    dragOffsetY = my - panel.getY();
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingPanel = null;
        for (Panel panel : panels) {
            panel.mouseReleased((int) mouseX, (int) mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggingPanel != null) {
            draggingPanel.setPos((int) mouseX - dragOffsetX, (int) mouseY - dragOffsetY);
            return true;
        }
        for (Panel panel : panels) {
            panel.mouseDragged((int) mouseX, (int) mouseY, button);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        for (Panel panel : panels) {
            if (panel.mouseScrolled((int) mouseX, (int) mouseY, verticalAmount)) return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Forward typing to focused inputs
        for (Panel panel : panels) {
            if (panel.keyPressed(keyCode, scanCode, modifiers)) return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (Panel panel : panels) {
            if (panel.charTyped(chr, modifiers)) return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}
