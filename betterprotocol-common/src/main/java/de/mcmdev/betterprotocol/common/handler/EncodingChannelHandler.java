package de.mcmdev.betterprotocol.common.handler;

import com.github.steveice10.packetlib.io.NetOutput;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.io.ByteBufNetOutput;
import de.mcmdev.betterprotocol.common.protocol.AbstractProtocolRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * A ChannelHandler that encodes {@link Packet}s so they can be send to a player using {@link de.mcmdev.betterprotocol.api.BetterProtocolAPI#send(Object, Packet)}
 */
public class EncodingChannelHandler extends MessageToByteEncoder<Packet> {

    private final AbstractProtocolRegistry protocolRegistry;

    public EncodingChannelHandler(AbstractProtocolRegistry protocolRegistry) {
        this.protocolRegistry = protocolRegistry;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf byteBuf) {
        int before = byteBuf.writerIndex();
        try {
            NetOutput out = new ByteBufNetOutput(byteBuf);
            protocolRegistry.getPacketHeader().writePacketId(out, protocolRegistry.getOutgoingId(packet));
            packet.write(out);
        } catch (Throwable t) {
            byteBuf.writerIndex(before);
        }
    }
}
