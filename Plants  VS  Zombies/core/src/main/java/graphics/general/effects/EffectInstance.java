package graphics.general.effects;

import graphics.general.animation.AnimationController;
import graphics.general.animation.condition.AnimationCondition;
import graphics.general.effects.anchers.EffectAnchor;
import graphics.general.effects.anchers.KinematicAnchor;
import graphics.general.instance.VisibilityController;
import graphics.general.modifier.VisualEffectController;
import graphics.general.modifier.visualModifiers.FadeModifier;

import java.util.List;

public class EffectInstance<M> {

    private final String effectId;
    private final M context;
    private final EffectAnchor anchor;
    private final EffectLayer layer;
    protected final VisibilityController visibilityController;
    private final VisualEffectController modifierController;

    private final AnimationController<EffectVariant<M, EffectTag>> controller;
    private final List<EffectVariant<M, EffectTag>> sequence;
    private AnimationCondition<? super M> activeCondition;

    private final float despawnDelay;
    private final float fadeDuration;

    private int sequenceStage;
    private boolean finished;

    private boolean fadeStarted;
    private float landedWaitTime;
    private FadeModifier fadeModifier;
    private boolean flipX;
    private float holdDurationSeconds;
    private float holdTime;
    private float scaleX = 1f;
    private float scaleY = 1f;

    public EffectInstance(String effectId,
                          M context,
                          EffectAnchor anchor,
                          EffectLayer layer,
                          List<EffectVariant<M, EffectTag>> sequence,
                          float despawnDelay,
                          float fadeDuration) {
        this.effectId = effectId;
        this.context = context;
        this.anchor = anchor;
        this.layer = layer;
        this.sequence = sequence == null ? List.of() : List.copyOf(sequence);
        this.controller = new AnimationController<>();
        this.sequenceStage = 0;
        this.finished = false;
        this.visibilityController = new VisibilityController();
        this.modifierController = new VisualEffectController();
        this.despawnDelay = despawnDelay;
        this.fadeDuration = fadeDuration;
    }

    public void forceFinish() {
        this.finished = true;
    }

    public void update(float delta) {
        if (finished) {
            return;
        }

        if (activeCondition != null) {
            activeCondition.update(delta);
            if (!activeCondition.matches(context)) {
                finished = true;
                return;
            }
        }
        anchor.update(delta);
        controller.update(delta);
        modifierController.update(delta);
        if (anchor instanceof KinematicAnchor) {
            updateKinematicLifecycle(delta);
            return;
        }

        EffectVariant<M, EffectTag> variant = getCurrentVariant();
        if (variant != null && variant.isLoop() && holdDurationSeconds > 0f) {
            holdTime += delta;
            if (holdTime >= holdDurationSeconds) {
                if (sequenceStage < sequence.size() - 1) {
                    sequenceStage++;
                    holdTime = 0f;
                    controller.setFinished(false);
                } else {
                    finished = true;
                }
            }
            return;
        }

        if (controller.isFinished()) {
            if (holdDurationSeconds > 0f && sequenceStage >= sequence.size() - 1) {
                holdTime += delta;
                if (holdTime < holdDurationSeconds) {
                    return;
                }
            }
            if (sequenceStage < sequence.size() - 1) {
                sequenceStage++;
                controller.setFinished(false);
                holdTime = 0f;
            } else {
                finished = true;
            }
        }
    }

    private void updateKinematicLifecycle(float delta) {
        KinematicAnchor kinematic = (KinematicAnchor) anchor;

        if (!fadeStarted) {
            if (kinematic.hasLanded()) {
                landedWaitTime += delta;
                if (landedWaitTime >= despawnDelay) {
                    fadeStarted = true;
                    if (fadeDuration <= 0f) {
                        finished = true;
                    } else {
                        fadeModifier = new FadeModifier(fadeDuration);
                        modifierController.add(fadeModifier);
                    }
                }
            }
        } else if (fadeModifier != null && fadeModifier.isFinished()) {
            finished = true;
        }
    }

    /** Current clip variant, clamped to the end of the sequence. */
    public EffectVariant<M, EffectTag> getCurrentVariant() {
        if (sequence.isEmpty()) {
            return null;
        }
        return sequence.get(Math.min(sequenceStage, sequence.size() - 1));
    }

    public String getEffectId()       { return effectId; }
    public M getContext()             { return context; }
    public EffectAnchor getAnchor()   { return anchor; }
    public EffectLayer getLayer()     { return layer; }
    public AnimationController<EffectVariant<M, EffectTag>> getController() {
        return controller;
    }
    public boolean isFinished()       { return finished; }

    public VisibilityController getVisibilityController() {
        return visibilityController;
    }

    public VisualEffectController getModifierController() {
        return modifierController;
    }

    public boolean isFlipX() {
        return flipX;
    }

    public void setFlipX(boolean flipX) {
        this.flipX = flipX;
    }

    public void setHoldDurationSeconds(float holdDurationSeconds) {
        this.holdDurationSeconds = holdDurationSeconds;
    }

    public float getScale() {
        return scaleY;
    }

    public float getScaleX() {
        return scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public void setScale(float scale) {
        float value = scale <= 0f ? 1f : scale;
        this.scaleX = value;
        this.scaleY = value;
    }

    public void setScale(float scaleX, float scaleY) {
        this.scaleX = scaleX <= 0f ? 1f : scaleX;
        this.scaleY = scaleY <= 0f ? 1f : scaleY;
    }
    public void setActiveCondition(AnimationCondition<? super M> activeCondition) {
        this.activeCondition = activeCondition;
    }
    public boolean isConditionSatisfied() {
        return activeCondition == null || activeCondition.matches(context);
    }
}
