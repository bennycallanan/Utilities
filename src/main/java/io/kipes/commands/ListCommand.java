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

public class ListCommand implements CommandExecutor, TabCompleter {

    private String getStaffOnline() {
        String message = "";

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (!Utilities.getDataStorage().isStaffModeActive(player) && !Utilities.getDataStorage().isVanished(player) && player.hasPermission("perm.staff")) {
                message += player.getName() + ChatColor.GOLD + ", " + ChatColor.YELLOW;
            }
        }

        if (message.length() > 2) {
            message = message.substring(0, message.length() - 2);
        }

        if (message.length() == 0) {
            message = "None";
        }

        return message;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        if (arguments.length >= 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6There is currently &e" + Bukkit.getServer().getOnlinePlayers().length + " &6out of &e" + Bukkit.getServer().getMaxPlayers() + " &6players online."));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Online Staff: &e" + getStaffOnline()));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] arguments) {
        return Collections.emptyList();
    }

}