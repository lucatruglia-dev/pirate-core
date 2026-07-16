package it.lucatruglia.piratecore;

import org.bukkit.plugin.java.JavaPlugin;

import it.lucatruglia.piratecore.managers.BossBarManager;
import it.lucatruglia.piratecore.managers.DatabaseManager;
import it.lucatruglia.piratecore.managers.PluginManager;
import it.lucatruglia.piratecore.managers.LevelManager;
import it.lucatruglia.piratecore.commands.LevelCommand;
import it.lucatruglia.piratecore.commands.TestCommand;
import it.lucatruglia.piratecore.listeners.PlayerListener;

public class piratecore extends JavaPlugin {

    private static piratecore instance;


    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize managers
        PluginManager.getInstance().initialize();
        LevelManager.initialize();
        DatabaseManager.initialize(this);
        
        this.getCommand("ktest").setExecutor(new TestCommand());
        this.getCommand("klevel").setExecutor(new LevelCommand());
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        
        getLogger().info(getDescription().getName() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        BossBarManager.getInstance().hideAll();
        DatabaseManager.close();

        instance = null;
        getLogger().info(getDescription().getName() + " has been disabled!");
    }

    public static piratecore getInstance(){
        return instance;
    }
    
}
