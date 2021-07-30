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
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * Reads all outgoing packets, fires a {@link PacketEvent} and rewrites or cancels the packet when
 * necessary. Can also listen to {@link Packet}'s sent using the API
 *
 * @param <P> The player type
 */
public class RewritingWriteChannelHandler<P> extends ChannelOutboundHandlerAdapter {

    private final P player;
    private final AbstractProtocolRegistry protocolRegistry;
    private final CommonEventBus<P> eventBus;

    public RewritingWriteChannelHandler(
            P player, AbstractProtocolRegistry protocolRegistry, CommonEventBus<P> eventBus) {
        this.player = player;
        this.protocolRegistry = protocolRegistry;
        this.eventBus = eventBus;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
            throws Exception {

        // if the passing data already is a packet (from send method), just fire the event, cancel
        // if needed, and pass
        if (msg instanceof Packet) {
            Packet packet = (Packet) msg;
            PacketEvent packetEvent = new PacketEvent(player, packet.getClass(), packet);
            eventBus.post(packetEvent);
            if (packetEvent.isCancelled()) return;

            super.write(ctx, packetEvent.getPacket(), promise);
            return;
        }

        // read the packet id from data, create an outgoing packet from the id, and write the data
        // to the packet
        ByteBuf byteBuf = (ByteBuf) msg;
        ByteBufNetInput netInput = new ByteBufNetInput(byteBuf);
        int i = protocolRegistry.getPacketHeader().readPacketId(netInput);
        Packet packet = protocolRegistry.createOutgoingPacket(i);
        packet.read(netInput);

        // fire a packet event
        PacketEvent packetEvent = new PacketEvent(player, packet.getClass(), packet);
        eventBus.post(packetEvent);

        // don't handle the packet further if the event was cancelled
        if (packetEvent.isCancelled()) return;

        // create a new buffer, write the packet id and packet data
        ByteBuf buffer = Unpooled.buffer();
        ByteBufNetOutput netOutput = new ByteBufNetOutput(buffer);
        protocolRegistry
                .getPacketHeader()
                .writePacketId(netOutput, protocolRegistry.getOutgoingId(packetEvent.getPacket()));
        packetEvent.getPacket().write(netOutput);

        byteBuf.resetReaderIndex();

        // pass the new packet
        super.write(ctx, buffer, promise);
    }
}
