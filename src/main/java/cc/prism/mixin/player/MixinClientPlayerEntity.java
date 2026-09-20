package cc.prism.mixin.player;

import cc.prism.Prism;
import cc.prism.module.impl.movement.NoJumpDelay;
import cc.prism.module.impl.movement.NoSlow;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class MixinClientPlayerEntity {

    @Shadow protected int jumpingCooldown;

    /** NoJumpDelay: reset cooldown every tick when enabled */
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (Prism.MODULE_MANAGER == null) return;
        NoJumpDelay njd = Prism.MODULE_MANAGER.get(NoJumpDelay.class);
        if (njd != null && njd.isEnabled()) {
            jumpingCooldown = 0;
        }
    }

    /** NoSlow: bypass item use slowdown */
    @Inject(method = "isUsingItem", at = @At("RETURN"), cancellable = true)
    private void onIsUsingItem(CallbackInfoReturnable<Boolean> cir) {
        if (!NoSlow.isActive() || !NoSlow.isVanillaMode()) return;
        // Return false so the sprint speed penalty isn't applied
        cir.setReturnValue(false);
    }
}






