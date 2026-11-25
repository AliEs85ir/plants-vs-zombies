// graphics/general/animation/AnimationLibrary.java
package graphics.general.animation;

import com.badlogic.gdx.Gdx;
import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.pam.PamPlayer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class AnimationLibrary {
    private final PamPlayer pamPlayer;
    private final Map<ClipKey<?>, ClipRef> clipCache;
    private final Set<Object> loadedAssets;
    private final Set<String> loadedPamPaths;
    private final Set<String> failedPamPaths;

    public AnimationLibrary(PamPlayer pamPlayer) {
        this.pamPlayer = pamPlayer;
        this.clipCache = new HashMap<>();
        this.loadedAssets = new HashSet<>();
        this.loadedPamPaths = new HashSet<>();
        this.failedPamPaths = new HashSet<>();
    }

    public <K> void loadAssets(K key, String pamPath) {
        tryLoadAssets(key, pamPath);
    }

    public <K> boolean tryLoadAssets(K key, String pamPath) {
        if (loadedAssets.contains(key)) {
            return true;
        }
        if (!tryEnsureLoaded(pamPath)) {
            return false;
        }
        loadedAssets.add(key);
        return true;
    }

    public void ensureLoaded(String pamPath) {
        tryEnsureLoaded(pamPath);
    }

    public boolean tryEnsureLoaded(String pamPath) {
        if (pamPath == null || pamPath.isBlank()) {
            return false;
        }
        String resolved = PamPathResolver.get().resolve(pamPath);
        if (loadedPamPaths.contains(resolved)) {
            return true;
        }
        if (failedPamPaths.contains(resolved)) {
            return false;
        }
        if (!PamPathResolver.get().exists(resolved)) {
            failedPamPaths.add(resolved);
            Gdx.app.error("AnimationLibrary", "PAM file does not exist: " + resolved);
            return false;
        }
        try {
            pamPlayer.loadSync(resolved);
            loadedPamPaths.add(resolved);
            return true;
        } catch (RuntimeException e) {
            failedPamPaths.add(resolved);
            Gdx.app.error("AnimationLibrary", "Failed to load PAM: " + resolved, e);
            return false;
        }
    }

    public <K> ClipRef getClip(ClipKey<K> key) {
        return clipCache.get(key);
    }

    public <K> void putClip(ClipKey<K> key, ClipRef clip) {
        clipCache.put(key, clip);
    }

    public <K> void unloadAssets(K key) {
        clipCache.keySet().removeIf(clipKey -> Objects.equals(clipKey.entityKey(), key));
        loadedAssets.remove(key);
    }

    public <K> void unloadAllExcept(Set<K> activeKeys) {
        clipCache.keySet().removeIf(clipKey -> !activeKeys.contains(clipKey.entityKey()));
        loadedAssets.retainAll(activeKeys);
    }

    public PamPlayer getPamPlayer() {
        return pamPlayer;
    }

    public ClipRef resolveClip(String pamPath, String requestedClip) {
        String resolvedPath = PamPathResolver.get().resolve(pamPath);
        if (!tryEnsureLoaded(resolvedPath)) {
            return null;
        }
        List<String> available = pamPlayer.clips(resolvedPath);
        String resolvedClip = PamClipResolver.resolve(requestedClip, available);
        if (resolvedClip == null) {
            return null;
        }
        if (requestedClip != null && !requestedClip.equals(resolvedClip)) {
            Gdx.app.error(
                "AnimationLibrary",
                "Clip '" + requestedClip + "' is not in " + resolvedPath + ", using '" + resolvedClip + "'"
            );
        }
        return pamPlayer.getClip(resolvedPath, resolvedClip);
    }
}
