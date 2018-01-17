package io.kipes.scheduler;

import io.kipes.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

public class AlertTask extends BukkitRunnable {

    private Player player;

    public AlertTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (Utilities.getDataStorage().isFrozen(player)) {
            player.setHealth(((Damageable) player).getMaxHealth());
            player.setFireTicks(0);
            player.setFoodLevel(20);
            player.setSaturation(3.0F);

            for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                player.removePotionEffect(potionEffect.getType());
            }

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&m-------------------------------"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cscheisse, you have been frozen."));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cConnect to &ets.exlode.org"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cIf you logout you will be perm banned."));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&m-------------------------------"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
        }
    }

}