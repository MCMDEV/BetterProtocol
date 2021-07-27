package de.mcmdev.betterprotocol.common.inject;

import de.mcmdev.betterprotocol.common.listener.CommonEventBus;
import de.mcmdev.betterprotocol.common.protocol.AbstractProtocolRegistry;

import io.netty.channel.ChannelHandler;

/**
 * A netty pipeline injector
 *
 * @param <P>
 */
public abstract class Injector<P> {

    protected final String stage;
    protected final String name;
    protected final AbstractProtocolRegistry protocolRegistry;
    protected final CommonEventBus<P> eventBus;

    public Injector(
            String stage,
            String name,
            AbstractProtocolRegistry protocolRegistry,
            CommonEventBus<P> eventBus) {
        this.stage = stage;
        this.name = name;
        this.protocolRegistry = protocolRegistry;
        this.eventBus = eventBus;
    }

    /**
     * The {@link ChannelHandler} you want to inject
     *
     * @param player The player type, used by some ChannelHandlers
     * @return The channel handler
     */
    public abstract ChannelHandler getHandler(P player);

    /**
     * Injects the ChannelHandler into the player's netty pipeline
     *
     * @param player The target player
     */
    public abstract void inject(P player);

    /**
     * Removes the ChannelHandler from the player's netty pipeline
     *
     * @param player The target player
     */
    public abstract void uninject(P player);
}
