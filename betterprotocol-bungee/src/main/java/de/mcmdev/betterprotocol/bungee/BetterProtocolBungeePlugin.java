package de.mcmdev.betterprotocol.bungee;

import net.md_5.bungee.api.plugin.Plugin;

public class BetterProtocolBungeePlugin extends Plugin {

    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerListener(this, new TestListener());
    }
}
