package de.mcmdev.betterprotocol.api;

import com.github.steveice10.packetlib.packet.Packet;

public interface EventBus<P> {

    /**
     * Registers a {@link PacketListenerFunction} for all incoming and outgoing packets of one class
     *
     * @param packetClass The packet type you want to listen to
     * @param packetListenerFunction Your packet listener
     */
    <T extends Packet> void listen(
            Class<T> packetClass, PacketListenerFunction<T, P> packetListenerFunction);

    /**
     * Registers all functions on the listener that are annotated with {@link PacketHandler} as
     * packet handlers.
     *
     * @param listener The listener whose handlers to register
     */
    void listen(PacketListener listener);
}
