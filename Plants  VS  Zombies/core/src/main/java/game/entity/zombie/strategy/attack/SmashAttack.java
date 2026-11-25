package game.entity.zombie.strategy.attack;

import app.model.App;
import app.view.game.PlayScreen;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.entity.zombie.data.properties.AllStarProperty;
import game.entity.zombie.data.properties.GargantuarProperty;
import game.service.Zombie.ZombieService;
import graphics.general.event.VisualEvent;
import graphics.general.event.ZombieVisualEventType;

public class SmashAttack implements AttackStrategy{

    @Override
    public void executeAttack(Zombie zombie, Plant targetPlant, ZombieService zombieService, long currentTick) {
        if (targetPlant == null || targetPlant.isDead()) {
            return;
        }

        if (currentTick < zombie.getNextAttackTick()) {
            return;
        }

        int damage = zombie.getPropertyData().getSmashDamage();
        long smashDurationTicks = (long) (zombie.getPropertyData().getSmashDuration() * 10);

        //doDamage(zombie, targetPlant, currentTick, damage);
        zombie.setNextAttackTick(currentTick + smashDurationTicks);

        if(zombie.getPropertyData() instanceof AllStarProperty) {

            zombie.pushEvent(ZombieVisualEventType.TACKLE);

            doDamage(zombie, targetPlant, currentTick, damage);
            game.audio.Sfx.bowlingHit();
        }

        if(zombie.getPropertyData() instanceof GargantuarProperty) {
            zombie.pushEvent(ZombieVisualEventType.SMASH);
            App.getCurrentGameEngine().getTaskManager().scheduleRelative(
                currentTick,
                16,
                () -> {
                    PlayScreen.shake(0.5f, 20f);
                    game.audio.Sfx.play(game.audio.AudioId.SFX_GARGANTUAR_THUMP);
                    doDamage(zombie, targetPlant, currentTick, damage);
                }
            );
        }
    }


    private static void doDamage(Zombie zombie, Plant targetPlant, long currentTick, int damage) {
        targetPlant.takeDamage(damage, zombie, currentTick);
    }
}
