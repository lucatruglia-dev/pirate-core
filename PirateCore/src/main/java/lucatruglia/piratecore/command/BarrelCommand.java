package lucatruglia.piratecore.command;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lucatruglia.piratecore.managers.BarrelManager;
import lucatruglia.piratecore.models.ListMessage;
import lucatruglia.piratecore.utils.Logs;

public class BarrelCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("spawn")) {
                Player p = (Player) sender;
                Location loc = p.getLocation();

                boolean res = BarrelManager.getInstance().spawnBarrel(loc, 6, 50, 10, "default");
                if (res) {
                    Logs.sendSuccessMessageToPlayer(p, "Barrel", "Barrel spawnato");
                } else {
                    Logs.sendWarningMessageToPlayer(p, "Barrel", "Nessun blocco d'acqua trovato");
                }

                return true;
            }

            if (args[0].equalsIgnoreCase("id") && args.length == 2) {
                Player p = (Player) sender;
                Location loc = p.getLocation();

                boolean res = BarrelManager.getInstance().spawnBarrel(loc, args[1]);
                if (res) {
                    Logs.sendSuccessMessageToPlayer(p, "Barrel", "Barrel spawnato");
                } else {
                    Logs.sendWarningMessageToPlayer(p, "Barrel", "Nessun blocco d'acqua trovato");
                }
                return true;
            }

            else if (args[0].equalsIgnoreCase("cs") && args.length == 4) {
                Player p = (Player) sender;
                Location loc = p.getLocation();
                int maxlife = Integer.parseInt(args[1]);
                int xpReward = Integer.parseInt(args[2]);
                int moneyReward = Integer.parseInt(args[3]);

                boolean res = BarrelManager.getInstance().spawnBarrel(loc, maxlife, xpReward, moneyReward, "default");
                if (res) {
                    Logs.sendSuccessMessageToPlayer(p, "Barrel", "Barrel spawnato");
                } else {
                    Logs.sendWarningMessageToPlayer(p, "Barrel", "Nessun blocco d'acqua trovato");
                }
                return true;
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
