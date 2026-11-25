package game.entity.zombie.ability;

import app.model.App;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import common.model.Position;
import common.utils.ProjectileMath;
import game.component.contacts.ContactBits;
import game.entity.zombie.Zombie;
import game.entity.zombie.strategy.movement.FlyingMovement;
import game.service.Zombie.ZombieService;
import game.world.playground.Grid;
import graphics.general.event.ZombieVisualEventType;
import graphics.general.instance.VisibilityController;

public class ImpThrowAbility extends AbstractZombieAbility{

    private boolean hasThrownImp = false;

    @Override
    public void update(Zombie zombie, ZombieService zombieService, long currentTick) {
        if (hasThrownImp) return;

        double healthRatio = zombie.getCurrentHealth() / zombie.getPropertyData().getInitialHealth();
        if (healthRatio <= 0.7) {
            zombie.pushEvent(ZombieVisualEventType.FIRE);
            App.getCurrentGameEngine().getTaskManager().scheduleRelative(
                currentTick, 8, () -> throwImp(zombie, zombieService)
            );
            hasThrownImp = true;
        }
    }

    private void throwImp(Zombie zombie, ZombieService zombieService) {
        float offsetX = 70;
        float offsetY = 180;
        Position startPx = new Position(zombie.getPosition().getX() - offsetX, zombie.getPosition().getY() + offsetY);

        Position endPx = new Position(
            startPx.getX() - 500,
            zombie.getPosition().getY()
        );
        String number = zombie.getName().replaceAll("\\D+", "");
        String alias = "Imp" + number;
        System.err.println(alias);
        Zombie imp = zombieService.spawnZombie(alias, startPx);
        if (imp == null) return;

        float arcHeightPx = 400f;
        float gravityPxPerTick2 = -20f;

        Vector2 startVec = startPx.toVector();
        Vector2 endVec   = endPx.toVector();
        Vector2 velocity = ProjectileMath.calculateRequiredVelocityPx(startVec, endVec, arcHeightPx, gravityPxPerTick2);


        FlyingMovement flyMovement = new FlyingMovement(
            startVec,
            velocity,
            gravityPxPerTick2,
            endVec.y
        );
        imp.setMovementStrategy(flyMovement);
        imp.setState(Zombie.ZombieState.WALKING);
        imp.getAbilities().add(new FlyingAbility());
        game.audio.Sfx.playRandom(game.audio.AudioId.SFX_THROW, game.audio.AudioId.SFX_THROW2);
        game.audio.Sfx.playRandom(game.audio.AudioId.VOX_IMP, game.audio.AudioId.VOX_IMP2);


    }

    @Override
    public void onDeath(Zombie zombie) {

    }



}
