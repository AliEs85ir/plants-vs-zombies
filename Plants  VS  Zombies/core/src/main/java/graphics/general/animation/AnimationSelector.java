package graphics.general.animation;
import graphics.general.animation.strategy.VariantSelectionStrategy;
import graphics.general.instance.RenderInstance;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AnimationSelector<
    K,
    S extends Enum<S>,
    V extends AnimationVariant<?, ?>,
    I extends RenderInstance<?, S, ?, V>
    > {
    private final Map<S, VariantSelectionStrategy<S, V, I>> globalStrategyRegistry;
    private final Map<K, Map<S, VariantSelectionStrategy<S, V, I>>> specificStrategyRegistry;
    private final VariantSelectionStrategy<S, V, I> defaultStrategy;

    public AnimationSelector(Class<S> stateEnumClass, VariantSelectionStrategy<S, V, I> defaultStrategy) {
        this.globalStrategyRegistry = new EnumMap<>(stateEnumClass);
        this.specificStrategyRegistry = new HashMap<>();
        this.defaultStrategy = defaultStrategy;
    }

    public void registerStrategy(S state, VariantSelectionStrategy<S, V, I> strategy) {
        globalStrategyRegistry.put(state, strategy);
    }

    public void registerStrategy(K key, S state, VariantSelectionStrategy<S, V, I> strategy) {
        specificStrategyRegistry
            .computeIfAbsent(key, k -> new EnumMap<>(state.getDeclaringClass()))
            .put(state, strategy);
    }

    public V selectVariant(AnimationProfile<K, S, V> profile, I instance, S state) {
        List<V> variants = profile.getVariants(state);
        if (variants.isEmpty()) {
            return null;
        }

        Map<S, VariantSelectionStrategy<S, V, I>> specificMap = specificStrategyRegistry.get(profile.getKey());
        if(specificMap != null) {
            VariantSelectionStrategy<S, V, I> entityStrategy = specificMap.get(state);
            if (entityStrategy != null) {
                return entityStrategy.select(variants, instance);
            }
        }

        VariantSelectionStrategy<S, V, I> strategy = globalStrategyRegistry.getOrDefault(state, defaultStrategy);
        return strategy.select(variants, instance);
    }
}
