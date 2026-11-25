package graphics.general.animation;


import java.util.*;

public class AnimationProfile<K, S extends Enum<S>, V extends AnimationVariant<?, ?>> {
    private final K key;
    private final String pamPath;
    private final Map<S, List<V>> variantsMap;

    public AnimationProfile(K key, String pamPath, Class<S> stateEnumClass) {
        this.key = key;
        this.pamPath = pamPath;
        this.variantsMap = new EnumMap<>(stateEnumClass);
    }

    public void addVariant(S state, V variant) {
        variantsMap.computeIfAbsent(state, k -> new ArrayList<>()).add(variant);
    }

    public List<V> getVariants(S state) {
        return variantsMap.getOrDefault(state, Collections.emptyList());
    }

    public Map<S, List<V>> getAllVariantsMap() {
        return Collections.unmodifiableMap(variantsMap);
    }

    public K getKey() {
        return key;
    }

    public String getPamPath() {
        return pamPath;
    }
}
