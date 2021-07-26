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

    /**
     * Returns the receiver/sender of the packet
     * @return The receiver/sender of the packet
     */
    public P getPlayer() {
        return player;
    }

    /**
     * Returns the class of the packet
     * @return The class of the packet
     */
    public Class<T> getPacketType() {
        return packetType;
    }

    /**
     * Returns the involved packet itself
     * @return The involved packet
     */
    public T getPacket() {
        return packet;
    }

    /**
     * Replaces the packet involved in the event
     * @param packet The new packet
     */
    public void setPacket(T packet) {
        this.packet = packet;
    }

    /**
     * If true, prevents further processing/sending of the packet
     * @param cancelled The cancel state
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * If true, prevents further processing/sending of the packet
     * @return The cancel state
     */
    public boolean isCancelled() {
        return cancelled;
    }
}
