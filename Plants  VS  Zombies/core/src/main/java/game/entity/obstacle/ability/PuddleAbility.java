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

public class PuddleAbility implements ObstacleAbility{
    private Cell myCell;
    private Zombie myZombie;
    private boolean addedSpawnEffect;
    private boolean registered;

    public PuddleAbility(Cell myCell) {
        this.myCell = myCell;
        this.addedSpawnEffect = false;
        this.registered = true;
    }

    @Override
    public void update(Obstacle obstacle, ObstacleService obstacleService, long currentTick) {
        if(!updateDisplay(obstacle)) {
            return;
        }
        // if plant on me , kill it
        if(!addedSpawnEffect) {
            ZombieRenderInstance instance = App.getRealTimeEngine()
                .getGraphicsEngine().getZombieInstance(myZombie);
            if(instance != null) {
                instance.getModifierController().add(new WhiteModifier());
                addedSpawnEffect = true;
            }
        }
    }

    @Override
    public void onDeath(Obstacle obstacle) {

    }


    public void onWaveStart(GameWorld world, int waveNumber, Obstacle obstacle) {
        if(!updateDisplay(obstacle)) {
            return;
        }
        addedSpawnEffect = false;
        obstacle.pushEvent(ObstacleVisualEventType.PUDDLE_EXIT);
        EffectRequest<Obstacle> request = new EffectRequest<>(
            "impact plant death",
            null,
            new WorldAnchor(obstacle.getPosition()),
            EffectLayer.FRONT
        );
        App.getRealTimeEngine().getGraphicsEngine().getEffectManager().spawn(request);

        game.audio.Sfx.play(game.audio.AudioId.VOX_DOLPHIN_APPEARS);
        game.audio.Sfx.play(game.audio.AudioId.SFX_ZOMBIE_ENTERING_WATER);
        ZombieService service = new ZombieService(world);
        String alias = findAppropriateZombie(obstacle, world);
        myZombie = service.spawnZombie(alias, obstacle.getPosition());
    }

    private String findAppropriateZombie(Obstacle obstacle, GameWorld world) {
        switch (obstacle.getType()) {
            case GARGANTUAR_PUDDLE -> {
                return "Gargantuar5";
            }
            case PUDDLE -> {
                return "ZombieBeachSnorkel";
            }
        }
        return null;
    }

    private boolean updateDisplay(Obstacle obstacle) {
        if(myCell.isWater()) {
            if(!registered) {
                App.getRealTimeEngine().getGraphicsEngine().registerObstacle(obstacle);
                myCell.setObstacle(obstacle);
                registered = true;
            }
            return true;
        }
        else {
            if(registered) {
                App.getRealTimeEngine().getGraphicsEngine().unregisterObstacle(obstacle);
                myCell.setObstacle(null);
                registered = false;
            }
            return false;
        }
    }
}
