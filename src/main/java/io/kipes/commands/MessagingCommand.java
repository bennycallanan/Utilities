package io.kipes.commands;

import io.kipes.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessagingCommand
        implements CommandExecutor {
    Utilities m;

    public void PrivateMessage(Utilities m) {
        m = m;
    }

    public void noPermsMessage(Player p) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', m.getConfig().getString("NoPerms")));
    }

    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        if ((sender instanceof Player)) {
            Player p = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("msg")) {
                if (p.hasPermission("chatmanager.comamnds.msg")) {
                    if (args.length < 2) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', m.getConfig().getString("msgUsage")));
                        return true;
                    }
                    Player t = Bukkit.getPlayer(args[0]);
                    if (t == p) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', m.getConfig().getString("msgToSelf")));
                        return true;
                    }
                    if (t == null) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', m.getConfig().getString("NullPlayer")).replaceAll("<target>", args[0]));
                        return true;
                    }
                    StringBuilder msgBuilder = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        msgBuilder.append(args[i]).append(" ");
                    }
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', m.getConfig().getString("msgPlayerFormat")).replaceAll("<target>", args[0]).replaceAll("<player>", p.getName()).replaceAll("<message>", msgBuilder.toString()));
                    t.sendMessage(ChatColor.translateAlternateColorCodes('&', m.getConfig().getString("msgTargetFormat")).replaceAll("<target>", args[0]).replaceAll("<player>", p.getName()).replaceAll("<message>", msgBuilder.toString()));
                    if (m.replym.containsKey(p.getName())) {
                        m.replym.remove(p.getName());
                    }
                    m.replym.put(p.getName(), t.getName());
                    if (m.replym.containsKey(t.getName())) {
                        m.replym.remove(t.getName());
                    }
                    m.replym.put(t.getName(), p.getName());
                } else {
                    noPermsMessage(p);
                    return true;
                }
            }
            if (cmd.getName().equalsIgnoreCase("replym")) {
                if (p.hasPermission("chatmanager.commands.replym")) {
                    if (!m.replym.containsKey(sender.getName())) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', m.getConfig().getString("Noreplym")));
                    } else {
                        Player t = Bukkit.getPlayer((String) m.replym.get(p.getName()));
                        if (t == p) {
                            p.sendMessage(ChatColor.RED + "ERROR");
                            return true;
                        }
                        if (t == null) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', m.getConfig().getString("NullPlayer")).replaceAll("<target>", args[0]));
                            return true;
                        }
                        StringBuilder msgBuilder = new StringBuilder();
                        for (int i = 0; i < args.length; i++) {
                            msgBuilder.append(args[i]).append(" ");
                        }
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', m.getConfig().getString("msgPlayerFormat")).replaceAll("<target>", t.getName()).replaceAll("<player>", p.getName()).replaceAll("<message>", msgBuilder.toString()));
                        t.sendMessage(ChatColor.translateAlternateColorCodes('&', m.getConfig().getString("msgTargetFormat")).replaceAll("<target>", t.getName()).replaceAll("<player>", p.getName()).replaceAll("<message>", msgBuilder.toString()));
                        if (m.replym.containsKey(p.getName())) {
                            m.replym.remove(p.getName());
                        }
                        m.replym.put(p.getName(), t.getName());
                        if (m.replym.containsKey(t.getName())) {
                            m.replym.remove(t.getName());
                        }
                        m.replym.put(t.getName(), p.getName());
                    }
                } else {
                    noPermsMessage(p);
                    return true;
                }
            }
        }
        return true;
    }
}
