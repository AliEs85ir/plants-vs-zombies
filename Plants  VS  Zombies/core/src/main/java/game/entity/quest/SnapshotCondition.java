package game.entity.quest;

import java.util.function.Predicate;

public class SnapshotCondition implements QuestCondition {
    private final Predicate<LevelFacts> conditionPredicate;
    private boolean isSatisfied;

    public SnapshotCondition(Predicate<LevelFacts> conditionPredicate) {
        this.conditionPredicate = conditionPredicate;
        this.isSatisfied = false;
    }

    @Override
    public void handleEvent(GameEvent event) {
    }

    @Override
    public boolean verifyFinalState(LevelFacts facts) {
        if (isSatisfied) {
            return true;
        }
        if (facts == null || conditionPredicate == null) {
            return false;
        }
        isSatisfied = conditionPredicate.test(facts);
        return isSatisfied;
    }

    @Override
    public int getCurrentProgress() {
        return isSatisfied ? 1 : 0;
    }

    @Override
    public void setCurrentProgress(int currentProgress) {
        this.isSatisfied = currentProgress >= 1;
    }

    @Override
    public int getRequiredProgress() {
        return 1;
    }

    @Override
    public void setRequiredProgress(int requiredProgress) {
    }

    @Override
    public boolean isReached() {
        return isSatisfied;
    }

    @Override
    public void reset() {
        this.isSatisfied = false;
    }
}
