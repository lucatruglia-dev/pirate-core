package lucatruglia.piratecore.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


import lucatruglia.piratecore.events.OnPlayerFoundTreasureEvent;
import lucatruglia.piratecore.managers.TimerManager;
import lucatruglia.piratecore.managers.economy.RewardManager;
import lucatruglia.piratecore.managers.player.PlayerManager;
import lucatruglia.piratecore.managers.treasure.Rarity;
import lucatruglia.piratecore.models.Reward;

public class OnPlayerFoundTreasureListener implements Listener {
    @EventHandler
    public void OnPlayerFoundTreasure(OnPlayerFoundTreasureEvent event){
        Player player = event.getPlayer();
        timer(player);
        rewards(player, event.getRarity());
    }
    
    private void timer(Player player){
        TimerManager.getInstance().cancelTimer(player);
        TimerManager.getInstance().startCountdown(player, 5, "Prendi in tempo il bottino", () -> {
            player.sendMessage("§dTempo terminato");
        });
    }

    private void rewards(Player player, Rarity rarity){
        Reward reward = RewardManager.getInstance().getTreasureReward(rarity);
        PlayerManager.getInstance().addMoney(player, reward.money(), true, 1.0);
        PlayerManager.getInstance().addXP(player, reward.xp(), true, 1.0);
    }
}
