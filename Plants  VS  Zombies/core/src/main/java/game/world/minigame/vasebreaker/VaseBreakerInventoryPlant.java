package game.world.minigame.vasebreaker;

import app.enums.plant.PlantName;

public class VaseBreakerInventoryPlant {
    private final long id;
    private final PlantName plantName;
    private final long addedTick;
    private final float worldX;
    private final float worldY;

    public static final long LIFETIME_TICKS = 50;

    public VaseBreakerInventoryPlant(long id, PlantName plantName, long addedTick,
                                     float worldX, float worldY) {
        this.id = id;
        this.plantName = plantName;
        this.addedTick = addedTick;
        this.worldX = worldX;
        this.worldY = worldY;
    }

    public VaseBreakerInventoryPlant(long id, PlantName plantName, long addedTick) {
        this(id, plantName, addedTick, 0f, 0f);
    }

    public VaseBreakerInventoryPlant(PlantName plantName, long addedTick) {
        this(-1L, plantName, addedTick);
    }

    public long getId() {
        return id;
    }

    public PlantName getPlantName() {
        return plantName;
    }

    public long getAddedTick() {
        return addedTick;
    }

    public float getWorldX() {
        return worldX;
    }

    public float getWorldY() {
        return worldY;
    }

    public boolean isExpired(long currentTick) {
        return currentTick - addedTick >= LIFETIME_TICKS;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof VaseBreakerInventoryPlant item)) return false;
        return id == item.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
