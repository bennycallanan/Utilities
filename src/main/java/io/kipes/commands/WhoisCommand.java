package io.kipes.commands;

import io.kipes.Utilities;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class WhoisCommand implements CommandExecutor, TabCompleter {

    public void printDetails(CommandSender sender, Player target) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&m-----------------------------------------------------"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &eDisplaying &c" + target.getName() + " &eInformation."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "  &eHealth: &a" + ((Damageable) target).getHealth() + "/" + ((Damageable) target).getMaxHealth()));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "  &eHunger/Saturation: &a" + target.getFoodLevel() + "/20 (" + target.getSaturation() + ")"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "  &eExp/Level: &a" + target.getExp() + "/" + target.getLevel()));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "  &eLocation: &a(" + target.getLocation().getBlockX() + ", " + target.getLocation().getBlockY() + ", " + target.getLocation().getBlockZ() + ") &7[" + target.getLocation().getWorld().getName() + "]"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "  &eVanished: &a" + Utilities.getDataStorage().isVanished(target)));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "  &eFrozen: &a" + Utilities.getDataStorage().isFrozen(target)));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "  &eGame Mode: &a" + WordUtils.capitalizeFully(target.getGameMode().name())));

        if (sender.hasPermission("perm.admin")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "  &eIP Address: &a" + target.getAddress()));
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "  &eIP Address: &cHidden"));
        }

        String usingVersion = "";
        int clientVersion = ((CraftPlayer) target).getHandle().playerConnection.networkManager.getVersion();

        switch (clientVersion) {
            case 4:
                usingVersion = "&a1.7.2 > 1.7.5";
                break;
            case 5:
                usingVersion = "&a1.7.6 > 1.7.10";
                break;
            case 47:
                usingVersion = "&a1.8 > 1.8.8";
                break;
            default:
                usingVersion = "&cUnknown";
        }

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "  &eClient Version: &r" + usingVersion));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&m-----------------------------------------------------"));
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        if (sender.hasPermission("perm.staff")) {
            if (arguments.length == 1) {
                Player target = Bukkit.getServer().getPlayerExact(arguments[0]);

                if (target == null) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlayer named '" + arguments[0] + "' not found."));
                } else {
                    printDetails(sender, target);
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /" + label + " <playerName>"));
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