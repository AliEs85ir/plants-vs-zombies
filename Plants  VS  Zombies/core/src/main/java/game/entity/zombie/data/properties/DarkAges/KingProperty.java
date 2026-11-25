package game.entity.zombie.data.properties.DarkAges;

import app.enums.plant.PlantName;
import app.enums.zombie.KnightTargets;
import game.entity.zombie.data.properties.ZombieProperty;

import java.util.List;

public class KingProperty extends ZombieProperty {
    private boolean canBeFlicked;
    private boolean canBeFlickedOff;
    private boolean canBePlantTossedStrong;
    private boolean canBePlantTossedWeak;
    private boolean canSurrender;
    private double delayBetweenKnightings;
    private String knightHelm;
    private int knightingAreaX;
    private int knightingAreaY;
    private List<PlantName> plantablePlants;
    private boolean skipHeadDropState;
    private List<KnightTargets> validKnightTargets;

}
