package game.world.chapter.meta;

import app.enums.plant.PlantName;

import java.util.Collections;
import java.util.List;

public class ChapterMetadata {

    private final String description;
    private final String difficulty;
    private final int selectionCount;
    private final List<PlantName> recommendedPlants;

    public ChapterMetadata(String description, String difficulty, int selectionCount,
                           List<PlantName> recommendedPlants) {
        this.description = (description != null) ? description : "";
        this.difficulty = (difficulty != null) ? difficulty : "";
        this.selectionCount = selectionCount;
        this.recommendedPlants = (recommendedPlants != null)
            ? Collections.unmodifiableList(recommendedPlants)
            : Collections.emptyList();
    }

    public String getDescription() {
        return description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getSelectionCount() {
        return selectionCount;
    }

    public List<PlantName> getRecommendedPlants() {
        return recommendedPlants;
    }
}
