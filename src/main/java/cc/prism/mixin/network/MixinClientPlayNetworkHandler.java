package cc.prism.mixin.network;

import cc.prism.Prism;
import cc.prism.event.impl.PacketEvent;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public abstract class MixinClientPlayNetworkHandler {

    @Inject(
        method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketSendListener;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onSendPacket(Packet<?> packet, PacketSendListener callbacks, CallbackInfo ci) {
        if (Prism.EVENT_BUS == null) return;
        PacketEvent event = new PacketEvent(packet, PacketEvent.Direction.SEND);
        Prism.EVENT_BUS.post(event);
        if (event.isCancelled()) ci.cancel();
    }
}






