package io.kipes.addons.commands;

import io.kipes.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class HelpopCommand extends CooldownCommand {
    public HelpopCommand(Utilities plugin) {
        super(plugin, plugin.getConfig().getInt("helpop-cooldown"));
    }

    @Override
    public boolean onCooldownedCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /helpop <message>");
            return true;
        }

        StringBuilder reason = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            reason.append(args[i]);
            reason.append(' ');
        }

        reason.deleteCharAt(reason.length() - 1);

        String msg = ChatColor.RED + sender.getName() + ChatColor.YELLOW + " has requested " + ChatColor.RED + reason.toString() + ChatColor.GREEN + " [" + plugin.getSQLAddon().getBungeeManager().getServerName() + "]";
        plugin.getSQLAddon().getBungeeManager().broadcastBungee(msg, "perm.staff", null);

        sender.sendMessage(ChatColor.GREEN + "Your message has been sent to our staff members.");

        return true;
    }

}
