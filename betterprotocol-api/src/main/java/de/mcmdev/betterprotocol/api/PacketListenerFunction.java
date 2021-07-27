package de.mcmdev.betterprotocol.api;

import com.github.steveice10.packetlib.packet.Packet;

@FunctionalInterface
public interface PacketListenerFunction<T extends Packet, P> {

    void handle(PacketEvent<T, P> event);
}
