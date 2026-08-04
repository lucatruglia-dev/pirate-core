package lucatruglia.piratecore.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import lucatruglia.piratecore.managers.player.BossBarManager;

public class BossBarListener implements Listener {
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        BossBarManager.getInstance().onPlayerQuit(event.getPlayer());
    }
}