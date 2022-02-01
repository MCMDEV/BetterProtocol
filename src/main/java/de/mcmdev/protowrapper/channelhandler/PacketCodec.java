package de.mcmdev.protowrapper.channelhandler;

import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.packet.PacketProtocol;
import com.github.steveice10.packetlib.tcp.io.ByteBufNetInput;
import com.github.steveice10.packetlib.tcp.io.ByteBufNetOutput;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

public class PacketCodec extends ByteToMessageCodec<Packet> {

	private final PacketProtocol packetProtocol;

	public PacketCodec(PacketProtocol packetProtocol) {
		this.packetProtocol = packetProtocol;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf buf) {
		int initial = buf.writerIndex();

		try {
			NetOutput out = new ByteBufNetOutput(buf);

			packetProtocol.getPacketHeader().writePacketId(out, packetProtocol.getClientboundId(packet));
			packet.write(out);
			out.flush();
		} catch (Throwable t) {
			t.printStackTrace();
			// Reset writer index to make sure incomplete data is not written out.
			buf.writerIndex(initial);
		}
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) {
		int initial = buf.readerIndex();

		try {
			NetInput in = new ByteBufNetInput(buf);

			int id = packetProtocol.getPacketHeader().readPacketId(in);
			if (id == -1) {
				buf.readerIndex(initial);
				return;
			}

			Packet packet = packetProtocol.createServerboundPacket(id, in);

			if (buf.readableBytes() > 0) {
				throw new IllegalStateException("Packet \"" + packet.getClass().getSimpleName() + "\" not fully read.");
			}

			out.add(packet);
		} catch (Throwable t) {
			// Advance buffer to end to make sure remaining data in this packet is skipped.
			buf.readerIndex(buf.readerIndex() + buf.readableBytes());
		}
	}
}
