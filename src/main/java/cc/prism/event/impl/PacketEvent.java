package cc.prism.event.impl;

import cc.prism.event.Event;
import net.minecraft.network.packet.Packet;

public class PacketEvent extends Event {

    public enum Direction { SEND, RECEIVE }

    private final Packet<?> packet;
    private final Direction direction;

    public PacketEvent(Packet<?> packet, Direction direction) {
        this.packet = packet;
        this.direction = direction;
    }

    public Packet<?> getPacket() { return packet; }
    public Direction getDirection() { return direction; }

    @Override
    public boolean isCancellable() { return true; }
}
