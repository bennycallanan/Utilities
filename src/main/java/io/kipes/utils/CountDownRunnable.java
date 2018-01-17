package io.kipes.utils;

import io.kipes.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class CountDownRunnable extends BukkitRunnable {

    Utilities utilites;
    private Integer startCountdown = Integer.valueOf(0);

    public CountDownRunnable(Integer startCount) {
        // TODO Auto-generated constructor stub
    }

    public void CountdownRunnable(Integer start) {
        this.startCountdown = start;
    }

    public void run() {
        this.startCountdown = Integer.valueOf(this.startCountdown.intValue() - 1);
        String time = utilites.getConfig().getString("showcountdown");
        if ((this.startCountdown.intValue() == Integer.parseInt(time)) && (utilites.getConfig().getString("countdowninchat") == "true")) {
            Bukkit.broadcastMessage(utilites.getConfig().getString("rstart"));
        }
        if ((this.startCountdown.intValue() <= Integer.parseInt(time)) && (utilites.getConfig().getString("countdowninchat") == "true")) {
            Bukkit.broadcastMessage(ChatColor.GOLD + " " + utilites.getConfig().getString("countcolor") + this.startCountdown);
        }
        if ((this.startCountdown.intValue() <= Integer.parseInt(time)) && (utilites.getConfig().getString("countdowninaction") == "true")) {
        }
        if ((this.startCountdown.intValue() <= Integer.parseInt(time)) && (utilites.getConfig().getString("countdowninchat") == "false")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + " " + utilites.getConfig().getString("countcolor") + this.startCountdown);
        }
        if (this.startCountdown.intValue() == 0) {
            Bukkit.broadcastMessage(ChatColor.GOLD + utilites.getConfig().getString("finish"));
            Bukkit.getScheduler().cancelAllTasks();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), utilites.getConfig().getString("restartcommand"));
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "srestartreset");
        }
    }
}
