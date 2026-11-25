package graphics.general.effects;

import graphics.general.effects.strategy.EffectSelectionStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class EffectDefinition<M> {

    private final String id;
    private final String pamPath;
    private final List<EffectVariant<M, EffectTag>> variants;
    private final EffectSelectionStrategy<M> selectionStrategy;
    private final EffectLayer defaultLayer;
    private final boolean followTarget;

    private final float groundOffset;
    private final float despawnDelay;
    private final float fadeDuration;

    public EffectDefinition(String id,
                            String pamPath,
                            List<EffectVariant<M, EffectTag>> variants,
                            EffectSelectionStrategy<M> selectionStrategy,
                            EffectLayer defaultLayer,
                            boolean followTarget,
                            float groundOffset,
                            float despawnDelay,
                            float fadeDuration) {
        this.id = id;
        this.pamPath = pamPath;
        this.variants = Collections.unmodifiableList(
            variants == null ? new ArrayList<>() : new ArrayList<>(variants));
        this.selectionStrategy = selectionStrategy;
        this.defaultLayer = defaultLayer == null ? EffectLayer.DEFAULT : defaultLayer;
        this.followTarget = followTarget;
        this.groundOffset = groundOffset;
        this.despawnDelay = despawnDelay;
        this.fadeDuration = fadeDuration;
    }

    public String getId()                                    { return id; }
    public String getPamPath()                               { return pamPath; }
    public List<EffectVariant<M, EffectTag>> getVariants()   { return variants; }
    public EffectSelectionStrategy<M> getSelectionStrategy() { return selectionStrategy; }
    public EffectLayer getDefaultLayer()                     { return defaultLayer; }
    public boolean isFollowTarget()                          { return followTarget; }
    public float getGroundOffset()                           { return groundOffset; }
    public float getDespawnDelay()                           { return despawnDelay; }
    public float getFadeDuration()                           { return fadeDuration; }

    @Override
    public String toString() {
        return "EffectDefinition(" + id + ", " + variants.size() + " clips)";
    }
}
