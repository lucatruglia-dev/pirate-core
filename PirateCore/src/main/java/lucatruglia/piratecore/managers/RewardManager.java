package lucatruglia.piratecore.managers;

import org.bukkit.configuration.file.FileConfiguration;

public class RewardManager {
    private static RewardManager instance;
    private FileConfiguration data;

    public static RewardManager getInstance() {
        if (instance == null) {
            instance = new RewardManager();
        }
        return instance;
    }

    public void initialize() {
        ConfigManager.getInstance().reloadConfig("settings/levels.yml");
        data = ConfigManager.getInstance().getConfig("settings/levels.yml");
        instance = this;
    }


    public Object getCondition(String path){
        return data.get("conditions."+path);
    }

}
