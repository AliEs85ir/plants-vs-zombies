package game.entity.zombie.boss.boss;

import app.enums.obstacle.ObstacleType;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import common.model.Position;
import game.entity.obstacle.Obstacle;
import game.entity.obstacle.ability.GlacierAbility;
import game.entity.obstacle.ability.ObstacleAbility;
import game.entity.plant.Plant;
import game.entity.zombie.ability.ZombieAbility;
import game.entity.zombie.boss.ai.MammothAI;
import game.entity.zombie.data.properties.Property;
import game.entity.zombie.strategy.attack.AttackStrategy;
import game.entity.zombie.strategy.movement.MovementStrategy;
import game.entity.zombie.strategy.takedamage.TakingDamageStrategy;
import game.service.Zombie.ZombieService;
import game.world.GameWorld;
import game.world.playground.Grid;

import java.util.List;

public class Mammoth extends Zomboss {

    private BehaviorTree<Mammoth> behaviorTree;


    public Mammoth(double currentHealth,
                        MovementStrategy movementStrategy,
                        AttackStrategy attackStrategy,
                        TakingDamageStrategy takingDamageStrategy,
                        Position position, List<ZombieAbility> abilities,
                        GameWorld world, Plant targetPlant,
                        Property propertyData) {

        super(currentHealth,
            movementStrategy,
            attackStrategy,
            takingDamageStrategy,
            position, abilities,
            world, targetPlant, propertyData);
        behaviorTree = MammothAI.buildTree(this);
    }

    @Override
    public void update(ZombieService zombieService, long currentTick) {
        updateLaneMask();
        super.update(zombieService, currentTick);
        if (hasFrontGlacier() && isStunned()) {
            endStun();
        }
        if (shouldEnterStun()) {
            behaviorTree.resetTask();
        }
        behaviorTree.step();
    }

    @Override
    public void updateLaneMask() {
        super.updateLaneMask();
        Grid grid = getWorld().getGrid();
        Position positionDown = new Position(getPosition().getX(),
            getPosition().getY() - grid.getConfig().getCellHeight());
        int lane2 = getWorld().getGrid().getEntityLane(positionDown);
        Position up = new Position(getPosition().getX(),
            getPosition().getY() + grid.getConfig().getCellHeight());
        int lane3 = getWorld().getGrid().getEntityLane(up);
        laneMask |= (1 << lane2) | (1 << lane3);
    }

    @Override
    public boolean shouldEnterStun() {
        return !isStunned() && !hasFrontGlacier();
    }

    public boolean hasFrontGlacier() {
        for (Obstacle obstacle : getWorld().getObstacles()) {
            if (obstacle.getType() == ObstacleType.MAMMOTH_GLACIER) {
                for(ObstacleAbility ability : obstacle.getAbilities()) {
                    if(ability instanceof GlacierAbility glacierAbility) {
                        if(!glacierAbility.isDead()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
