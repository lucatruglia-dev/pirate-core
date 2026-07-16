package it.lucatruglia.piratecore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import it.lucatruglia.piratecore.managers.BossBarManager;
import it.lucatruglia.piratecore.managers.PlayerLevelManager;
import it.lucatruglia.piratecore.managers.PlayerLevelManager.PlayerLevelData;

public class TestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("hide") || args[0].equalsIgnoreCase("remove")
                    || args[0].equalsIgnoreCase("clear")) {
                if (args.length > 1 && sender.hasPermission("piratecore.bossbar.other")) {
                    Player target = player.getServer().getPlayerExact(args[1]);

                    if (target == null) {
                        player.sendMessage("Player not found.");
                        return true;
                    }

                    BossBarManager.getInstance().hide(target);
                    player.sendMessage("Bossbar rimossa a " + target.getName());
                    return true;
                }

                BossBarManager.getInstance().hide(player);
                player.sendMessage("La tua bossbar e' stata rimossa.");
                return true;
            }

            if (args[0].equalsIgnoreCase("show")) {

                PlayerLevelData data = PlayerLevelManager.getInstance().getPlayer(player);

                if (data != null) {

                    int livelloAttuale = data.level();

                    long xpTotale = data.totalXp();
                    long xpMancanti = data.xpToNextLevel();

                    double progresso = data.progress();
                    
                    BossBarManager.getInstance().show(player, "" + 
                        livelloAttuale + "      --- " + xpTotale +"/"+ (xpTotale+xpMancanti) + " XP ---      "+livelloAttuale+1 , 
                    progresso);
                }

                // "0      --- 0/1000 XP ---      1", 0.5D

                player.sendMessage("Bossbar mostrata.");
                return true;
            }

            player.sendMessage("Usage: /" + label + " show | hide [player]");
            return true;
        }

        BossBarManager.getInstance().show(player, "0      --- 0/1000 XP ---      1", 0.5D);

        return true;
    }
}
