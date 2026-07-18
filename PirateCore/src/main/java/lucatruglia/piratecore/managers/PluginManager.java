package lucatruglia.piratecore.managers;

import org.bukkit.plugin.java.JavaPlugin;

public class PluginManager {
    private static PluginManager instance;
    
    public static PluginManager getInstance() {
        if (instance == null) {
            instance = new PluginManager();
        }
        return instance;
    }
    
    /**
     * Orchestra l'inizializzazione di tutti i manager nell'ordine corretto.
     */
    public void initialize(JavaPlugin plugin) {
        BossBarManager.getInstance().initialize(plugin);
        ConfigManager.getInstance().initialize();
        DatabaseManager.getInstance().initialize();
        PlayerManager.getInstance().initialize();
        LevelManager.getInstance().initialize();
        RewardManager.getInstance().initialize();
        
        instance = this;
    }
}