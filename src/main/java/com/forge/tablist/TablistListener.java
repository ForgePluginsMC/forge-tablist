package com.forge.tablist;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/** Applies tablist styling on join and serves the animated MOTD on server-list pings. */
public final class TablistListener implements Listener {

    private final ForgeTablist plugin;

    public TablistListener(ForgeTablist plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.applyTablistName(event.getPlayer());
        plugin.pushCurrentFrame(event.getPlayer());
    }

    @EventHandler
    public void onPing(PaperServerListPingEvent event) {
        event.motd(plugin.currentMotd(event.getNumPlayers(), event.getMaxPlayers()));
        int max = plugin.motdMaxPlayers();
        if (max >= 0) {
            event.setMaxPlayers(max);
        }
    }
}
