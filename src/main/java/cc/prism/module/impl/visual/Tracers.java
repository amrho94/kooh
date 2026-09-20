package cc.prism.module.impl.visual;

import cc.prism.event.EventTarget;
import cc.prism.event.impl.Render2DEvent;
import cc.prism.module.Category;
import cc.prism.module.Module;
import cc.prism.property.BooleanProperty;
import cc.prism.property.ColorProperty;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class Tracers extends Module {

    private final BooleanProperty players = addProperty(new BooleanProperty("Players", true));
    private final BooleanProperty mobs    = addProperty(new BooleanProperty("Mobs",    false));
    private final ColorProperty   color   = addProperty(new ColorProperty("Color",     0xFF4B9DFF));

    public Tracers() {
        super("Tracers", "Draws lines to nearby entities", Category.VISUAL);
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if (mc.player == null || mc.world == null) return;

        DrawContext ctx  = event.getContext();
        int screenW = mc.getWindow().getScaledWidth();
        int screenH = mc.getWindow().getScaledHeight();

        // Origin: bottom-centre of the screen (where your crosshair "shoots" from)
        int originX = screenW / 2;
        int originY = screenH;

        Matrix4f proj     = new Matrix4f(mc.gameRenderer.getBasicProjectionMatrix(mc.options.getFov().getValue()));
        Matrix4f modelView = new Matrix4f(mc.gameRenderer.getCamera().getRotationMatrix());

        Camera  camera = mc.gameRenderer.getCamera();
        Vec3d   camPos = camera.getPos();
        int     col    = color.getColor();

        for (Entity entity : mc.world.getEntities()) {
            if (entity == mc.player) continue;
            if (!shouldRender(entity)) continue;

            Vec3d entityPos = entity.getLerpedPos(event.getDelta())
                    .add(0, entity.getEyeHeight(entity.getPose()) * 0.5, 0);

            double rx = entityPos.x - camPos.x;
            double ry = entityPos.y - camPos.y;
            double rz = entityPos.z - camPos.z;

            float[] screen = worldToScreen(rx, ry, rz, modelView, proj, screenW, screenH);
            if (screen == null) continue;

            int targetX = (int) screen[0];
            int targetY = (int) screen[1];

            drawLine(ctx, originX, originY, targetX, targetY, col);
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private boolean shouldRender(Entity entity) {
        if (!(entity instanceof LivingEntity living)) return false;
        if (living.isDead()) return false;
        if (entity instanceof PlayerEntity) return players.getValue();
        return mobs.getValue();
    }

    /**
     * Projects a camera-relative world coordinate into 2D screen space.
     *
     * @return float[2] {sx, sy} or {@code null} when behind the near plane.
     */
    private static float[] worldToScreen(double rx, double ry, double rz,
                                          Matrix4f modelView, Matrix4f proj,
                                          int screenW, int screenH) {
        Vector4f vec = new Vector4f((float) rx, (float) ry, (float) rz, 1f);
        vec.mul(modelView);
        vec.mul(proj);

        if (vec.w <= 0f) return null;

        float ndcX =  vec.x / vec.w;
        float ndcY = -vec.y / vec.w;

        float sx = (ndcX + 1f) * 0.5f * screenW;
        float sy = (ndcY + 1f) * 0.5f * screenH;

        return new float[]{ sx, sy };
    }

    /**
     * Draws a 1-pixel-wide line between two screen points using Bresenham's
     * algorithm expressed as a series of {@link DrawContext#fill} calls.
     */
    private static void drawLine(DrawContext ctx, int x0, int y0, int x1, int y1, int col) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        int maxSteps = dx + dy + 1; // safety cap – never iterate more pixels than the diagonal
        int steps = 0;

        while (steps++ < maxSteps) {
            ctx.fill(x0, y0, x0 + 1, y0 + 1, col);
            if (x0 == x1 && y0 == y1) break;
            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; x0 += sx; }
            if (e2 <  dx) { err += dx; y0 += sy; }
        }
    }
}
