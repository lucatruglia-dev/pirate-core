package lucatruglia.piratecore.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import lucatruglia.piratecore.events.OnPlayerJoinOnHisChestBoatEvent;
// import lucatruglia.piratecore.managers.boat.Boat;
import lucatruglia.piratecore.managers.boat.BoatTrailManager;

public class OnPlayerJoinOnHisChestBoatListener implements Listener {
    @EventHandler
    public void onPlayerJoinOnHisChestBoat(OnPlayerJoinOnHisChestBoatEvent event) {
        Player player = event.getPlayer();
        List<String> activatedTrails = event.getBoat().getActivatedTrails();
        // Boat boat = event.getBoat();
        for (String trail : activatedTrails) {
            String traildRealId = BoatTrailManager.getInstance().getTrailID(trail);
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "rltrails:trail set "+player.getName()+" "+traildRealId);
        }

       
    }

    
}
