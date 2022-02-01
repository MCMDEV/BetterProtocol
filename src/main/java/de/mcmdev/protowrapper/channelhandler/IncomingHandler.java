package de.mcmdev.protowrapper.channelhandler;

import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.packet.PacketProtocol;
import com.github.steveice10.packetlib.tcp.io.ByteBufNetInput;
import de.mcmdev.protowrapper.ProtoWrapper;
import de.mcmdev.protowrapper.event.PacketEvent;
import de.mcmdev.protowrapper.event.PacketListener;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.bukkit.entity.Player;

public class IncomingHandler extends ChannelInboundHandlerAdapter {

	private final Player player;
	private final ProtoWrapper protoWrapper;
	private final PacketProtocol packetProtocol;

	public IncomingHandler(Player player, ProtoWrapper protoWrapper, PacketProtocol packetProtocol) {
		this.player = player;
		this.protoWrapper = protoWrapper;
		this.packetProtocol = packetProtocol;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// read the packet id from the data, create an incoming packet from the id, and write the
		// data to the packet
		ByteBuf byteBuf = (ByteBuf) msg;
		ByteBufNetInput netInput = new ByteBufNetInput(byteBuf);
		int id = packetProtocol.getPacketHeader().readPacketId(netInput);
		Packet packet = packetProtocol.createServerboundPacket(id, netInput);

		// fire a packet event
		PacketEvent packetEvent = new PacketEvent(player, packet);
		for (PacketListener packetListener : protoWrapper.getPacketListeners()) {
			packetListener.onReceive(packetEvent);
		}

		// don't handle the packet further if the event was cancelled
		if (packetEvent.isCancelled()) return;

		byteBuf.resetReaderIndex();

		// pass the new packet
		super.channelRead(ctx, packetEvent.isDirty() ? packetEvent.getPacket() : msg);
	}
}
