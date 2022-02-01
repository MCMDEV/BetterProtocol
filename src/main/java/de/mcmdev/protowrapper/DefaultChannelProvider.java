package de.mcmdev.protowrapper;

import io.netty.channel.Channel;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class DefaultChannelProvider implements Function<Player, Channel> {
	@Override
	public Channel apply(Player player) {
		return ((CraftPlayer) player).getHandle().networkManager.channel;
	}
}
