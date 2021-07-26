package de.mcmdev.betterprotocol.bukkit.listener;

import com.github.steveice10.packetlib.packet.Packet;
import de.mcmdev.betterprotocol.api.EventBus;
import de.mcmdev.betterprotocol.api.PacketEvent;
import de.mcmdev.betterprotocol.api.PacketListener;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BukkitEventBus implements EventBus<Player> {

    private final Map<Class<? extends Packet>, List<PacketListener>> listenerMap = new HashMap<>();

    public <T extends Packet> void listen(Class<T> packetClass, PacketListener<T, Player> packetListener) {
        listenerMap.putIfAbsent(packetClass, new ArrayList<>());
        listenerMap.get(packetClass).add(packetListener);
    }

    public void post(PacketEvent<?, Player> event)   {
        List<PacketListener> packetListeners = listenerMap.get(event.getPacketType());
        if(packetListeners == null) return;
        packetListeners.forEach(packetListener -> packetListener.handle(event));
    }

}
