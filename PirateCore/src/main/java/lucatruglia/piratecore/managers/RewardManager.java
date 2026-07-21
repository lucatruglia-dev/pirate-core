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
    public Object getCondition(String path) {
        return data.get("conditions." + path);
    }

    public double getRewardMultiplier(Player player) {
        double maxReward = 1.0;

        String basePath = "reward_multiplier.";
        String[] playerGroups = PermissionManager.getPerms().getPlayerGroups(player);

        if (playerGroups == null) {
            return maxReward;
        }

        for (String group : playerGroups) {
            double result = data.getDouble(basePath + group, 0.0);

            if (result > maxReward) {
                maxReward = result;
            }
        }

        return maxReward;
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

        double reward_multiplier = getRewardMultiplier(player);
        int reward = (int) (Math.round(data.getInt(basePath + ".reward") * reward_multiplier));

        if (currentValue >= amount && currentValue % amount == 0) {
            PlayerManager.getInstance().addXP(player, reward, true);
            Logs.sendLog("RewardManager", player.getName() + " ha ricevuto " + reward + "XP per " + conditionKey);

            return true;
        }

        return false;
    }

    public int getBarrelDestroyReward(Player player){
        String basePath = "conditions.BARREL_DESTROYED.reward";
        int reward = (int) data.getInt(basePath) * (int) Math.round(getRewardMultiplier(player));
        return reward;
    }

}
