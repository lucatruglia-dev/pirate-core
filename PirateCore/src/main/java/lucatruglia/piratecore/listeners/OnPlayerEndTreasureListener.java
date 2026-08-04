package lucatruglia.piratecore.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import lucatruglia.piratecore.events.OnPlayerEndTreasureEvent;
import lucatruglia.piratecore.managers.TimerManager;

public class OnPlayerEndTreasureListener implements Listener {
    @EventHandler
    public void OnPlayerEndTreasure(OnPlayerEndTreasureEvent event){
        Player player = event.getPlayer();
        player.sendMessage("§dCaccia al tesoro terminata");
    
        TimerManager.getInstance().cancelTimer(player);
    }
}
