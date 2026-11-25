package app.enums.zombie;

public enum ZombieStatTier {
    TIER_0("0"),
    TIER_1("1"),
    TIER_2("2"),
    TIER_3("3"),
    TIER_4("4"),
    TIER_5("5"),
    TIER_6("6"),
    TIER_7("7")
    ;
    private final String tierValue;

    ZombieStatTier(String tierValue) {
        this.tierValue = tierValue;
    }

    public String getTierValue() {
        return tierValue;
    }
}
