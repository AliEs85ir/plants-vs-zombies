package game.world.minigame;

public interface MultiLevelMinigame {
    int TOTAL_LEVELS = 3;

    int getLevel();

    default int getTotalLevels() {
        return TOTAL_LEVELS;
    }

    default boolean isFinalLevel() {
        return getLevel() >= getTotalLevels();
    }
}
