package de.mcmdev.betterprotocol.bukkit.inject;

import com.github.steveice10.packetlib.io.NetOutput;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.io.ByteBufNetOutput;
import de.mcmdev.betterprotocol.common.protocol.StubMinecraftProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.bukkit.entity.Player;

public class EncodingInjector extends Injector {

    private final StubMinecraftProtocol stubMinecraftProtocol;

    public EncodingInjector() {
        super("better_protocol_outgoing_listener", "better_protocol_encoder");
        this.stubMinecraftProtocol = new StubMinecraftProtocol();
    }

    @Override
    public ChannelHandler getHandler(Player player) {
        return new MessageToByteEncoder<Packet>() {

            @Override
            protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) {
                int initial = byteBuf.writerIndex();

                try {
                    NetOutput out = new ByteBufNetOutput(byteBuf);

                    stubMinecraftProtocol.getPacketHeader().writePacketId(out, stubMinecraftProtocol.getOutgoingId(packet));
                    packet.write(out);
                } catch(Throwable t) {
                    // Reset writer index to make sure incomplete data is not written out.
                    byteBuf.writerIndex(initial);
                }
            }
        };
    }
}
