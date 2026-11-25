package graphics.general.animation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AnimationRegistry<
    K,
    S extends Enum<S>,
    V extends AnimationVariant<?, ?>,
    P extends AnimationProfile<K, S, V>
    > {
    private final Map<K, P> profiles = new HashMap<>();

    protected void registerProfile(P profile) {
        profiles.put(profile.getKey(), profile);
    }

    public P getProfile(K key) {
        return profiles.get(key);
    }

    public Collection<P> getAllProfiles() {
        return Collections.unmodifiableCollection(profiles.values());
    }
}
