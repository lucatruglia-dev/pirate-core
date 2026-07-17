package lucatruglia.piratecore.managers;

import lucatruglia.piratecore.PirateCore;
import lucatruglia.piratecore.models.PlayerData;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;
import java.util.Optional;
import java.util.UUID;

/**
 * Gestisce la connessione SQLite e le operazioni CRUD per i dati dei player.
 * 
 * Inizializzazione (in PirateCore.onEnable()):
 * DatabaseManager.getInstance().init(this);
 * 
 * Chiusura (in PirateCore.onDisable()):
 * DatabaseManager.getInstance().close();
 */
public class DatabaseManager {

    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    // ---------------------------------------------------------------
    // Init / Shutdown
    // ---------------------------------------------------------------

    /**
     * Crea la cartella dati e il file .db, apre la connessione
     * e crea la tabella se non esiste.
     */
    public void initialize() {
        JavaPlugin plugin = PirateCore.get();
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }

            File dbFile = new File(dataFolder, "players.db");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());

            this.createTables();
        } catch (SQLException e) {
            throw new IllegalStateException("Impossibile inizializzare il database SQLite", e);
        }
    }

    /**
     * Chiude la connessione al database.
     */
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                connection = null;
            }
        }
    }

    // ---------------------------------------------------------------
    // Internals
    // ---------------------------------------------------------------

    private void ensureInitialized() {
        if (connection == null) {
            throw new IllegalStateException(
                    "DatabaseManager non inizializzato. Chiamare init() prima di usare i metodi CRUD.");
        }
    }

    // ---------------------------------------------------------------
    // Schema
    // ---------------------------------------------------------------

    private void createTables() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS player_data (
                    uuid       TEXT PRIMARY KEY,
                    name       TEXT NOT NULL,
                    total_xp   INTEGER NOT NULL DEFAULT 0,
                    level      INTEGER NOT NULL DEFAULT 1
                )
                """;

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    // ---------------------------------------------------------------
    // CRUD
    // ---------------------------------------------------------------

    /**
     * Carica i dati di un player dal database tramite UUID.
     * 
     * @param uuid UUID del player
     * @return Optional contenente PlayerData, vuoto se non trovato
     */
    public Optional<PlayerData> loadPlayer(UUID uuid) {
        this.ensureInitialized();

        String sql = "SELECT uuid, name, total_xp, level FROM player_data WHERE uuid = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new PlayerData(
                            UUID.fromString(rs.getString("uuid")),
                            rs.getString("name"),
                            rs.getLong("total_xp"),
                            rs.getInt("level")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Carica i dati di un player dal database tramite nome.
     * Utile per i placeholder specifici per player (es. %piratecore_level_Kcalu_%).
     * 
     * @param name Nome del player
     * @return Optional contenente PlayerData, vuoto se non trovato
     */
    public Optional<PlayerData> loadPlayerByName(String name) {
        this.ensureInitialized();

        String sql = "SELECT uuid, name, total_xp, level FROM player_data WHERE name = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new PlayerData(
                            UUID.fromString(rs.getString("uuid")),
                            rs.getString("name"),
                            rs.getLong("total_xp"),
                            rs.getInt("level")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Salva (INSERT o UPDATE) i dati di un player.
     * 
     * @param data PlayerData da salvare
     */
    public void savePlayer(PlayerData data) {
        this.ensureInitialized();

        String sql = """
                INSERT INTO player_data (uuid, name, total_xp, level)
                VALUES (?, ?, ?, ?)
                ON CONFLICT(uuid) DO UPDATE SET
                    name     = excluded.name,
                    total_xp = excluded.total_xp,
                    level    = excluded.level
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, data.uuid().toString());
            ps.setString(2, data.name());
            ps.setLong(3, data.totalXp());
            ps.setInt(4, data.level());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Aggiorna solo il nome di un player (utile se il player cambia nickname).
     */
    public void updateName(UUID uuid, String newName) {
        this.ensureInitialized();

        String sql = "UPDATE player_data SET name = ? WHERE uuid = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Verifica se un player esiste già nel database.
     *
     * @param uuid UUID del player
     * @return true se il player ha una riga nella tabella player_data
     */
    public boolean playerExists(UUID uuid) {
        this.ensureInitialized();

        String sql = "SELECT COUNT(*) FROM player_data WHERE uuid = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina un player dal database tramite UUID.
     * 
     * @param uuid UUID del player da eliminare
     * @return true se il player è stato eliminato con successo, false se non
     *         esisteva o c'è stato un errore
     */
    public boolean deletePlayer(UUID uuid) {
        this.ensureInitialized();

        String sql = "DELETE FROM player_data WHERE uuid = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            } else {
                // Nessuna riga eliminata (player non trovato)
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
