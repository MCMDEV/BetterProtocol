package de.mcmdev.protowrapper.channelhandler;

import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.packet.PacketProtocol;
import com.github.steveice10.packetlib.tcp.io.ByteBufNetInput;
import de.mcmdev.protowrapper.ProtoWrapper;
import de.mcmdev.protowrapper.event.PacketEvent;
import de.mcmdev.protowrapper.event.PacketListener;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.bukkit.entity.Player;

/**
 * Reads all outgoing packets, fires all event handlers and either:
 * If any event handler cancels, the packet will not be processed.
 * If no event handler cancels, the packet will be rewritten if any changes were made.
 */
public class OutgoingHandler extends ChannelOutboundHandlerAdapter {

	private final Player player;
	private final ProtoWrapper protoWrapper;
	private final PacketProtocol packetProtocol;

	public OutgoingHandler(Player player, ProtoWrapper protoWrapper, PacketProtocol packetProtocol) {
		this.player = player;
		this.protoWrapper = protoWrapper;
		this.packetProtocol = packetProtocol;
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		// if the passing data already is a packet (from send method), just fire the event, cancel
		// if needed, and pass
		Packet packet;
		if (msg instanceof Packet) {
			packet = (Packet) msg;
		} else {
			// read the packet id from data, create an outgoing packet from the id, and write the data
			// to the packet
			ByteBuf byteBuf = (ByteBuf) msg;
			ByteBufNetInput netInput = new ByteBufNetInput(byteBuf);
			int id = packetProtocol.getPacketHeader().readPacketId(netInput);
			packet = packetProtocol.createClientboundPacket(id, netInput);
		}

		// fire a packet event
		PacketEvent packetEvent = new PacketEvent(player, packet);
		for (PacketListener packetListener : protoWrapper.getPacketListeners()) {
			packetListener.onSend(packetEvent);
		}

		// don't handle the packet further if the event was cancelled
		if (packetEvent.isCancelled()) return;

		if (msg instanceof ByteBuf byteBuf) {
			byteBuf.resetReaderIndex();
		}

		// pass the new packet
		super.write(ctx, packetEvent.isDirty() ? packetEvent.getPacket() : msg, promise);
	}
}
