package io.kipes.addons.commands;

import io.kipes.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ToggleReportsCommand implements CommandExecutor {
    private Utilities plugin;

    public ToggleReportsCommand(Utilities plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("perm.staff")) {
            sender.sendMessage(ChatColor.RED + "You don't have permissions to do this!");
            return true;
        }

        boolean b = !plugin.getSQLAddon().getReportManager().isReportsEnabled();
        plugin.getSQLAddon().getReportManager().setReportsEnabled(b);

        if (b)
            sender.sendMessage(ChatColor.GREEN + "Enabled reports.");
        else
            sender.sendMessage(ChatColor.RED + "Disabled reports.");

        return true;
    }
}
