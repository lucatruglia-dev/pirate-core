package it.lucatruglia.piratecore.managers;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.plugin.java.JavaPlugin;

public class DatabaseManager {

    private static Connection connection;

    private DatabaseManager() {
    }

    public static void initialize(JavaPlugin plugin) {
        try {
            File dataFolder = plugin.getDataFolder();

            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }

            File databaseFile = new File(dataFolder, "levels.db");
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath());

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS player_levels ("
                        + "uuid TEXT PRIMARY KEY, "
                        + "name TEXT NOT NULL, "
                        + "total_xp INTEGER NOT NULL DEFAULT 0, "
                        + "level INTEGER NOT NULL DEFAULT 1"
                        + ")");
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to initialize SQLite database", exception);
        }
    }

    public static Connection getConnection() {
        if (connection == null) {
            throw new IllegalStateException("DatabaseManager has not been initialized");
        }

        return connection;
    }

    public static void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException exception) {
                throw new IllegalStateException("Unable to close SQLite database", exception);
            } finally {
                connection = null;
            }
        }
    }
}