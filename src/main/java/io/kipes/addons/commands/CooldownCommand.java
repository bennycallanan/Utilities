package io.kipes.addons.commands;

import io.kipes.Utilities;
import io.kipes.addons.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public abstract class CooldownCommand implements CommandExecutor, Listener {
    protected Utilities plugin;
    protected int cooldown; // In seconds

    protected Map<CommandSender, Long> lastExecution = new HashMap<>();

    public CooldownCommand(Utilities plugin, int cooldown) {
        this.plugin = plugin;
        this.cooldown = cooldown;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        lastExecution.remove(e.getPlayer());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (lastExecution.containsKey(sender)) {
            long elapsed = System.currentTimeMillis() - lastExecution.get(sender);
            int elapsedSecs = (int) elapsed / 1000;

            if (elapsedSecs < cooldown) {
                sender.sendMessage(ChatColor.RED + "Please wait another " + ChatColor.GOLD + Utils.formatTime(cooldown - elapsedSecs) + ChatColor.RED + ".");
                return true;
            }
        }

        lastExecution.put(sender, System.currentTimeMillis());

        return onCooldownedCommand(sender, cmd, label, args);
    }

    public abstract boolean onCooldownedCommand(CommandSender sender, Command cmd, String label, String[] args);
}
