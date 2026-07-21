package lucatruglia.piratecore.listeners;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;

import lucatruglia.piratecore.managers.BarrelManager;

public class ArmorStandListener implements Listener {
    
    @EventHandler
    public void onArmorStandHit(EntityDamageByEntityEvent event) {
        
        if (!(event.getEntity() instanceof ArmorStand)){
            event.setCancelled(true);
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            event.setCancelled(true);
            return;
        }

        ArmorStand as = (ArmorStand) event.getEntity();
        Player player = (Player) event.getDamager();
        
        if (as.getPersistentDataContainer().has(BarrelManager.actualLifeKey, PersistentDataType.INTEGER)){
            BarrelManager.getInstance().onBarrelHit(player, as);
        }


        event.setCancelled(true);
        return;
    }

}
