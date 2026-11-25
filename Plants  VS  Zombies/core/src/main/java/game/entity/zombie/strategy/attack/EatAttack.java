package game.entity.zombie.strategy.attack;

import app.model.App;
import game.engine.time.GameClock;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;
import graphics.general.event.VisualEvent;
import graphics.general.event.ZombieVisualEventType;
import graphics.zombie.ZombieRenderInstance;

public class EatAttack implements AttackStrategy{
    @Override
    public void executeAttack(Zombie zombie, Plant targetPlant, ZombieService zombieService, long currentTick) {
        if (targetPlant == null || targetPlant.isDead()) {
            return;
        }

        double damagePerSecond = zombie.getPropertyData().getEatDPS() * zombie.getEatSpeedMultiplier();
        targetPlant.takeDamage(damagePerSecond / GameClock.TICKS_PER_SECOND, zombie , currentTick);

        zombie.pushEvent(ZombieVisualEventType.EAT);
    }
}
