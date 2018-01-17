package io.kipes.addons.commands;

import io.kipes.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckReportsCommand implements CommandExecutor {
    private Utilities plugin;

    public CheckReportsCommand(Utilities plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("perm.staff")) {
            sender.sendMessage(ChatColor.RED + "You don't have permissions to do this!");
            return true;
        }

        plugin.getSQLAddon().getReportManager().openGui((Player) sender, 0);

        return true;
    }
}
