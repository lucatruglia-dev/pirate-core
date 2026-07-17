package lucatruglia.piratecore.listeners;

import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import lucatruglia.piratecore.managers.DatabaseManager;
import lucatruglia.piratecore.managers.PlayerManager;
import lucatruglia.piratecore.models.PlayerData;
import lucatruglia.piratecore.utils.Logs;

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
}