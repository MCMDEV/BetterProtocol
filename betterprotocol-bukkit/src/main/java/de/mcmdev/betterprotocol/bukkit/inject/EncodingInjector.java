package de.mcmdev.betterprotocol.bukkit.inject;

import de.mcmdev.betterprotocol.common.handler.EncodingChannelHandler;
import de.mcmdev.betterprotocol.common.listener.CommonEventBus;
import de.mcmdev.betterprotocol.common.protocol.AbstractProtocolRegistry;

import io.netty.channel.ChannelHandler;

import org.bukkit.entity.Player;

public class EncodingInjector extends BukkitInjector {

    public EncodingInjector(
            AbstractProtocolRegistry protocolRegistry, CommonEventBus<Player> eventBus) {
        super(
                "better_protocol_outgoing_listener",
                "better_protocol_encoder",
                protocolRegistry,
                eventBus);
    }

    @Override
    public ChannelHandler getHandler(Player player) {
        return new EncodingChannelHandler(protocolRegistry);
    }
}
