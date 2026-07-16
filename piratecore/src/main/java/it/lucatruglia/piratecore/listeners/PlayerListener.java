package it.lucatruglia.piratecore.listeners;

import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import it.lucatruglia.piratecore.managers.BossBarManager;
import it.lucatruglia.piratecore.managers.PlayerLevelManager;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PlayerLevelManager.getInstance().ensurePlayer(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        BossBarManager.getInstance().hide(player);
    }
}
