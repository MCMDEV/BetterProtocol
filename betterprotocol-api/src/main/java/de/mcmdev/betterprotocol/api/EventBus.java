package de.mcmdev.betterprotocol.api;
import com.github.steveice10.packetlib.packet.Packet;

public interface EventBus<P> {

    /**
     * Registers a {@link PacketListener} for all incoming and outgoing packets of one class
     *
     * @param packetClass The packet type you want to listen to
     * @param packetListener Your packet listener
     */
    <T extends Packet> void listen(Class<T> packetClass, PacketListener<T, P> packetListener);

}
