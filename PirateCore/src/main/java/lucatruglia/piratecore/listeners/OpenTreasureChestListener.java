package lucatruglia.piratecore.listeners;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import lucatruglia.piratecore.events.OpenTreasureChestEvent;
import lucatruglia.piratecore.managers.TreasureMapManager;
import lucatruglia.piratecore.utils.Logs;

public class OpenTreasureChestListener implements Listener {
    @EventHandler
    public void onPlayerOpenTreasureChest(OpenTreasureChestEvent event) {
        Player player = event.getPlayer();
        UUID chestOwnerUUID = event.getOwnerUUID();
        UUID mapUUID = event.getMapUUID();

        Logs.sendLog("onPlayerOpenTreasureChest", ""+player.getName()+" sta provando ad aprire il tesoro ("+mapUUID.toString()+")");

        if(player.getUniqueId() == chestOwnerUUID){
            Logs.sendWarningMessageToPlayer(player, "kMap", "Non puoi aprire il tesoro di qualcun altro.");
            return;
        }

        if(TreasureMapManager.getInstance().hasPlayerFoundTreasure(player)==true){
            return;
        }

        TreasureMapManager.getInstance().playerFoundTreasure(player);
        Logs.sendSuccessMessageToPlayer(player, "kMap", "Bravo, hai trovato il tesoro!");
    }
}
