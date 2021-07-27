package de.mcmdev.betterprotocol.bukkit.inject;

import de.mcmdev.betterprotocol.common.handler.RewritingWriteChannelHandler;
import de.mcmdev.betterprotocol.common.listener.CommonEventBus;
import de.mcmdev.betterprotocol.common.protocol.AbstractProtocolRegistry;
import io.netty.channel.ChannelHandler;
import org.bukkit.entity.Player;

public class OutgoingInjector extends BukkitInjector {

    public OutgoingInjector(AbstractProtocolRegistry protocolRegistry, CommonEventBus<Player> eventBus) {
        super("encoder", "better_protocol_outgoing_listener", protocolRegistry, eventBus);
    }

    @Override
    public ChannelHandler getHandler(Player player) {
        return new RewritingWriteChannelHandler<>(player, protocolRegistry, eventBus);
    }
}
