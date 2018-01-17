package io.kipes.commands;

import io.kipes.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class InspectCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("perm.staff")) {
                if (arguments.length == 0 || arguments.length > 1) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /" + label + " <playerName>"));
                } else {
                    Player target = Bukkit.getServer().getPlayerExact(arguments[0]);
                    if (target == null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlayer named '" + arguments[0] + "' not found."));
                    } else {
                        if (target.equals(player)) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can not inspect yourself."));
                        } else {
                            Utilities.getDataStorage().openInspectionMenu(player, target);
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou are now inspecting the inventory of &c" + target.getName() + "&e."));
                        }
                    }
                }
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou do not have permissions to execute this command."));
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can not execute this command on console."));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] arguments) {
        if (arguments.length > 1) {
            return Collections.emptyList();
        }

        return null;
    }

}