package graphics.general.animation;

import graphics.general.animation.condition.AnimationCondition;
import graphics.general.effects.EffectCue;

import java.util.*;

public class AnimationVariant<M, T extends Enum<T>> {

    private final String clipName;
    private final boolean loop;
    private final int priority;
    private final T tag;
    private final AnimationCondition<M> condition;
    private final String pamPath;
    private final List<EffectCue> effectCues;
    private final Map<String, Boolean> visibility;

    public AnimationVariant(
        String clipName,
        boolean loop,
        int priority,
        T tag,
        AnimationCondition<M> condition,
        String pamPath
    ) {
        this(clipName, loop, priority, tag, condition, pamPath, Collections.emptyMap());
    }
    public AnimationVariant(
        String clipName,
        boolean loop,
        int priority,
        T tag,
        AnimationCondition<M> condition,
        String pamPath,
        Map<String, Boolean> visibility
    ) {
        this.clipName = clipName;
        this.loop = loop;
        this.priority = priority;
        this.tag = tag;
        this.condition = condition;
        this.pamPath = pamPath;
        this.effectCues = new ArrayList<>();
        this.visibility = visibility;
    }

    public AnimationVariant(
        String clipName,
        boolean loop,
        int priority,
        T tag,
        AnimationCondition<M> condition
    ) {
        this(
            clipName,
            loop,
            priority,
            tag,
            condition,
            null
        );
    }

    public AnimationVariant(
        String clipName,
        boolean loop,
        int priority,
        T tag
    ) {
        this(
            clipName,
            loop,
            priority,
            tag,
            null,
            null
        );
    }

    public String getClipName() {
        return clipName;
    }

    public boolean isLoop() {
        return loop;
    }

    public int getPriority() {
        return priority;
    }

    public T getTag() {
        return tag;
    }

    public String getPamPath() {
        return pamPath;
    }

    public boolean hasCondition() {
        return condition != null;
    }

    public boolean matchesCondition(M model) {
        return condition == null || condition.matches(model);
    }

    public boolean addEffectCue(EffectCue cue) {
        if (cue == null) {
            return false;
        }

        effectCues.add(cue);
        return true;
    }

    public List<EffectCue> getEffectCues() {
        return Collections.unmodifiableList(effectCues);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AnimationVariant<?, ?> that =
            (AnimationVariant<?, ?>) o;

        return Objects.equals(clipName, that.clipName)
            && Objects.equals(pamPath, that.pamPath)
            && loop == that.loop
            && Objects.equals(tag, that.tag);
    }

    public Map<String, Boolean> getVisibility() {
        return visibility;
    }

    @Override
    public int hashCode() {
        return Objects.hash(clipName, pamPath, loop, tag);
    }
}
