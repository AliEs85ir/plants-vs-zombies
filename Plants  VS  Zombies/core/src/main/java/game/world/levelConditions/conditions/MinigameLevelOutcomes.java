package game.world.levelConditions.conditions;

import game.world.GameWorld;
import game.world.minigame.MultiLevelMinigame;

public final class MinigameLevelOutcomes {
    private MinigameLevelOutcomes() {
    }

    public static LevelOutcome cleared(GameWorld world) {
        if (world == null || world.getCurrentChapter() == null) {
            return LevelOutcome.PLAYER_WON;
        }
        if (world.getCurrentChapter().getMinigameLogic() instanceof MultiLevelMinigame multi
            && !multi.isFinalLevel()) {
            return LevelOutcome.NEXT_LEVEL;
        }
        return LevelOutcome.PLAYER_WON;
    }
}
