package game.service.special_levels;

import app.enums.plant.PlantName;

public final class ConveyorBeltItem {

    private final long id;
    private final PlantName plantName;

    public ConveyorBeltItem(long id, PlantName plantName) {
        this.id = id;
        this.plantName = plantName;
    }

    public long getId() {
        return id;
    }

    public PlantName getPlantName() {
        return plantName;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof ConveyorBeltItem item)) return false;
        return id == item.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
