package io.kipes.addons.mysql;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnection {
    private MysqlDataSource dataSource;
    private Connection conn;
    private boolean keepConnected;

    public DatabaseConnection(String host, String user, String pass, String db, boolean keepConnected) {
        dataSource = new MysqlDataSource();
        dataSource.setServerName(host);
        dataSource.setUser(user);
        dataSource.setPassword(pass);
        dataSource.setDatabaseName(db);

        this.keepConnected = keepConnected;
    }

    public DatabaseConnection(String host, String user, String pass, String db) {
        this(host, user, pass, db, true);
    }

    // Use '?' to define a parameter (Will be replaced in order)
    public boolean query(String query, Object... parameters) throws SQLException {
        refreshConnection();

        PreparedStatement ps = conn.prepareStatement(query);

        for (int i = 0; i < parameters.length; i++) {
            ps.setObject(i + 1, parameters[i]);
        }

        if (!keepConnected)
            disconnect();
        return ps.execute();
    }

    // Returns a ResultSet, or null if an error occurred
    public ResultSet queryResults(String query, Object... parameters) throws SQLException {
        refreshConnection();

        PreparedStatement ps = conn.prepareStatement(query);

        for (int i = 0; i < parameters.length; i++) {
            ps.setObject(i + 1, parameters[i]);
        }

        if (!keepConnected)
            disconnect();
        return ps.executeQuery();
    }

    public void disconnect() {
        try {
            conn.close();
        } catch (Exception e) {
        }
    }

    private void refreshConnection() {
        try {
            if (conn == null || conn.isClosed() || !conn.isValid(1)) {
                // Start new connection
                try {
                    conn.close();
                } catch (Exception e) {
                }

                conn = dataSource.getConnection();
            } // else, connection is still valid
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
