package de.mcmdev.betterprotocol.common.listener;

import com.github.steveice10.packetlib.packet.Packet;
import de.mcmdev.betterprotocol.api.EventBus;
import de.mcmdev.betterprotocol.api.PacketEvent;
import de.mcmdev.betterprotocol.api.PacketListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds all registered {@link PacketListener}'s and fires them
 * @param <P> The player type
 */
public class CommonEventBus<P> implements EventBus<P> {

    private final Map<Class<? extends Packet>, List<PacketListener>> listenerMap = new HashMap<>();

    public <T extends Packet> void listen(Class<T> packetClass, PacketListener<T, P> packetListener) {
        listenerMap.putIfAbsent(packetClass, new ArrayList<>());
        listenerMap.get(packetClass).add(packetListener);
    }

    public void post(PacketEvent<?, P> event)   {
        List<PacketListener> packetListeners = listenerMap.get(event.getPacketType());
        if(packetListeners == null) return;
        packetListeners.forEach(packetListener -> packetListener.handle(event));
    }
}
