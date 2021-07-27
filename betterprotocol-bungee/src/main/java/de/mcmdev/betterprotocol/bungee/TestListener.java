package de.mcmdev.betterprotocol.bungee;

import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.io.ByteBufNetInput;
import de.mcmdev.betterprotocol.common.protocol.AbstractProtocolRegistry;
import de.mcmdev.betterprotocol.common.protocol.ProtocolRegistry_1_16_5;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.netty.ChannelWrapper;
import org.joor.Reflect;

public class TestListener implements Listener {

    private final AbstractProtocolRegistry abstractProtocolRegistry = new ProtocolRegistry_1_16_5();

    @EventHandler
    public void onJoin(PostLoginEvent event)    {
        Channel channel = getChannelWrapper((UserConnection) event.getPlayer()).getHandle();
        System.out.println(channel.pipeline().toMap());
        channel.pipeline().addBefore("packet-decoder", "better_protocol_decoder", new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                ByteBuf byteBuf = (ByteBuf) msg;
                int readerIndex = byteBuf.readerIndex();
                ByteBufNetInput netInput = new ByteBufNetInput(byteBuf);
                int i = abstractProtocolRegistry.getPacketHeader().readPacketId(netInput);
                Packet packet = abstractProtocolRegistry.createIncomingPacket(i);
                packet.read(netInput);

                System.out.println(packet);

                byteBuf.readerIndex(readerIndex);
                super.channelRead(ctx, byteBuf);
            }
        });
    }

    private ChannelWrapper getChannelWrapper(UserConnection userConnection) {
        return Reflect.on(userConnection)
                .get("ch");
    }

}
