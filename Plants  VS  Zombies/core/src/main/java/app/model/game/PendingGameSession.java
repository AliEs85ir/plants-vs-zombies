package app.model.game;

import app.enums.plant.PlantName;
import game.world.chapter.Chapter;
import game.world.minigame.Minigame;

import java.util.ArrayList;
import java.util.List;

public class PendingGameSession {
    private Chapter selectedChapter;
    private final List<PlantName> selectedPlants = new ArrayList<>();

    public Chapter getSelectedChapter() { return selectedChapter; }
    public void setSelectedChapter(Chapter chapter) {
        this.selectedChapter = chapter;
    }

    // Helper methods
    public boolean isMinigame() {
        return selectedChapter != null && selectedChapter.isMinigame();
    }

    public Minigame getMinigameLogic() {
        if (!isMinigame()) return null;
        return selectedChapter.getMinigameLogic();
    }

    public List<PlantName> getSelectedPlants() {
        return selectedPlants;
    }

    public void clear() {
        selectedChapter = null;
        selectedPlants.clear();
    }

    public void addPlant(PlantName plantName) {
        selectedPlants.add(plantName);
    }

}
