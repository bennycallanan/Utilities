package io.kipes.addons.managers;

import io.kipes.Utilities;
import io.kipes.addons.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ReportManager implements Listener {
    private static final String TABLE_REPORTS = "reports";

    private Utilities plugin;
    private List<Report> reports = new ArrayList<>();

    private Map<Player, Integer> inGui = new HashMap<>();

    private boolean reportsEnabled = true;

    public ReportManager(Utilities plugin) {
        this.plugin = plugin;
        createReportsTable();

        new BukkitRunnable() {
            public void run() {
                updateReports();
            }
        }.runTaskTimerAsynchronously(plugin, 0, 20 * 30);

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent e) {
        inGui.remove(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        inGui.remove(e.getPlayer());
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        if (!inGui.containsKey(e.getWhoClicked()))
            return;

        e.setCancelled(true);

        if (!(e.getWhoClicked() instanceof Player))
            return;

        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType() == Material.AIR)
            return;

        Player p = (Player) e.getWhoClicked();

        if (item.getType() == Material.REDSTONE_BLOCK) {
            p.closeInventory();
        } else if (item.getType() == Material.REDSTONE_TORCH_ON) {
            int adj = 0;
            if (item.getItemMeta().getDisplayName().contains("<")) {
                // Previous page
                adj = -1;
            } else if (item.getItemMeta().getDisplayName().contains(">")) {
                // Next page
                adj = 1;
            }

            openGui(p, inGui.get(p) + adj);
        } else if (item.getType() == Material.SKULL_ITEM) {
            int id = Integer.parseInt(ChatColor.stripColor(item.getItemMeta().getDisplayName()).substring(1).split(" ")[0]);

            for (Player staff : Bukkit.getServer().getOnlinePlayers()) {
                if (staff.hasPermission("perm.staff")) {
                    if (staff.equals(p)) {
                        p.sendMessage(ChatColor.GREEN + "You have marked this report as complete.");
                    } else {
                        staff.sendMessage(ChatColor.RED + "Report Complete");
                        for (String s : item.getItemMeta().getLore()) {
                            staff.sendMessage(s);
                        }
                    }
                }

            }


            p.closeInventory();

            new BukkitRunnable() {
                public void run() {
                    removeReport(id);
                }
            }.runTaskAsynchronously(plugin);
        }
    }

    public void openGui(Player p, int page) {
        // Number of pages. Each page has 6 * 9 - 3 slots.
        // Full chest - 3 control items.
        int perPage = 6 * 9 - 3;
        int numPages = (int) Math.ceil((double) reports.size() / (double) perPage);

        if (page < 0) page = 0;
        if (page > numPages) page = numPages;

        Inventory inv = Bukkit.createInventory(null, 6 * 9, "Reports - Page " + (page + 1));

        inv.setItem(3 + 5 * 9, Utils.makeItem(Material.REDSTONE_TORCH_ON, 1, 0, ChatColor.RED.toString() + ChatColor.BOLD + "<<<"));
        inv.setItem(4 + 5 * 9, Utils.makeItem(Material.REDSTONE_BLOCK, 1, 0, ChatColor.DARK_RED + "Exit"));
        inv.setItem(5 + 5 * 9, Utils.makeItem(Material.REDSTONE_TORCH_ON, 1, 0, ChatColor.RED.toString() + ChatColor.BOLD + ">>>"));

        int offset = perPage * page;
        for (int i = 0; i < perPage; i++) {
            int repIndex = i + offset;
            if (reports.size() <= repIndex)
                break;

            Report rep = reports.get(repIndex);
            ItemStack is = Utils.getHead(rep.name);
            ItemMeta im = is.getItemMeta();
            im.setDisplayName(ChatColor.GOLD + "#" + rep.id + " " + rep.name);
            im.setLore(Arrays.asList(ChatColor.RED + "Name: " + ChatColor.YELLOW + rep.name,
                    ChatColor.RED + "Reporter: " + ChatColor.YELLOW + rep.reporter,
                    ChatColor.RED + "Reason: " + ChatColor.YELLOW + rep.reason,
                    ChatColor.RED + "ID: " + ChatColor.YELLOW + "#" + rep.id,
                    ChatColor.RED + "Server: " + ChatColor.YELLOW + plugin.getSQLAddon().getBungeeManager().getServerName()));
            is.setItemMeta(im);

            inv.addItem(is);
        }

        p.openInventory(inv);

        if (inGui.containsKey(p))
            inGui.remove(p);

        inGui.put(p, page);
    }

    public Report report(String who, String reporter, String reason) {
        try {
            plugin.getSQLAddon().getDatabaseConnection().query("INSERT INTO " + TABLE_REPORTS + "(name,reporter,reason) VALUES (?,?,?)",
                    who, reporter, reason);

            ResultSet res = plugin.getSQLAddon().getDatabaseConnection().queryResults("SELECT id FROM " + TABLE_REPORTS + " WHERE name=? AND reporter=? AND reason=?",
                    who, reporter, reason);
            res.next();

            int id = res.getInt("id");
            plugin.getLogger().info("Report submitted with ID #" + id);

            String broadcastMsg = ChatColor.RED + who + " has been reported by " + reporter + " for " + reason + " [" + plugin.getSQLAddon().getBungeeManager().getServerName() + "]";
            plugin.getSQLAddon().getBungeeManager().broadcastBungee(broadcastMsg, "perm.staff", null);

            Report rep = new Report(id, who, reporter, reason);
            reports.add(rep);

            return rep;
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().warning("Could not report player.");
        }

        return null;
    }

    public void removeReport(int id) {
        try {
            plugin.getSQLAddon().getDatabaseConnection().query("DELETE FROM " + TABLE_REPORTS + " WHERE id=?",
                    String.valueOf(id));

            for (int i = 0; i < reports.size(); i++) {
                if (reports.get(i).id == id)
                    reports.remove(i--);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().warning("Could not remove report #" + id + ".");
        }
    }

    public void updateReports() {
        reports.clear();

        try {
            ResultSet res = plugin.getSQLAddon().getDatabaseConnection().queryResults("SELECT * FROM " + TABLE_REPORTS + " WHERE 1");
            while (res.next()) {
                reports.add(new Report(res));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().warning("Could not fetch reports.");
        }
    }

    public void clearReports() {
        try {
            plugin.getSQLAddon().getDatabaseConnection().query("DELETE FROM " + TABLE_REPORTS + " WHERE 1");

            reports.clear();
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().warning("Could not clear reports.");
        }
    }

    public List<Report> getReports() {
        return Collections.unmodifiableList(reports);
    }

    private void createReportsTable() {
        try {
            plugin.getSQLAddon().getDatabaseConnection().query("SELECT * FROM " + TABLE_REPORTS + " LIMIT 1");
            plugin.getLogger().info("Reports table does exist.");
        } catch (SQLException e) {
            plugin.getLogger().info("Reports table does not exist. Making one..");

            try {
                plugin.getSQLAddon().getDatabaseConnection().query("CREATE TABLE " + TABLE_REPORTS + " ( `id` INT NOT NULL AUTO_INCREMENT , `name` TINYTEXT NOT NULL , `reporter` TINYTEXT NOT NULL , `reason` TINYTEXT NOT NULL , PRIMARY KEY (`id`));");
            } catch (SQLException e1) {
                e1.printStackTrace();
                plugin.getLogger().warning("Could not make reports table.");
            }
        }
    }

    public boolean isReportsEnabled() {
        return reportsEnabled;
    }

    public void setReportsEnabled(boolean reportsEnabled) {
        this.reportsEnabled = reportsEnabled;
    }

    public class Report {
        public final int id;
        public final String name;
        public final String reporter;
        public final String reason;

        private Report(ResultSet res) throws SQLException {
            this.id = res.getInt("id");
            this.name = res.getString("name");
            this.reporter = res.getString("reporter");
            this.reason = res.getString("reason");
        }

        private Report(int id, String name, String reporter, String reason) {
            this.id = id;
            this.name = name;
            this.reporter = reporter;
            this.reason = reason;
        }

        @Override
        public String toString() {
            return "[id=" + id + ",name=" + name + ",reporter=" + reporter + ",reason=" + reason + "]";
        }
    }
}
