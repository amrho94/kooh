package cc.prism.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class RotationUtil {
    private static final Minecraft mc = Minecraft.getInstance();

    /** Compute yaw (degrees) from player to a target position. */
    public static float getYaw(Vec3 target) {
        if (mc.player == null) return 0;
        Vec3 eye = mc.player.getEyePos();
        double dx = target.x - eye.x;
        double dz = target.z - eye.z;
        return (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90);
    }

    /** Compute pitch (degrees) from player eye to a target position. */
    public static float getPitch(Vec3 target) {
        if (mc.player == null) return 0;
        Vec3 eye = mc.player.getEyePos();
        double dx = target.x - eye.x;
        double dy = target.y - eye.y;
        double dz = target.z - eye.z;
        double dist = Math.sqrt(dx * dx + dz * dz);
        return (float) -Math.toDegrees(Math.atan2(dy, dist));
    }

    public static float[] getRotationsToEntity(Entity Entity) {
        Vec3 target = Entity.position().add(0, Entity.getHeight() / 2.0, 0);
        return new float[]{ getYaw(target), getPitch(target) };
    }

    /** Normalise yaw to [-180, 180]. */
    public static float normaliseYaw(float yaw) {
        yaw %= 360;
        if (yaw > 180) yaw -= 360;
        if (yaw < -180) yaw += 360;
        return yaw;
    }

    /** Clamp pitch to [-90, 90]. */
    public static float clampPitch(float pitch) {
        return Math.max(-90, Math.min(90, pitch));
    }
}






