package lucatruglia.piratecore;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import lucatruglia.piratecore.managers.PluginManager;
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

        this.enablePlaceholderAPI();
        PluginManager.getInstance().initialize(this);

        getCommand("klevel").setExecutor(new LevelCommand());
        getCommand("kbar").setExecutor(new BossBarCommand());
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