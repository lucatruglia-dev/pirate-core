package lucatruglia.piratecore.managers;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

import lucatruglia.piratecore.models.PlayerData;
import lucatruglia.piratecore.utils.Logs;

public class PlayerManager {
    private static PlayerManager instance;
    
    public static PlayerManager getInstance() {
        if (instance == null) {
            instance = new PlayerManager();
        }
        return instance;
    }

    public void initialize() {
        instance = this;
    }

    public PlayerData addXP(Player player, int amount){
        Logs.sendSuccessMessageToPlayer(player, "XP", "Congratulazioni! Hai guadagnato "+amount+ "XP");
        PlayerManager.getInstance().showBar(player);
        return this.setXP_p(player, amount, true);
    }
    
    public PlayerData setXP(Player player, int amount){
        Logs.sendSuccessMessageToPlayer(player, "XP", "XP impostato a "+amount);
        PlayerManager.getInstance().showBar(player);
        return this.setXP_p(player, amount, false);
    } 
 
    private PlayerData setXP_p(Player player, int amount, boolean add){
        UUID playerUUID = player.getUniqueId();
        long newXP = (long) amount;
        Optional<PlayerData> data = DatabaseManager.getInstance().loadPlayer(playerUUID);
        
        if (data.isEmpty()) {
            PlayerData newPlayerData = new PlayerData(playerUUID, player.getName(), newXP, LevelManager.getInstance().getLevelByXP(newXP));
            this.initPlayerData(newPlayerData);
            Logs.sendLog(player.getName(), "giocatore non presente nel db: ("+newXP+", "+0+")");
            return newPlayerData;
        }

        if (add){
            newXP = data.get().totalXp()+(long)amount;
        }
        
        int newLevel = LevelManager.getInstance().getLevelByXP(newXP);
        
        PlayerData newPlayerData = new PlayerData(playerUUID, data.get().name(), newXP, newLevel);

        DatabaseManager.getInstance().savePlayer(
            newPlayerData
        );
        Logs.sendLog(data.get().name(), "impostato: ("+newXP+", "+newLevel+") "+ (add ? "Add" : "Set"));
        return newPlayerData;
    } 
    
    public PlayerData getInfo(Player player){
        UUID playerUUID = player.getUniqueId();
        Optional<PlayerData> data = DatabaseManager.getInstance().loadPlayer(playerUUID);
        if (data.isEmpty()) {
            return this.initPlayerData(player,0,0);
        }
        Logs.sendLog(data.get().name(), "info richieste");
        return data.get();
    }

    public PlayerData initPlayerData(Player player, long xp, int level){
        UUID playerUUID = player.getUniqueId();
        PlayerData pData = new PlayerData(playerUUID, player.getName(), xp, level);
        DatabaseManager.getInstance().savePlayer(pData);
        return pData;
    }

    public PlayerData initPlayerData(PlayerData pData){
        DatabaseManager.getInstance().savePlayer(pData);
        return pData;
    }

    public boolean removePlayerData(Player player){
        return DatabaseManager.getInstance().deletePlayer(player.getUniqueId());
    }


    public void showBar(Player player){
        PlayerData data = PlayerManager.getInstance().getInfo(player);
        long xpNeededForLevel = LevelManager.getInstance().getTotalXpNeededForLevel(data.level() + 1);
        BossBarManager.getInstance().showTimedBar(
                player,
                "" + data.level() + "             " + data.totalXp() + "/"
                        + LevelManager.getInstance().getTotalXpNeededForLevel(data.level() + 1) + "             "
                        + (data.level() + 1),
                ((double) data.totalXp() / xpNeededForLevel),
                BarColor.GREEN,
                BarStyle.SOLID,
                5 // 30 secondi, invisibili al giocatore
        );
    }
    
    
}
