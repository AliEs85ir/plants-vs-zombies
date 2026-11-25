package game.entity.obstacle.ability;

import app.enums.plant.PlantName;
import app.model.App;
import game.entity.obstacle.Obstacle;
import game.entity.zombie.data.properties.Property;
import game.service.Zombie.ZombieService;
import game.service.obstacle.ObstacleService;
import game.world.GameWorld;
import game.world.chapter.Chapter;
import game.world.chapter.rule.speciallevelRules.VaseBreakerRule;
import game.world.minigame.vasebreaker.VaseBreakerMode;
import game.world.minigame.vasebreaker.VaseBreakerWorld;
import game.world.playground.Cell;
import graphics.general.event.ObstacleVisualEventType;

import java.util.List;
import java.util.Random;

public class VaseAbility implements ObstacleAbility{
    private final Random random;
    private Cell myCell;
    private boolean dropped;
    private boolean broken;

    public VaseAbility(Cell myCell) {
        this.myCell = myCell;
        this.dropped = false;
        this.broken = false;
        this.random = new Random();
    }

    @Override
    public void update(Obstacle obstacle, ObstacleService obstacleService, long currentTick) {
        if(!dropped) {
            obstacle.pushEvent(ObstacleVisualEventType.VASE_DROP);
            dropped = true;
        }
        if(broken) {
            return;
        }
        if(App.getCameraController().didIGotClickedRectangle(obstacle,200,200)) {
            obstacle.pushEvent(ObstacleVisualEventType.VASE_BREAK);
            broken = true;
            game.audio.Sfx.vaseBroken(obstacle.getType() == app.enums.obstacle.ObstacleType.GARGANTUAR_VASE);
            spawnEntity(obstacle, obstacleService, currentTick);
            obstacle.die();
        }


    }

    @Override
    public void onDeath(Obstacle obstacle) {

    }

    public void spawnEntity(Obstacle obstacle, ObstacleService service, long currentTick) {
        switch (obstacle.getType()) {
            case RANDOM_VASE -> {
                List<Property> zombies = service.getWorld().getCurrentChapter() != null
                    ? service.getWorld().getCurrentChapter().getAvailableZombies()
                    : List.of();
                boolean isZombie = random.nextInt(100) < randomVaseZombieChance(service.getWorld())
                    && zombies != null
                    && !zombies.isEmpty();
                if (isZombie) {
                    Property zombie = zombies.get(random.nextInt(zombies.size()));
                    spawnZombie(obstacle, service, zombie.getAlias());
                } else {
                    spawnPlantCard(obstacle, service, currentTick);
                }
            }
            case PLANT_VASE -> spawnPlantCard(obstacle, service, currentTick);
            case GARGANTUAR_VASE -> {
                spawnZombie(obstacle, service, "Gargantuar1");
            }
        }
    }

    private void spawnZombie(Obstacle obstacle, ObstacleService service, String alias) {
        ZombieService zombieService = new ZombieService(service.getWorld());
        zombieService.spawnZombie(alias, obstacle.getPosition());
    }

    private void spawnPlantCard(Obstacle obstacle, ObstacleService service, long currentTick) {
        GameWorld world = service.getWorld();
        if (!(world instanceof VaseBreakerWorld vaseWorld)) {
            return;
        }
        PlantName plantName = pickPlant(world);
        if (plantName == null) {
            return;
        }
        float worldX = (float) obstacle.getPosition().getX();
        float worldY = (float) obstacle.getPosition().getY();
        vaseWorld.addPlantToInventory(plantName, currentTick, worldX, worldY);
        world.addEvent("Plant " + plantName.getLabel()
            + " spawned due to vase break and added to inventory!");
    }

    private int randomVaseZombieChance(GameWorld world) {
        if (world != null
            && world.getCurrentChapter() != null
            && world.getCurrentChapter().getMinigameLogic() instanceof VaseBreakerMode mode) {
            return mode.getRandomVaseZombieChance();
        }
        return 50;
    }

    private PlantName pickPlant(GameWorld world) {
        List<PlantName> pool = resolvePlantPool(world);
        if (pool == null || pool.isEmpty()) {
            return null;
        }
        return pool.get(random.nextInt(pool.size()));
    }

    private List<PlantName> resolvePlantPool(GameWorld world) {
        VaseBreakerRule rule = world.getVaseBreakerRule();
        if (rule != null && !rule.getPlantPool().isEmpty()) {
            return rule.getPlantPool();
        }
        Chapter chapter = world.getCurrentChapter();
        if (chapter != null && chapter.getAvailablePlants() != null
            && !chapter.getAvailablePlants().isEmpty()) {
            return chapter.getAvailablePlants();
        }
        if (world instanceof VaseBreakerWorld vaseWorld
            && vaseWorld.getLogic() instanceof VaseBreakerMode mode) {
            return mode.getAvailablePlants();
        }
        return List.of();
    }
}
