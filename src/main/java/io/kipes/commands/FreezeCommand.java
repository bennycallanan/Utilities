package io.kipes.commands;

import io.kipes.Utilities;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class FreezeCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        if (sender.hasPermission("perm.staff")) {
            if (arguments.length == 0 || arguments.length > 1) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /" + label + " <playerName>"));
            } else {
                Player target = Bukkit.getServer().getPlayerExact(arguments[0]);
                if (target == null) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlayer named '" + arguments[0] + "' not found."));
                } else {
                    if (target.equals(sender)) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can not freeze yourself."));
                    } else {
                        if (sender instanceof Player) {
                            if (target.hasPermission("perm.staff") || target.isOp()) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can not freeze an Staff Member."));
                            } else {
                                if (Utilities.getDataStorage().isFrozen(target)) {
                                    Utilities.getDataStorage().setFreeze(sender, target, false);
                                } else {
                                    Utilities.getDataStorage().setFreeze(sender, target, true);
                                }
                            }
                        } else {
                            if (Utilities.getDataStorage().isFrozen(target)) {
                                Utilities.getDataStorage().setFreeze(sender, target, false);
                            } else {
                                Utilities.getDataStorage().setFreeze(sender, target, true);
                            }
                        }
                    }
                }
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou do not have permissions to execute this command."));
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