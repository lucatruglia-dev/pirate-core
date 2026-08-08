package lucatruglia.piratecore.gui;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lucatruglia.piratecore.utils.Utils;

public class TrailAvailableGUI {
    private List<String> available_gui;
    private List<String> activated_gui;
    private Player player;
    private SimpleGUI gui;

    public TrailAvailableGUI(List<String> available_gui, List<String> activated_gui, Player player) {
        this.available_gui = available_gui;
        this.activated_gui = activated_gui;
        this.player = player;

        init();
    }

    private void init() {
        gui = new SimpleGUI(Utils.colorize("&3Le tue scie colorate"));
        for (String string : available_gui) {
            gui.setItem(11, Material.DIAMOND, "§b§l"+string, () -> {
                player.sendMessage("§aHai cliccato su "+string);
            });
        }
    }

    public void open(){
        gui.open(player);
    }

}
