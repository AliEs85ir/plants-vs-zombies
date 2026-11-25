package game.entity.zombie.boss.boss;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import common.model.Position;
import game.entity.plant.Plant;
import game.entity.zombie.ability.ZombieAbility;
import game.entity.zombie.boss.ai.SharkAI;
import game.entity.zombie.data.properties.Property;
import game.entity.zombie.strategy.attack.AttackStrategy;
import game.entity.zombie.strategy.movement.MovementStrategy;
import game.entity.zombie.strategy.takedamage.TakingDamageStrategy;
import game.service.Zombie.ZombieService;
import game.world.GameWorld;
import game.world.playground.Grid;

import java.util.List;

public class Shark extends Zomboss{
    private BehaviorTree<Shark> behaviorTree;


    public Shark(double currentHealth,
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
        behaviorTree = SharkAI.buildTree(this);
    }

    @Override
    public void update(ZombieService zombieService, long currentTick) {
        updateLaneMask();
        super.update(zombieService, currentTick);
        if (shouldEnterStun()) {
            behaviorTree.resetTask();
        }
        behaviorTree.step();
    }
    @Override
    public void updateLaneMask() {
        super.updateLaneMask();
        Grid grid = getWorld().getGrid();
        Position positionDown = new Position(getPosition().getX(), getPosition().getY() - grid.getConfig().getCellHeight());
        int lane2 = getWorld().getGrid().getEntityLane(positionDown);
        laneMask |= (1 << lane2);
    }
}
