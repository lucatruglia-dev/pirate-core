package lucatruglia.piratecore.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import lucatruglia.piratecore.events.OnPlayerLeftOnHisChestBoatEvent;

public class OnPlayerLeftOnHisChestBoatListener implements Listener {
    @EventHandler
    public void onPlayerLeftOnHisChestBoat(OnPlayerLeftOnHisChestBoatEvent event) {
        Player player = event.getPlayer();
        // Boat boat = event.getBoat();
        // Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "trails set " + player.getName() + " off");

        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "rltrails:trail set "+player.getName()+" off");


        player.sendMessage("Sei uscito nella tua barca");
    }

    
}
