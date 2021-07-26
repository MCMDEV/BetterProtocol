package de.mcmdev.betterprotocol.api;

import com.github.steveice10.packetlib.packet.Packet;

public class PacketEvent<T extends Packet, P> {

    private final P player;
    private final Class<T> packetType;
    private T packet;
    private boolean cancelled = false;

    public PacketEvent(P player, Class<T> packetType, T packet) {
        this.player = player;
        this.packetType = packetType;
        this.packet = packet;
    }

    public P getPlayer() {
        return player;
    }

    public Class<T> getPacketType() {
        return packetType;
    }

    public T getPacket() {
        return packet;
    }

    public void setPacket(T packet) {
        this.packet = packet;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
