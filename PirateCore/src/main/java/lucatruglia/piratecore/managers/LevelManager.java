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
            return 0;

        long totalXp = 0;
        long xpNeeded = firstLevelXP;

        for (int i = 1; i < level; i++) {
            totalXp += xpNeeded;
            xpNeeded = (long) (xpNeeded * multiplier);
        }

        return totalXp;
    }

    public long getXpNeededForLevel(int level) {
        if (level <= 1)
            return 0;

        long xpNeeded = firstLevelXP;
        for (int i = 2; i < level; i++) {
            xpNeeded = (long) (xpNeeded * multiplier);
        }
        return xpNeeded;
    }

    public int getLevelByXP(long xp) {
        if (xp < firstLevelXP)
            return 1;

        int level = 1;
        long totalXpNeeded = 0;
        long xpNeeded = firstLevelXP;

        while (xp >= totalXpNeeded + xpNeeded) {
            totalXpNeeded += xpNeeded;
            level++;
            xpNeeded = (long) (xpNeeded * multiplier);
        }

        return level;
    }

}
