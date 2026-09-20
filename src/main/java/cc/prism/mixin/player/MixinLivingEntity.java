package cc.prism.mixin.player;

import cc.prism.Prism;
import cc.prism.module.impl.combat.Velocity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    /**
     * Hook into takeKnockback so Velocity (Jump Reset mode) can jump on hit.
     * Standard/Hypixel modes are handled via PacketEvent in Velocity.java.
     */
    @Inject(method = "takeKnockback", at = @At("HEAD"), cancellable = true)
    private void onTakeKnockback(double strength, double x, double z, CallbackInfo ci) {
        // No direct cancellation here â€” Velocity uses packet-level cancellation.
        // This hook is a placeholder for future Velocity modes that need it.
    }
}






