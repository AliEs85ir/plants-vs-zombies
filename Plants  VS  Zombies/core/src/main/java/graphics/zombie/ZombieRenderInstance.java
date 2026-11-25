package graphics.zombie;

import common.model.Position;
import game.component.AssetManager;
import game.component.contacts.LawnMowerZombieContact;
import game.component.contacts.ObstacleZombieContact;
import game.component.contacts.ZombiePlantContact;
import game.component.physic.PhysicService;
import game.entity.zombie.Zombie;
import game.entity.zombie.strategy.movement.GroundTrackedMovement;
import app.enums.effect.EffectType;
import app.model.App;
import graphics.general.animation.AnimationController;
import graphics.general.event.ZombieVisualEventType;
import graphics.general.instance.RenderInstance;
import graphics.general.modifier.visualModifiers.GreenModifier;
import graphics.zombie.visibility.VisibilityConfig;
import graphics.zombie.visibility.VisibilitySystem;
import pvz.libpvz.pam.ClipRef;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import java.util.Map;

public class ZombieRenderInstance extends RenderInstance<Zombie, ZombieVisualState, ZombieVisualEventType, ZombieAnimationVariant> {
    private float flashTimer = 0f;
    private float flashDuration = 0.15f;
    private VisibilitySystem visibilitySystem;
    private final GroundSwatchTracker groundSwatchTracker = new GroundSwatchTracker();

    public ZombieRenderInstance(Zombie zombie, ZombieAnimationProfile profile) {
        super(zombie, ZombieVisualState.WALK);

        if (zombie != null && zombie.dropsPlantFood()) {
            getModifierController().add(new GreenModifier());
        }

        if (GroundTrackedMovement.appliesTo(zombie)) {
            getVisibilityController().setInvisible(GroundSwatchTracker.PART_NAME);
        }

        if (profile != null) {
            VisibilityConfig config = profile.getVisibilityConfig();
            if (config != null && config.hasAnyRules()) {
                this.visibilitySystem = new VisibilitySystem(zombie, this, config);
            }
        }
    }

    @Override
    public void update(float delta) {
        applyWalkTimeScale();
        super.update(delta);
        updateFlash(delta);

        if (visibilitySystem != null) {
            visibilitySystem.update();
            Map<String, Boolean> visibility = visibilitySystem.getVisibilityMap();
            applyVisibilityToPAM(visibility);
        }
    }
    @Override
    protected float generateRandomVariationInterval() {
        return 3.0f + (float) (Math.random() * 4.0f);
    }

    public Zombie getZombie() { return getModel(); }

    public GroundSwatchTracker getGroundSwatchTracker() {
        return groundSwatchTracker;
    }

    private void applyWalkTimeScale() {
        Zombie zombie = getZombie();
        if (zombie.getState() != Zombie.ZombieState.WALKING) {
            return;
        }
        AnimationController<ZombieAnimationVariant> controller = getAnimationController();
        if (zombie.getSpeed() <= 0 || zombie.hasEffect(EffectType.STUN)) {
            controller.setTimeScale(0f);
            return;
        }
        controller.setTimeScale((float) Math.max(0d, zombie.getMoveSpeedMultiplier()));
    }

    public void advanceGroundTrack() {
        Zombie zombie = getZombie();
        if (!GroundTrackedMovement.isDriving(zombie)
            || zombie.isDead()
            || zombie.getState() != Zombie.ZombieState.WALKING
            || zombie.getBody() == null
            || zombie.hasEffect(EffectType.STUN)
            || App.getRealTimeEngine() == null
            || !App.getRealTimeEngine().canAdvanceSimulation()) {
            return;
        }
        if (ZombiePlantContact.bindIfBlocking(zombie)) {
            return;
        }
        AnimationController<ZombieAnimationVariant> controller = getAnimationController();
        ClipRef clip = controller.getCurrentClip();
        if (clip == null || controller.getCurrentVariant() == null
            || !isWalkClip(controller.getCurrentVariant().getClipName())) {
            return;
        }
        groundSwatchTracker.syncClip(AssetManager.player, clip);
        float dx = groundSwatchTracker.consumeWorldDeltaX(controller.getStateTime());
        if (dx == 0f) {
            return;
        }
        Body body = zombie.getBody();
        Vector2 pos = body.getPosition();
        float ppm = PhysicService.PIXEL_PER_METER;
        body.setTransform(pos.x + dx / ppm, pos.y, body.getAngle());
        body.setLinearVelocity(0f, 0f);
        zombie.setPosition(new Position(body.getPosition().x * ppm, body.getPosition().y * ppm));
        ZombiePlantContact.bindIfBlocking(zombie);
        LawnMowerZombieContact.resolveOverlaps(zombie);
        ObstacleZombieContact.resolveOverlaps(zombie);
    }

    private static boolean isWalkClip(String clipName) {
        return clipName != null && clipName.startsWith("walk");
    }

    public void triggerFlash() {
        flashTimer = flashDuration;
    }

    public void updateFlash(float delta) {
        if (flashTimer > 0f) {
            flashTimer -= delta;
        }
    }

    public float getFlashAlpha() {
        if (flashTimer <= 0f) {
            return 0f;
        }

        return flashTimer / flashDuration;
    }

    private void applyVisibilityToPAM(Map<String, Boolean> visibility) {
        visibilityController.resetAll();

        for (Map.Entry<String, Boolean> entry : visibility.entrySet()) {
            visibilityController.setLayer(entry.getKey(), entry.getValue());
        }
        visibilityController.applyPersistentOverrides();
    }

}
