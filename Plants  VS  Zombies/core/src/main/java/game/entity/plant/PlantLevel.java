package game.entity.plant;

import app.enums.plant.PlantName;

public class PlantLevel {
    private PlantName plantName;
    private int level = 1;

    public PlantLevel() {
    }

    public PlantLevel(PlantName plantName) {
        this(plantName, 1);
    }

    public PlantLevel(PlantName plantName, int level) {
        this.plantName = plantName;
        this.level = Math.max(1, level);
    }

    public PlantName getPlantName() {
        return plantName;
    }

    public int getLevel() {
        return Math.max(1, level);
    }

    public void setLevel(int level) {
        this.level = Math.max(1, level);
    }
}
