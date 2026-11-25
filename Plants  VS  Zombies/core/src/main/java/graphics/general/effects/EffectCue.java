package graphics.general.effects;

import com.badlogic.gdx.math.Vector2;
import graphics.general.animation.condition.AnimationCondition;

import java.util.HashMap;
import java.util.Map;

/**
 * This class says which effect to play, when , where , and with what offset.
 */
public class EffectCue {
    private final String effectId;
    private final EffectTrigger trigger;
    private final EffectAnchorType anchorType;
    private final Vector2 offset;
    private final float delay;
    private final float normalizedTime;
    private final boolean perLoop;
    private final boolean once;

    private final AnimationCondition<Object> condition;
    private final Map<String, Boolean> visibilityOverrides;
    private final Vector2 velocity;

    public EffectCue(String effectId,
                     EffectTrigger trigger,
                     EffectAnchorType anchorType,
                     Vector2 offset,
                     float delay,
                     float normalizedTime,
                     boolean perLoop,
                     boolean once,
                     AnimationCondition<Object> condition,
                     Map<String, Boolean> visibilityOverrides,
                     Vector2 velocity) {
        this.effectId = effectId;
        this.trigger = trigger;
        this.anchorType = anchorType;
        this.offset = offset == null ? new Vector2() : offset;
        this.delay = delay;
        this.normalizedTime = normalizedTime;
        this.perLoop = perLoop;
        this.once = once;
        this.condition = condition;
        this.visibilityOverrides = visibilityOverrides != null ? visibilityOverrides : new HashMap<>();
        this.velocity = velocity != null ? velocity : new Vector2();
    }

    public String getEffectId()                         { return effectId; }
    public EffectTrigger getTrigger()                   { return trigger; }
    public EffectAnchorType getAnchorType()             { return anchorType; }
    public Vector2 getOffset()                             { return offset; }
    public float getDelay()                             { return delay; }
    public float getNormalizedTime()                    { return normalizedTime; }
    public boolean isPerLoop()                          { return perLoop; }
    public boolean isOnce()                             { return once; }
    public boolean canTrigger(Object context) {
        return condition == null || condition.matches(context);
    }

    public Map<String, Boolean> getVisibilityOverrides() { return visibilityOverrides; }
    public Vector2 getVelocity() { return velocity; }

    @Override
    public String toString() {
        return "EffectCue(" + effectId + " @" + trigger + " " + anchorType + ")";
    }
}
