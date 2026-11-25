package game.entity.zombie.ability;

import app.enums.obstacle.ObstacleType;
import app.model.App;
import game.entity.obstacle.ObstacleFactory;
import game.entity.obstacle.ability.TakeDamageAbility;
import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;
import game.world.GameWorld;
import game.world.playground.Cell;
import graphics.general.effects.EffectLayer;
import graphics.general.effects.EffectRequest;
import graphics.general.effects.anchers.WorldAnchor;
import graphics.general.event.ZombieVisualEventType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TombRaiserAbility extends AbstractZombieAbility{

    private final long COOLDOWN_TICKS = 400;
    private final int TOMBS_PER_CAST;
    private long lastCastTick = -1;
    private int remainingAmmo;


    public TombRaiserAbility() {
        this.TOMBS_PER_CAST = 2;
        this.remainingAmmo = 5;

    }

    @Override
    public void update(Zombie zombie, ZombieService zombieService, long currentTick) {

        if(canRaiseTomb(currentTick)) {
            lastCastTick = currentTick;
            zombie.pushEvent(ZombieVisualEventType.SPAWN_TOMB);
            App.getCurrentGameEngine().getTaskManager().scheduleRelative(
                currentTick,
                20,
                () -> raiseTomb(zombie, zombieService, currentTick)
            );
        }
    }


    @Override
    public void onDeath(Zombie zombie) {

    }


    private boolean canRaiseTomb(long currentTick) {
        if (remainingAmmo <= 0) {
            return false;
        }
        return lastCastTick < 0 || (currentTick - lastCastTick) >= COOLDOWN_TICKS;
    }

    private void raiseTomb(Zombie zombie, ZombieService zombieService, long currentTick) {
        List<Cell> tombCells = zombieService.giveRandomCells(TOMBS_PER_CAST);
        for(Cell cell : tombCells) {
            App.getCurrentGameEngine().getTaskManager().scheduleRelative(
                currentTick,
                10,
                () -> spawnTomb(cell)
            );
            App.getRealTimeEngine().getGraphicsEngine().getEffectManager().spawn(
                new EffectRequest<>("spawn tomb",
                    null,
                    new WorldAnchor(cell.getWorldPosition()),
                    EffectLayer.FRONT )
            );
            game.audio.Sfx.play(game.audio.AudioId.SFX_DIRT_RISE);
            game.audio.Sfx.play(game.audio.AudioId.SFX_GRAVESTONE_RUMBLE);
        }
    }

    public static void spawnTomb(Cell cell) {
        if (cell == null || cell.hasPlant() || cell.hasObstacle()) {
            return;
        }
        ObstacleFactory.getInstance().createRuntimeObstacle(
            ObstacleType.EGYPT_GRAVE,
            40,
            80,
            cell.getWorldX(),
            cell.getWorldY(),
            List.of(new TakeDamageAbility(400))
        );
    }


}
