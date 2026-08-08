package lucatruglia.piratecore.managers.boat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import lucatruglia.piratecore.PirateCore;
import lucatruglia.piratecore.managers.ConfigManager;

public class BoatTrailManager {
    public static BoatTrailManager instance;
    private FileConfiguration config;
    private final String filePath = "settings/boat_trails.yml";

    public static BoatTrailManager getInstance() {
        if (instance == null) {
            instance = new BoatTrailManager();
        }
        return instance;
    }

    public void initialize() {
        this.config = ConfigManager.getInstance().getConfig(filePath);
        instance = this;
    }

    public List<String> getAllTrails() {
        Set<String> trailKeys = config.getConfigurationSection("trails").getKeys(false);

        String[] trailsArray = trailKeys.toArray(new String[0]);
        List<String> result = new ArrayList<String>();

        // Esempio di utilizzo
        for (String trail : trailsArray) {
            result.add(trail);
        }
        return result;
    }

    public String getTrailID(String trail) {
        return this.config.getString("trails." + trail.toUpperCase());
    }

    public void enableTrailToPlayer(Player player, String pirateTrailID) {
        PirateCore.get().getServer().dispatchCommand(PirateCore.get().getServer().getConsoleSender(),
                "rltrails:trail set " + player.getName() + " " + getTrailID(pirateTrailID));
    }

    public void disableTrailToPlayer(Player player) {
        PirateCore.get().getServer().dispatchCommand(PirateCore.get().getServer().getConsoleSender(),
                "rltrails:trail set " + player.getName() + " off");
    }
}
