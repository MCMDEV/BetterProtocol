package de.mcmdev.betterprotocol.bukkit;

import com.github.steveice10.packetlib.packet.Packet;

import de.mcmdev.betterprotocol.BetterProtocol;
import de.mcmdev.betterprotocol.bukkit.inject.EncodingInjector;
import de.mcmdev.betterprotocol.bukkit.inject.IncomingInjector;
import de.mcmdev.betterprotocol.bukkit.inject.OutgoingInjector;
import de.mcmdev.betterprotocol.common.BetterProtocolPlatform;
import de.mcmdev.betterprotocol.common.BetterProtocolPlugin;
import de.mcmdev.betterprotocol.common.inject.Injector;
import de.mcmdev.betterprotocol.common.listener.CommonEventBus;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class BetterProtocolBukkitPlugin extends JavaPlugin implements BetterProtocolPlugin<Player> {

    private BetterProtocolPlatform<Player> bukkitPlatform;

    @Override
    public void onEnable() {
        this.bukkitPlatform = new BetterProtocolPlatform<>(this, new CommonEventBus<>());
        this.bukkitPlatform.initialize();

        BetterProtocol.provide(this.bukkitPlatform);
    }

    @Override
    public void registerInjectors() {
        bukkitPlatform.registerInjector(
                new IncomingInjector(
                        bukkitPlatform.getProtocolRegistry(), bukkitPlatform.getEventBus()));
        bukkitPlatform.registerInjector(
                new OutgoingInjector(
                        bukkitPlatform.getProtocolRegistry(), bukkitPlatform.getEventBus()));
        bukkitPlatform.registerInjector(
                new EncodingInjector(
                        bukkitPlatform.getProtocolRegistry(), bukkitPlatform.getEventBus()));
    }

    @Override
    public void registerListeners(Set<Injector<Player>> injectors) {
        getServer()
                .getPluginManager()
                .registerEvents(
                        new Listener() {

                            @EventHandler
                            public void onJoin(PlayerJoinEvent event) {
                                injectors.forEach(injector -> injector.inject(event.getPlayer()));
                            }

                            @EventHandler
                            public void onQuit(PlayerQuitEvent event) {
                                injectors.forEach(injector -> injector.uninject(event.getPlayer()));
                            }
                        },
                        this);
    }

    @Override
    public String getVersion() {
        return Bukkit.getVersion();
    }

    @Override
    public void send(Player player, Packet packet) {
        ((CraftPlayer) player)
                .getHandle()
                .playerConnection
                .networkManager
                .channel
                .writeAndFlush(packet);
    }
}
