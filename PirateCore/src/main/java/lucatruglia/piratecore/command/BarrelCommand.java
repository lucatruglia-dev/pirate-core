package lucatruglia.piratecore.command;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import lucatruglia.piratecore.managers.barrel.AutoSpawnerManager;
import lucatruglia.piratecore.managers.barrel.BarrelManager;
import lucatruglia.piratecore.models.BarrelData;
import lucatruglia.piratecore.models.ListMessage;
import lucatruglia.piratecore.utils.Logs;

public class BarrelCommand implements CommandExecutor {

    // public final UUID worldUUID =
    // UUID.fromString("b23dea45-474e-467d-bca1-e25f4c973dd3");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            printHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "spawn" -> {
                if (!(sender instanceof Player p)) {
                    sender.sendMessage("Solo giocatori.");
                    return true;
                }
                BarrelData res = BarrelManager.getInstance().spawnBarrel(p.getLocation(), "barile1");
                if (res == null) {
                    Logs.sendWarningMessageToPlayer(p, "kBarrel", "Error.");
                } else {
                    Logs.sendSuccessMessageToPlayer(p, "kBarrel", "Spawnato.");
                }
                return true;
            }

            case "id" -> {
                if (!(sender instanceof Player p)) {
                    sender.sendMessage("Solo giocatori.");
                    return true;
                }
                if (args.length < 2) {
                    Logs.sendWarningMessageToPlayer(p, "kBarrel", "Specifica un ID.");
                    return true;
                }
                BarrelData res = BarrelManager.getInstance().spawnBarrel(p.getLocation(), args[1]);
                if (res == null) {
                    Logs.sendWarningMessageToPlayer(p, "kBarrel", "Error.");
                } else {
                    Logs.sendSuccessMessageToPlayer(p, "kBarrel", "Spawnato.");
                }
                return true;
            }

            case "autospawn" -> {
                AutoSpawnerManager.getInstance().barrel();
                sender.sendMessage("Fatto (?).");
                return true;
            }

            case "coord" -> {
                AutoSpawnerManager.getInstance().generateCoord();
                sender.sendMessage("Fatto (?).");
                return true;
            }

            case "tp" -> {
                if (args.length < 3) {
                    sender.sendMessage("Uso: /kbarrel tp <player> <UUID>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage("Player non trovato.");
                    return true;
                }
                Entity entity = Bukkit.getEntity(UUID.fromString(args[2]));
                if (entity != null) {
                    target.teleport(entity);
                    sender.sendMessage("Teletrasportato.");
                } else {
                    sender.sendMessage("Entità non trovata.");
                }
                return true;
            }

            case "list" -> {
                if (args.length < 2) {
                    sender.sendMessage("Uso: /kbarrel list <player> [amount]");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage("Player non trovato.");
                    return true;
                }
                int amount = args.length >= 3 ? Integer.parseInt(args[2]) : 10;
                AutoSpawnerManager.getInstance().barrelList(target, amount);
                return true;
            }

            case "remove" -> {
                if (!(sender instanceof Player p)) {
                    sender.sendMessage("Solo giocatori.");
                    return true;
                }
                Location loc = p.getLocation();
                boolean removed = BarrelManager.getInstance()
                        .destroyBarrelAt(loc.getWorld(), loc.getBlockX(), loc.getBlockZ(), p);
                p.sendMessage(removed ? "Barrel rimosso." : "Nessun barrel trovato qui.");
                return true;
            }

            case "clear" -> {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("Solo giocatori.");
                    return true;
                }
                sender.sendMessage("§eRimozione avviata...");
                AutoSpawnerManager.getInstance().cleanUp();
                sender.sendMessage("§eFatto.");
                return true;
            }

            default -> {
                printHelp(sender);
                return true;
            }
        }
    }

    private void printHelp(CommandSender sender) {
        if (sender instanceof Player p) {
            Logs.sendListMessageToPlayer(p, new ListMessage("Help", List.of(
                    new ListMessage.Row("/kbarrel spawn", "Per spawnare un barile"),
                    new ListMessage.Row("/kbarrel id <id>", "Spawna barile da config"),
                    new ListMessage.Row("/kbarrel autospawn", "Spawna tutti i barili nella mappa"),
                    new ListMessage.Row("/kbarrel list <player> [amount]", "Lista dei barili"),
                    new ListMessage.Row("/kbarrel tp <player> <UUID>", "Teletrasporta ad un barrel"))));
        } else {
            sender.sendMessage("--- kBarrel Help ---"); // fallback console
        }
    }

}
