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

public class IncomingInjector extends Injector {
    private final BukkitEventBus packetEventBus;
    private final StubMinecraftProtocol stubMinecraftProtocol;

    public IncomingInjector(BukkitEventBus packetEventBus) {
        super("decoder", "better_protocol_incoming_listener");
        this.packetEventBus = packetEventBus;
        this.stubMinecraftProtocol = new StubMinecraftProtocol();
    }

    @Override
    public ChannelHandler getHandler(Player player) {
        return new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                ByteBuf byteBuf = (ByteBuf) msg;
                ByteBufNetInput netInput = new ByteBufNetInput(byteBuf);
                int i = stubMinecraftProtocol.getPacketHeader().readPacketId(netInput);
                Packet packet = stubMinecraftProtocol.createIncomingPacket(i);
                packet.read(netInput);

                PacketEvent packetEvent = new PacketEvent(player, packet.getClass(), packet);
                packetEventBus.post(packetEvent);

                if(packetEvent.isCancelled()) return;

                ByteBuf buffer = Unpooled.buffer();
                ByteBufNetOutput netOutput = new ByteBufNetOutput(buffer);
                stubMinecraftProtocol.getPacketHeader().writePacketId(netOutput, stubMinecraftProtocol.getIncomingId(packetEvent.getPacket()));
                packetEvent.getPacket().write(netOutput);

                byteBuf.resetReaderIndex();
                super.channelRead(ctx, buffer);
            }

            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                super.write(ctx, msg, promise);
            }
        };
    }
}
