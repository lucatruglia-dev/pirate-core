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
        EconomyManager.getInstance().initialize(plugin);
        PlayerManager.getInstance().initialize();
        LevelManager.getInstance().initialize();
        RewardManager.getInstance().initialize();
        AnimationManager.getInstance().initialize();
        BarrelManager.getInstance().initialize();
    }

    public static void loadListeners(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new PlayerListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new BossBarListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ArmorStandListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new LevelUpListener(), plugin);
    }

    public static void loadCommands(JavaPlugin plugin) {
        plugin.getCommand("klevel").setExecutor(new LevelCommand());
        plugin.getCommand("kbar").setExecutor(new BossBarCommand());
        plugin.getCommand("kbarrel").setExecutor(new BarrelCommand());
    }


    public static void loadExtensions(JavaPlugin plugin) {
        PirateCoreExpansion.enable(plugin);
    }

    
}