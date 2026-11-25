package game.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class SfxController {
    private static final long INSTANCE_TTL_NANOS = 2_000_000_000L;

    private final AudioSettings settings;
    private final EnumMap<AudioId, Sound> sounds = new EnumMap<>(AudioId.class);
    private final EnumMap<AudioId, ArrayDeque<ActivePlay>> plays = new EnumMap<>(AudioId.class);
    private final Map<AudioId, Long> loops = new HashMap<>();

    SfxController(AudioSettings settings) {
        this.settings = settings;
    }

    void preload(AudioId id) {
        sound(id);
    }

    void play(AudioId id) {
        AudioDefinition definition = id.definition();
        Sound sound = sound(id);
        if (sound == null) {
            return;
        }
        ArrayDeque<ActivePlay> active = activePlays(id);
        prune(active);
        if (active.size() >= definition.maxConcurrent()) {
            ActivePlay oldest = active.pollFirst();
            if (oldest != null) {
                sound.stop(oldest.soundId);
            }
        }
        long soundId = sound.play(volumeOf(definition), definition.defaultPitch(), 0f);
        active.addLast(new ActivePlay(soundId, TimeUtils.nanoTime()));
    }

    void loop(AudioId id) {
        if (loops.containsKey(id)) {
            return;
        }
        Sound sound = sound(id);
        if (sound == null) {
            return;
        }
        AudioDefinition definition = id.definition();
        long soundId = sound.loop(volumeOf(definition), definition.defaultPitch(), 0f);
        loops.put(id, soundId);
    }

    void stop(AudioId id) {
        Sound sound = sounds.get(id);
        if (sound == null) {
            return;
        }
        Long loopId = loops.remove(id);
        if (loopId != null) {
            sound.stop(loopId);
        }
        ArrayDeque<ActivePlay> active = plays.get(id);
        if (active == null) {
            return;
        }
        for (ActivePlay play : active) {
            sound.stop(play.soundId);
        }
        active.clear();
    }

    void stopAll() {
        List<AudioId> ids = new ArrayList<>(sounds.keySet());
        for (AudioId id : ids) {
            stop(id);
        }
        loops.clear();
        plays.clear();
    }

    void applyVolumes() {
        for (Map.Entry<AudioId, Long> entry : loops.entrySet()) {
            Sound sound = sounds.get(entry.getKey());
            if (sound == null) {
                continue;
            }
            sound.setVolume(entry.getValue(), volumeOf(entry.getKey().definition()));
        }
    }

    void dispose() {
        stopAll();
        for (Sound sound : sounds.values()) {
            sound.dispose();
        }
        sounds.clear();
    }

    private Sound sound(AudioId id) {
        Sound cached = sounds.get(id);
        if (cached != null) {
            return cached;
        }
        FileHandle file = AudioFiles.resolve(id.definition().path());
        if (file == null || !file.exists()) {
            Gdx.app.error("Audio", "Missing sfx file: " + id.definition().path());
            return null;
        }
        try {
            Sound loaded = Gdx.audio.newSound(file);
            sounds.put(id, loaded);
            return loaded;
        } catch (Exception exception) {
            Gdx.app.error("Audio", "Could not load sfx: " + id.definition().path(), exception);
            return null;
        }
    }

    private ArrayDeque<ActivePlay> activePlays(AudioId id) {
        return plays.computeIfAbsent(id, key -> new ArrayDeque<>());
    }

    private void prune(ArrayDeque<ActivePlay> active) {
        long now = TimeUtils.nanoTime();
        while (!active.isEmpty() && now - active.peekFirst().startedAt > INSTANCE_TTL_NANOS) {
            active.removeFirst();
        }
    }

    private float volumeOf(AudioDefinition definition) {
        return settings.resolve(definition.category(), definition.defaultVolume(), 1f);
    }

    private static final class ActivePlay {
        final long soundId;
        final long startedAt;

        ActivePlay(long soundId, long startedAt) {
            this.soundId = soundId;
            this.startedAt = startedAt;
        }
    }
}
