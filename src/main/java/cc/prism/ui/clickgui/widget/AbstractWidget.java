package cc.prism.ui.clickgui.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Base class for all inline setting widgets inside the ClickGUI. */
public abstract class AbstractWidget {
    public static final int HEIGHT = 12;

    public abstract void render(GuiGraphicsExtractor ctx, int x, int y, int w, int mx, int my);
    public int getHeight() { return HEIGHT; }

    public boolean mouseClicked(int mx, int my, int button, int x, int y, int w) { return false; }
    public void mouseReleased(int mx, int my, int button, int x, int y, int w) {}
    public void mouseDragged(int mx, int my, int button, int x, int y, int w) {}
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) { return false; }
    public boolean charTyped(char chr, int modifiers) { return false; }

    protected boolean isHovering(int mx, int my, int x, int y, int w) {
        return mx >= x && mx <= x + w && my >= y && my <= y + getHeight();
    }
}






