package de.mcmdev.protowrapper.injector;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import org.bukkit.entity.Player;

import java.util.function.Function;

/**
 * Adds a custom {@link io.netty.channel.ChannelHandler} to the channel pipeline of the given connection
 * before another handler in the channel pipeline.
 */
public class PrestageInjector implements Injector {

	private final String name;
	private final String predecessor;
	private final Function<Player, ChannelHandler> channelHandlerFunction;

	public PrestageInjector(String name, String predecessor, Function<Player, ChannelHandler> channelHandlerFunction) {
		this.name = name;
		this.predecessor = predecessor;
		this.channelHandlerFunction = channelHandlerFunction;
	}

	@Override
	public void inject(Player player, Channel channel) {
		channel.pipeline().addBefore(predecessor, name, channelHandlerFunction.apply(player));
	}

	@Override
	public void uninject(Player player, Channel channel) {
		channel.pipeline().addBefore(predecessor, name, channelHandlerFunction.apply(player));
	}

}
