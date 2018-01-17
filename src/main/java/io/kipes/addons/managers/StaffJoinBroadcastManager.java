package io.kipes.addons.managers;

import io.kipes.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;

public class StaffJoinBroadcastManager implements Listener {
    private Utilities plugin;

    public StaffJoinBroadcastManager(Utilities plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        if (e.getPlayer().hasPermission(Permissions.STAFF_JOIN_MESSAGES)) {
            List<String> targetPlayers = new ArrayList<>(plugin.getSQLAddon().getBungeeManager().getLatestPlayerList());
            //targetPlayers.remove(e.getPlayer().getName());

            String msg = ChatColor.AQUA + "[Staff] " + e.getPlayer().getName() + " connected to " + plugin.getSQLAddon().getBungeeManager().getServerName();
            plugin.getSQLAddon().getBungeeManager().broadcastBungee(msg, Permissions.RECEIVE_STAFF_BROADCASTS, targetPlayers);
        }
    }
}
