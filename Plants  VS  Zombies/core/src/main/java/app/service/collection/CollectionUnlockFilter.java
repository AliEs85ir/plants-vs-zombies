package app.service.collection;

/** Unlock / discovery filter for collection grids. Plants use unlocked; zombies use seen/discovered. */
public enum CollectionUnlockFilter {
    ALL,
    UNLOCKED,
    LOCKED,
    DISCOVERED,
    UNDISCOVERED;

    public static CollectionUnlockFilter plantDefault() {
        return ALL;
    }

    public static CollectionUnlockFilter zombieDefault() {
        return ALL;
    }

    public boolean isPlantStatus() {
        return this == ALL || this == UNLOCKED || this == LOCKED;
    }

    public boolean isZombieStatus() {
        return this == ALL || this == DISCOVERED || this == UNDISCOVERED;
    }
}
