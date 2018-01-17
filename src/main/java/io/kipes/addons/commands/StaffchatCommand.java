package io.kipes.addons.commands;

import io.kipes.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class StaffchatCommand implements CommandExecutor {
    private Utilities plugin;

    public StaffchatCommand(Utilities plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("perm.staff")) {
            sender.sendMessage(ChatColor.RED + "You don't have permissions to do this!");
            return true;
        }

        UUID uuid = ((Player) sender).getUniqueId();

        boolean b = !plugin.getSQLAddon().getStaffChatManager().isInStaffChat(uuid);

        new BukkitRunnable() {
            public void run() {
                plugin.getSQLAddon().getStaffChatManager().setInStaffChat(uuid, b);
            }
        }.runTaskAsynchronously(plugin);

        if (b)
            sender.sendMessage(ChatColor.GREEN + "You are now in staffchat.");
        else
            sender.sendMessage(ChatColor.RED + "You are not in staffchat anymore.");

        System.out.println("asd");

        return true;
    }
}
