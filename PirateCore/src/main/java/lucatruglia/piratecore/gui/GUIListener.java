package lucatruglia.piratecore.gui;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class GUIListener implements Listener {

    private static final Map<Inventory, SimpleGUI> activeGUIs = new HashMap<>();

    public static void registerGUI(Inventory inv, SimpleGUI gui) {
        activeGUIs.put(inv, gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getClickedInventory();
        if (inv == null) return;

        SimpleGUI gui = activeGUIs.get(inv);
        if (gui != null) {
            // Impedisce al player di prendere l'item
            event.setCancelled(true);
            
            // Esegue l'azione associata allo slot cliccato
            gui.handleClick(event.getSlot());
        }
    }
}