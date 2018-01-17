package io.kipes.commands;

import net.minecraft.util.com.google.common.primitives.Ints;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

public class SetSlotsCommand implements CommandExecutor, TabCompleter {

    private void setMaxPlayers(int amount) throws ReflectiveOperationException {
        String bukkitversion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
        Object playerlist = Class.forName("org.bukkit.craftbukkit." + bukkitversion + ".CraftServer").getDeclaredMethod("getHandle", null).invoke(Bukkit.getServer(), null);
        Field maxplayers = playerlist.getClass().getSuperclass().getDeclaredField("maxPlayers");
        maxplayers.setAccessible(true);
        maxplayers.set(playerlist, amount);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        if (sender.hasPermission("perm.admin")) {
            if (arguments.length < 1) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /" + label + " <amount>"));
            } else {
                Integer amount = Ints.tryParse((String) arguments[0]);
                if (amount == null) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c'" + amount + "' is not a valid number."));
                } else if (amount <= 0) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c'" + amount + "' is not a valid number."));
                } else {
                    for (Player staff : Bukkit.getServer().getOnlinePlayers()) {
                        if (!staff.isOp()) continue;
                        staff.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aServer slots updated from " + Bukkit.getServer().getMaxPlayers() + " -> " + amount + "."));
                    }

                    try {
                        this.setMaxPlayers(amount);
                    } catch (ReflectiveOperationException expeption) {
                        expeption.printStackTrace();
                    }
                }
            }
        }

        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] arguments) {
        return Collections.emptyList();
    }

}
