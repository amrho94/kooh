package cc.prism.ui.hud;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Base class for HUD elements rendered via Render2DEvent. */
public abstract class HudElement {
    private int x, y;

    public HudElement(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract void render(GuiGraphicsExtractor ctx, float delta);

    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
}






