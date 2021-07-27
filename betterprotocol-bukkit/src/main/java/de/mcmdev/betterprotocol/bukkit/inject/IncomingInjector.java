package de.mcmdev.betterprotocol.bukkit.inject;

import de.mcmdev.betterprotocol.common.handler.RewritingReadChannelHandler;
import de.mcmdev.betterprotocol.common.listener.CommonEventBus;
import de.mcmdev.betterprotocol.common.protocol.AbstractProtocolRegistry;

import io.netty.channel.ChannelHandler;

import org.bukkit.entity.Player;

public class IncomingInjector extends BukkitInjector {

    public IncomingInjector(
            AbstractProtocolRegistry protocolRegistry, CommonEventBus<Player> eventBus) {
        super("decoder", "better_protocol_incoming_listener", protocolRegistry, eventBus);
    }

    @Override
    public ChannelHandler getHandler(Player player) {
        return new RewritingReadChannelHandler<>(player, protocolRegistry, eventBus);
    }
}
