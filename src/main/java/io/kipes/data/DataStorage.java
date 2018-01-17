package io.kipes.data;

import io.kipes.Utilities;
import io.kipes.scheduler.AlertTask;
import io.kipes.useful.TimeUtils;
import io.kipes.utils.InvUtils;
import io.kipes.utils.PlayerInv;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class DataStorage {

    private Long muteChatMillis;

    private boolean slowChatEnabled;
    private int slowChatSeconds;
    private Map<UUID, Long> slowedPlayers;

    private Set<UUID> staffChat;
    private Set<UUID> staffMode;
    private Set<UUID> vanished;

    private Map<UUID, UUID> inspectedPlayer;
    private Map<UUID, PlayerInv> storedInventories;

    private Set<UUID> frozen;
    private Map<UUID, AlertTask> alertTasks;

    private Map<UUID, UUID> lastReplied;

    public DataStorage() {
        muteChatMillis = 0L;

        slowChatEnabled = false;
        slowChatSeconds = 5;
        slowedPlayers = new HashMap<>();

        staffChat = new HashSet<>();
        staffMode = new HashSet<>();
        vanished = new HashSet<>();

        inspectedPlayer = new HashMap<>();
        storedInventories = new HashMap<>();

        frozen = new HashSet<>();
        alertTasks = new HashMap<>();

        lastReplied = new HashMap<>();
    }

    public UUID getLastReplied(Player player) {
        if (lastReplied.containsKey(player.getUniqueId())) {
            return lastReplied.get(player.getUniqueId());
        }

        return null;
    }

    public void setLastReplied(Player player, Player replied) {
        lastReplied.put(player.getUniqueId(), replied.getUniqueId());
    }

    public boolean isChatSlowed() {
        return this.slowChatEnabled;
    }

    public void setChatSlowed(boolean status) {
        this.slowChatEnabled = status;
    }

    public boolean isSlowed(Player player) {
        return slowedPlayers.containsKey(player.getUniqueId());
    }

    public void setSlowed(Player player) {
        this.slowedPlayers.put(player.getUniqueId(), System.currentTimeMillis() + (1000 * slowChatSeconds));

        new BukkitRunnable() {
            public void run() {
                slowedPlayers.remove(player.getUniqueId());
            }
        }.runTaskLater(Utilities.getInstance(), 20L * slowChatSeconds);
    }

    public int getSlowTime(Player player) {
        return (int) ((this.slowedPlayers.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000);
    }

    public void setMuteChatMillis(long value) {
        muteChatMillis = (System.currentTimeMillis() + value);
    }

    public boolean isChatMuted() {
        return getMillisecondLeft() > 0L;
    }

    public long getMillisecondLeft() {
        return muteChatMillis - System.currentTimeMillis();
    }

    public boolean isStaffChatActive(Player player) {
        return staffChat.contains(player.getUniqueId());
    }

    public void setStaffChat(Player player, boolean status) {
        if (status) {
            staffChat.add(player.getUniqueId());
        } else {
            staffChat.remove(player.getUniqueId());
        }
    }

    public Player getInspectedPlayer(Player player) {
        return Bukkit.getServer().getPlayer(inspectedPlayer.get(player.getUniqueId()));
    }

    public void removeInspectedPlayer(Player player) {
        inspectedPlayer.remove(player.getUniqueId());
    }

    public boolean isVanished(Player player) {
        return vanished.contains(player.getUniqueId());
    }

    public Set<UUID> getVanished() {
        return vanished;
    }

    public boolean isStaffModeActive(Player player) {
        return staffMode.contains(player.getUniqueId());
    }

    public boolean hasPreviousInventory(Player player) {
        return storedInventories.containsKey(player.getUniqueId());
    }

    public void saveInventory(Player player) {
        storedInventories.put(player.getUniqueId(), InvUtils.playerInventoryFromPlayer(player));
    }

    public void loadInventory(Player player) {
        player.getInventory().setContents(storedInventories.get(player.getUniqueId()).getContents());
        player.getInventory().setArmorContents(storedInventories.get(player.getUniqueId()).getArmorContents());
        player.updateInventory();

        storedInventories.remove(player.getUniqueId());
    }

    public void setStaffMode(Player player, boolean status) {
        if (status) {
            if (player.hasPermission("perm.staff")) {
                staffMode.add(player.getUniqueId());

                saveInventory(player);

                PlayerInventory playerInventory = player.getInventory();
                playerInventory.setArmorContents(new ItemStack[]{
                        new ItemStack(Material.AIR),
                        new ItemStack(Material.AIR),
                        new ItemStack(Material.AIR),
                        new ItemStack(Material.AIR)
                });
                playerInventory.clear();

                setItems(player);
                setVanished(player, true);

                player.updateInventory();

                if (player.getGameMode() == GameMode.SURVIVAL) {
                    player.setGameMode(GameMode.CREATIVE);
                }
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou do not have permissions to enable the staffmode."));
            }
        } else {
            staffMode.remove(player.getUniqueId());

            PlayerInventory playerInventory = player.getInventory();
            playerInventory.setArmorContents(new ItemStack[]{
                    new ItemStack(Material.AIR),
                    new ItemStack(Material.AIR),
                    new ItemStack(Material.AIR),
                    new ItemStack(Material.AIR)
            });
            playerInventory.clear();

            setVanished(player, false);

            if (hasPreviousInventory(player)) {
                loadInventory(player);
            }

            player.updateInventory();

            if (!player.hasPermission("perm.admin") && player.getGameMode() == GameMode.CREATIVE) {
                player.setGameMode(GameMode.SURVIVAL);
            }
        }
    }

    public void setVanished(Player player, boolean status) {
        if (status) {
            vanished.add(player.getUniqueId());

            for (Player online : Bukkit.getServer().getOnlinePlayers()) {
                if (!online.hasPermission("perm.staff")) {
                    online.hidePlayer(player);
                }
            }

            if (isStaffModeActive(player)) {
                PlayerInventory playerInventory = player.getInventory();
                playerInventory.setItem(2, getVanishItemFor(player));
            }
        } else {
            vanished.remove(player.getUniqueId());

            for (Player online : Bukkit.getServer().getOnlinePlayers()) {
                online.showPlayer(player);
            }

            if (isStaffModeActive(player)) {
                PlayerInventory playerInventory = player.getInventory();
                playerInventory.setItem(2, getVanishItemFor(player));
            }
        }
    }

    public void setItems(Player player) {
        PlayerInventory playerInventory = player.getInventory();

        ItemStack inspect = new ItemStack(Material.BOOK, 1);
        ItemMeta inspectMeta = inspect.getItemMeta();
        inspectMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aInventory Inspector"));
        inspect.setItemMeta(inspectMeta);

        ItemStack randomtp = new ItemStack(Material.INK_SACK, 1, (short) 12);
        ItemMeta randomtpMeta = randomtp.getItemMeta();
        randomtpMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aRandom Teleport"));
        randomtp.setItemMeta(randomtpMeta);

        ItemStack freezer = new ItemStack(Material.ICE, 1);
        ItemMeta freezerMeta = freezer.getItemMeta();
        freezerMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aFreezer"));
        freezer.setItemMeta(freezerMeta);

        ItemStack stafflist = new ItemStack(Material.COMPASS, 1);
        ItemMeta stafflistMeta = stafflist.getItemMeta();
        stafflistMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aTeleport"));
        stafflist.setItemMeta(stafflistMeta);

        ItemStack checkreports = new ItemStack(Material.IRON_FENCE, 1);
        ItemMeta checkreportsMeta = checkreports.getItemMeta();
        checkreportsMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aReports"));
        checkreports.setItemMeta(checkreportsMeta);

	/*	ItemStack compass = new ItemStack(Material.COMPASS, 1);
		ItemMeta compassMeta = compass.getItemMeta();
		compassMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&bTeleport Compass"));
		compassMeta.setLore(new ColorUtils().translateFromArray(Arrays.asList("&7Right click block: Move through", "&7Left click: Move to block in line of sight")));
		compass.setItemMeta(compassMeta);

		ItemStack book = new ItemStack(Material.BOOK, 1);
		ItemMeta bookMeta = book.getItemMeta();
		bookMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&bInspection Tool"));
		bookMeta.setLore(new ColorUtils().translateFromArray(Collections.singletonList("&7Right click player to inspect inventory")));
		book.setItemMeta(bookMeta);

		ItemStack blazeRod = new ItemStack(Material.ICE, 1);
		ItemMeta blazeRodMeta = blazeRod.getItemMeta();
		blazeRodMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&bFreeze Player"));
		blazeRodMeta.setLore(new ColorUtils().translateFromArray(Collections.singletonList("&7Right click player to update freeze status")));
		blazeRod.setItemMeta(blazeRodMeta);

		ItemStack carpet = new ItemStack(Material.WOOD_AXE);
		ItemMeta carpetMeta = carpet.getItemMeta();
		carpetMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&bWorld Edit"));
		carpetMeta.setLore(new ColorUtils().translateFromArray(Collections.singletonList("&7The normal worldedit wand")));
		carpet.setItemMeta(carpetMeta);

		ItemStack record10 = new ItemStack(Material.RECORD_10, 1);
		ItemMeta record10Meta = record10.getItemMeta();
		record10Meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&bRandom Teleportation"));
		record10Meta.setLore(new ColorUtils().translateFromArray(Collections.singletonList("&7Right click to teleport to a random player")));
		record10.setItemMeta(record10Meta); */

        playerInventory.setItem(0, inspect);
        playerInventory.setItem(1, randomtp);
        playerInventory.setItem(6, freezer);
        playerInventory.setItem(7, stafflist);
        playerInventory.setItem(8, checkreports);
		
	/*	playerInventory.setItem(0, compass);
		playerInventory.setItem(1, book);
		playerInventory.setItem(4, blazeRod);
		playerInventory.setItem(2, carpet);
		playerInventory.setItem(7, getVanishItemFor(player));
		playerInventory.setItem(8, record10); */
    }

    private ItemStack getVanishItemFor(Player player) {
        ItemStack inkSack = null;

        if (isVanished(player)) {
            inkSack = new ItemStack(Material.FEATHER, 1);
            ItemMeta inkSackMeta = inkSack.getItemMeta();
            inkSackMeta.addEnchant(Enchantment.DURABILITY, 10, true);
            inkSackMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aVanish &7(On)"));
            inkSack.setItemMeta(inkSackMeta);
        } else {
            inkSack = new ItemStack(Material.FEATHER, 1);
            ItemMeta inkSackMeta = inkSack.getItemMeta();
            inkSackMeta.addEnchant(Enchantment.DURABILITY, 10, true);
            inkSackMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aVanish &7(Off)"));
            inkSack.setItemMeta(inkSackMeta);
        }

        return inkSack;
    }

    public void openInspectionMenu(Player player, Player target) {
        Inventory inventory = Bukkit.getServer().createInventory(null, 9 * 6, ChatColor.translateAlternateColorCodes('&', "&eInspecting: &c" + target.getName()));

        new BukkitRunnable() {
            @Override
            public void run() {
                inspectedPlayer.put(player.getUniqueId(), target.getUniqueId());

                PlayerInventory playerInventory = target.getInventory();

                ItemStack speckledMelon = new ItemStack(Material.SPECKLED_MELON, (int) ((Damageable) target).getHealth());
                ItemMeta speckledMelonMeta = speckledMelon.getItemMeta();
                speckledMelonMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aHealth"));
                speckledMelon.setItemMeta(speckledMelonMeta);

                ItemStack cookedBeef = new ItemStack(Material.COOKED_BEEF, target.getFoodLevel());
                ItemMeta cookedBeefMeta = cookedBeef.getItemMeta();

                cookedBeefMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aHunger"));
                cookedBeef.setItemMeta(cookedBeefMeta);

                ItemStack brewingStand = new ItemStack(Material.BREWING_STAND_ITEM, target.getPlayer().getActivePotionEffects().size());
                ItemMeta brewingStandMeta = brewingStand.getItemMeta();

                brewingStandMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aActive Potion Effects"));
                ArrayList<String> brewingStandLore = new ArrayList<>();

                for (PotionEffect potionEffect : target.getPlayer().getActivePotionEffects()) {
                    String effectName = potionEffect.getType().getName();
                    int effectLevel = potionEffect.getAmplifier();
                    effectLevel++;
                    brewingStandLore.add(ChatColor.translateAlternateColorCodes('&', "&e" + WordUtils.capitalizeFully(effectName).replace("_", " ") + " " + effectLevel + "&7: &c" + TimeUtils.IntegerCountdown.setFormat(potionEffect.getDuration() / 20)));
                }

                brewingStandMeta.setLore(brewingStandLore);
                brewingStand.setItemMeta(brewingStandMeta);

                ItemStack compass = new ItemStack(Material.COMPASS, 1);
                ItemMeta compassMeta = compass.getItemMeta();


                ItemStack ice = new ItemStack(Material.ICE, 1);
                ItemMeta iceMeta = ice.getItemMeta();

                if (Utilities.getDataStorage().isFrozen(target)) {
                    iceMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aFrozen&7: &aTrue"));
                } else {
                    iceMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aFrozen&7: &cFalse"));
                }

                ice.setItemMeta(iceMeta);

                ItemStack orangeGlassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1);

                inventory.setContents(playerInventory.getContents());
                inventory.setItem(36, playerInventory.getHelmet());
                inventory.setItem(37, playerInventory.getChestplate());
                inventory.setItem(38, playerInventory.getLeggings());
                inventory.setItem(39, playerInventory.getBoots());
                inventory.setItem(40, playerInventory.getItemInHand());

                for (int i = 41; i <= 46; i++) {
                    inventory.setItem(i, orangeGlassPane);
                }

                inventory.setItem(47, speckledMelon);
                inventory.setItem(48, cookedBeef);
                inventory.setItem(49, brewingStand);
                inventory.setItem(50, compass);
                inventory.setItem(51, ice);

                for (int i = 52; i <= 53; i++) {
                    inventory.setItem(i, orangeGlassPane);
                }

                if (!player.getOpenInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&', "&eInspecting: &c" + target.getName()))) {
                    cancel();
                    inspectedPlayer.remove(player.getUniqueId());
                }
            }
        }.runTaskTimer(Utilities.getInstance(), 0L, 5L);

        player.openInventory(inventory);
    }

    public void removeFreeze(Player player) {
        this.frozen.remove(player.getUniqueId());
    }

    public void setFreeze(CommandSender sender, Player target, boolean status) {
        if (status) {
            AlertTask alertTask = new AlertTask(target);
            alertTask.runTaskTimerAsynchronously(Utilities.getInstance(), 0L, 5 * 20);

            frozen.add(target.getUniqueId());
            alertTasks.put(target.getUniqueId(), alertTask);

            if (sender instanceof Player) {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.RED + "Staff Only: " + target.getName() + " is now frozen, frozen by " + sender.getName() + "."));

                for (Player staff : Bukkit.getServer().getOnlinePlayers()) {
                    if (staff.hasPermission("perm.staff")) {
                        if (staff.equals(sender)) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.RED + target.getName() + " is now frozen."));
                        } else {
                            staff.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.RED + "Staff Only: " + target.getName() + " is now frozen, frozen by " + sender.getName() + "."));
                        }
                    }
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.RED + target.getName() + " is now frozen."));

                for (Player staff : Bukkit.getServer().getOnlinePlayers()) {
                    if (staff.hasPermission("perm.staff")) {
                        staff.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.RED + "Staff Only: " + target.getName() + " is now frozen, frozen by " + sender.getName() + "."));
                    }
                }
            }
        } else {
            alertTasks.get(target.getUniqueId()).cancel();
            alertTasks.remove(target.getUniqueId());
            frozen.remove(target.getUniqueId());

            target.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou are no longer frozen."));

            if (sender instanceof Player) {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.RED + "Staff Only: " + target.getName() + " is no longer frozen, removed by " + sender.getName() + "."));

                for (Player staff : Bukkit.getServer().getOnlinePlayers()) {
                    if (staff.hasPermission("perm.staff")) {
                        if (staff.equals(sender)) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.RED + target.getName() + " is no longer frozen."));
                        } else {
                            staff.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.RED + "Staff Only: " + target.getName() + " is no longer frozen, removed by " + sender.getName() + "."));
                        }
                    }
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.RED + target.getName() + " is no longer frozen."));

                for (Player staff : Bukkit.getServer().getOnlinePlayers()) {
                    if (staff.hasPermission("perm.staff")) {
                        staff.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.RED + "Staff Only: " + target.getName() + " is no longer frozen, removed by " + sender.getName() + "."));
                    }
                }
            }
        }
    }

    public boolean isFrozen(Player player) {
        return frozen.contains(player.getUniqueId());
    }

    public AlertTask getAlertTask(Player player) {
        return alertTasks.get(player.getUniqueId());
    }

}