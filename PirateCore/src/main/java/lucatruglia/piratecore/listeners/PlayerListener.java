package lucatruglia.piratecore.listeners;

import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import com.gamingmesh.jobs.api.JobsLevelUpEvent;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;

import lucatruglia.piratecore.PirateCore;
import lucatruglia.piratecore.managers.DatabaseManager;
import lucatruglia.piratecore.managers.PlayerManager;
import lucatruglia.piratecore.managers.RewardManager;
import lucatruglia.piratecore.models.PlayerData;
import lucatruglia.piratecore.utils.Logs;
import me.clip.placeholderapi.PlaceholderAPI;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        Boolean existOnDb = DatabaseManager.getInstance().playerExists(p.getUniqueId());
        if (!existOnDb) {
            PlayerManager.getInstance().initPlayerData(new PlayerData(p.getUniqueId(), p.getName(), 0, 0));
            Logs.sendLog("onPlayerJoin", p.getName() + " è stato aggiunto al db");
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null) {
            return;
        }

        PirateCore.get().getServer()
                .broadcastMessage("[PirateCore TEST]" + victim.getName() + " è stato ucciso da" + killer.getName());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        String placeholder = "%cmi_user_stats_blocks_mined%";
        String parsedValue = PlaceholderAPI.setPlaceholders(player, placeholder);
        int blocks_mined = Integer.parseInt(parsedValue);

        // La logica di valutazione e ricompensa è stata spostata in RewardManager
        RewardManager.getInstance().evaluateMilestoneCondition(player, "BLOCKS_MINED", blocks_mined);
    }

    @EventHandler
    public void onJobsLevelUp(JobsLevelUpEvent event) {
        // 1. Ottieni il JobsPlayer (l'utente lato Jobs)
        JobsPlayer jPlayer = event.getPlayer();

        // 2. Ottieni il Job coinvolto
        Job job = event.getJob();

        // 3. Recupera la progressione del giocatore per QUEL lavoro
        JobProgression progression = jPlayer.getJobProgression(job);

        if (progression != null) {

            int newLevel = progression.getLevel();

            // Dati sull'esperienza
            // double currentExp = progression.getExperience();
            // double maxExp = progression.getMaxExperience();

            // Messaggio al player di Bukkit
            if (jPlayer.isOnline()) {
                // jPlayer.getPlayer().sendMessage("§aSei salito dal livello " + oldLevel + " al livello " + newLevel
                //         + " in " + job.getName() + "!");

                Logs.sendSuccessMessageToPlayer(jPlayer.getPlayer(), job.getName(), "Hai raggiunto il livello &e" + newLevel);
                PlayerManager.getInstance().addXP(jPlayer.getPlayer(), 100, true);
            }
        }
    }

    /*
    @EventHandler
    public void onJobsExpGain(JobsExpGainEvent event) {
        // 1. Prendi l'OfflinePlayer fornito dall'evento
        OfflinePlayer offlinePlayer = event.getPlayer();

        // 2. Per ricavare il JobsPlayer (dati del lavoro), usa il PlayerManager di Jobs
        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(offlinePlayer.getUniqueId());

        if (jPlayer == null)
            return;

        // 3. Per interagire con il giocatore nel server (es. mandare messaggi),
        // assicurati che sia effettivamente online
        if (offlinePlayer.isOnline()) {
            Player player = offlinePlayer.getPlayer();

            // Ora hai tutto il controllo che ti serve
            Job job = event.getJob();
            double expGained = event.getExp();

            var progression = jPlayer.getJobProgression(job);
            if (progression != null) {
                PlayerManager.getInstance().addXP(jPlayer.getPlayer(), (int) Math.round(expGained), false);
                
            }
        }
    }
    */

}