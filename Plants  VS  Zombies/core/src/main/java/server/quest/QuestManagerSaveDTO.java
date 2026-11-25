package server.quest;

import java.util.List;

public class QuestManagerSaveDTO {
    private String lastResetDate;
    private List<QuestStateDTO> activeQuests;
    private List<QuestStateDTO> completedQuests;

    public QuestManagerSaveDTO() {
    }

    public QuestManagerSaveDTO(String lastResetDate, List<QuestStateDTO> activeQuests, List<QuestStateDTO> completedQuests) {
        this.lastResetDate = lastResetDate;
        this.activeQuests = activeQuests;
        this.completedQuests = completedQuests;
    }

    public String getLastResetDate() {
        return lastResetDate;
    }

    public void setLastResetDate(String lastResetDate) {
        this.lastResetDate = lastResetDate;
    }

    public List<QuestStateDTO> getActiveQuests() {
        return activeQuests;
    }

    public void setActiveQuests(List<QuestStateDTO> activeQuests) {
        this.activeQuests = activeQuests;
    }

    public List<QuestStateDTO> getCompletedQuests() {
        return completedQuests;
    }

    public void setCompletedQuests(List<QuestStateDTO> completedQuests) {
        this.completedQuests = completedQuests;
    }
}
