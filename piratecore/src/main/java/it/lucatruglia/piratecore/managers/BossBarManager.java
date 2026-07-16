package it.lucatruglia.piratecore.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import it.lucatruglia.piratecore.piratecore;

public class BossBarManager {

    private static BossBarManager instance;

    private final Map<UUID, BossBar> bars = new HashMap<>();

    private BossBarManager() {
    }

    public static BossBarManager getInstance() {
        if (instance == null) {
            instance = new BossBarManager();
        }

        return instance;
    }

    public void show(Player player, String title, double progress) {
        this.hide(player);

        BossBar bossBar = Bukkit.createBossBar(
                this.getKey(player),
                title,
                BarColor.RED,
                BarStyle.SEGMENTED_10);

        bossBar.setProgress(Math.max(0.0D, Math.min(1.0D, progress)));
        bossBar.addPlayer(player);

        this.bars.put(player.getUniqueId(), bossBar);
    }

    public void hide(Player player) {
        BossBar bossBar = this.bars.remove(player.getUniqueId());

        if (bossBar != null) {
            bossBar.removePlayer(player);
            bossBar.removeAll();
        }
    }

    public void hideAll() {
        for (BossBar bossBar : this.bars.values()) {
            bossBar.removeAll();
        }

        this.bars.clear();
    }

    public boolean hasBar(Player player) {
        return this.bars.containsKey(player.getUniqueId());
    }

    private NamespacedKey getKey(Player player) {
        return new NamespacedKey(piratecore.getInstance(), "bossbar_" + player.getUniqueId());
    }
}