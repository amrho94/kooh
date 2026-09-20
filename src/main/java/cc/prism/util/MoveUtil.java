package cc.prism.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class MoveUtil {
    private static final Minecraft mc = Minecraft.getInstance();

    public static double getSpeed() {
        if (mc.player == null) return 0;
        double x = mc.player.getDeltaMovement().x;
        double z = mc.player.getDeltaMovement().z;
        return Math.sqrt(x * x + z * z);
    }

    public static boolean isMoving() {
        return getSpeed() > 0.003;
    }

    public static void setSpeed(double speed) {
        if (mc.player == null) return;
        double yaw = Math.toRadians(mc.player.getYRot());
        mc.player.setDeltaMovement(
            -Math.sin(yaw) * speed,
            mc.player.getDeltaMovement().y,
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






