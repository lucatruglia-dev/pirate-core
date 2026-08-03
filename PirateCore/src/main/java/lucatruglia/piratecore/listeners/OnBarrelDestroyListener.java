package lucatruglia.piratecore.listeners;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import lucatruglia.piratecore.events.OnBarrelDestroyEvent;
import lucatruglia.piratecore.managers.BarrelManager;
import lucatruglia.piratecore.managers.PlayerManager;
import lucatruglia.piratecore.models.BarrelData;
import lucatruglia.piratecore.models.BarrelReward;
import lucatruglia.piratecore.models.ListMessage;
import lucatruglia.piratecore.utils.Logs;

public class OnBarrelDestroyListener implements Listener {
    @EventHandler
    public void onBarrelDestroy(OnBarrelDestroyEvent event) {
        BarrelData barrel_data = event.getBarrel_info();
        Player player = event.getPlayer();
        BarrelReward reward = event.getBarrelReward();
        String id = barrel_data.config_id();

        for (ItemStack item : BarrelManager.getInstance().getDrops(id)) {
            PlayerManager.getInstance().dropItem(player, item);
        }

        Logs.sendListMessageToPlayer(player, new ListMessage("Barrel distrutto",
                List.of(
                        new ListMessage.Row("XP", "+" + reward.xp()),
                        new ListMessage.Row("Dobloni", "+" + reward.money() + "$"))));

        PlayerManager.getInstance().addXP(player, reward.xp(), false, 1.0);
        PlayerManager.getInstance().addMoney(player, (double) reward.money(), false, 1.0);
    }
}
