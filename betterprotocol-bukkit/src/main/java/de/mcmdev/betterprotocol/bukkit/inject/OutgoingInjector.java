package de.mcmdev.betterprotocol.bukkit.inject;

import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.io.ByteBufNetInput;
import com.github.steveice10.packetlib.tcp.io.ByteBufNetOutput;
import de.mcmdev.betterprotocol.api.PacketEvent;
import de.mcmdev.betterprotocol.bukkit.listener.BukkitEventBus;
import de.mcmdev.betterprotocol.common.protocol.StubMinecraftProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.bukkit.entity.Player;

public class OutgoingInjector extends Injector {
    private final BukkitEventBus packetEventBus;
    private final StubMinecraftProtocol stubMinecraftProtocol;

    public OutgoingInjector(BukkitEventBus packetEventBus) {
        super("encoder", "better_protocol_outgoing_listener");
        this.packetEventBus = packetEventBus;
        this.stubMinecraftProtocol = new StubMinecraftProtocol();
    }

    @Override
    public ChannelHandler getHandler(Player player) {
        return new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                super.channelRead(ctx, msg);
            }

            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                if(msg instanceof Packet)   {
                    Packet packet = (Packet) msg;
                    PacketEvent packetEvent = new PacketEvent(player, packet.getClass(), packet);
                    packetEventBus.post(packetEvent);
                    if(packetEvent.isCancelled()) return;

                    super.write(ctx, packetEvent.getPacket(), promise);
                    return;
                }

                ByteBuf byteBuf = (ByteBuf) msg;
                ByteBufNetInput netInput = new ByteBufNetInput(byteBuf);
                int i = stubMinecraftProtocol.getPacketHeader().readPacketId(netInput);
                Packet packet = stubMinecraftProtocol.createOutgoingPacket(i);
                packet.read(netInput);

                PacketEvent packetEvent = new PacketEvent(player, packet.getClass(), packet);
                packetEventBus.post(packetEvent);

                if(packetEvent.isCancelled()) return;

                ByteBuf buffer = Unpooled.buffer();
                ByteBufNetOutput netOutput = new ByteBufNetOutput(buffer);
                stubMinecraftProtocol.getPacketHeader().writePacketId(netOutput, stubMinecraftProtocol.getOutgoingId(packetEvent.getPacket()));
                packetEvent.getPacket().write(netOutput);

                byteBuf.resetReaderIndex();
                super.write(ctx, buffer, promise);
            }
        };
    }
}
