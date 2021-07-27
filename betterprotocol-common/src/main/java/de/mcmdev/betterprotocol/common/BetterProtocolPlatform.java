package de.mcmdev.betterprotocol.common;

import com.github.steveice10.packetlib.packet.Packet;

import de.mcmdev.betterprotocol.api.BetterProtocolAPI;
import de.mcmdev.betterprotocol.common.inject.Injector;
import de.mcmdev.betterprotocol.common.listener.CommonEventBus;
import de.mcmdev.betterprotocol.common.protocol.AbstractProtocolRegistry;
import de.mcmdev.betterprotocol.common.protocol.ProtocolRegistry_1_16_5;

import java.util.HashSet;
import java.util.Set;

/**
 * The common core of BetterProtocol, which implements the API
 *
 * @param <P> The player type
 */
public class BetterProtocolPlatform<P> implements BetterProtocolAPI<P> {

    private final BetterProtocolPlugin<P> plugin;
    private AbstractProtocolRegistry protocolRegistry;
    private final CommonEventBus<P> eventBus;
    private final Set<Injector<P>> injectors = new HashSet<>();

    public BetterProtocolPlatform(BetterProtocolPlugin<P> plugin, CommonEventBus<P> eventBus) {
        this.plugin = plugin;
        this.eventBus = eventBus;
    }

    public void initialize() {
        this.protocolRegistry = findProtocolRegistry();
        plugin.registerInjectors();
        plugin.registerListeners(injectors);
    }

    @Override
    public void send(P player, Packet packet) {
        plugin.send(player, packet);
    }

    public void registerInjector(Injector<P> injector) {
        this.injectors.add(injector);
    }

    public AbstractProtocolRegistry getProtocolRegistry() {
        return protocolRegistry;
    }

    @Override
    public CommonEventBus<P> getEventBus() {
        return eventBus;
    }

    private AbstractProtocolRegistry findProtocolRegistry() {
        if (plugin.getVersion().contains("1.16.5")) {
            return new ProtocolRegistry_1_16_5();
        }

        throw new IllegalStateException("Unsupported version " + plugin.getVersion());
    }
}
