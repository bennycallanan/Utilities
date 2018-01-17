package io.kipes.listeners;

import io.kipes.Utilities;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    public ChatListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Utilities.getInstance());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (Utilities.getDataStorage().isChatMuted() && !player.hasPermission("perm.staff")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThe global chat is currently disabled for another &6" + DurationFormatUtils.formatDurationWords(Utilities.getDataStorage().getMillisecondLeft(), true, true) + "&c."));
            return;
        }

        if (Utilities.getDataStorage().isChatSlowed() && !player.hasPermission("perm.staff")) {
            if (Utilities.getDataStorage().isSlowed(player)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "Chat is currently slowed, try again in " + ChatColor.GRAY + Utilities.getDataStorage().getSlowTime(player) + ChatColor.GREEN + " seconds."));
            } else {
                Utilities.getDataStorage().setSlowed(player);

            }
        }
    }
}