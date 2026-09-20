package cc.prism.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class MoveUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static double getSpeed() {
        if (mc.player == null) return 0;
        double x = mc.player.getVelocity().x;
        double z = mc.player.getVelocity().z;
        return Math.sqrt(x * x + z * z);
    }

    public static boolean isMoving() {
        return getSpeed() > 0.003;
    }

    public static void setSpeed(double speed) {
        if (mc.player == null) return;
        double yaw = Math.toRadians(mc.player.getYaw());
        mc.player.setVelocity(
            -Math.sin(yaw) * speed,
            mc.player.getVelocity().y,
             Math.cos(yaw) * speed
        );
    }

    public static double[] getMotionFromYaw(double yaw, double speed) {
        double rad = Math.toRadians(yaw);
        return new double[]{ -Math.sin(rad) * speed, Math.cos(rad) * speed };
    }

    public static boolean isInputtingMovement() {
        if (mc.player == null) return false;
        return mc.options.forwardKey.isPressed()
            || mc.options.backKey.isPressed()
            || mc.options.leftKey.isPressed()
            || mc.options.rightKey.isPressed();
    }
}
