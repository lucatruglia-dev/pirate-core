package lucatruglia.piratecore.managers.treasure;

public enum Rarity {
    COMMON(0), RARE(1), EPIC(2), LEGENDARY(3);

    private Rarity(int val) {
        this.val = val;
    }

    private int val;

    public int getVal() {
        return this.val;
    }

}
