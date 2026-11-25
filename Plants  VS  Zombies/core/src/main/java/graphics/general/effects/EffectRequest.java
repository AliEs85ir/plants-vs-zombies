package graphics.general.effects;

import graphics.general.animation.condition.AnimationCondition;
import graphics.general.effects.anchers.EffectAnchor;

import java.util.Map;

public class EffectRequest<M> {

    private final String effectId;
    private final M context;
    private final EffectAnchor anchor;
    private final EffectLayer layer; // nullable -> default from definition
    private Map<String, Boolean> visibilityOverrides;
    private boolean flipX;
    private float holdDurationSeconds;
    private float scaleX = 1f;
    private float scaleY = 1f;
    private AnimationCondition<? super M> activeCondition;

    public EffectRequest(String effectId, M context, EffectAnchor anchor, EffectLayer layer) {
        this(effectId, context, anchor, layer, null);
    }

    public EffectRequest(String effectId, M context, EffectAnchor anchor, EffectLayer layer,
                         AnimationCondition<? super M> activeCondition) {
        this.effectId = effectId;
        this.context = context;
        this.anchor = anchor;
        this.layer = layer;
        this.activeCondition = activeCondition;
    }

    public String getEffectId()     { return effectId; }
    public M getContext()           { return context; }
    public EffectAnchor getAnchor() { return anchor; }
    public EffectLayer getLayer()   { return layer; }

    public Map<String, Boolean> getVisibilityOverrides() {
        return visibilityOverrides;
    }

    public void setVisibilityOverrides(Map<String, Boolean> visibilityOverrides) {
        this.visibilityOverrides = visibilityOverrides;
    }

    public boolean isFlipX() {
        return flipX;
    }

    public void setFlipX(boolean flipX) {
        this.flipX = flipX;
    }

    public float getHoldDurationSeconds() {
        return holdDurationSeconds;
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

    public AnimationCondition<? super M> getActiveCondition() {
        return activeCondition;
    }

    public void setActiveCondition(AnimationCondition<? super M> activeCondition) {
        this.activeCondition = activeCondition;
    }
}
