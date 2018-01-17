package io.kipes.addons.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class Utils {
    private Utils() {
    }

    public static ItemStack makeItem(Material type, int amount, int damage, String customName) {
        ItemStack is = new ItemStack(type, amount, (short) damage);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(customName);
        is.setItemMeta(im);

        return is;
    }

    public static ItemStack getHead(String owner) {
        ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta sm = (SkullMeta) is.getItemMeta();
        sm.setOwner(owner);
        is.setItemMeta(sm);

        return is;
    }

    public static String formatTime(int secs) {
        if (secs < 60)
            return secs + " second(s)";

        double mins = (double) secs / 60d;
        if (mins < 60)
            return (int) mins + " minute(s)";

        double hrs = mins / 60d;
        if (hrs < 24)
            return (int) hrs + " hour(s)";

        double days = hrs / 24d;
        return (int) days + " day(s)";
    }
}
