package server.quest;

public class QuestStateDTO {
    private String questName;
    private int currentProgress;
    private int currentTierIndex;
    private boolean completed;
    private String targetPlant;
    private String targetCategory;
    private int targetValue;

    public QuestStateDTO() {
    }

    public QuestStateDTO(String questName, int currentProgress, int currentTierIndex, boolean completed,
                         String targetPlant, String targetCategory, int targetValue) {
        this.questName = questName;
        this.currentProgress = currentProgress;
        this.currentTierIndex = currentTierIndex;
        this.completed = completed;
        this.targetPlant = targetPlant;
        this.targetCategory = targetCategory;
        this.targetValue = targetValue;
    }

    public String getQuestName() {
        return questName;
    }

    public void setQuestName(String questName) {
        this.questName = questName;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
    }

    public int getCurrentTierIndex() {
        return currentTierIndex;
    }

    public void setCurrentTierIndex(int currentTierIndex) {
        this.currentTierIndex = currentTierIndex;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getTargetPlant() {
        return targetPlant;
    }

    public void setTargetPlant(String targetPlant) {
        this.targetPlant = targetPlant;
    }

    public String getTargetCategory() {
        return targetCategory;
    }

    public void setTargetCategory(String targetCategory) {
        this.targetCategory = targetCategory;
    }

    public int getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(int targetValue) {
        this.targetValue = targetValue;
    }
}
