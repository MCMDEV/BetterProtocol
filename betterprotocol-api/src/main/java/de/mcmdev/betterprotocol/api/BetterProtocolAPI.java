package de.mcmdev.betterprotocol.api;

import com.github.steveice10.packetlib.packet.Packet;

public interface BetterProtocolAPI<P> {

    void send(P player, Packet packet);

    EventBus<P> getEventBus();

}
