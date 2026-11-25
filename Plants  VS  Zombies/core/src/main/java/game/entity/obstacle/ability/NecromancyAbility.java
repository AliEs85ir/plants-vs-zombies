package game.entity.obstacle.ability;

import app.model.App;
import game.entity.obstacle.Obstacle;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;
import game.service.obstacle.ObstacleService;
import game.world.GameWorld;
import game.world.playground.Cell;
import graphics.general.effects.EffectLayer;
import graphics.general.effects.EffectRequest;
import graphics.general.effects.anchers.WorldAnchor;
import graphics.general.event.ObstacleVisualEventType;
import graphics.general.modifier.visualModifiers.RedDamageModifier;
import graphics.general.modifier.visualModifiers.WhiteModifier;
import graphics.plant.PlantRenderInstance;
import graphics.zombie.ZombieRenderInstance;

public class NecromancyAbility implements ObstacleAbility{
    private Cell myCell;
    private Zombie myZombie;
    private boolean addedSpawnEffect;

    public NecromancyAbility(Cell myCell) {
        this.myCell = myCell;
        this.addedSpawnEffect = false;
    }

    @Override
    public void update(Obstacle obstacle, ObstacleService obstacleService, long currentTick) {
        if(myCell.hasPlant()) {
            for(Plant plant : myCell.getPlants()) {
                PlantRenderInstance instance = App.getRealTimeEngine()
                    .getGraphicsEngine().getPlantInstance(plant);
                if(instance != null) {
                    instance.getModifierController().add(new RedDamageModifier(1f));
                    plant.takeDamage(2, null, currentTick);
                }
            }
        }
        if(!addedSpawnEffect) {
            ZombieRenderInstance instance = App.getRealTimeEngine()
                .getGraphicsEngine().getZombieInstance(myZombie);
            if(instance != null) {
                instance.getModifierController().add(new WhiteModifier());
                addedSpawnEffect = true;
                App.getCurrentGameEngine().getTaskManager().scheduleRelative(
                    currentTick,
                    20,
                    () -> obstacle.pushEvent(ObstacleVisualEventType.IDLE)
                );
            }
        }
    }

    @Override
    public void onDeath(Obstacle obstacle) {

    }


    public void onWaveStart(GameWorld world, int waveNumber, Obstacle obstacle) {
        if(!myCell.hasObstacle()) {
            return;
        }
        addedSpawnEffect = false;
        obstacle.pushEvent(ObstacleVisualEventType.SPAWN_ZOMBIE);
        EffectRequest<Obstacle> request = new EffectRequest<>(
            "dirt spawn",
            null,
            new WorldAnchor(obstacle.getPosition()),
            EffectLayer.FRONT
        );
        App.getRealTimeEngine().getGraphicsEngine().getEffectManager().spawn(request);

        game.audio.Sfx.play(game.audio.AudioId.SFX_DIRT_RISE);
        game.audio.Sfx.play(game.audio.AudioId.SFX_GRAVESTONE_RUMBLE);
        ZombieService service = new ZombieService(world);
        myZombie = service.spawnZombie("ZombieNewspaper", obstacle.getPosition());
    }
}
