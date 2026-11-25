package game.entity.zombie.data.properties.iceAge;

import game.entity.zombie.data.properties.ZombieProperty;
import game.entity.zombie.data.zombieActions.ZombieAction;

import java.util.List;

public class HunterProperty extends ZombieProperty {
    private List<ZombieAction> actions;
    private boolean chillInsteadOfFreeze;
    private int farAttackRange;
    private int nearAttackRange;
    private int snowballPerBarrage;
    private int numberOfIceBlocksToSpawnWith;

}
