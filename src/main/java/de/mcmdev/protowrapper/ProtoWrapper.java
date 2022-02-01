package de.mcmdev.protowrapper;

import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.codec.MinecraftCodec;
import com.github.steveice10.mc.protocol.data.ProtocolState;
import de.mcmdev.protowrapper.channelhandler.IncomingHandler;
import de.mcmdev.protowrapper.channelhandler.OutgoingHandler;
import de.mcmdev.protowrapper.channelhandler.PacketCodec;
import de.mcmdev.protowrapper.event.PacketListener;
import de.mcmdev.protowrapper.injector.Injector;
import de.mcmdev.protowrapper.injector.PrestageInjector;
import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class ProtoWrapper {

	/**
	 * A set of all active injectors that will be applied to every player that opens a connection to the server.
	 */
	private final Set<Injector> injectors = new LinkedHashSet<>();
	/**
	 * A function to retrieve the netty channel from a player.
	 * You can use {@link DefaultChannelProvider} if you don't have your own provider.
	 */
	private final Function<Player, Channel> channelProvider;
	/**
	 * An ordered list of packet listeners whose corresponding methods are called
	 * when a packet is received or sent.
	 */
	private final List<PacketListener> packetListeners = new ArrayList<>();

	/**
	 * The default constructor and entrypoint for ProtoWrapper.
	 * Don't forget to call {@link #hook(JavaPlugin)}.
	 *
	 * @param channelProvider A function that can resolve the netty channel of a player
	 * @param disableEvents   Whether event handling should be skipped. Should be set to true if event handling is not
	 *                        used, as it can save a bit of memory.
	 */
	public ProtoWrapper(Function<Player, Channel> channelProvider, boolean disableEvents) {
		this.channelProvider = channelProvider;
		MinecraftProtocol minecraftProtocol = prepareProtocol();
		if (disableEvents) {
			injectors.add(new PrestageInjector("pw_encoder", "encoder", player -> new PacketCodec(minecraftProtocol)));
		} else {
			injectors.add(new PrestageInjector("pw_incoming", "decoder", player -> new IncomingHandler(player, this, minecraftProtocol)));
			injectors.add(new PrestageInjector("pw_outgoing", "encoder", player -> new OutgoingHandler(player, this, minecraftProtocol)));
			injectors.add(new PrestageInjector("pw_encoder", "pw_outgoing", player -> new PacketCodec(minecraftProtocol)));
		}
	}

	/**
	 * ProtoWrapper needs to register some listeners on player join to inject its custom packet handler.
	 * Call this method in the {@link JavaPlugin#onEnable()} part of your plugin.
	 *
	 * @param plugin The instance of your plugin
	 */
	public void hook(JavaPlugin plugin) {
		Bukkit.getPluginManager().registerEvents(new Listener() {

			@EventHandler
			private void onJoin(PlayerJoinEvent event) {
				inject(event.getPlayer());
			}

		}, plugin);
	}

	/**
	 * Injects all active injectors into the players channel.
	 *
	 * @param player The player whose channel to inject into.
	 */
	public void inject(Player player) {
		Channel channel = channelProvider.apply(player);
		injectors.forEach(injector -> injector.inject(player, channel));
	}

	/**
	 * The modifiable list of packet listeners. The order of elements in this list is the order
	 * in which packet events are processed.
	 *
	 * @return The list of packet listeners
	 */
	public List<PacketListener> getPacketListeners() {
		return packetListeners;
	}

	private MinecraftProtocol prepareProtocol() {
		MinecraftProtocol minecraftProtocol = new MinecraftProtocol(MinecraftCodec.CODEC);
		try {
			Method method = MinecraftProtocol.class.getDeclaredMethod("setState", ProtocolState.class);
			method.setAccessible(true);
			method.invoke(minecraftProtocol, ProtocolState.GAME);
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return minecraftProtocol;
	}

}
