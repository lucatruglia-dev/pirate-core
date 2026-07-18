package lucatruglia.piratecore;

import org.bukkit.plugin.java.JavaPlugin;

import lucatruglia.piratecore.command.*;
import lucatruglia.piratecore.listeners.*;
import lucatruglia.piratecore.managers.*;
import lucatruglia.piratecore.placeholders.PirateCoreExpansion;

public class Loader {

    public static void loadManagers(JavaPlugin plugin) {
        BossBarManager.getInstance().initialize(plugin);
        ConfigManager.getInstance().initialize();
        DatabaseManager.getInstance().initialize();
        PlayerManager.getInstance().initialize();
        LevelManager.getInstance().initialize();
        RewardManager.getInstance().initialize();
    }

    public static void loadListeners(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new PlayerListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new BossBarListener(), plugin);
    }

    public static void loadCommands(JavaPlugin plugin) {
        plugin.getCommand("klevel").setExecutor(new LevelCommand());
        plugin.getCommand("kbar").setExecutor(new BossBarCommand());
    }

    public static void loadExtensions(JavaPlugin plugin) {
        PirateCoreExpansion.enable(plugin);
    }

    
}