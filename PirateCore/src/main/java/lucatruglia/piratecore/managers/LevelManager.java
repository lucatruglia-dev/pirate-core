package lucatruglia.piratecore.managers;

public class LevelManager {
    private static LevelManager instance;

    private double multiplier;
    private double firstLevelXP;

    public static LevelManager getInstance() {
        if (instance == null) {
            instance = new LevelManager();
        }
        return instance;
    }

    public void initialize() {
        this.multiplier = ConfigManager.getInstance().getDouble("settings/levels.yml", "multiplier");
        this.firstLevelXP = ConfigManager.getInstance().getDouble("settings/levels.yml", "firstLevelXP");

        instance = this;
    }

    public double getMultiplier(){
        return this.multiplier;
    }

    public double getfirstLevelXP(){
        return this.firstLevelXP;
    }

    public double getTotalXpNeededForLevel(int level) {
        if (level <= 1)
            return 0;

        double totalXp = 0;
        double xpNeeded = firstLevelXP;

        for (int i = 1; i < level; i++) {
            totalXp += xpNeeded;
            xpNeeded = xpNeeded * multiplier;
        }

        return totalXp;
    }

    public double getXpNeededForLevel(int level) {
        if (level <= 1)
            return 0;

        double xpNeeded = firstLevelXP;
        for (int i = 2; i < level; i++) {
            xpNeeded = xpNeeded * multiplier;
        }
        return xpNeeded;
    }

    public int getLevelByXP(double xp) {
        if (xp < firstLevelXP)
            return 1;

        int level = 1;
        double totalXpNeeded = 0;
        double xpNeeded = firstLevelXP;

        while (xp >= totalXpNeeded + xpNeeded) {
            totalXpNeeded += xpNeeded;
            level++;
            xpNeeded = xpNeeded * multiplier;
        }

        return level;
    }

}
