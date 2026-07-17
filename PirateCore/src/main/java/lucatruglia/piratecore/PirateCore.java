package lucatruglia.piratecore;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import lucatruglia.piratecore.managers.BossBarManager;
import lucatruglia.piratecore.managers.ConfigManager;
import lucatruglia.piratecore.managers.DatabaseManager;
import lucatruglia.piratecore.managers.LevelManager;
import lucatruglia.piratecore.managers.PlayerManager;
import lucatruglia.piratecore.managers.PluginManager;
import lucatruglia.piratecore.managers.RewardManager;
import lucatruglia.piratecore.placeholders.PirateCoreExpansion;
import lucatruglia.piratecore.command.BossBarCommand;
import lucatruglia.piratecore.command.LevelCommand;
import lucatruglia.piratecore.listeners.BossBarListener;
import lucatruglia.piratecore.listeners.PlayerListener;

public class PirateCore extends JavaPlugin {

    private static PirateCore instance;

    public static PirateCore get() {
        return instance;
    }

    @Override
    public void onEnable() {

        instance = this;

        // Initialize managers

        this.enablePlaceholderAPI();

        BossBarManager.getInstance().initialize(this);
        PluginManager.getInstance().initialize();
        ConfigManager.getInstance().initialize();
        DatabaseManager.getInstance().initialize();
        PlayerManager.getInstance().initialize();
        LevelManager.getInstance().initialize();
        RewardManager.getInstance().initialize();

        getCommand("klevel").setExecutor(new LevelCommand());
        getCommand("kbar").setExecutor(new BossBarCommand());
        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new BossBarListener(), this);


        getLogger().info("PirateCore has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("PirateCore has been disabled!");
    }

    private void enablePlaceholderAPI() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            // Crea e registra la tua espansione
            boolean registered = new PirateCoreExpansion().register();
            if (registered) {
                getLogger().info("PlaceholderAPI expansion registered successfully!");
            } else {
                getLogger().warning("Failed to register PlaceholderAPI expansion!");
            }
        } else {
            getLogger().warning("PlaceholderAPI not found! Placeholders will not work.");
        }
    }

}