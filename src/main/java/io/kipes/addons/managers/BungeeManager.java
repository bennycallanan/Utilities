package io.kipes.addons.managers;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.kipes.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("deprecation")
public class BungeeManager implements PluginMessageListener, Listener {
    private static final String BUNGEE = "BungeeCord";

    private Utilities plugin;

    private List<String> latestPlayerList = new ArrayList<>();
    private String serverName = null;

    public BungeeManager(Utilities plugin) {
        this.plugin = plugin;

        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, BUNGEE);
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, BUNGEE, this);

        Bukkit.getPluginManager().registerEvents(this, plugin);

        new BukkitRunnable() {
            public void run() {
                // Update the player list as we know it every 5 seconds
                updatePlayerList();
            }
        }.runTaskTimer(plugin, 20 * 5, 20 * 5);

        updatePlayerList();
        updateServerName();
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        new BukkitRunnable() {
            public void run() {
                updatePlayerList();
                updateServerName();
            }
        }.runTaskLater(plugin, 5);
    }

    private void updatePlayerList() {
        if (Bukkit.getServer().getOnlinePlayers().size() == 0)
            return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("PlayerList");
        out.writeUTF("ALL");

        Player p = Bukkit.getOnlinePlayers()[0];

        p.sendPluginMessage(plugin, BUNGEE, out.toByteArray());
    }

    private void updateServerName() {
        if (serverName != null || Bukkit.getServer().getOnlinePlayers().size() == 0)
            return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServer");

        Player p = Bukkit.getOnlinePlayers()[0];

        p.sendPluginMessage(plugin, BUNGEE, out.toByteArray());
    }

    // When permission is null, no permission is used.
    // When pool is null, the message will be sent to every player (Which has the correct permission).
    public void broadcastBungee(String msg, String permission, List<String> pool) {
        if (Bukkit.getServer().getOnlinePlayers().size() == 0)
            return;

        if (pool == null)
            pool = latestPlayerList;

        // If no permission, just send the message to every player in the network
        if (permission == null) {
            sendMessages(msg, pool);
            return;
        }

        List<String> players = new ArrayList<>();

        for (String pl : pool) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(pl);

            if (op.isOp()) {
                players.add(pl);
            } else {
				/*Map<String, Boolean> perms = Utilities.getPermissionsService().getPlayerPermissions(null, null, pl);
				if(perms.containsKey(permission) && perms.get(permission)) {
					// Player has the permission!
					players.add(pl);
				}*/
            }
        }

        sendMessages(msg, players);
    }

    // When players is null, the message will be sent to all online players.
    public void sendMessages(String msg, List<String> players) {
        if (Bukkit.getServer().getOnlinePlayers().size() == 0)
            return;

        if (players == null) players = latestPlayerList;

        Player p = Bukkit.getOnlinePlayers()[0];

        for (String pl : players) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Message");
            out.writeUTF(pl);
            out.writeUTF(msg);

            p.sendPluginMessage(plugin, BUNGEE, out.toByteArray());
        }
    }

    // Might be out of date by max 5 seconds.
    public List<String> getLatestPlayerList() {
        return Collections.unmodifiableList(latestPlayerList);
    }

    // Might return null when no server name is fetched yet.
    public String getServerName() {
        return serverName;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals(BUNGEE))
            return;

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        if (subChannel.equals("PlayerList")) {
            in.readUTF(); // "ALL" - the server name

            latestPlayerList.clear();

            String[] playerList = in.readUTF().split(", ");
            for (String plr : playerList)
                latestPlayerList.add(plr);
        } else if (subChannel.equals("GetServer")) {
            serverName = in.readUTF();
            plugin.getLogger().info("Got server name: " + serverName);
        }
    }
}
