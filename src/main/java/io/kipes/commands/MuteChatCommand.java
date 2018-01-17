package io.kipes.commands;

import io.kipes.Utilities;
import io.kipes.useful.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MuteChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        if (sender.hasPermission("perm.staff")) {
            long currentTicks = Utilities.getDataStorage().getMillisecondLeft();
            Long newTicks;

            if (currentTicks > 0L) {
                newTicks = 0L;
            } else {
                if (arguments.length < 1) {
                    newTicks = TimeUtils.parse("10m");
                } else {
                    newTicks = TimeUtils.parse(arguments[0]);

                    if (newTicks == -1L) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid duration, use the correct format: 5m5s [5 minutes and 15 second]"));
                        return true;
                    }
                }
            }

            Utilities.getDataStorage().setMuteChatMillis(newTicks);

            if (Utilities.getDataStorage().isChatMuted()) {
                Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&a" + sender.getName() + " has disabled the global chat."));
            } else {
                Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&a" + sender.getName() + " has enabled the global chat."));
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cNo Permission."));
        }
        return true;
    }

}