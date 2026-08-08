package lucatruglia.piratecore.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lucatruglia.piratecore.gui.TrailAvailableGUI;
import lucatruglia.piratecore.managers.boat.BoatManager;

public class TrailCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player)){
            return true;
        }

        TrailAvailableGUI gui = new TrailAvailableGUI(
            BoatManager.getInstance().getAvailableTrails(player), 
            BoatManager.getInstance().getActivedTrails(player), 
            player);
        gui.open();
        
        return true;

    }
    
}
