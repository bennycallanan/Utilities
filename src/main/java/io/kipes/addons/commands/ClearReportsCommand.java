package io.kipes.addons.commands;

import io.kipes.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class ClearReportsCommand implements CommandExecutor {
    private Utilities plugin;

    public ClearReportsCommand(Utilities plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("perm.staff")) {
            sender.sendMessage(ChatColor.RED + "You don't have permissions to do this!");
            return true;
        }

        new BukkitRunnable() {
            public void run() {
                plugin.getSQLAddon().getReportManager().clearReports();
            }
        }.runTaskAsynchronously(plugin);

        sender.sendMessage(ChatColor.GREEN + "Cleared ALL reports.");

        return true;
    }
}
