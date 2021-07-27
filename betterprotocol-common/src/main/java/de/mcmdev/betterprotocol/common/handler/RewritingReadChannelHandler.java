package de.mcmdev.betterprotocol.common.handler;

import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.io.ByteBufNetInput;
import com.github.steveice10.packetlib.tcp.io.ByteBufNetOutput;
import de.mcmdev.betterprotocol.api.PacketEvent;
import de.mcmdev.betterprotocol.common.listener.CommonEventBus;
import de.mcmdev.betterprotocol.common.protocol.AbstractProtocolRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Reads all incoming packets, fires a {@link PacketEvent} and rewrites or cancels the packet when necessary
 * @param <P> The player type
 */
public class RewritingReadChannelHandler<P> extends ChannelInboundHandlerAdapter {

    private final P player;
    private final AbstractProtocolRegistry protocolRegistry;
    private final CommonEventBus<P> eventBus;

    public RewritingReadChannelHandler(P player, AbstractProtocolRegistry protocolRegistry, CommonEventBus<P> eventBus) {
        this.player = player;
        this.protocolRegistry = protocolRegistry;
        this.eventBus = eventBus;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        ByteBufNetInput netInput = new ByteBufNetInput(byteBuf);
        int i = protocolRegistry.getPacketHeader().readPacketId(netInput);
        Packet packet = protocolRegistry.createIncomingPacket(i);
        packet.read(netInput);

        PacketEvent packetEvent = new PacketEvent(player, packet.getClass(), packet);
        eventBus.post(packetEvent);

        if(packetEvent.isCancelled()) return;

        ByteBuf buffer = Unpooled.buffer();
        ByteBufNetOutput netOutput = new ByteBufNetOutput(buffer);
        protocolRegistry.getPacketHeader().writePacketId(netOutput, protocolRegistry.getIncomingId(packetEvent.getPacket()));
        packetEvent.getPacket().write(netOutput);

        byteBuf.resetReaderIndex();
        super.channelRead(ctx, buffer);
    }
}
