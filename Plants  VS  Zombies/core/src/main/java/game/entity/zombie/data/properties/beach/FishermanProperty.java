package game.entity.zombie.data.properties.beach;

import app.enums.plant.PlantName;
import game.entity.zombie.data.properties.ZombieProperty;

import java.util.List;

public class FishermanProperty extends ZombieProperty {
    private List<String> AllowedLowPlants;
    private boolean canBeFlicked;
    private boolean canBeFlickedOff;
    private boolean canBePlantTossedStrong;
    private boolean canBePlantTossedWeak;
    private boolean canSpawnPlantFood;
    private boolean canSurrender;
    private boolean diesOutOfWater;
    private float castTimePerGridSquare;
    private int castingAreaHeight;
    private int castingAreaMaxRange;
    private int castingAreaMinRange;
    private float delayBeforeReeling;
    private float delayBetweenCasting;
    private double maxTideLoweredPercent;
    private List<PlantName> plantablePlants;
    private double reelTimePerGridSquare;
    private boolean skipHeadDropState;

    @Override
    public float getTimeBetweenCasts() {
        return delayBetweenCasting;
    }
}
