package game.service.plant;

import app.enums.plant.PlantName;
import app.model.App;
import common.model.Position;
import game.audio.Sfx;
import game.entity.plant.Plant;
import game.entity.projectile.Projectile;
import game.service.sun.SunSpawner;
import game.world.GameWorld;
import graphics.general.effects.EffectLayer;
import graphics.general.effects.EffectRequest;
import graphics.general.effects.anchers.WorldAnchor;

public class PlantService {
    private final GameWorld gameWorld;
    private final SunSpawner sunSpawner;

    public PlantService(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        this.sunSpawner = new SunSpawner(gameWorld);
    }

    public SunSpawner getSunSpawner() {
        return sunSpawner;
    }

    public void addProjectile(Projectile projectile){
        Sfx.onEvent(projectile.getOwner());
        gameWorld.addProjectile(projectile);
        if (App.getRealTimeEngine() != null && App.getRealTimeEngine().getGraphicsEngine() != null) {
            App.getRealTimeEngine().getGraphicsEngine().registerProjectile(projectile);
            addEffect(projectile);
        }
    }

    private void addEffect(Projectile projectile)
    {
        Position position1 = projectile.getPosition();
        Position position = new Position(position1.getX()-50 , position1.getY());
        Plant plant =  projectile.getOwner();
        PlantName name = plant.getDefinition().getName();

        switch (name)
        {
            case THREEPEATER,MEGA_GATLING_PEA -> App.getRealTimeEngine().getGraphicsEngine().getEffectManager().spawn(
                new EffectRequest<>(
                    "new projectile",
                    null,
                    new WorldAnchor(position),
                    EffectLayer.FRONT
                )
            );
        }

    }

    public GameWorld getGameWorld() {
        return gameWorld;
    }

    public void removePlant(Plant plant) {
        if (plant != null && !plant.isDead()) {
            plant.markDead(gameWorld);
        }
    }
}
