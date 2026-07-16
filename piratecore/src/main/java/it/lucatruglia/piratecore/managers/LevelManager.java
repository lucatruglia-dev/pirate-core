package it.lucatruglia.piratecore.managers;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import it.lucatruglia.piratecore.piratecore;


public class LevelManager {

    private static JavaPlugin plugin;
    private static YamlConfiguration yaml;

    private LevelManager() {
    }

    public static void initialize() {
        plugin = piratecore.getInstance();
        plugin.saveResource("settings/levels.yml", false);

        File file = new File(plugin.getDataFolder(), "settings/levels.yml");
        yaml = YamlConfiguration.loadConfiguration(file);
    }

    public static long getBaseXp() {
        return yaml.getLong("base-xp", 1000L);
    }

    public static double getMultiplier() {
        return yaml.getDouble("multiplier", 1.2D);
    }

    public static long getRequiredXpForNextLevel(int currentLevel) {
        int safeLevel = Math.max(1, currentLevel);
        return Math.round(getBaseXp() * Math.pow(getMultiplier(), safeLevel - 1));
    }

    public static int calculateLevel(long totalXp) {
        int level = 1;
        long remainingXp = Math.max(0L, totalXp);

        while (remainingXp >= getRequiredXpForNextLevel(level)) {
            remainingXp -= getRequiredXpForNextLevel(level);
            level++;
        }

        return level;
    }

    public static long getXpIntoCurrentLevel(long totalXp) {
        int level = 1;
        long remainingXp = Math.max(0L, totalXp);

        while (remainingXp >= getRequiredXpForNextLevel(level)) {
            remainingXp -= getRequiredXpForNextLevel(level);
            level++;
        }

        return remainingXp;
    }

    public static long getXpToNextLevel(long totalXp) {
        int currentLevel = calculateLevel(totalXp);
        long xpIntoLevel = getXpIntoCurrentLevel(totalXp);
        return Math.max(0L, getRequiredXpForNextLevel(currentLevel) - xpIntoLevel);
    }

    public static double getProgress(long totalXp) {
        long requiredXp = getRequiredXpForNextLevel(calculateLevel(totalXp));

        if (requiredXp <= 0L) {
            return 1.0D;
        }

        return (double) getXpIntoCurrentLevel(totalXp) / (double) requiredXp;
    }
}
