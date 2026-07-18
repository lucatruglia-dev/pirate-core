package lucatruglia.piratecore;

import org.bukkit.plugin.java.JavaPlugin;

public class PirateCore extends JavaPlugin {

    private static PirateCore instance;

    public static PirateCore get() {
        return instance;
    }

    @Override
    public void onEnable() {

        instance = this;

        Loader.loadExtensions(this);

        Loader.loadManagers(this);
        Loader.loadListeners(this);
        Loader.loadCommands(this);

        getLogger().info("PirateCore has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("PirateCore has been disabled!");
    }

    

    

}