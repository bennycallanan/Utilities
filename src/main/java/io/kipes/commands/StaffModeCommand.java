package io.kipes.commands;

import io.kipes.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class StaffModeCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("perm.staff")) {
                if (arguments.length > 0) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /" + label));
                    return true;
                } else {
                    if (Utilities.getDataStorage().isStaffModeActive(player)) {
                        Utilities.getDataStorage().setStaffMode(player, false);
                        player.setGameMode(GameMode.SURVIVAL);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYour staff mode has been &cdisabled&e."));
                        return true;
                    } else {
                        Utilities.getDataStorage().setStaffMode(player, true);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYour staff mode has been &aenabled&e."));
                        return true;
                    }
                }
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou do not have permissions to execute this command."));
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can not execute this command on console."));
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] arguments) {
        if (arguments.length > 1) {
            return Collections.emptyList();
        }

        return null;
    }
}