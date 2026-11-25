package graphics.general.effects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import graphics.general.animation.AnimationLibrary;
import graphics.general.animation.ClipKey;
import graphics.general.effects.anchers.EffectAnchor;
import graphics.general.effects.anchers.EntityAnchor;
import graphics.general.effects.anchers.KinematicAnchor;
import graphics.general.effects.anchers.WorldAnchor;
import graphics.general.effects.strategy.FirstEffectSelectionStrategy;
import graphics.general.animation.PamPathResolver;
import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.pam.ClipRef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class EffectManager {
    private final AnimationLibrary animationLibrary;
    private final EffectRegistry registry;
    private final EffectRenderer renderer;

    private final List<EffectInstance<?>> active = new ArrayList<>();

    public EffectManager(AnimationLibrary animationLibrary,
                         EffectRegistry registry,
                         EffectRenderer renderer) {
        this.animationLibrary = animationLibrary;
        this.registry = registry;
        this.renderer = renderer;
    }


    /** Spawn an effect from a gameplay-driven request (arbitrary anchor). */
    public <M> EffectInstance<M> spawn(EffectRequest<M> request) {
        EffectDefinition<?> raw = registry.get(request.getEffectId());
        if (raw == null) {
            return null;
        }

        @SuppressWarnings("unchecked")
        EffectDefinition<M> definition = (EffectDefinition<M>) raw;

        EffectInstance<M> instance = new EffectInstance<>(
            request.getEffectId(),
            request.getContext(),
            request.getAnchor(),
            request.getLayer() != null ? request.getLayer() : definition.getDefaultLayer(),
            definition.getSelectionStrategy().select(definition, tempContext(request)),
            definition.getDespawnDelay(),
            definition.getFadeDuration()
        );

        instance.setActiveCondition(request.getActiveCondition());

        EffectVariant<M, EffectTag> variant = instance.getCurrentVariant();
        if (variant != null && variant.getVisibility() != null) {
            for (Map.Entry<String, Boolean> entry : variant.getVisibility().entrySet()) {
                instance.getVisibilityController().setLayer(entry.getKey(), entry.getValue());
            }
        }
        if (request.getVisibilityOverrides() != null) {
            for (Map.Entry<String, Boolean> entry : request.getVisibilityOverrides().entrySet()) {
                instance.getVisibilityController().setLayer(entry.getKey(), entry.getValue());
            }
        }
        instance.setFlipX(request.isFlipX());
        instance.setHoldDurationSeconds(request.getHoldDurationSeconds());
        instance.setScale(request.getScaleX(), request.getScaleY());

        if (instance.getCurrentVariant() != null && instance.isConditionSatisfied()) {
            active.add(instance);
        }
        return instance;
    }

    /**
     * Spawn an effect from an animation cue attached to an entity's clip.
     * `positionProvider` is how the manager retrieves the entity's position
     * for SELF-anchored cues regardless of entity type.
     */
    public <M> void spawnFromCue(EffectCue cue,
                                 M owner,
                                 Function<M, Vector2> positionProvider) {
        EffectAnchor anchor;
        switch (cue.getAnchorType()) {
            case SELF:
                anchor = new EntityAnchor<>(owner, positionProvider, cue.getOffset().cpy());
                break;
            case WORLD:
                anchor = new WorldAnchor(cue.getOffset().cpy());
                break;
            case KINEMATIC:
                EffectDefinition<?> def = registry.get(cue.getEffectId());

                Vector2 ownerPosition = positionProvider.apply(owner).cpy();
                Vector2 start = ownerPosition.cpy().add(cue.getOffset());

                float groundOffset = def != null ? def.getGroundOffset() : 20f;
                float groundY = ownerPosition.y - groundOffset;

                anchor = new KinematicAnchor(
                    start,
                    cue.getVelocity().cpy(),
                    5f,
                    groundY
                );
                break;
            default:
                return;
        }

        EffectRequest<M> request = new EffectRequest<>(cue.getEffectId(), owner, anchor, null);
        request.setVisibilityOverrides(cue.getVisibilityOverrides());
        spawn(request);
    }

    /**
     * Spawns a clip from an already-loaded PAM without a pre-authored effect
     * entry. Visibility is applied at request time so callers can keep only
     * the layers they want (for example a dropped hand or head).
     */
    public <M> EffectInstance<M> spawnPamClip(String pamPath,
                                              String clipName,
                                              M context,
                                              EffectAnchor anchor,
                                              EffectLayer layer,
                                              Map<String, Boolean> visibility) {
        if (pamPath == null || pamPath.isBlank() || clipName == null || clipName.isBlank()) {
            return null;
        }
        String effectId = "pam-clip:" + pamPath + "#" + clipName;
        if (registry.get(effectId) == null) {
            EffectVariant<Object, EffectTag> variant = new EffectVariant<>(
                clipName,
                true,
                0,
                EffectTag.DEFAULT,
                null,
                null,
                Collections.emptyMap()
            );
            registry.register(new EffectDefinition<>(
                effectId,
                pamPath,
                List.of(variant),
                new FirstEffectSelectionStrategy<>(),
                layer == null ? EffectLayer.DEFAULT : layer,
                false,
                20f,
                1.2f,
                0.6f
            ));
        }
        EffectRequest<M> request = new EffectRequest<>(effectId, context, anchor, layer);
        request.setVisibilityOverrides(visibility);
        return spawn(request);
    }

    public boolean hasEffect(String effectId) {
        return effectId != null && registry.get(effectId) != null;
    }


    public void update(float delta) {
        Iterator<EffectInstance<?>> it = active.iterator();
        while (it.hasNext()) {
            EffectInstance<?> instance = it.next();
            instance.update(delta);
            if (instance.isFinished()) {
                it.remove();
            }
        }
    }

    /** Ensure the effect's PAM is loaded and bind the current clip to the controller. */
    public void resolveClips() {
        for (EffectInstance<?> instance : active) {
            EffectDefinition<?> raw = registry.get(instance.getEffectId());
            if (raw == null) {
                continue;
            }
            bindClip(instance, raw);
        }
    }

    public void render(SpriteBatch batch, EffectLayer layer) {
        for (EffectInstance<?> instance : active) {
            if (instance.getLayer() != layer) {
                continue;
            }
            renderHandle(batch, instance);
        }
    }

    public void reset() {
        active.clear();
    }


    private static <M> EffectInstance<M> tempContext(EffectRequest<M> request) {
        // Dummy instance only to funnel `context` into the strategy.
        return new EffectInstance<>(
            request.getEffectId(),
            request.getContext(),
            request.getAnchor(),
            request.getLayer(),
            List.of(),
            0f,
            0f
        );
    }

    private <M> void bindClip(EffectInstance<M> instance, EffectDefinition<?> raw) {
        @SuppressWarnings("unchecked")
        EffectDefinition<M> definition = (EffectDefinition<M>) raw;
        EffectVariant<M, EffectTag> variant = instance.getCurrentVariant();
        if (variant == null) {
            return;
        }

        String pamPath = variant.getPamPath() != null
            ? variant.getPamPath()
            : definition.getPamPath();
        if (pamPath == null) {
            return;
        }
        String resolvedPamPath = PamPathResolver.get().resolve(pamPath);

        animationLibrary.ensureLoaded(resolvedPamPath);

        ClipKey<String> clipKey = new ClipKey<>(instance.getEffectId(), variant.getClipName(), resolvedPamPath);

        ClipRef clip = animationLibrary.getClip(clipKey);
        if (clip == null) {
            clip = animationLibrary.resolveClip(resolvedPamPath, variant.getClipName());
            if (clip == null) {
                return;
            }
            animationLibrary.putClip(clipKey, clip);
        }

        instance.getController().changeVariant(variant, clip, resolvedPamPath);
    }

    private <M> void renderHandle(SpriteBatch batch, EffectInstance<M> instance) {
        renderer.render(batch, instance);
    }
}
