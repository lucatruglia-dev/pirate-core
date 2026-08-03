package lucatruglia.piratecore.command;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lucatruglia.piratecore.managers.AutoSpawnerManager;
import lucatruglia.piratecore.managers.BarrelManager;
import lucatruglia.piratecore.models.BarrelData;
import lucatruglia.piratecore.models.ListMessage;
import lucatruglia.piratecore.utils.Logs;

public class BarrelCommand implements CommandExecutor {

    //public final UUID worldUUID = UUID.fromString("b23dea45-474e-467d-bca1-e25f4c973dd3");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("spawn")) {
                Player p = (Player) sender;
                Location loc = p.getLocation();

                BarrelData res = BarrelManager.getInstance().spawnBarrel(loc, "barile1");
                if (res==null) {
                    Logs.sendWarningMessageToPlayer(p, "kBarrel", "Error.");
                    return true;
                } 

                Logs.sendSuccessMessageToPlayer(p, "kBarrel", "Spawnato.");

                return true;
            }

            if (args[0].equalsIgnoreCase("id") && args.length == 2) {
                Player p = (Player) sender;
                Location loc = p.getLocation();

                BarrelData res = BarrelManager.getInstance().spawnBarrel(loc, args[1]);
                if (res==null) {
                    Logs.sendWarningMessageToPlayer(p, "kBarrel", "Error.");
                    return true;
                } 

                Logs.sendSuccessMessageToPlayer(p, "kBarrel", "Spawnato.");

                return true;
            }

            if (args[0].equalsIgnoreCase("autospawn")) {
                AutoSpawnerManager.getInstance().barrel();
            }

            
        }

        if (sender instanceof Player) {
            Logs.sendListMessageToPlayer((Player) sender,
                    new ListMessage("Help", List.of(
                            new ListMessage.Row("/kbarrel spawn", "Per spawnare un barile base"),
                            new ListMessage.Row("/kbarrel cs <life> <xpReward> <moneyReward>",
                                    "Per spawnare un barile custom"))));
        }

        return true;
    }



}
