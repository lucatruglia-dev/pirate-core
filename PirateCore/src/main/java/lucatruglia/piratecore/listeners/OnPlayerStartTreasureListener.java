package lucatruglia.piratecore.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import lucatruglia.piratecore.events.OnPlayerStartTreasureEvent;
import lucatruglia.piratecore.managers.TimerManager;
import lucatruglia.piratecore.managers.treasure.Rarity;
import lucatruglia.piratecore.models.Coordinate;
import lucatruglia.piratecore.models.ListMessage;
import lucatruglia.piratecore.models.ListMessage.Row;
import lucatruglia.piratecore.utils.Logs;
import lucatruglia.piratecore.utils.Utils;

public class OnPlayerStartTreasureListener implements Listener {
    @EventHandler
    public void OnPlayerStartTreasure(OnPlayerStartTreasureEvent event) {
        Player player = event.getPlayer();
        Rarity rarity = event.getRarity();
        Coordinate coordinate = event.getTrasureCoord();
        event.getPlayer().sendMessage("§dCaccia al tesoro iniziata");
        timer(player);
        Logs.sendListMessageToPlayer(
                player,
                new ListMessage("Caccia al tesoro iniziata",
                        new ArrayList<Row>(List.of(
                                new ListMessage.Row("🌟 Rarità", rarity.name()),
                                new ListMessage.Row("📌 Coordinate", Utils.coordToString(coordinate.x(), coordinate.y())),
                                new ListMessage.Row("⌚ Tempo a disposizione", "60 minuti"))),
                        new ArrayList<ListMessage.Button>(List.of(
                                new ListMessage.Button("ANNULLA", "")))));
    }

    private void timer(Player player){
        TimerManager.getInstance().startCountdown(player, 60 * 60, "Trova il tesoro", () -> {
            player.sendMessage("§dTempo scaduto, non hai trovato in tempo il tesoro");
        });
    }
}
