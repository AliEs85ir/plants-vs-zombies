package graphics.general.instance;

import graphics.general.animation.AnimationController;
import graphics.general.animation.AnimationVariant;
import graphics.general.effects.EffectEvent;
import graphics.general.effects.EffectInstance;
import graphics.general.effects.SequentialEffectResolver;
import graphics.general.effects.anchers.EffectAnchor;
import graphics.general.event.VisualEvent;
import graphics.general.modifier.VisualEffectController;
import graphics.general.rotation.RotationFunction;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;

public abstract class RenderInstance<
    M,
    S extends Enum<S>,
    E extends Enum<E>,
    V extends AnimationVariant<?,?>>
{
    protected final M model;
    protected final AnimationController<V> animationController;
    protected final VisibilityController visibilityController;
    protected final VisualEffectController modifierController;
    protected final Queue<VisualEvent<E>> eventQueue;
    protected VisualEvent<E> currentEvent;
    private final Queue<EffectEvent<M>> effectQueue = new LinkedList<>();
    private final SequentialEffectResolver<M> effectResolver;
    private RotationFunction<M> rotationFunction;

    protected S currentVisualState;
    protected int animationStage;
    protected float variationTimer;
    protected float nextVariationInterval;

    // state;
    private boolean sequentialState = false;
    private int sequenceLength = 0;
    private float visualStateElapsed;


    public RenderInstance(M model, S initialVisualState) {
        this.model = model;
        this.animationController = new AnimationController<>();
        this.visibilityController = new VisibilityController();
        this.modifierController = new VisualEffectController();
        this.eventQueue = new ArrayDeque<>();
        this.currentVisualState = initialVisualState;
        this.animationStage = 0;
        this.variationTimer = 0.0f;
        this.visualStateElapsed = 0f;
        this.nextVariationInterval = generateRandomVariationInterval();
        this.effectResolver = new SequentialEffectResolver<>(model);
    }

    public void update(float delta) {
        effectResolver.resolve(effectQueue);
        animationController.update(delta);
        modifierController.update(delta);
        variationTimer += delta;
        visualStateElapsed += delta;

        if (shouldAutoAdvanceSequential()
            && sequentialState
            && animationController.isFinished()
            && animationStage < sequenceLength - 1) {
            animationStage++;
            animationController.setFinished(false);
        }


        updatePosition();
    }

    protected boolean shouldAutoAdvanceSequential() {
        return true;
    }

    protected void updatePosition() {
    }

    public boolean isReadyForRemoval() {
        return false;
    }

    public boolean shouldTriggerVariation() {
        return variationTimer >= nextVariationInterval;
    }

    public void resetVariationTimer() {
        this.variationTimer = 0.0f;
        this.nextVariationInterval = generateRandomVariationInterval();
    }

    protected abstract float generateRandomVariationInterval();

    public void pushEvent(VisualEvent<E> event) {
        eventQueue.add(event);
    }

    public VisualEvent<E> pollEvent() {
        return eventQueue.poll();
    }

    public M getModel() {
        return model;
    }

    public AnimationController<V> getAnimationController() {
        return animationController;
    }

    public VisibilityController getVisibilityController() {
        return visibilityController;
    }

    public S getCurrentVisualState() {
        return currentVisualState;
    }

    public int getAnimationStage() {
        return animationStage;
    }

    public void setAnimationStage(int animationStage) {
        this.animationStage = animationStage;
    }

    public VisualEvent<E> topEvent() {
        return eventQueue.peek();
    }

    public VisualEvent<E> getCurrentEvent() {
        return currentEvent;
    }

    public void setCurrentEvent(VisualEvent<E> currentEvent) {
        this.currentEvent = currentEvent;
    }


    public void setSequentialInfo(boolean sequential, int length) {
        this.sequentialState = sequential;
        this.sequenceLength = length;
    }

    public void clearSequentialInfo() {
        this.sequentialState = false;
        this.sequenceLength = 0;
    }


    public void setCurrentVisualState(S newState) {
        if (this.currentVisualState != newState) {
            this.animationStage = 0;
            this.visualStateElapsed = 0f;
            clearSequentialInfo();
        }
        this.currentVisualState = newState;
    }

    public boolean isSequential() {
        return sequentialState;
    }

    public int getSequenceLength() {
        return sequenceLength;
    }

    public float getVisualStateElapsed() {
        return visualStateElapsed;
    }

    public VisualEffectController getModifierController(){
        return modifierController;
    }

    public void pushEffect(String effectId, EffectAnchor anchor) {
        effectQueue.add(new EffectEvent<>(effectId, anchor, false));
    }

    public void pushInterruptingEffect(String effectId, EffectAnchor anchor) {
        effectQueue.add(new EffectEvent<>(effectId, anchor, true));
    }

    public EffectInstance<M> getCurrentEffect() {
        return effectResolver.getCurrentEffect();
    }

    public void stopEffects() {
        effectQueue.clear();
        effectResolver.stop();
    }

    public void setRotationFunction(RotationFunction<M> rotationFunction) {
        this.rotationFunction = rotationFunction;
    }

    public float getRotation() {
        if (rotationFunction != null && model != null) {
            return rotationFunction.getRotation(model);
        }
        return 0f;
    }
}
