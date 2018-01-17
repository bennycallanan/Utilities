package io.kipes.listeners;

import io.kipes.Utilities;
import io.kipes.utils.InvUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatTrackListener implements Listener {

    public StatTrackListener() {
        Bukkit.getPluginManager().registerEvents(this, Utilities.getInstance());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player dead = event.getEntity();
        Player killer = dead.getKiller();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        if (InvUtils.hasArmorEquipped(dead)) {
            for (ItemStack armorContents : dead.getInventory().getArmorContents()) {
                if (killer != null) {
                    InvUtils.addDeath(armorContents, ChatColor.translateAlternateColorCodes('&', "&e" + dead.getName() + " &fkilled by &e" + killer.getName() + " &6" + dateFormat.format(new Date()).replace("AM", "").replace("PM", "")));
                } else {
                    InvUtils.addDeath(armorContents, ChatColor.translateAlternateColorCodes('&', "&e" + dead.getName() + " &fdied &6" + dateFormat.format(new Date()).replace("AM", "").replace("PM", "")));
                }
            }
        }

        if (killer != null && killer.getGameMode() != GameMode.CREATIVE) {
            ItemStack itemStack = killer.getItemInHand();
            if (InvUtils.hasSwordInHand(killer)) {
                int killsIndex = 1;
                int[] lastKills = {3, 4, 5};
                int currentKills = 1;

                List<String> itemLore = new ArrayList<String>();
                ItemMeta itemMeta = itemStack.getItemMeta();

                if (itemMeta.hasLore()) {
                    itemLore = itemMeta.getLore();
                    boolean hasForgedMeta = false;

                    for (String string : itemMeta.getLore()) {
                        if (string.toLowerCase().contains("forged")) {
                            hasForgedMeta = true;
                        }
                    }

                    if (hasForgedMeta) {
                        killsIndex++;
                        for (int i = 0; i < lastKills.length; i++) {
                            lastKills[i] += 1;
                        }
                    }

                    if (itemMeta.getLore().size() > killsIndex) {
                        String killCounter = itemLore.get(killsIndex);
                        currentKills += Integer.parseInt(ChatColor.stripColor(killCounter.split(":")[1]).trim());
                    }

                    for (int j : lastKills) {
                        if (j != lastKills[(lastKills.length - 1)]) {
                            if (itemLore.size() > j) {
                                String spacer = itemMeta.getLore().get(j);

                                if (itemLore.size() <= j + 1) {
                                    itemLore.add("");
                                }
                                itemLore.set(j + 1, spacer);
                            }
                        }
                    }
                }

                if (itemLore.size() <= killsIndex) {
                    for (int k = itemLore.size(); k <= killsIndex + 1; k++) {
                        itemLore.add("");
                    }
                }

                itemLore.set(killsIndex, ChatColor.translateAlternateColorCodes('&', "&6&lKills&6: &f" + currentKills));
                int firsKill = lastKills[0];

                if (itemLore.size() <= firsKill) {
                    for (int i = itemLore.size(); i <= firsKill + 1; i++) {
                        itemLore.add("");
                    }
                }

                itemLore.set(firsKill, ChatColor.translateAlternateColorCodes('&', "&e" + dead.getName() + " &fkilled by &e" + killer.getName() + " &6" + dateFormat.format(new Date()).replace("AM", "").replace("PM", "")));
                itemMeta.setLore(itemLore);
                itemStack.setItemMeta(itemMeta);
            }
        }
    }

}