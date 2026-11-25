package game.entity.zombie.ability;

import app.enums.obstacle.ObstacleType;
import app.model.App;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import common.model.Position;
import common.utils.ProjectileMath;
import game.component.contacts.ContactBits;
import game.component.physic.BodyFactory;
import game.entity.Entity;
import game.entity.obstacle.Obstacle;
import game.entity.obstacle.ObstacleFactory;
import game.entity.obstacle.ability.ObstacleAbility;
import game.entity.obstacle.ability.OctopusAbility;
import game.entity.obstacle.ability.TakeDamageAbility;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;
import game.world.GameWorld;
import game.world.playground.Cell;
import game.world.playground.Grid;
import graphics.general.event.ObstacleVisualEventType;
import graphics.general.event.ZombieVisualEventType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class OctopusThrowAbility extends AbstractZombieAbility{
    private long lastThrow = -1;
    private final long cooldown = 300;

    @Override
    public void update(Zombie zombie, ZombieService zombieService, long currentTick) {

        if(canThrow(currentTick) && selectRandomPlant(zombie.getWorld()) != null) {
            zombie.pushEvent(ZombieVisualEventType.OCTOPUS_THROW);
            App.getCurrentGameEngine().getTaskManager().scheduleRelative(
                currentTick,
                5,
                () -> throwOctopus(zombie, zombieService)
            );
            lastThrow = currentTick;
        }
    }

    private boolean canThrow(long currentTick) {
        return lastThrow < 0 || currentTick - lastThrow >= cooldown;
    }

    @Override
    public void onDeath(Zombie zombie) {

    }

    private void throwOctopus(Zombie zombie, ZombieService zombieService) {
        float offsetX = 30;
        float offsetY = 30;
        Position startPos = new Position(zombie.getPosition().getX() - offsetX, zombie.getPosition().getY() + offsetY);
        Plant randomPlant = selectRandomPlant(zombie.getWorld());
        if(randomPlant == null) {
            return;
        }
        ObstacleFactory.getInstance().createRuntimeObstacle(
            ObstacleType.OCTOPUS,
            30,
            30,
            (float) startPos.getX(),
            (float) startPos.getY(),
            List.of(new OctopusAbility(randomPlant, 300))
        );

    }


    private Plant selectRandomPlant(GameWorld world) {
        List<Plant> chosen = new ArrayList<>();
        for(Plant plant : world.getPlants()) {
            if(plant.isCanAct() && isPlantValid(world, plant)){
                chosen.add(plant);
            }
        }
        Collections.shuffle(chosen);
        if(chosen.isEmpty()) {
            return null;
        }
        return chosen.get(0);
    }
    private boolean isPlantValid(GameWorld world, Plant plant) {
        for(Obstacle obstacle : world.getObstacles()) {
            if(obstacle.getType() == ObstacleType.OCTOPUS) {
                for(ObstacleAbility ability : obstacle.getAbilities()) {
                    if(ability instanceof  OctopusAbility octopusAbility) {
                        if(octopusAbility.getTargetPlant().equals(plant)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

}
