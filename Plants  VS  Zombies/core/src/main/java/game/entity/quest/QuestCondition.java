package game.entity.quest;

public interface QuestCondition {
    void handleEvent(GameEvent event);
    boolean verifyFinalState(LevelFacts facts);
    int getCurrentProgress();
    void setCurrentProgress(int currentProgress);
    int getRequiredProgress();
    void setRequiredProgress(int requiredProgress);
    boolean isReached();
    void reset();
}
