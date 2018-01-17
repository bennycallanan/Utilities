package io.kipes.addons.managers;

import io.kipes.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StaffChatManager implements Listener {
    private static final String TABLE_STAFFCHAT = "staffchat";

    private Utilities plugin;
    private List<UUID> inStaffChat = new ArrayList<>();

    public StaffChatManager(Utilities plugin) {
        this.plugin = plugin;

        createReportsTable();

        new BukkitRunnable() {
            public void run() {
                refreshInStaffChat();
            }
        }.runTaskTimerAsynchronously(plugin, 0, 20 * 5);

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        if (e.getPlayer().hasPermission(Permissions.STAFFCHAT)) {
            new BukkitRunnable() {
                public void run() {
                    refreshInStaffChat();
                }
            }.runTaskAsynchronously(plugin);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent e) {
        if (isInStaffChat(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);

            chat(e.getPlayer(), e.getMessage());
        }
    }

    public void chat(Player p, String msg) {
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        String totalMsg = ChatColor.AQUA + "[Staff] " + "[" + plugin.getSQLAddon().getBungeeManager().getServerName() + "] " + p.getName() + ": " + msg;

        plugin.getSQLAddon().getBungeeManager().broadcastBungee(totalMsg, Permissions.STAFFCHAT, null);
    }

    public void setInStaffChat(UUID uuid, boolean in) {
        try {
            if (in) {
                inStaffChat.add(uuid);

                plugin.getSQLAddon().getDatabaseConnection().query("INSERT INTO " + TABLE_STAFFCHAT + "(uuid) VALUES (?)", uuid.toString());
            } else {
                inStaffChat.remove(uuid);

                plugin.getSQLAddon().getDatabaseConnection().query("DELETE FROM " + TABLE_STAFFCHAT + " WHERE uuid=?", uuid.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().warning("Could not update staffchat table.");
        }
    }

    public boolean isInStaffChat(UUID uuid) {
        return inStaffChat.contains(uuid);
    }

    private void refreshInStaffChat() {
        inStaffChat.clear();

        try {
            ResultSet res = plugin.getSQLAddon().getDatabaseConnection().queryResults("SELECT * FROM " + TABLE_STAFFCHAT);
            while (res.next()) {
                inStaffChat.add(UUID.fromString(res.getString("uuid")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().warning("Could not refresh players in staffchat.");
        }
    }

    private void createReportsTable() {
        try {
            plugin.getSQLAddon().getDatabaseConnection().query("SELECT * FROM " + TABLE_STAFFCHAT + " LIMIT 1");
            plugin.getLogger().info("Staffchat table does exist.");
        } catch (SQLException e) {
            plugin.getLogger().info("Staffchat table does not exist. Making one..");

            try {
                plugin.getSQLAddon().getDatabaseConnection().query("CREATE TABLE " + TABLE_STAFFCHAT + " ( `uuid` TINYTEXT NOT NULL );");
            } catch (SQLException e1) {
                e1.printStackTrace();
                plugin.getLogger().warning("Could not make staffchat table.");
            }
        }
    }
}
