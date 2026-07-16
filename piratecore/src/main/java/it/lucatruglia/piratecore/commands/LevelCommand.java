package it.lucatruglia.piratecore.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import it.lucatruglia.piratecore.managers.PlayerLevelManager;
import it.lucatruglia.piratecore.managers.PlayerLevelManager.PlayerLevelData;

public class LevelCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            this.sendUsage(sender, label);
            return true;
        }

        String action = args[0].toLowerCase();

        if (action.equals("info")) {
            return this.handleInfo(sender, args, label);
        }

        if (action.equals("add")) {
            return this.handleAdd(sender, args, label);
        }

        if (action.equals("set")) {
            return this.handleSet(sender, args, label);
        }

        this.sendUsage(sender, label);
        return true;
    }

    private boolean handleInfo(CommandSender sender, String[] args, String label) {
        OfflinePlayer targetPlayer = null;

        if (args.length >= 2) {
            targetPlayer = this.resolvePlayer(args[1]);

            if (targetPlayer == null) {
                sender.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }

            if (sender instanceof Player player && !player.getUniqueId().equals(targetPlayer.getUniqueId())
                    && !sender.hasPermission("piratecore.level.other")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to view other players.");
                return true;
            }
        } else if (sender instanceof Player player) {
            targetPlayer = player;
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " info <player>");
            return true;
        }

        PlayerLevelData data = PlayerLevelManager.getInstance().getPlayer(targetPlayer);
        sender.sendMessage(ChatColor.GOLD + "Level data for " + ChatColor.YELLOW + data.name() + ChatColor.GOLD + ":");
        sender.sendMessage(ChatColor.GRAY + "Level: " + ChatColor.WHITE + data.level());
        sender.sendMessage(ChatColor.GRAY + "Total XP: " + ChatColor.WHITE + data.totalXp());
        sender.sendMessage(ChatColor.GRAY + "XP into level: " + ChatColor.WHITE + data.xpIntoCurrentLevel());
        sender.sendMessage(ChatColor.GRAY + "XP to next: " + ChatColor.WHITE + data.xpToNextLevel());
        sender.sendMessage(ChatColor.GRAY + "Progress: " + ChatColor.WHITE + String.format("%.2f%%", data.progress() * 100.0D));
        return true;
    }

    private boolean handleAdd(CommandSender sender, String[] args, String label) {
        if (!sender.hasPermission("piratecore.level.admin") && !(sender instanceof org.bukkit.command.ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " add <player> <amount>");
            return true;
        }

        OfflinePlayer targetPlayer = this.resolvePlayer(args[1]);

        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        long amount = this.parseAmount(sender, args[2]);
        if (amount < 0L) {
            return true;
        }

        PlayerLevelData data = PlayerLevelManager.getInstance().addXp(targetPlayer, amount);
        sender.sendMessage(ChatColor.GREEN + "Added " + amount + " XP to " + data.name() + ". New level: " + data.level());

        if (targetPlayer.isOnline() && targetPlayer.getPlayer() != null) {
            targetPlayer.getPlayer().sendMessage(ChatColor.GOLD + "You gained " + amount + " XP. Level: " + data.level());
        }
        return true;
    }

    private boolean handleSet(CommandSender sender, String[] args, String label) {
        if (!sender.hasPermission("piratecore.level.admin") && !(sender instanceof org.bukkit.command.ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " set <player> <amount>");
            return true;
        }

        OfflinePlayer targetPlayer = this.resolvePlayer(args[1]);

        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        long amount = this.parseAmount(sender, args[2]);
        if (amount < 0L) {
            return true;
        }

        PlayerLevelData data = PlayerLevelManager.getInstance().setXp(targetPlayer, amount);
        sender.sendMessage(ChatColor.GREEN + "Set " + data.name() + " XP to " + data.totalXp() + ". New level: " + data.level());

        if (targetPlayer.isOnline() && targetPlayer.getPlayer() != null) {
            targetPlayer.getPlayer().sendMessage(ChatColor.GOLD + "Your XP was set to " + data.totalXp() + ". Level: " + data.level());
        }
        return true;
    }

    private long parseAmount(CommandSender sender, String rawAmount) {
        try {
            return Long.parseLong(rawAmount);
        } catch (NumberFormatException exception) {
            sender.sendMessage(ChatColor.RED + "Amount must be a number.");
            return -1L;
        }
    }

    private void sendUsage(CommandSender sender, String label) {
        sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " info [player] | add <player> <amount> | set <player> <amount>");
    }

    private OfflinePlayer resolvePlayer(String name) {
        Player onlinePlayer = Bukkit.getPlayerExact(name);

        if (onlinePlayer != null) {
            return onlinePlayer;
        }

        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (offlinePlayer.getName() != null && offlinePlayer.getName().equalsIgnoreCase(name)) {
                return offlinePlayer;
            }
        }

        return null;
    }
}