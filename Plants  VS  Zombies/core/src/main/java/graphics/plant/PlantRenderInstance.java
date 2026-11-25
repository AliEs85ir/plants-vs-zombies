package graphics.plant;

import common.model.Position;
import game.entity.plant.Plant;
import game.entity.plant.PlantFoodState;
import graphics.general.animation.AnimationController;
import graphics.general.event.PlantVisualEventType;
import graphics.general.instance.RenderInstance;

public class PlantRenderInstance extends RenderInstance<Plant, PlantVisualState, PlantVisualEventType, PlantAnimationVariant> {

    private Position cachedPosition;
    private final PlantDamageFlashController damageFlashController = new PlantDamageFlashController();

    public PlantRenderInstance(Plant plant) {
        super(plant, PlantVisualState.IDLE);
        this.cachedPosition = plant.getPosition();
    }

    @Override
    protected void updatePosition() {
        Position current = getModel().getPosition();
        if (current != null) {
            cachedPosition = current;
        }
    }

    public Position getRenderPosition() {
        return cachedPosition;
    }

    public boolean ownsPlantFoodAnimation() {
        return getCurrentVisualState() == PlantVisualState.PLANT_FOOD
            && getPlant().getPlantFoodState() != null
            && getPlant().getPlantFoodState().isActive();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        damageFlashController.update(delta);
        advancePlantFoodSequence();
    }

    public PlantDamageFlashController getDamageFlashController() {
        return damageFlashController;
    }

    @Override
    protected boolean shouldAutoAdvanceSequential() {
        return !ownsPlantFoodAnimation();
    }

    private void advancePlantFoodSequence() {
        if (!ownsPlantFoodAnimation()) {
            return;
        }
        PlantFoodState plantFoodState = getPlant().getPlantFoodState();
        int requested = plantFoodState.consumeRequestedAnimationStage();
        if (requested >= 0 && requested != getAnimationStage()) {
            setAnimationStage(requested);
        }
        if (!isSequential() || getSequenceLength() <= 0) {
            return;
        }
        AnimationController<PlantAnimationVariant> controller = getAnimationController();
        PlantAnimationVariant variant = controller.getCurrentVariant();
        if (variant == null || variant.isLoop()) {
            return;
        }
        int last = getSequenceLength() - 1;
        int stage = getAnimationStage();
        if (controller.isFinished() && stage < last) {
            setAnimationStage(stage + 1);
        }
    }

    @Override
    protected float generateRandomVariationInterval() {
        return 4.0f + (float) (Math.random() * 4.0f);
    }

    public Plant getPlant() { return getModel(); }
    public boolean shouldTriggerIdleVariation() { return shouldTriggerVariation(); }
    public void resetIdleTimer() { resetVariationTimer(); }

    @Override
    public boolean isReadyForRemoval() {
        return getCurrentVisualState() == PlantVisualState.DEATH
            && getAnimationController().isFinished();
    }

    public void ensureDeathAnimationFinishes() {
        if (getCurrentVisualState() != PlantVisualState.DEATH) return;

        AnimationController<PlantAnimationVariant> controller = getAnimationController();
        PlantAnimationVariant current = controller.getCurrentVariant();

        if (current != null && current.isLoop()) {
            PlantAnimationVariant nonLoop = new PlantAnimationVariant(
                current.getClipName(),
                false,
                current.getPriority(),
                current.getTag()
            );
            controller.changeVariant(
                nonLoop,
                controller.getCurrentClip(),
                controller.getCurrentPamPath()
            );
        }
    }
}
