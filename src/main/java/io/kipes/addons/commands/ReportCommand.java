package io.kipes.addons.commands;

import io.kipes.Utilities;
import io.kipes.addons.managers.ReportManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class ReportCommand extends CooldownCommand {
    public ReportCommand(Utilities plugin) {
        super(plugin, plugin.getConfig().getInt("report-cooldown"));
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCooldownedCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /report <player> <reason>");
            return true;
        }

        if (!plugin.getSQLAddon().getReportManager().isReportsEnabled()) {
            sender.sendMessage(ChatColor.RED + "Reports have been disabled!");
            return true;
        }

        OfflinePlayer op = Bukkit.getOfflinePlayer(args[0]);
        String target = op.getName();

        StringBuilder reason = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            reason.append(args[i]);
            reason.append(' ');
        }
        reason.deleteCharAt(reason.length() - 1);

        new BukkitRunnable() {
            public void run() {
                ReportManager.Report rep = plugin.getSQLAddon().getReportManager().report(target, sender.getName(), reason.toString());

                sender.sendMessage(ChatColor.GREEN + "Your report has been sent to our staff members.");
            }
        }.runTaskAsynchronously(plugin);

        return true;
    }
}
