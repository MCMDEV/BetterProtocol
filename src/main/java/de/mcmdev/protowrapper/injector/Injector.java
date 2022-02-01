package de.mcmdev.protowrapper.injector;

import io.netty.channel.Channel;
import org.bukkit.entity.Player;

/**
 * Implementations of this interface can be added into a netty channel/removed from a netty channel.
 */
public interface Injector {

	void inject(Player player, Channel channel);

	void uninject(Player player, Channel channel);

}
