package lucatruglia.piratecore.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import lucatruglia.piratecore.utils.Logs;

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

    /**
     * Recupera un valore raw dalle condizioni (utile per accessi generici).
     */
    public Object getCondition(String path){
        return data.get("conditions."+path);
    }

    /**
     * Valuta una condizione basata su "milestone" (amount, reward).
     * Se il currentValue soddisfa la milestone, assegna XP al player.
     *
     * @param player       Giocatore da premiare
     * @param conditionKey Chiave della condizione (es. "BLOCKS_MINED")
     * @param currentValue Valore attuale (es. blocchi minati)
     * @return true se la reward è stata assegnata
     */
    public boolean evaluateMilestoneCondition(Player player, String conditionKey, long currentValue) {
        String basePath = "conditions." + conditionKey;

        if (!data.contains(basePath + ".amount") || !data.contains(basePath + ".reward")) {
            Logs.sendLog("RewardManager", "Condizione " + conditionKey + " non valida o incompleta");
            return false;
        }

        int amount = data.getInt(basePath + ".amount");
        int reward = data.getInt(basePath + ".reward");

        if(PermissionManager.getPerms().playerInGroup(player, "vip")){
            reward = (int) Math.round(reward * 1.5);
        }
        

        if (currentValue >= amount && currentValue % amount == 0) {
            PlayerManager.getInstance().addXP(player, reward);
            Logs.sendLog("RewardManager", player.getName() + " ha ricevuto " + reward + "XP per " + conditionKey);
            return true;
        }

        return false;
    }

}
