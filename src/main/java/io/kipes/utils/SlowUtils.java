package io.kipes.utils;

import io.kipes.Utilities;
import org.bukkit.Bukkit;

import java.util.ArrayList;

public class SlowUtils {
    public static boolean slowmode;
    public static boolean pausemode;
    public static int slowtime;
    public static ArrayList<String> slowplayers;

    public static void init() {
        slowmode = false;
        pausemode = false;
        slowtime = 0;
        slowplayers = new ArrayList();
        slowplayers.clear();
    }

    public static void setSlow(String name) {
        slowplayers.add(name);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Utilities.plugin, new Runnable() {
            public void run() {
                SlowUtils.slowplayers.remove(this);
            }
        }, 20L * slowtime);
    }
}
