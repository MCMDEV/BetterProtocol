package de.mcmdev.betterprotocol.api;

import com.github.steveice10.packetlib.packet.Packet;

public interface BetterProtocolAPI<P> {

    /**
     * Sends a packet to the player
     * @param player The target receiver
     * @param packet The packet you want to send
     */
    void send(P player, Packet packet);

    /**
     * Returns the EventBus used to register a {@link PacketListener}
     * @return The EventBus
     */
    EventBus<P> getEventBus();

}
