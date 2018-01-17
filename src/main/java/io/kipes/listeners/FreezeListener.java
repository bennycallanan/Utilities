package io.kipes.listeners;

import io.kipes.Utilities;
import io.kipes.scheduler.AlertTask;
import io.kipes.useful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;

public class FreezeListener implements Listener {

    public FreezeListener() {
        Bukkit.getPluginManager().registerEvents(this, Utilities.getInstance());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Location from = event.getFrom();
        Location to = event.getTo();

        if (Utilities.getDataStorage().isFrozen(player)) {
            // from.getBlockY() != to.getBlockY()
            if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
                event.setTo(from);
            }
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();

            if (Utilities.getDataStorage().isFrozen(player)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can not interact with that item while you are frozen."));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (event.getMessage().startsWith("/perms")) {
            if (!(event.getPlayer().hasPermission("perm.staff") && event.getPlayer().isOp())) {
                event.setCancelled(true);
            }
        }

        if (Utilities.getDataStorage().isFrozen(player)) {
            if (!event.getMessage().startsWith("/helpop")
                    && !event.getMessage().startsWith("/faction chat")
                    && !event.getMessage().startsWith("/fac chat")
                    && !event.getMessage().startsWith("/f chat")
                    && !event.getMessage().startsWith("/faction c")
                    && !event.getMessage().startsWith("/fac c")
                    && !event.getMessage().startsWith("/f c")
                    && !event.getMessage().startsWith("/helpop")
                    && !event.getMessage().startsWith("/request")
                    && !event.getMessage().startsWith("/msg")
                    && !event.getMessage().startsWith("/message")
                    && !event.getMessage().startsWith("/reply")) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can not use commands while you are frozen."));
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (event.getBlock() != null) {
            if (Utilities.getDataStorage().isFrozen(player)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can not place blocks while you are frozen."));
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (event.getBlock() != null) {
            if (Utilities.getDataStorage().isFrozen(player)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can not break blocks while you are frozen."));
            }
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();

        if (Utilities.getDataStorage().isFrozen(player)) {
            Utilities.getDataStorage().getAlertTask(player).cancel();
            Utilities.getDataStorage().removeFreeze(player);
        }
    }

    public Player getDamager(Entity entity) {
        if (entity instanceof Player) {
            return (Player) entity;
        }

        if (entity instanceof Projectile) {
            Projectile projectile = (Projectile) entity;

            if (projectile.getShooter() != null && projectile.getShooter() instanceof Player) {
                return (Player) projectile.getShooter();
            }
        }
        return null;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player damager = getDamager(event.getDamager());
        Player damaged = getDamager(event.getEntity());

        if (damager != null && damaged != null && damaged != damager) {
            if (Utilities.getDataStorage().isFrozen(damager)) {
                damager.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can not attack players while frozen."));
                event.setCancelled(true);
            }

            if (Utilities.getDataStorage().isFrozen(damaged)) {
                damager.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can not attack " + damaged.getName() + " because he is currently frozen."));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (Utilities.getDataStorage().isFrozen(player)) {
            for (Player staff : Bukkit.getServer().getOnlinePlayers()) {
                if (staff.hasPermission("perm.staff") || staff.isOp()) {
                    FancyMessage fancyMessage = new FancyMessage("");
                    fancyMessage.then(ChatColor.translateAlternateColorCodes('&', "&7[Alert] " + "&6" + player.getName() + " &chas disconnected while frozen. "));
                    fancyMessage.then(ChatColor.translateAlternateColorCodes('&', "&7(Click here to ban)"));
                    fancyMessage.tooltip(ChatColor.translateAlternateColorCodes('&', "&aClick to permanently ban " + player.getName()));
                    fancyMessage.command("/ban " + player.getName() + " Refusal to Screensharing.");
                    fancyMessage.send(staff);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (Utilities.getDataStorage().isFrozen(player)) {
            AlertTask alertTask = new AlertTask(player);
            alertTask.runTaskTimerAsynchronously(Utilities.getInstance(), 0L, 5 * 20);

            for (Player staff : Bukkit.getServer().getOnlinePlayers()) {
                if (staff.hasPermission("perm.staff")) {
                    FancyMessage fancyMessage = new FancyMessage("");
                    fancyMessage.then(ChatColor.translateAlternateColorCodes('&', "&7[Alert] " + "&6" + player.getName() + " &chas joined but he is frozen. "));
                    fancyMessage.then(ChatColor.translateAlternateColorCodes('&', "&7(Click here to ban)"));
                    fancyMessage.tooltip(ChatColor.translateAlternateColorCodes('&', "&aClick to permanently ban " + player.getName()));
                    fancyMessage.command("/ban " + player.getName() + " Refusal to Screensharing.");
                    fancyMessage.send(staff);
                }
            }
        }
    }

}