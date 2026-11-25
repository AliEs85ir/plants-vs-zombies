package game.entity.zombie.ability;

import app.model.App;
import com.badlogic.gdx.physics.box2d.Filter;
import game.component.contacts.ContactBits;
import game.entity.effect.Effect;
import game.entity.effect.NoActEffect;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;
import game.world.GameWorld;
import graphics.general.effects.EffectConditions;
import graphics.general.effects.EffectLayer;
import graphics.general.effects.EffectRequest;
import graphics.general.effects.anchers.WorldAnchor;
import graphics.general.event.ZombieVisualEventType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class WizardAbility extends AbstractZombieAbility{
    List<Plant> castedPlants = new ArrayList<>();
    private long lastThrow = -1;
    private final long cooldown = 150;


    @Override
    public void update(Zombie zombie, ZombieService zombieService, long currentTick) {
        if(!canCast(currentTick) || zombie.getWorld().getPlants().isEmpty()) {
            return;
        }
        Plant target = choosePlant(zombie);
        if(target == null) {
            return;
        }
        zombie.pushEvent(ZombieVisualEventType.TURN_INTO_SHEEP);
        game.audio.Sfx.play(game.audio.AudioId.VOX_EVILLAUGH);
        App.getCurrentGameEngine().getTaskManager().scheduleRelative(
            currentTick,
            10,
            () -> sheep(zombie, target)
        );

        lastThrow = currentTick;

    }

    private static Plant choosePlant(Zombie zombie) {
        List<Plant> plants = zombie.getWorld().getPlants();
        Collections.shuffle(plants);
        for(Plant plant : plants) {
            if(plant.isCanAct()) {
                return plant;
            }
        }
        return null;
    }

    private void sheep(Zombie zombie, Plant target) {
        if(target.isDead() || !target.isCanAct()) {
            return;
        }
        target.setCanAct(false);
        EffectRequest<Plant> request = new EffectRequest<>(
            "sheep transformation",
            target,
            new WorldAnchor(target.getPosition()),
            EffectLayer.FRONT,
            EffectConditions.sheepCondition(zombie, target)
        );
        Filter filter = target.getBody().getFixtureList().first().getFilterData();
        filter.maskBits = ContactBits.OBSTACLE;
        App.getRealTimeEngine().getGraphicsEngine().unregisterPlant(target);
        App.getRealTimeEngine().getGraphicsEngine().getEffectManager().spawn(request);
        castedPlants.add(target);
    }

    private boolean canCast(long currentTick) {
        return lastThrow < 0 || currentTick - lastThrow > cooldown;
    }

    @Override
    public void onDeath(Zombie zombie) {
        for(Plant plant : castedPlants) {
            if(!plant.isDead()) {
                plant.setCanAct(true);
                App.getRealTimeEngine().getGraphicsEngine().registerPlant(plant);
                Filter filter = plant.getBody().getFixtureList().first().getFilterData();
                filter.maskBits = ContactBits.OBSTACLE | ContactBits.ZOMBIE;
                EffectRequest<Object> request = new EffectRequest<>(
                    "sheep die",
                    null,
                    new WorldAnchor(plant.getPosition()),
                    EffectLayer.FRONT
                );
                App.getRealTimeEngine().getGraphicsEngine().getEffectManager().spawn(request);
            }
        }
    }

}
