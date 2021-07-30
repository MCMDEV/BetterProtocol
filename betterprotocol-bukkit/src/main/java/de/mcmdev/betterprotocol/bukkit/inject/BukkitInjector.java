package de.mcmdev.betterprotocol.bukkit.inject;

import de.mcmdev.betterprotocol.common.inject.Injector;
import de.mcmdev.betterprotocol.common.listener.CommonEventBus;
import de.mcmdev.betterprotocol.common.protocol.AbstractProtocolRegistry;
import io.netty.channel.Channel;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public abstract class BukkitInjector extends Injector<Player> {

    public BukkitInjector(
            String stage,
            String name,
            AbstractProtocolRegistry protocolRegistry,
            CommonEventBus<Player> bukkitEventBus) {
        super(stage, name, protocolRegistry, bukkitEventBus);
    }

    public void inject(Player player) {
        Channel channel =
                ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
        // don't inject if the Injector is already there
        if (channel.pipeline().get(name) != null) return;
        // add the injector before the stage
        channel.pipeline().addBefore(stage, name, getHandler(player));
    }

    public void uninject(Player player) {
        Channel channel =
                ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
        // don't uninject the Injector if it isn't there
        if (channel.pipeline().get(name) == null) return;
        channel.pipeline().remove(name);
    }
}
