package lucatruglia.piratecore.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lucatruglia.piratecore.managers.BossBarManager;
import lucatruglia.piratecore.utils.Logs;

public class BossBarCommand implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if(!(sender instanceof Player)){
            Logs.sendLog("bossbar", "solo i player possono eseguire questo comando");
            return false;
        }

        Player player = (Player) sender;


        if (args.length == 0) {
            player.sendMessage("§c/kbar <enable|disable>");
            return false;
        }

        if(args[0].equalsIgnoreCase("enable")){
            BossBarManager.getInstance().showBar(player, "prova", 0.5);
        }

        else if(args[0].equalsIgnoreCase("disable")){
            BossBarManager.getInstance().hideBar(player);
        }

        
        return true;
    }
    
}
