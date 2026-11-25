package game.entity.zombie.strategy.attack;

import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.entity.zombie.strategy.movement.MovementStrategy;
import game.service.Zombie.ZombieService;

public class SwapAfterAttack implements AttackStrategy{

    private final AttackStrategy initialAttack;
    private final AttackStrategy nextAttack;
    private final MovementStrategy nextMovement;

    public SwapAfterAttack(AttackStrategy initialAttack,
                                   AttackStrategy nextAttack,
                                   MovementStrategy nextMovement) {
        this.initialAttack = initialAttack;
        this.nextAttack = nextAttack;
        this.nextMovement = nextMovement;
    }

    @Override
    public void executeAttack(Zombie zombie, Plant targetPlant, ZombieService zombieService, long currentTick) {
        initialAttack.executeAttack(zombie, targetPlant, zombieService, currentTick);
        zombie.setMovementStrategy(nextMovement);
        zombie.setAttackStrategy(nextAttack);
        zombie.setMoveSpeedMultiplier(1);
    }
}
