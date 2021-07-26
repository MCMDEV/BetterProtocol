package de.mcmdev.betterprotocol.bukkit;

import com.github.steveice10.packetlib.packet.Packet;
import de.mcmdev.betterprotocol.BetterProtocol;
import de.mcmdev.betterprotocol.api.BetterProtocolAPI;
import de.mcmdev.betterprotocol.api.EventBus;
import de.mcmdev.betterprotocol.bukkit.inject.EncodingInjector;
import de.mcmdev.betterprotocol.bukkit.inject.IncomingInjector;
import de.mcmdev.betterprotocol.bukkit.inject.Injector;
import de.mcmdev.betterprotocol.bukkit.inject.OutgoingInjector;
import de.mcmdev.betterprotocol.bukkit.listener.BukkitEventBus;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class BetterProtocolBukkitPlugin extends JavaPlugin implements BetterProtocolAPI<Player> {

    private BukkitEventBus packetEventBus;
    private final List<Injector> injectors = new ArrayList<>();

    @Override
    public void onEnable() {
        this.packetEventBus = new BukkitEventBus();
        injectors.add(new IncomingInjector(packetEventBus));
        injectors.add(new OutgoingInjector(packetEventBus));
        injectors.add(new EncodingInjector());
        getServer().getPluginManager().registerEvents(new Listener() {

            @EventHandler
            public void onJoin(PlayerJoinEvent event)   {
                injectors.forEach(injector -> injector.inject(event.getPlayer()));
            }

            @EventHandler
            public void onQuit(PlayerQuitEvent event)   {
                injectors.forEach(injector -> injector.uninject(event.getPlayer()));
            }

        }, this);

        BetterProtocol.provide(this);
    }

    public EventBus<Player> getEventBus() {
        return packetEventBus;
    }

    public void send(Player player, Packet packet)  {
        ((CraftPlayer)player).getHandle().playerConnection.networkManager.channel.writeAndFlush(packet);
    }
}
