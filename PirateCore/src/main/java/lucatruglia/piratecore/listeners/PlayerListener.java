package lucatruglia.piratecore.listeners;

import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import lucatruglia.piratecore.PirateCore;
import lucatruglia.piratecore.managers.DatabaseManager;
import lucatruglia.piratecore.managers.PlayerManager;
import lucatruglia.piratecore.managers.RewardManager;
import lucatruglia.piratecore.models.PlayerData;
import lucatruglia.piratecore.utils.Logs;
import me.clip.placeholderapi.PlaceholderAPI;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        Boolean existOnDb = DatabaseManager.getInstance().playerExists(p.getUniqueId());
        if(!existOnDb){
            PlayerManager.getInstance().initPlayerData(new PlayerData(p.getUniqueId(), p.getName(), 0, 0));
            Logs.sendLog("onPlayerJoin", p.getName() + " è stato aggiunto al db");
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        
        if (killer == null) {
            return;
        }

        PirateCore.get().getServer().broadcastMessage("[PirateCore TEST]"+victim.getName()+ " è stato ucciso da" + killer.getName());
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        String placeholder = "%cmi_user_stats_blocks_mined%";
        
        String parsedValue = PlaceholderAPI.setPlaceholders(player, placeholder);

        int blocks_mined = Integer.parseInt(parsedValue);

        int requirement = (int) RewardManager.getInstance().getCondition("BLOCKS_MINED.amount");
        int reward = (int) RewardManager.getInstance().getCondition("BLOCKS_MINED.reward");

        if (blocks_mined%requirement==0){
            PlayerManager.getInstance().addXP(player, reward);
        }
    }


}