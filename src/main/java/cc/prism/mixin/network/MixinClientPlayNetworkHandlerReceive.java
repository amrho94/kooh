package cc.prism.mixin.network;

import cc.prism.Prism;
import cc.prism.event.impl.PacketEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class MixinClientPlayNetworkHandlerReceive {

    /**
     * Hook to intercept received packets.
     * We inject into onPacket (which is what ClientPacketListener processes) â€”
     * specifically targeting ClientboundSetEntityMotionPacket handling for Velocity module.
     */
    @Inject(method = "onEntityVelocityUpdate", at = @At("HEAD"), cancellable = true)
    private void onEntityVelocityUpdate(net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket packet, CallbackInfo ci) {
        if (Prism.EVENT_BUS == null) return;
        PacketEvent event = new PacketEvent(packet, PacketEvent.Direction.RECEIVE);
        Prism.EVENT_BUS.post(event);
        if (event.isCancelled()) ci.cancel();
    }
}






