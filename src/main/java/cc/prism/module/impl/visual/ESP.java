package cc.prism.module.impl.visual;

import cc.prism.event.EventTarget;
import cc.prism.event.impl.Render2DEvent;
import cc.prism.module.Category;
import cc.prism.module.Module;
import cc.prism.property.BooleanProperty;
import cc.prism.property.ColorProperty;
import cc.prism.property.ModeProperty;
import cc.prism.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class ESP extends Module {

    private final ModeProperty mode         = addProperty(new ModeProperty("Mode", "Box", "Box", "Corners"));
    private final BooleanProperty players   = addProperty(new BooleanProperty("Players", true));
    private final BooleanProperty mobs      = addProperty(new BooleanProperty("Mobs", false));
    private final ColorProperty color       = addProperty(new ColorProperty("Color", 0xFF4B9DFF));
    private final BooleanProperty throughWalls = addProperty(new BooleanProperty("Through Walls", true));

    public ESP() {
        super("ESP", "Highlights entities through walls", Category.VISUAL);
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if (mc.player == null || mc.world == null) return;

        GuiGraphicsExtractor ctx = event.getContext();
        int screenW = mc.getWindow().getScaledWidth();
        int screenH = mc.getWindow().getScaledHeight();

        // Matrices needed for world-to-screen projection
        Matrix4f proj    = new Matrix4f(mc.gameRenderer.getBasicProjectionMatrix(mc.options.getFov().getValue()));
        Matrix4f modelView = new Matrix4f(mc.gameRenderer.getCamera().getRotationMatrix());

        Camera camera = mc.gameRenderer.getCamera();
        Vec3 camPos  = camera.position();

        for (Entity Entity : mc.world.getEntities()) {
            if (Entity == mc.player) continue;
            if (!shouldRender(Entity)) continue;

            // Entity eye / center position interpolated
            Vec3 entityPos = Entity.getLerpedPos(event.getDelta()).add(0, Entity.getEyeHeight(Entity.getPose()) * 0.5, 0);

            // Translate relative to camera
            double rx = entityPos.x - camPos.x;
            double ry = entityPos.y - camPos.y;
            double rz = entityPos.z - camPos.z;

            float[] screen = worldToScreen(rx, ry, rz, modelView, proj, screenW, screenH);
            if (screen == null) continue; // behind camera

            int sx = (int) screen[0];
            int sy = (int) screen[1];

            // Draw Entity label
            String name    = getEntityName(Entity);
            int    dist    = (int) mc.player.distanceTo(Entity);
            String label   = name + " Â§7[" + dist + "m]";
            int    tw      = RenderUtil.textWidth(label);
            int    th      = RenderUtil.textHeight();
            int    col     = color.getColor();

            // Background pill
            int bgCol = 0x88000000;
            RenderUtil.fillRect(ctx, sx - tw / 2 - 2, sy - th / 2 - 2,
                    sx + tw / 2 + 2, sy + th / 2 + 2, bgCol);

            // Colored top border
            RenderUtil.fillRect(ctx, sx - tw / 2 - 2, sy - th / 2 - 2,
                    sx + tw / 2 + 2, sy - th / 2 - 1, col);

            // Text
            RenderUtil.drawText(ctx, label, sx - tw / 2, sy - th / 2, col, true);

            // "Corners" mode: draw small corner marks
            if (mode.is("Corners")) {
                int bw = tw + 4;
                int bh = th + 4;
                int x1 = sx - bw / 2;
                int y1 = sy - bh / 2;
                int x2 = sx + bw / 2;
                int y2 = sy + bh / 2;
                int cs = Math.max(3, bw / 5); // corner segment length
                drawCorners(ctx, x1, y1, x2, y2, cs, col);
            }
        }
    }

    // â”€â”€ Helpers â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private boolean shouldRender(Entity Entity) {
        if (!(Entity instanceof LivingEntity living)) return false;
        if (living.isDeadOrDying()) return false;
        if (Entity instanceof Player) return players.getValue();
        return mobs.getValue();
    }

    private String getEntityName(Entity Entity) {
        if (Entity instanceof Player player) {
            return player.getGameProfile().getName();
        }
        // Trim the EntityType key for a clean name
        String raw = Entity.getType().toString();
        if (raw.contains(":")) raw = raw.substring(raw.indexOf(':') + 1);
        // Capitalise first letter
        if (!raw.isEmpty()) raw = Character.toUpperCase(raw.charAt(0)) + raw.substring(1);
        return raw.replace('_', ' ');
    }

    /**
     * Projects a camera-relative world position to 2D screen coordinates.
     *
     * @return float[2] {screenX, screenY} or null if behind the camera.
     */
    private static float[] worldToScreen(double rx, double ry, double rz,
                                          Matrix4f modelView, Matrix4f proj,
                                          int screenW, int screenH) {
        Vector4f vec = new Vector4f((float) rx, (float) ry, (float) rz, 1f);
        vec.mul(modelView);
        vec.mul(proj);

        if (vec.w <= 0f) return null; // behind near plane

        // NDC â†’ screen
        float ndcX =  vec.x / vec.w;
        float ndcY = -vec.y / vec.w;

        float sx = (ndcX + 1f) * 0.5f * screenW;
        float sy = (ndcY + 1f) * 0.5f * screenH;

        return new float[]{ sx, sy };
    }

    /** Draws the four L-shaped corner highlights of a rectangle. */
    private static void drawCorners(GuiGraphicsExtractor ctx,
                                    int x1, int y1, int x2, int y2,
                                    int len, int col) {
        // Top-left
        ctx.fill(x1,       y1, x1 + len, y1 + 1, col);
        ctx.fill(x1,       y1, x1 + 1,   y1 + len, col);
        // Top-right
        ctx.fill(x2 - len, y1, x2,       y1 + 1, col);
        ctx.fill(x2 - 1,   y1, x2,       y1 + len, col);
        // Bottom-left
        ctx.fill(x1,       y2 - 1, x1 + len, y2,       col);
        ctx.fill(x1,       y2 - len, x1 + 1, y2,       col);
        // Bottom-right
        ctx.fill(x2 - len, y2 - 1, x2,       y2,       col);
        ctx.fill(x2 - 1,   y2 - len, x2,     y2,       col);
    }
}






