package game.world.minigame.izombie_couchplay;

import app.enums.obstacle.ObstacleType;
import app.enums.plant.PlantName;
import com.badlogic.gdx.math.Vector2;
import game.component.physic.PhysicService;
import game.engine.GameSimulationEngine;
import game.engine.system.lawnmower.LawnMowerSimulationSystem;
import game.engine.system.sun.SunSimulationSystem;
import game.engine.system.wave.WaveSimulationSystem;
import game.entity.obstacle.ObstacleFactory;
import game.entity.obstacle.ability.BrainAbility;
import game.service.Zombie.ZombieService;
import game.world.GameWorld;
import game.world.levelConditions.conditions.IZombieCondition;
import game.world.minigame.Minigame;
import graphics.GraphicsEngine;

import java.util.List;
import java.util.Random;

public class IZombieCouchPlayMode implements Minigame {
    private Random random = new Random();

    public void setLayoutSeed(long seed) {
        this.random = new Random(seed);
    }

    @Override
    public void onMinigameStart(GameWorld world, GraphicsEngine graphicsEngine, PhysicService physicService) {
        world.setLevelCondition(new IZombieCondition());
        ZombieService service = new ZombieService(world);
        for(Vector2 vector2 : world.getMap().getZombieSpawns()) {
            service.spawnZombie("SunProducer", vector2);
        }
        for(Vector2 vector2 : world.getMap().getMapConfig().getLawnMowerSpawns()) {
            ObstacleFactory.getInstance().createRuntimeObstacle(
                ObstacleType.BRAIN,
                40,
                40,
                vector2.x,
                vector2.y,
                List.of(new BrainAbility())
            );
        }

    }

    @Override
    public void onTick(GameWorld world, long currentTick) {
    }

    @Override
    public void onEngineStart(GameSimulationEngine engine) {
        engine.removeSystem(SunSimulationSystem.class);
        engine.removeSystem(WaveSimulationSystem.class);
        engine.removeSystem(LawnMowerSimulationSystem.class);
    }

    @Override
    public void setWorld(GameWorld world) {
    }

    @Override
    public List<PlantName> getPlants() {
        return List.of();
    }

    @Override
    public List<String> getAvailableZombies() {
        return List.of();
    }
}
