package io.kipes.commands;

import io.kipes.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class VanishCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("perm.staff")) {
                if (arguments.length > 0) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /" + label));
                } else {
                    if (Utilities.getDataStorage().isVanished(player)) {
                        Utilities.getDataStorage().setVanished(player, false);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYour vanish has been &cdisabled&e."));
                    } else {
                        Utilities.getDataStorage().setVanished(player, true);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYour vanish has been &aenabled&e."));
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