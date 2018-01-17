package io.kipes.listeners;

import io.kipes.Utilities;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Random;
import java.util.UUID;

public class StaffModeListener implements Listener {

    public StaffModeListener() {
        Bukkit.getPluginManager().registerEvents(this, Utilities.getInstance());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getClickedInventory() != null) {
            if (Utilities.getDataStorage().isStaffModeActive(player)) {
                event.setCancelled(true);
            }

            if (event.getInventory().getTitle().contains(ChatColor.translateAlternateColorCodes('&', "&eInspecting: "))) {
                Player inspected = Utilities.getDataStorage().getInspectedPlayer(player);

                if (event.getRawSlot() == 51) {
                    if (inspected != null) {
                        if (Utilities.getDataStorage().isFrozen(inspected)) {
                            Utilities.getDataStorage().setFreeze(player, inspected, false);
                        } else {
                            if (inspected.hasPermission("perm.admin") || inspected.isOp()) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can not freeze a staff member."));
                            } else {
                                Utilities.getDataStorage().setFreeze(player, inspected, true);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();

            if (Utilities.getDataStorage().isStaffModeActive(player) && player.hasPermission("perm.staff")) {
                ItemStack itemStack = player.getItemInHand();

                if (itemStack != null) {
                    if (itemStack.getType() == Material.INK_SACK) {
                        if (Bukkit.getServer().getOnlinePlayers().length > 1) {
                            Random random = new Random();

                            int size = random.nextInt(Bukkit.getServer().getOnlinePlayers().length);
                            Player online = (Player) Bukkit.getServer().getOnlinePlayers()[size];

                            if (online.equals(player)) {
                                random.nextInt();
                                onPlayerInteract(event);
                                return;
                            }

                            event.setCancelled(true);
                            player.teleport(online);
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou've been randomly teleported to &c" + online.getName() + "&e."));
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cCould not find players to teleport."));
                        }
                    } else if (itemStack.getType() == Material.FEATHER) {
                        if (Utilities.getDataStorage().isVanished(player)) {
                            Utilities.getDataStorage().setVanished(player, false);
                        } else {
                            Utilities.getDataStorage().setVanished(player, true);
                        }
                    } else if (itemStack.getType() == Material.IRON_FENCE) {
                        player.performCommand("checkreports");
                    }
                }
            }
        }
    }


    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() != null && event.getRightClicked() instanceof Player) {
            Player player = event.getPlayer();

            if (Utilities.getDataStorage().isStaffModeActive(player) && player.hasPermission("perm.staff")) {
                ItemStack itemStack = player.getItemInHand();

                if (itemStack != null) {
                    Player target = (Player) event.getRightClicked();

                    if (itemStack.getType() == Material.BOOK) {
                        if (target != null && !player.getName().equals(target.getName())) {
                            Utilities.getDataStorage().openInspectionMenu(player, target);
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou are now inspecting the inventory of &c" + target.getName() + "&e."));
                        }
                    } else if (itemStack.getType() == Material.CARROT_STICK) {
                        if (target != null && !player.getName().equals(target.getName())) {
                            if (player.getVehicle() != null) {
                                player.getVehicle().eject();
                            }

                            target.setPassenger(player);
                        }
                    } else if (itemStack.getType() == Material.ICE) {
                        if (target != null && !player.getName().equals(target.getName())) {
                            if (Utilities.getDataStorage().isFrozen(target)) {
                                Utilities.getDataStorage().setFreeze(player, target, false);
                            } else {
                                if (target.hasPermission("perm.staff") || target.isOp()) {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can not freeze a staff member."));
                                } else {
                                    Utilities.getDataStorage().setFreeze(player, target, true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (Utilities.getDataStorage().getVanished().size() > 0) {
            for (UUID uuid : Utilities.getDataStorage().getVanished()) {
                Player vanishedPlayer = Bukkit.getServer().getPlayer(uuid);
                if (vanishedPlayer != null) {
                    player.hidePlayer(vanishedPlayer);
                }
            }
        }
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (Utilities.getDataStorage().isStaffModeActive(player)) {
            Utilities.getDataStorage().setStaffMode(player, false);
            Utilities.getDataStorage().removeInspectedPlayer(player);

            PlayerInventory playerInventory = player.getInventory();
            playerInventory.setArmorContents(new ItemStack[]{new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
            playerInventory.clear();

            Utilities.getDataStorage().setVanished(player, false);

            if (Utilities.getDataStorage().hasPreviousInventory(player)) {
                Utilities.getDataStorage().loadInventory(player);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (Utilities.getDataStorage().isStaffModeActive(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();

        if (Utilities.getDataStorage().isVanished(player) || Utilities.getDataStorage().isStaffModeActive(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (Utilities.getDataStorage().isVanished(player) || Utilities.getDataStorage().isStaffModeActive(player)) {
            if (block != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (Utilities.getDataStorage().isVanished(player) || Utilities.getDataStorage().isStaffModeActive(player)) {
            if (block != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPearl(EntityDamageByEntityEvent e) {
        Player player = (Player) e.getEntity();
        Projectile projectile = (Projectile) e.getDamager();

        if (Utilities.getDataStorage().isVanished(player) || Utilities.getDataStorage().isStaffModeActive(player)) {

            projectile.isValid();
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();

            if (Utilities.getDataStorage().isVanished(player) || Utilities.getDataStorage().isStaffModeActive(player) && player.hasPermission("hijix.staff")) {
                event.setCancelled(true);
            }
        } else if (event.getEntity() instanceof LivingEntity && event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();

            if (Utilities.getDataStorage().isVanished(player) || Utilities.getDataStorage().isStaffModeActive(player) && player.hasPermission("hijix.staff")) {
                event.setCancelled(true);
            }
        }
    }

}