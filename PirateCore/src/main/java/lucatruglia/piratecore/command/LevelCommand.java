package lucatruglia.piratecore.command;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lucatruglia.piratecore.PirateCore;
import lucatruglia.piratecore.managers.BossBarManager;
import lucatruglia.piratecore.managers.LevelManager;
import lucatruglia.piratecore.managers.PlayerManager;
import lucatruglia.piratecore.models.PlayerData;
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

        player.sendMessage("===============");
        player.sendMessage("Nome: " + data.name());
        player.sendMessage(
                "XP: " + data.totalXp() + "/" + xpNeededForLevel);
        player.sendMessage("LEVEL: " + data.level());
        player.sendMessage("===============");
        player.sendMessage("MULTIPLIER: " + LevelManager.getInstance().getMultiplier());
        player.sendMessage("FIRST LEVEL XP: " + LevelManager.getInstance().getfirstLevelXP());
        player.sendMessage("===============");
        
        BossBarManager.getInstance().showPlayerLevelBar(player);
        
    }

    private void setXPSubCommand(Player player, String targetPlayer, String amount) {
        Player targetP = PirateCore.get().getServer().getPlayer(targetPlayer);
        PlayerData pData = PlayerManager.getInstance().setXP(targetP, Integer.parseInt(amount));

        player.sendMessage("(" + targetPlayer + ") -> settato xp a " + amount);
    }

    private void addXPSubCommand(Player player, String targetPlayer, String amount) {
        Player targetP = PirateCore.get().getServer().getPlayer(targetPlayer);
        PlayerData pData = PlayerManager.getInstance().addXP(targetP, Integer.parseInt(amount));

        player.sendMessage("(" + targetPlayer + ") -> aggiunto xp: " + amount);
    }


    

}
