package de.mcmdev.betterprotocol.common.handler;

import com.github.steveice10.packetlib.io.NetOutput;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.io.ByteBufNetOutput;
import de.mcmdev.betterprotocol.common.protocol.AbstractProtocolRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * A ChannelHandler that encodes {@link Packet}s so they can be send to a player using {@link
 * de.mcmdev.betterprotocol.api.BetterProtocolAPI#send(Object, Packet)}
 */
public class EncodingChannelHandler extends MessageToByteEncoder<Packet> {

    private final AbstractProtocolRegistry protocolRegistry;

    public EncodingChannelHandler(AbstractProtocolRegistry protocolRegistry) {
        this.protocolRegistry = protocolRegistry;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf byteBuf) {
        // safe the writerIndex before the encoding
        int before = byteBuf.writerIndex();
        try {
            // create a NetOutput, which Packet data can be written to, write the packet id, and
            // then the packet data.
            NetOutput out = new ByteBufNetOutput(byteBuf);
            protocolRegistry
                    .getPacketHeader()
                    .writePacketId(out, protocolRegistry.getOutgoingId(packet));
            packet.write(out);
        } catch (Throwable t) {
            // in case of error, set writerIndex from before
            byteBuf.writerIndex(before);
        }
    }
}
