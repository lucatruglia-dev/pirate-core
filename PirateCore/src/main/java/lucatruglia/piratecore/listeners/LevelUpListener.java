package lucatruglia.piratecore.listeners;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import lucatruglia.piratecore.events.LevelUpEvent;
import lucatruglia.piratecore.utils.Logs;

public class LevelUpListener implements Listener {
    @EventHandler
    public void onLevelUp(LevelUpEvent event) {
        Player player = event.getPlayer();
        int newlevel = event.getNewlevel();

        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        Logs.sendSuccessMessageToPlayer(player, "LEVELUP "+newlevel, "Nuovo livello!");
    }
}
