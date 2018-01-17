package io.kipes.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        if (sender.hasPermission("perm.staff")) {
            if (arguments.length == 0) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /" + label + " <reason>"));
            } else {
                for (Player online : Bukkit.getServer().getOnlinePlayers()) {
                    online.sendMessage(new String[101]);
                    online.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aPublic chat has been cleared by " + sender.getName() + "."));
                }
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou do not have permissions to execute this command."));
        }
        return true;
    }

}