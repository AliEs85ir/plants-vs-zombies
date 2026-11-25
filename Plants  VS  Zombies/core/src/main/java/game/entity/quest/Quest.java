package game.entity.quest;

import app.enums.quest.QuestCategory;
import app.enums.quest.QuestName;
import app.enums.quest.QuestPriority;

import java.util.List;

public class Quest {
    private final QuestName name;
    private final String title;
    private final String description;
    private final QuestCategory category;
    private final QuestPriority priority;
    private final QuestCondition condition;
    private final List<QuestReward> rewards;
    private final List<Integer> progressionTiers;
    private int currentTierIndex;
    private boolean completed;

    private String targetPlant;
    private String targetCategory;
    private int targetValue = -1;

    public Quest(QuestName name, String title, String description, QuestCategory category,
                 QuestPriority priority, QuestCondition condition, List<QuestReward> rewards,
                 List<Integer> progressionTiers) {
        this.name = name;
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.condition = condition;
        this.rewards = rewards;
        this.progressionTiers = progressionTiers;
        this.currentTierIndex = 0;
        this.completed = false;

        if (progressionTiers != null && !progressionTiers.isEmpty() && condition != null) {
            this.condition.setRequiredProgress(progressionTiers.get(0));
        }
    }

    public Quest(QuestName name, String title, String description, QuestCategory category,
                 QuestPriority priority, QuestCondition condition, List<QuestReward> rewards) {
        this(name, title, description, category, priority, condition, rewards, null);
    }

    public void updateProgress(GameEvent event) {
        if (completed || condition == null) return;
        condition.handleEvent(event);
    }

    public boolean evaluateEndState(LevelFacts facts) {
        if (completed) return true;
        if (condition == null) return false;
        return condition.verifyFinalState(facts);
    }

    public boolean checkAndAdvanceTier() {
        if (completed || condition == null) return false;
        if (condition.isReached()) {
            if (progressionTiers != null && currentTierIndex < progressionTiers.size() - 1) {
                currentTierIndex++;
                condition.reset();
                condition.setRequiredProgress(progressionTiers.get(currentTierIndex));
                return false;
            } else {
                completed = true;
                return true;
            }
        }
        return false;
    }

    public QuestName getName() { return name; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public QuestCategory getCategory() { return category; }
    public QuestPriority getPriority() { return priority; }
    public QuestCondition getCondition() { return condition; }
    public List<QuestReward> getRewards() { return rewards; }
    public boolean isCompleted() { return completed; }

    public boolean isReadyToClaim() {
        return !completed && condition != null && condition.isReached();
    }

    public void restoreProgress(int currentProgress, int currentTierIndex, boolean completed) {
        if (condition == null) {
            this.completed = completed;
            return;
        }
        if (progressionTiers != null && !progressionTiers.isEmpty()) {
            int lastIndex = progressionTiers.size() - 1;
            this.currentTierIndex = Math.max(0, Math.min(currentTierIndex, lastIndex));
            condition.setRequiredProgress(progressionTiers.get(this.currentTierIndex));
        } else {
            this.currentTierIndex = Math.max(0, currentTierIndex);
        }
        condition.setCurrentProgress(Math.max(0, currentProgress));
        this.completed = completed;
    }
    public int getCurrentTier() { return currentTierIndex + 1; }
    public int getCurrentTierIndex() { return currentTierIndex; }
    public List<Integer> getProgressionTiers() { return progressionTiers; }

    public String getTargetPlant() { return targetPlant; }
    public void setTargetPlant(String targetPlant) { this.targetPlant = targetPlant; }

    public String getTargetCategory() { return targetCategory; }
    public void setTargetCategory(String targetCategory) { this.targetCategory = targetCategory; }

    public int getTargetValue() { return targetValue; }
    public void setTargetValue(int targetValue) { this.targetValue = targetValue; }
}
