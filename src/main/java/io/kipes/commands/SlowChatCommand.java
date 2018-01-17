package io.kipes.commands;

import io.kipes.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SlowChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        if (sender.hasPermission("perm.staff")) {
            if (arguments.length == 0) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /" + label + " <seconds> or /" + label + " off."));
            } else {
                if (arguments[0].equalsIgnoreCase("off")) {
                    Utilities.getDataStorage().setChatSlowed(false);

                    for (Player online : Bukkit.getServer().getOnlinePlayers()) {
                        online.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a" + sender.getName() + " has un-slowed the global chat."));
                    }
                } else if (arguments[0].chars().allMatch(Character::isDigit)) {
                    Utilities.getDataStorage().setChatSlowed(true);

                    for (Player online : Bukkit.getServer().getOnlinePlayers()) {
                        online.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a" + sender.getName() + " has slowed the global chat."));
                    }
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /" + label + " <seconds> or /" + label + " off."));
                }
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou do not have permissions to execute this command."));
        }
        return true;
    }

}