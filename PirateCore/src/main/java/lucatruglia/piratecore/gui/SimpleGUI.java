package lucatruglia.piratecore.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class SimpleGUI {

    private final Inventory inventory;
    private final Map<Integer, Runnable> actions = new HashMap<>();

    public SimpleGUI(String title) {
        // 27 slot = Chest singola (3 righe da 9 slot)
        this.inventory = Bukkit.createInventory(null, 27, title);
    }

    /**
     * Aggiunge un item in uno slot specifico ed associa la funzione da eseguire al click.
     */
    public void setItem(int slot, Material material, String displayName, Runnable action) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            item.setItemMeta(meta);
        }

        inventory.setItem(slot, item);
        
        if (action != null) {
            actions.put(slot, action);
        }
    }

    /**
     * Apre la GUI al giocatore.
     */
    public void open(Player player) {
        player.openInventory(inventory);
        GUIListener.registerGUI(inventory, this);
    }

    public void handleClick(int slot) {
        Runnable action = actions.get(slot);
        if (action != null) {
            action.run();
        }
    }
}