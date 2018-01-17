package io.kipes.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PanicCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        if (sender.hasPermission("perm.media")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou have used your &4&lPANIC&c."));
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "ss " + sender.getName());

            for (Player players : Bukkit.getOnlinePlayers()) {
                if (players.hasPermission("perm.staff")) {
                    players.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&l" + sender.getName() + "&c has used &4&lPANIC&c."));
                }
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permissions to use this command."));
        }
        return false;
    }

}