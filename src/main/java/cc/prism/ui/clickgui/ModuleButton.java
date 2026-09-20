package cc.prism.ui.clickgui;

import cc.prism.module.Module;
import cc.prism.module.impl.client.InterfaceModule;
import cc.prism.property.*;
import cc.prism.ui.clickgui.widget.*;
import cc.prism.util.ColorUtil;
import cc.prism.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders a single module row inside a Panel.
 * When expanded, property widgets are shown below.
 */
public class ModuleButton {
    public static final int HEIGHT = 14;
    private static final int GEAR_SIZE = 9;

    private final Module module;
    private final Panel parent;
    private boolean expanded = false;

    private final List<AbstractWidget> widgets = new ArrayList<>();

    public ModuleButton(Module module, Panel parent) {
        this.module = module;
        this.parent = parent;
        buildWidgets();
    }

    private void buildWidgets() {
        widgets.clear();
        for (Property<?> prop : module.getProperties()) {
            if (prop instanceof BooleanProperty bp)
                widgets.add(new BooleanWidget(bp));
            else if (prop instanceof NumberProperty np)
                widgets.add(new SliderWidget(np));
            else if (prop instanceof ModeProperty mp)
                widgets.add(new ModeWidget(mp));
            else if (prop instanceof ColorProperty cp)
                widgets.add(new ColorPickerWidget(cp));
        }
    }

    // â”€â”€ Rendering â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    public void render(GuiGraphicsExtractor ctx, int panelX, int panelY, int panelW, int mx, int my, float anim) {
        int accent = InterfaceModule.accentColor.getColor();
        boolean enabled = module.isEnabled();
        boolean hovering = isHoveringModule(mx, my, panelX, panelY, panelW);

        // Module row background
        int rowColor = hovering ? 0xCC2A2A2A : 0xCC1E1E1E;
        RenderUtil.fillRect(ctx, panelX, panelY, panelX + panelW, panelY + HEIGHT, rowColor);

        // Left enabled bar
        if (enabled) {
            RenderUtil.fillRect(ctx, panelX, panelY, panelX + 2, panelY + HEIGHT, accent);
        }

        // Module name
        int nameColor = enabled ? 0xFFFFFFFF : 0xFF888888;
        RenderUtil.drawText(ctx, module.getName(), panelX + 5, panelY + 3, nameColor, false);

        // Gear icon (settings toggle) â€” right side
        if (!module.getProperties().isEmpty()) {
            int gearX = panelX + panelW - GEAR_SIZE - 3;
            int gearY = panelY + (HEIGHT - GEAR_SIZE) / 2;
            int gearColor = expanded ? accent : 0xFF666666;
            // Draw gear as a simple unicode char
            RenderUtil.drawText(ctx, "âš™", gearX, gearY - 1, gearColor, false);
        }

        // Expanded settings
        if (expanded) {
            int widgetY = panelY + HEIGHT;
            for (AbstractWidget widget : widgets) {
                widget.render(ctx, panelX, widgetY, panelW, mx, my);
                widgetY += widget.getHeight();
            }
        }
    }

    public int getTotalHeight() {
        int h = HEIGHT;
        if (expanded) {
            for (AbstractWidget w : widgets) h += w.getHeight();
        }
        return h;
    }

    // â”€â”€ Input â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    public boolean mouseClicked(int mx, int my, int button, int panelX, int panelY, int panelW) {
        // Check gear icon
        if (!module.getProperties().isEmpty()) {
            int gearX = panelX + panelW - GEAR_SIZE - 3;
            if (mx >= gearX && mx <= gearX + GEAR_SIZE + 2 && my >= panelY && my <= panelY + HEIGHT) {
                if (button == 0) {
                    expanded = !expanded;
                    return true;
                }
            }
        }

        // Check module row (left part = toggle)
        if (isHoveringModule(mx, my, panelX, panelY, panelW)) {
            if (button == 0) {
                module.toggle();
                return true;
            }
        }

        // Check widgets
        if (expanded) {
            int widgetY = panelY + HEIGHT;
            for (AbstractWidget widget : widgets) {
                if (widget.mouseClicked(mx, my, button, panelX, widgetY, panelW)) return true;
                widgetY += widget.getHeight();
            }
        }
        return false;
    }

    public void mouseReleased(int mx, int my, int button, int panelX, int panelY, int panelW) {
        if (!expanded) return;
        int widgetY = panelY + HEIGHT;
        for (AbstractWidget widget : widgets) {
            widget.mouseReleased(mx, my, button, panelX, widgetY, panelW);
            widgetY += widget.getHeight();
        }
    }

    public void mouseDragged(int mx, int my, int button, int panelX, int panelY, int panelW) {
        if (!expanded) return;
        int widgetY = panelY + HEIGHT;
        for (AbstractWidget widget : widgets) {
            widget.mouseDragged(mx, my, button, panelX, widgetY, panelW);
            widgetY += widget.getHeight();
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!expanded) return false;
        for (AbstractWidget w : widgets) {
            if (w.keyPressed(keyCode, scanCode, modifiers)) return true;
        }
        return false;
    }

    public boolean charTyped(char chr, int modifiers) {
        if (!expanded) return false;
        for (AbstractWidget w : widgets) {
            if (w.charTyped(chr, modifiers)) return true;
        }
        return false;
    }

    private boolean isHoveringModule(int mx, int my, int panelX, int panelY, int panelW) {
        return mx >= panelX && mx <= panelX + panelW && my >= panelY && my <= panelY + HEIGHT;
    }

    public Module getModule() { return module; }
    public boolean isExpanded() { return expanded; }
}






