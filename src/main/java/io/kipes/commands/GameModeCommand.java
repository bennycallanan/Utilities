package io.kipes.commands;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameModeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to execute this command.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("perm.admin")) {
            player.sendMessage(ChatColor.RED + "You do not have permission for that command.");
            return true;
        }

        if (label.equalsIgnoreCase("gms")) {
            player.setGameMode(GameMode.SURVIVAL);
        } else if (label.equalsIgnoreCase("gmc")) {
            player.setGameMode(GameMode.CREATIVE);
        } else if (label.equalsIgnoreCase("gamemode")) {
            if (arguments.length == 0) {
                player.sendMessage(ChatColor.RED + "Usage: /gamemode <gm>, /gms, /gmc");
                return true;
            } else {
                switch (arguments[0].toLowerCase()) {
                    case "0":
                        player.setAllowFlight(false);
                        player.setGameMode(GameMode.SURVIVAL);
                        player.sendMessage(ChatColor.YELLOW + "You have been set to survival mode.");
                        break;
                    case "1":
                        player.setAllowFlight(true);
                        player.setGameMode(GameMode.CREATIVE);
                        player.sendMessage(ChatColor.YELLOW + "You have been set to creative mode.");
                        break;
                    case "s":
                        player.setAllowFlight(false);
                        player.setGameMode(GameMode.SURVIVAL);
                        player.sendMessage(ChatColor.YELLOW + "You have been set to survival mode.");
                        break;
                    case "c":
                        player.setAllowFlight(true);
                        player.setGameMode(GameMode.CREATIVE);
                        player.sendMessage(ChatColor.YELLOW + "You have been set to creative mode.");
                        break;
                    case "survival":
                        player.setAllowFlight(false);
                        player.setGameMode(GameMode.SURVIVAL);
                        player.sendMessage(ChatColor.YELLOW + "You have been set to survival mode.");
                        break;
                    case "creative":
                        player.setAllowFlight(true);
                        player.setGameMode(GameMode.CREATIVE);
                        player.sendMessage(ChatColor.YELLOW + "You have been set to creative mode.");
                        break;
                    default:
                        player.sendMessage(ChatColor.RED + "Usage: /gamemode <gm>, /gms, /gmc");
                        break;
                }
            }
        }

        return true;
    }

}