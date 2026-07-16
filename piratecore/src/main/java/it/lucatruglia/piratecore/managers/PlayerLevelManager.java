package it.lucatruglia.piratecore.managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

public class PlayerLevelManager {

    private static PlayerLevelManager instance;

    private PlayerLevelManager() {
    }

    public static PlayerLevelManager getInstance() {
        if (instance == null) {
            instance = new PlayerLevelManager();
        }

        return instance;
    }

    public PlayerLevelData ensurePlayer(OfflinePlayer player) {
        return this.getOrCreate(player.getUniqueId(), player.getName());
    }

    public PlayerLevelData getPlayer(OfflinePlayer player) {
        return this.getOrCreate(player.getUniqueId(), player.getName());
    }

    public PlayerLevelData addXp(OfflinePlayer player, long amount) {
        PlayerLevelData current = this.getOrCreate(player.getUniqueId(), player.getName());
        long updatedTotalXp = Math.max(0L, current.totalXp() + amount);
        int updatedLevel = LevelManager.calculateLevel(updatedTotalXp);
        return this.save(player.getUniqueId(), player.getName(), updatedTotalXp, updatedLevel);
    }

    public PlayerLevelData setXp(OfflinePlayer player, long amount) {
        long updatedTotalXp = Math.max(0L, amount);
        int updatedLevel = LevelManager.calculateLevel(updatedTotalXp);
        return this.save(player.getUniqueId(), player.getName(), updatedTotalXp, updatedLevel);
    }

    private PlayerLevelData getOrCreate(UUID uuid, String name) {
        PlayerLevelData existing = this.findByUuid(uuid);

        if (existing != null) {
            if (name != null && !name.equals(existing.name())) {
                this.updateName(uuid, name);
                return new PlayerLevelData(uuid, name, existing.totalXp(), existing.level());
            }

            return existing;
        }

        return this.save(uuid, name, 0L, 1);
    }

    private PlayerLevelData findByUuid(UUID uuid) {
        String sql = "SELECT uuid, name, total_xp, level FROM player_levels WHERE uuid = ?";

        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(sql)) {
            statement.setString(1, uuid.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new PlayerLevelData(
                            UUID.fromString(resultSet.getString("uuid")),
                            resultSet.getString("name"),
                            resultSet.getLong("total_xp"),
                            resultSet.getInt("level"));
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to load player level data", exception);
        }

        return null;
    }

    private PlayerLevelData save(UUID uuid, String name, long totalXp, int level) {
        String sql = "INSERT INTO player_levels(uuid, name, total_xp, level) VALUES(?, ?, ?, ?) "
                + "ON CONFLICT(uuid) DO UPDATE SET name = excluded.name, total_xp = excluded.total_xp, level = excluded.level";

        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(sql)) {
            statement.setString(1, uuid.toString());
            statement.setString(2, name == null ? uuid.toString() : name);
            statement.setLong(3, totalXp);
            statement.setInt(4, level);
            statement.executeUpdate();
            return new PlayerLevelData(uuid, name == null ? uuid.toString() : name, totalXp, level);
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to save player level data", exception);
        }
    }

    private void updateName(UUID uuid, String name) {
        String sql = "UPDATE player_levels SET name = ? WHERE uuid = ?";

        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to update player name in database", exception);
        }
    }

    public record PlayerLevelData(UUID uuid, String name, long totalXp, int level) {

        public long xpIntoCurrentLevel() {
            return LevelManager.getXpIntoCurrentLevel(this.totalXp);
        }

        public long xpToNextLevel() {
            return LevelManager.getXpToNextLevel(this.totalXp);
        }

        public double progress() {
            return LevelManager.getProgress(this.totalXp);
        }
    }
}