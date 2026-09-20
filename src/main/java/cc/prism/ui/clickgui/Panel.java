package cc.prism.ui.clickgui;

import cc.prism.Prism;
import cc.prism.module.Category;
import cc.prism.module.Module;
import cc.prism.module.impl.client.InterfaceModule;
import cc.prism.util.ColorUtil;
import cc.prism.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.List;

/**
 * A per-category draggable panel in the ClickGUI.
 * Layout:
 *   [Header: CategoryName  X  count]     â† 16px
 *   [ModuleButton]                         â† 14px each
 *     [settings expanded inline]           â† variable height
 */
public class Panel {
    public static final int WIDTH  = 140;
    public static final int HEADER = 16;

    private final Category category;
    private int x, y;
    private boolean collapsed = false;

    private final List<ModuleButton> buttons = new ArrayList<>();

    public Panel(Category category, int x, int y) {
        this.category = category;
        this.x = x;
        this.y = y;
        for (Module module : Prism.MODULE_MANAGER.getByCategory(category)) {
            buttons.add(new ModuleButton(module, this));
        }
    }

    // â”€â”€ Rendering â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    public void render(GuiGraphicsExtractor ctx, int mx, int my, float anim) {
        int accent = InterfaceModule.accentColor.getColor();

        // Header
        int headerColor = 0xE8181818;
        RenderUtil.fillRect(ctx, x, y, x + WIDTH, y + HEADER, headerColor);
        // Accent left bar on header
        RenderUtil.fillRect(ctx, x, y, x + 2, y + HEADER, accent);
        // Category name
        String label = category.getName();
        int enabledCount = (int) buttons.stream().filter(b -> b.getModule().isEnabled()).count();
        String countStr = " Â§8" + enabledCount;
        RenderUtil.drawText(ctx, "Â§f" + label + countStr, x + 5, y + 4, 0xFFFFFFFF, false);
        // Collapse arrow
        String arrow = collapsed ? "â–¸" : "â–¾";
        RenderUtil.drawText(ctx, arrow, x + WIDTH - 10, y + 4, 0xFFAAAAAA, false);

        if (collapsed) return;

        // Body background
        int totalHeight = getBodyHeight();
        RenderUtil.fillRect(ctx, x, y + HEADER, x + WIDTH, y + HEADER + totalHeight, 0xD4131313);

        // Modules
        int buttonY = y + HEADER;
        for (ModuleButton btn : buttons) {
            btn.render(ctx, x, buttonY, WIDTH, mx, my, anim);
            buttonY += btn.getTotalHeight();
        }
    }

    public int getBodyHeight() {
        int h = 0;
        for (ModuleButton btn : buttons) h += btn.getTotalHeight();
        return h;
    }

    public int getTotalHeight() {
        return HEADER + (collapsed ? 0 : getBodyHeight());
    }

    // â”€â”€ Input â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    public boolean mouseClicked(int mx, int my, int button) {
        // Header click
        if (isHoveringHeader(mx, my)) {
            if (button == 0) {
                // Check collapse arrow
                if (mx >= x + WIDTH - 14 && mx <= x + WIDTH) {
                    collapsed = !collapsed;
                    return true;
                }
                return true; // drag handled in screen
            }
            if (button == 1) {
                collapsed = !collapsed;
                return true;
            }
        }
        if (collapsed) return false;

        // Module buttons
        int buttonY = y + HEADER;
        for (ModuleButton btn : buttons) {
            if (btn.mouseClicked(mx, my, button, x, buttonY, WIDTH)) return true;
            buttonY += btn.getTotalHeight();
        }
        return false;
    }

    public void mouseReleased(int mx, int my, int button) {
        if (collapsed) return;
        int buttonY = y + HEADER;
        for (ModuleButton btn : buttons) {
            btn.mouseReleased(mx, my, button, x, buttonY, WIDTH);
            buttonY += btn.getTotalHeight();
        }
    }

    public void mouseDragged(int mx, int my, int button) {
        if (collapsed) return;
        int buttonY = y + HEADER;
        for (ModuleButton btn : buttons) {
            btn.mouseDragged(mx, my, button, x, buttonY, WIDTH);
            buttonY += btn.getTotalHeight();
        }
    }

    public boolean mouseScrolled(int mx, int my, double delta) {
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (collapsed) return false;
        for (ModuleButton btn : buttons) {
            if (btn.keyPressed(keyCode, scanCode, modifiers)) return true;
        }
        return false;
    }

    public boolean charTyped(char chr, int modifiers) {
        if (collapsed) return false;
        for (ModuleButton btn : buttons) {
            if (btn.charTyped(chr, modifiers)) return true;
        }
        return false;
    }

    // â”€â”€ Helpers â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    public boolean isHoveringHeader(int mx, int my) {
        return mx >= x && mx <= x + WIDTH && my >= y && my <= y + HEADER;
    }

    public boolean isHovering(int mx, int my) {
        return mx >= x && mx <= x + WIDTH && my >= y && my <= y + getTotalHeight();
    }

    public void setPos(int x, int y) { this.x = x; this.y = y; }
    public int getX() { return x; }
    public int getY() { return y; }
    public Category getCategory() { return category; }
    public boolean isCollapsed() { return collapsed; }
}






