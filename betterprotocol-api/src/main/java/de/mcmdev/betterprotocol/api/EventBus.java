package de.mcmdev.betterprotocol.api;
import com.github.steveice10.packetlib.packet.Packet;

public interface EventBus<P> {

    <T extends Packet> void listen(Class<T> packetClass, PacketListener<T, P> packetListener);

}
