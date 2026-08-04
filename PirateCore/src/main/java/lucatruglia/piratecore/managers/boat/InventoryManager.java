package lucatruglia.piratecore.managers.boat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import lucatruglia.piratecore.utils.InventorySerializer;

import java.sql.*;
import java.util.UUID;

public class InventoryManager {

    private JavaPlugin plugin;
    private Connection connection;


    private static InventoryManager instance;

    public static InventoryManager getInstance() {
        if (instance == null) {
            instance = new InventoryManager();
        }
        return instance;
    }

    public void initialize(JavaPlugin plugin) {
        this.plugin = plugin;
        initDatabase();
        instance = this;
    }

    private void initDatabase() {
        try {
            // Crea o apre il file SQLite nella cartella del plugin
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/inventories.db");

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS player_inventories (" +
                                "uuid VARCHAR(36) PRIMARY KEY, " +
                                "inventory BLOB NOT NULL);");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Inizializza l'inventario per un player se non ne possiede già uno.
     * Dimensione standard di default: 27 slot (3 righe).
     */
    public void initializePlayer(Player player, int size, String title) {
        UUID uuid = player.getUniqueId();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String checkSql = "SELECT uuid FROM player_inventories WHERE uuid = ?;";
            try (PreparedStatement pstmt = connection.prepareStatement(checkSql)) {
                pstmt.setString(1, uuid.toString());
                ResultSet rs = pstmt.executeQuery();

                // Se non esiste ancora una riga per questo player, la creiamo con un inventario
                // vuoto
                if (!rs.next()) {
                    Inventory emptyInv = Bukkit.createInventory(null, size, title);
                    savePlayerInventory(player, emptyInv);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Salva l'inventario del player nel DB in modo asincrono.
     */
    public void savePlayerInventory(Player player, Inventory inv) {
        UUID uuid = player.getUniqueId();
        byte[] serializedData = InventorySerializer.toByteArray(inv.getContents());

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "INSERT INTO player_inventories (uuid, inventory) VALUES(?, ?) " +
                    "ON CONFLICT(uuid) DO UPDATE SET inventory = excluded.inventory;";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, uuid.toString());
                pstmt.setBytes(2, serializedData);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Recupera l'inventario del player dal DB (da eseguire in modo asincrono o al
     * join).
     */
    public Inventory getPlayerInventory(Player player, int size, String title) {
        UUID uuid = player.getUniqueId();
        Inventory inv = Bukkit.createInventory(null, size, title);

        String sql = "SELECT inventory FROM player_inventories WHERE uuid = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                byte[] bytes = rs.getBytes("inventory");
                ItemStack[] contents = InventorySerializer.fromByteArray(bytes);
                inv.setContents(contents);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return inv;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}