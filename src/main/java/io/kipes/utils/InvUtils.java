package io.kipes.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class InvUtils {

    public static PlayerInv playerInventoryFromPlayer(Player player) {
        PlayerInv inv = new PlayerInv();

        inv.setContents(player.getInventory().getContents());
        inv.setArmorContents(player.getInventory().getArmorContents());

        return inv;
    }

    public static boolean hasSwordInHand(Player player) {
        switch (player.getItemInHand().getType()) {
            case DIAMOND_SWORD:
                return true;
            case IRON_SWORD:
                return true;
            case STONE_SWORD:
                return true;
            case GOLD_SWORD:
                return true;
            case WOOD_SWORD:
                return true;
            default:
                return false;
        }
    }

    public static boolean hasArmorEquipped(Player player) {
        return player.getInventory().getHelmet() != null && player.getInventory().getChestplate() != null && player.getInventory().getLeggings() != null && player.getInventory().getBoots() != null;
    }

    public static ItemStack addToPart(ItemStack itemStack, String title, String register, int amount) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta.hasLore() && !itemMeta.getLore().isEmpty()) {
            List<String> lore = itemMeta.getLore();

            if (lore.contains(title)) {
                int titleIndex = lore.indexOf(title);
                int keys = 0;

                for (int i = titleIndex; i < lore.size(); i++) {
                    if (((String) lore.get(i)).equals("")) {
                        break;
                    }
                    keys++;
                }

                lore.add(titleIndex + 1, register);

                if (keys > amount) {
                    lore.remove(titleIndex + keys);
                }
            } else {
                lore.add("");
                lore.add(title);
                lore.add(register);
            }

            itemMeta.setLore(lore);
        } else {
            List<String> lore = new ArrayList<String>();

            lore.add("");
            lore.add(title);
            lore.add(register);

            itemMeta.setLore(lore);
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static ItemStack addDeath(ItemStack itemStack, String register) {
        return addToPart(itemStack, ChatColor.translateAlternateColorCodes('&', "&4&lDeaths:"), register, 10);
    }

}