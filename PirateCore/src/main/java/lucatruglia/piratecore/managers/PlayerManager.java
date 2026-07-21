package lucatruglia.piratecore.managers;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lucatruglia.piratecore.events.LevelUpEvent;
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

    public void addMoney(Player player, double amount, boolean sendMsg){
        if(EconomyManager.getEconomy() == null){
            Logs.sendLog("ECONOMY", "SI È VERIFICATO UN ERRORE");
            return;
        }
        EconomyManager.getEconomy().depositPlayer(player, amount);
        if(sendMsg)
            Logs.sendSuccessMessageToPlayer(player, "Balance", "Hai guadagnato "+amount+ "dobloni");
    }

    public void addXP(Player player, int amount, boolean sendMsg){
        this.setXP_p(player, amount, true);
        
        BossBarManager.getInstance().showPlayerLevelBar(player);
        if(sendMsg)
            Logs.sendSuccessMessageToPlayer(player, "XP", "Hai guadagnato "+amount+ "XP");
    }
    
    public void setXP(Player player, int amount, boolean sendMsg){
        this.setXP_p(player, amount, false);
        
        BossBarManager.getInstance().showPlayerLevelBar(player);
        if(sendMsg)
            Logs.sendSuccessMessageToPlayer(player, "XP", "XP impostati a "+amount);
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

        if(newLevel > data.get().level()){
            LevelUpEvent levelUpEvent = new LevelUpEvent(player, newLevel);
            Bukkit.getPluginManager().callEvent(levelUpEvent);
        }
        
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
            return this.initPlayerData(player,0, LevelManager.getInstance().getLevelByXP(0));
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


    public void dropItem(Player player, ItemStack item){
        player.getWorld().dropItemNaturally(player.getLocation(), item);
    }
    
    
}
