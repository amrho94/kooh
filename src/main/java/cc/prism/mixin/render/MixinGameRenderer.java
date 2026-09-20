package cc.prism.mixin.render;

import cc.prism.Prism;
import cc.prism.event.impl.Render3DEvent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.DeltaTracker;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Inject(method = "renderLevel", at = @At("TAIL"))
    private void onRenderWorld(DeltaTracker tickCounter, CallbackInfo ci) {
        if (Prism.MODULE_MANAGER == null) return;
        MatrixStack matrices = new MatrixStack();
        Prism.EVENT_BUS.post(new Render3DEvent(matrices, tickCounter.getTickDelta(true)));
    }
}






