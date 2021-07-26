package de.mcmdev.betterprotocol.bukkit.inject;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public abstract class Injector {

    private final String stage;
    private final String name;

    public Injector(String stage, String name) {
        this.stage = stage;
        this.name = name;
    }

    public abstract ChannelHandler getHandler(Player player);

    public void inject(Player player)   {
        Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
        if(channel.pipeline().get(name) != null) return;
        channel.pipeline().addBefore(stage, name, getHandler(player));
    }

    public void uninject(Player player)    {
        Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
        if(channel.pipeline().get(name) == null) return;
        channel.pipeline().remove(name);
    }
}
