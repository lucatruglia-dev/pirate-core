package lucatruglia.piratecore.managers;

public class LevelManager {
    private static LevelManager instance;

    private double multiplier;
    private long firstLevelXP;

    public static LevelManager getInstance() {
        if (instance == null) {
            instance = new LevelManager();
        }
        return instance;
    }

    public void initialize() {
        this.multiplier = ConfigManager.getInstance().getDouble("settings/levels.yml", "multiplier");
        this.firstLevelXP = ConfigManager.getInstance().getInt("settings/levels.yml", "firstLevelXP");

        instance = this;
    }

    public double getMultiplier(){
        return this.multiplier;
    }

    public long getfirstLevelXP(){
        return this.firstLevelXP;
    }

    public long getTotalXpNeededForLevel(int level) {
        if (level <= 1)
            return firstLevelXP; // Per il livello 1 servono 1000 XP totali

        long totalXp = 0;
        long xpNeeded = firstLevelXP;

        for (int i = 1; i <= level; i++) {
            totalXp += xpNeeded;
            xpNeeded = (long) (xpNeeded * multiplier);
        }

        return totalXp;
    }

    public long getXpNeededForLevel(int level) {
        if (level <= 1)
            return firstLevelXP;

        long xpNeeded = firstLevelXP;
        for (int i = 1; i < level; i++) {
            xpNeeded = (long) (xpNeeded * multiplier);
        }
        return xpNeeded;
    }

    public int getLevelByXP(long xp) {
        if (xp < firstLevelXP)
            return 0;

        int level = 1;
        long xpNeeded = firstLevelXP;
        long totalXpNeeded = firstLevelXP;

        while (totalXpNeeded <= xp) {
            level++;
            xpNeeded = (long) (xpNeeded * multiplier);
            totalXpNeeded += xpNeeded;
        }

        return level;
    }

}
