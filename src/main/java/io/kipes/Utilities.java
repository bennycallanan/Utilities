package io.kipes;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.kipes.addons.SQLAddons;
import io.kipes.commands.*;
import io.kipes.data.DataStorage;
import io.kipes.database.MySQL;
import io.kipes.listeners.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class Utilities extends JavaPlugin implements PluginMessageListener, Listener {

    @Getter
    public static int Hub1 = 100;
    @Getter
    public static int Hub2 = 100;
    @Getter
    public static int Hub3 = 100;
    public static Plugin plugin;
    @Getter
    private static Utilities instance;
    @Getter
    private static DataStorage dataStorage;
    public HashMap<String, String> replym = new HashMap();
    HashMap<Player, Player> reply = new HashMap();
    private MySQL mysql;
    private SQLAddons SQLAddons;
    private String mysqlHost, mysqlUser, mysqlPass, mysqlDb;

    public static List<String> getStringList(String path) {
        if (Utilities.getInstance().getConfig().contains(path)) {
            ArrayList<String> lines = new ArrayList<String>();

            Utilities.getInstance();
            for (String text : Utilities.getStringList(path)) {
                lines.add(ChatColor.translateAlternateColorCodes('&', text));
            }

            return lines;
        }

        return Arrays.asList(new String[]{
                ChatColor.translateAlternateColorCodes('&', "&cString list not found: '" + path + "'")
        });
    }

    @EventHandler
    private static void FinalBukkitInterfaceType(ProjectileLaunchEvent InterfaceParameterByteArray) {
        if (InterfaceParameterByteArray.getEntity() instanceof ThrownPotion && InterfaceParameterByteArray.getEntity().getShooter() instanceof Player) {
            Player ByteArrayHashmapCollector = (Player) InterfaceParameterByteArray.getEntity().getShooter();
            ThrownPotion InterfaceParameterByteArray1 = (ThrownPotion) InterfaceParameterByteArray.getEntity();
            if (!ByteArrayHashmapCollector.isDead() && ByteArrayHashmapCollector.isSprinting()) {
                Iterator<?> var3 = InterfaceParameterByteArray1.getEffects().iterator();

                while (var3.hasNext()) {
                    if (((PotionEffect) var3.next()).getType().equals(PotionEffectType.HEAL)) {
                        ByteArrayHashmapCollector.setHealth(((Damageable) ByteArrayHashmapCollector).getHealth() + 3.0D > ((Damageable) ByteArrayHashmapCollector).getMaxHealth() ? ((Damageable) ByteArrayHashmapCollector).getMaxHealth() : ((Damageable) ByteArrayHashmapCollector).getHealth() + 3.0D);
                        InterfaceParameterByteArray1.setVelocity(InterfaceParameterByteArray1.getVelocity().setY(-2));
                    }
                }
            }
        }
    }

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
        getCommand("hub").setExecutor(this);

        dataStorage = new DataStorage();
        setup();
        saveDefaultConfig();
        instance = this;


        mysqlHost = getConfig().getString("mysql.host");
        mysqlUser = getConfig().getString("mysql.user");
        mysqlPass = getConfig().getString("mysql.pass");
        mysqlDb = getConfig().getString("mysql.db");

        mysql = new MySQL(mysqlHost, mysqlUser, mysqlPass, mysqlDb);
        mysql.createUsers();

        registerManager();

        Bukkit.getServer().getPluginManager().registerEvents(this, this);

        SQLAddons = new SQLAddons(this);
        SQLAddons.onPluginEnabled();
    }

    private void registerManager() {
        org.bukkit.plugin.PluginManager manager = Bukkit.getServer().getPluginManager();
        manager.registerEvents(new AsyncHitListener(), this);
        new ChatListener();
        new FreezeListener();
        new StaffModeListener();
        new StatTrackListener();
    }

    @Override
    public void onDisable() {
        instance = null;

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (dataStorage.isStaffModeActive(player)) {
                dataStorage.setStaffMode(player, false);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYour staff made has been disabled due to a server reload."));
            }
        }
    }

    private void setup() {
        setupFiles();
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getServer().getPluginCommand("panic").setExecutor(new PanicCommand());
        Bukkit.getServer().getPluginCommand("ping").setExecutor(new PingCommand());
        Bukkit.getServer().getPluginCommand("slowchat").setExecutor(new SlowChatCommand());
        Bukkit.getServer().getPluginCommand("mutechat").setExecutor(new MuteChatCommand());
        Bukkit.getServer().getPluginCommand("setslots").setExecutor(new SetSlotsCommand());
        Bukkit.getServer().getPluginCommand("clearchat").setExecutor(new ClearChatCommand());
        Bukkit.getServer().getPluginCommand("freeze").setExecutor(new FreezeCommand());
        Bukkit.getServer().getPluginCommand("list").setExecutor(new ListCommand());
        Bukkit.getServer().getPluginCommand("inspect").setExecutor(new InspectCommand());
        Bukkit.getServer().getPluginCommand("staffmode").setExecutor(new StaffModeCommand());
        Bukkit.getServer().getPluginCommand("vanish").setExecutor(new VanishCommand());
        Bukkit.getServer().getPluginCommand("whois").setExecutor(new WhoisCommand());
        Bukkit.getServer().getPluginCommand("gamemode").setExecutor(new GameModeCommand());
        long timeMillis = System.currentTimeMillis();
    }

    public String getStaffOnline() {
        String message = "";

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (player.hasPermission("hijix.donor")) {
                message += ChatColor.GREEN + player.getName() + ChatColor.YELLOW + ", " + ChatColor.GREEN;
            }
        }

        if (message.length() > 2) {
            message = message.substring(0, message.length() - 2);
        }

        if (message.length() == 0) {
            message = ChatColor.GREEN + "None";
        }

        return message;
    }

    public ConsoleCommandSender getConsoleSender() {
        return Bukkit.getServer().getConsoleSender();
    }

    public FileConfigurationOptions getFileConfigurationOptions() {
        return getConfig().options();
    }

    public String getString(String path) {
        if (getConfig().contains(path)) {
            return ChatColor.translateAlternateColorCodes('&', getConfig().getString(path));
        }

        return ChatColor.translateAlternateColorCodes('&', "&cString not found: '" + path + "'");
    }

    private void setupFiles() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }

            File file1 = new File(getDataFolder().getAbsolutePath(), "config.yml");
            File file2 = new File(getDataFolder().getAbsolutePath(), "fails.yml");
            File file3 = new File(getDataFolder().getAbsoluteFile(), "notes.yml");

            if (!file1.exists()) {
                getFileConfigurationOptions().copyDefaults(true);
                saveConfig();
            }

            if (!file2.exists()) {
                file2.createNewFile();
            }

            if (!file3.exists()) {
                file3.createNewFile();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void createFailedFile() {
        try {
            FileConfiguration fail = YamlConfiguration.loadConfiguration(new File(Utilities.getInstance().getDataFolder(), "fails.yml"));
            File failFile = new File(instance.getDataFolder(), "fails.yml");
            fail.save(failFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("hub")) {
            Player player = (Player) sender;
            this.getCountOnHubs(player);
            if (Hub1 < Hub2 && Hub1 < Hub3) {
                connectToHub(player, "Hub-01");
                return true;
            }

            if (Hub2 < Hub1 && Hub2 < Hub3) {
                connectToHub(player, "Hub-02");
                return true;
            }

            if (Hub3 < Hub1 && Hub3 < Hub2) {
                connectToHub(player, "Hub-03");
                return true;
            }

            connectToHub(player, "Hub-01");
        }

        return true;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        this.getCountOnHubs(e.getPlayer());
    }


    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (channel.equalsIgnoreCase("BungeeCord")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            String subchannel = in.readUTF();
            if (subchannel.equalsIgnoreCase("PlayerCount")) {
                String server = in.readUTF();
                int playerCount;
                if (server.equalsIgnoreCase("Hub-01")) {
                    playerCount = in.readInt();
                    setHub1(playerCount);
                }

                if (server.equalsIgnoreCase("Hub-02")) {
                    playerCount = in.readInt();
                    setHub2(playerCount);
                }

                if (server.equalsIgnoreCase("Hub-03")) {
                    playerCount = in.readInt();
                    setHub3(playerCount);
                }
            }
        }
    }

    public void connectToHub(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }

    public String leastConnectedHub() {
        return "";
    }

    public void setHub1(int amount) {
        Hub1 = amount;
    }

    public void setHub2(int amount) {
        Hub2 = amount;
    }

    public void setHub3(int amount) {
        Hub3 = amount;
    }

    public void getCountOnHubs(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("PlayerCount");
        out.writeUTF("ALL");
        player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }

    public SQLAddons getSQLAddon() {
        return SQLAddons;
    }

    public String getMysqlHost() {
        return mysqlHost;
    }

    public String getMysqlUser() {
        return mysqlUser;
    }

    public String getMysqlPass() {
        return mysqlPass;
    }

    public String getMysqlDb() {
        return mysqlDb;
    }
}