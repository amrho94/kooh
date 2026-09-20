package cc.prism.mixin.render;

import cc.prism.Prism;
import cc.prism.event.impl.Render2DEvent;
import cc.prism.ui.hud.ArrayListHud;
import cc.prism.ui.hud.Watermark;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.InGameHud;
import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {
    private static final Watermark WATERMARK = new Watermark();
    private static final ArrayListHud ARRAYLIST = new ArrayListHud();

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(GuiGraphicsExtractor context, DeltaTracker tickCounter, CallbackInfo ci) {
        if (Prism.MODULE_MANAGER == null) return;

        float delta = tickCounter.getTickDelta(true);

        // Post event for modules
        Prism.EVENT_BUS.post(new Render2DEvent(context, delta));

        // Render HUD elements
        WATERMARK.render(context, delta);
        ARRAYLIST.render(context, delta);
    }
}






