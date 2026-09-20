package cc.prism.ui.hud;

import cc.prism.Prism;
import cc.prism.module.impl.client.InterfaceModule;
import cc.prism.util.ColorUtil;
import cc.prism.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/**
 * Top-left watermark: "Prism Client  v1.0  •  username"
 * Styled with gradient on the client name and white username.
 */
public class Watermark extends HudElement {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final String VERSION = "v1.0";

    public Watermark() {
        super(4, 4);
    }

    @Override
    public void render(DrawContext ctx, float delta) {
        if (!InterfaceModule.watermark.getValue()) return;

        int accent = InterfaceModule.rainbowMode.getValue()
                ? ColorUtil.rainbow(0f)
                : InterfaceModule.accentColor.getColor();

        String username = mc.getSession().getUsername();
        String clientText = "Prism Client ";
        String versionText = VERSION;
        String separatorText = "  §7•  ";
        String userText = username;

        int x = getX();
        int y = getY();
        int textHeight = RenderUtil.textHeight();

        // Background pill
        int totalWidth = RenderUtil.textWidth(clientText + versionText + "  •  " + userText) + 10;
        int totalHeight = textHeight + 6;
        RenderUtil.fillRect(ctx, x - 2, y - 1, x + totalWidth, y + totalHeight, 0xCC0D0D0D);
        // Accent left bar
        RenderUtil.fillRect(ctx, x - 2, y - 1, x, y + totalHeight, accent);

        // Render text parts
        RenderUtil.drawText(ctx, "§f§l" + clientText, x + 2, y + 3, 0xFFFFFFFF, false);
        int afterClient = x + 2 + RenderUtil.textWidth("§f§l" + clientText);

        RenderUtil.drawText(ctx, "§r" + versionText, afterClient, y + 3, accent, false);
        int afterVersion = afterClient + RenderUtil.textWidth(versionText);

        RenderUtil.drawText(ctx, separatorText, afterVersion, y + 3, 0xFFAAAAAA, false);
        int afterSep = afterVersion + RenderUtil.textWidth("  •  ");

        RenderUtil.drawText(ctx, "§f" + userText, afterSep, y + 3, 0xFFEEEEEE, false);
    }
}
