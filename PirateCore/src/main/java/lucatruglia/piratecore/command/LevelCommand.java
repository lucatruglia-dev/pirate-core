package lucatruglia.piratecore.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lucatruglia.piratecore.PirateCore;
import lucatruglia.piratecore.managers.BossBarManager;
import lucatruglia.piratecore.managers.LevelManager;
import lucatruglia.piratecore.managers.PlayerManager;
import lucatruglia.piratecore.models.ListMessage;
import lucatruglia.piratecore.models.PlayerData;
import lucatruglia.piratecore.models.ListMessage.Row;
import lucatruglia.piratecore.utils.Logs;

public class LevelCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            Logs.sendLog("ERRORE", "COMANDO NON ESEGUIBILE DA CONSOLE");
            return false;
        }

        Player player = (Player) sender;

        // "/klevel info"
        if (args[0].equalsIgnoreCase("info"))
            getInfoSubCommand(player);

        // "/klevel setxp <name> <amount>"
        else if (args[0].equalsIgnoreCase("setxp"))
            setXPSubCommand(player, args[1], args[2]);

        // "/klevel addxp <name> <amount>"
        else if (args[0].equalsIgnoreCase("addxp"))
            addXPSubCommand(player, args[1], args[2]);

        // "/klevel remove <name>"
        else if (args[0].equalsIgnoreCase("remove"))
            removePlayerSubCommand(player, args[1]);

        return true;
    }

    private void removePlayerSubCommand(Player player, String targetPlayerName){
        boolean status = PlayerManager.getInstance().removePlayerData(PirateCore.get().getServer().getPlayer(targetPlayerName));
        if(!status){
            player.sendMessage(targetPlayerName + " non esiste.");
            return;
        }
        player.sendMessage("Rimosso " + targetPlayerName + " dal db");
    }

    private void getInfoSubCommand(Player player) {
        PlayerData data = PlayerManager.getInstance().getInfo(player);

        long xpNeededForLevel = LevelManager.getInstance().getTotalXpNeededForLevel(data.level() + 1);

        

        Logs.sendListMessageToPlayer(player, 
            new ListMessage("Informazioni", new ArrayList<Row>(List.of(
                new Row("Nome", data.name()),
                new Row("XP", ""+data.totalXp() + "/" + xpNeededForLevel),
                new Row("Level", ""+data.level())
            )))
        );
        
        BossBarManager.getInstance().showPlayerLevelBar(player);
        
    }

    private void setXPSubCommand(Player player, String targetPlayer, String amount) {
        Player targetP = PirateCore.get().getServer().getPlayer(targetPlayer);
        PlayerManager.getInstance().setXP(targetP, Integer.parseInt(amount), true);

        player.sendMessage("(" + targetPlayer + ") -> settato xp a " + amount);
    }

    private void addXPSubCommand(Player player, String targetPlayer, String amount) {
        Player targetP = PirateCore.get().getServer().getPlayer(targetPlayer);
        PlayerManager.getInstance().addXP(targetP, Integer.parseInt(amount), true);

        player.sendMessage("(" + targetPlayer + ") -> aggiunto xp: " + amount);
    }


    

}
