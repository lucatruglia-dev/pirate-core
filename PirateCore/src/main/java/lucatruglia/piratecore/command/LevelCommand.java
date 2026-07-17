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

        return true;
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
        
        showBar(data, player, xpNeededForLevel);
        
    }

    private void setXPSubCommand(Player player, String targetPlayer, String amount) {
        Player targetP = PirateCore.get().getServer().getPlayer(targetPlayer);
        PlayerData pData = PlayerManager.getInstance().setXP(targetP, Integer.parseInt(amount));
        long xpNeededForLevel = LevelManager.getInstance().getTotalXpNeededForLevel(pData.level() + 1);

        player.sendMessage("(" + targetPlayer + ") -> settato xp a " + amount);

        showBar(pData, player, xpNeededForLevel);
    }

    private void addXPSubCommand(Player player, String targetPlayer, String amount) {
        Player targetP = PirateCore.get().getServer().getPlayer(targetPlayer);
        PlayerData pData = PlayerManager.getInstance().addXP(targetP, Integer.parseInt(amount));
        long xpNeededForLevel = LevelManager.getInstance().getTotalXpNeededForLevel(pData.level() + 1);

        player.sendMessage("(" + targetPlayer + ") -> aggiunto xp: " + amount);
        showBar(pData, player, xpNeededForLevel);
    }


    private void showBar(PlayerData data, Player player, long xpNeededForLevel){
        BossBarManager.getInstance().showTimedBar(
                player,
                "" + data.level() + "             " + data.totalXp() + "/"
                        + LevelManager.getInstance().getTotalXpNeededForLevel(data.level() + 1) + "             "
                        + (data.level() + 1),
                ((double) data.totalXp() / xpNeededForLevel),
                BarColor.GREEN,
                BarStyle.SOLID,
                5 // 30 secondi, invisibili al giocatore
        );
    }

}
