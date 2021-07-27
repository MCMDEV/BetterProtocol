package de.mcmdev.betterprotocol.common;

import com.github.steveice10.packetlib.packet.Packet;

import de.mcmdev.betterprotocol.common.inject.Injector;

import java.util.Set;

/**
 * Provides access to required platform-dependent methods
 *
 * @param <P>
 */
public interface BetterProtocolPlugin<P> {

    void send(P player, Packet packet);

    void registerInjectors();

    void registerListeners(Set<Injector<P>> injectors);

    String getVersion();
}
