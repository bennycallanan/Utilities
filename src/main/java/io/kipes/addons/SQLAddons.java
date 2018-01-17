package io.kipes.addons;

import io.kipes.Utilities;
import io.kipes.addons.commands.*;
import io.kipes.addons.managers.BungeeManager;
import io.kipes.addons.managers.ReportManager;
import io.kipes.addons.managers.StaffChatManager;
import io.kipes.addons.managers.StaffJoinBroadcastManager;
import io.kipes.addons.mysql.DatabaseConnection;

public class SQLAddons {
    private Utilities plugin;

    private DatabaseConnection databaseConnection;
    private BungeeManager bungeeManager;
    private ReportManager reportManager;
    private StaffJoinBroadcastManager staffJoinBroadcastManager;
    private StaffChatManager staffChatManager;

    public SQLAddons(Utilities plugin) {
        this.plugin = plugin;
    }

    public void onPluginEnabled() {
        databaseConnection = new DatabaseConnection(plugin.getMysqlHost(), plugin.getMysqlUser(), plugin.getMysqlPass(), plugin.getMysqlDb());
        bungeeManager = new BungeeManager(plugin);
        reportManager = new ReportManager(plugin);
        staffJoinBroadcastManager = new StaffJoinBroadcastManager(plugin);
        staffChatManager = new StaffChatManager(plugin);

        plugin.getCommand("helpop").setExecutor(new HelpopCommand(plugin));
        plugin.getCommand("checkreports").setExecutor(new CheckReportsCommand(plugin));
        plugin.getCommand("report").setExecutor(new ReportCommand(plugin));
        plugin.getCommand("staffchat").setExecutor(new StaffchatCommand(plugin));
        plugin.getCommand("clearreports").setExecutor(new ClearReportsCommand(plugin));
        plugin.getCommand("togglereports").setExecutor(new ToggleReportsCommand(plugin));
    }

    public DatabaseConnection getDatabaseConnection() {
        return databaseConnection;
    }

    public BungeeManager getBungeeManager() {
        return bungeeManager;
    }

    public ReportManager getReportManager() {
        return reportManager;
    }

    public StaffJoinBroadcastManager getStaffJoinBroadcastManager() {
        return staffJoinBroadcastManager;
    }

    public StaffChatManager getStaffChatManager() {
        return staffChatManager;
    }
}
