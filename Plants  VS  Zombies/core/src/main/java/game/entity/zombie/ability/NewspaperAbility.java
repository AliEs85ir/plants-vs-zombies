package game.entity.zombie.ability;

import app.model.App;
import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;

public class NewspaperAbility extends AbstractZombieAbility{
    private boolean applied = false;


    @Override
    public void update(Zombie zombie, ZombieService zombieService, long currentTick) {
        if(applied) return;
        if(zombie.getArmors().isEmpty()) {
            zombieService.getWorld().addEvent("______________!!!!!Newspaper got destroyed! OLD MAN GOT ANGRY!!!!______________");
            game.audio.Sfx.newspaperRipped();
            zombie.setMoveSpeedMultiplier(0);
            App.getCurrentGameEngine().getTaskManager().scheduleRelative(
                currentTick,
                10,
                () -> applyModifiers(zombie)
            );
            applied = true;
        }
    }

    private static void applyModifiers(Zombie zombie) {
        zombie.setEatSpeedMultiplier(1.1);
        zombie.setMoveSpeedMultiplier(1.3);
    }

    @Override
    public void onDeath(Zombie zombie) {

    }
}
