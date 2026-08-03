package lucatruglia.piratecore.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lucatruglia.piratecore.boat.BoatManager;

public class BoatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if(!(sender instanceof Player)){
            return false;
        }

        Player player = (Player) sender;
        
        BoatManager.getInstance().spawnBoat(player);

        return true;
    }
    
}
