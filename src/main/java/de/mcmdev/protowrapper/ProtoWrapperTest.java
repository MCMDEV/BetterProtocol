package de.mcmdev.protowrapper;

import com.github.steveice10.mc.protocol.data.game.MessageType;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundChatPacket;
import de.mcmdev.protowrapper.event.PacketEvent;
import de.mcmdev.protowrapper.event.PacketListener;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ProtoWrapperTest extends JavaPlugin implements Listener {

	private final ProtoWrapper protoWrapper = new ProtoWrapper(new DefaultChannelProvider(), false);

	@Override
	public void onEnable() {
		this.protoWrapper.hook(this);
		this.protoWrapper.getPacketListeners().add(new PacketListener() {
			@Override
			public void onReceive(PacketEvent event) {
				if (event.getPacket() instanceof ServerboundChatPacket chatPacket) {
					System.out.println(chatPacket.getMessage());
					event.setCancelled(true);
				}
			}

			@Override
			public void onSend(PacketEvent event) {
				System.out.println(event.getPacket().getClass().getSimpleName());
			}
		});
		getServer().getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void onChat(AsyncChatEvent event) {
		Player player = event.getPlayer();
		((CraftPlayer) player).getHandle().networkManager.channel.writeAndFlush(
				new ClientboundChatPacket(Component.text("ABC"), MessageType.SYSTEM)
		);
	}

}
