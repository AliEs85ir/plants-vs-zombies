package game.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class MusicController {
    private static final float LOOP_SEEK_EPSILON = 0.02f;

    private final AudioSettings settings;
    private final List<StreamedTrack> tracks = new ArrayList<>();
    private static final Map<String, FileHandle> STREAM_CACHE = new ConcurrentHashMap<>();
    private StreamedTrack exclusiveMusic;
    private StreamedTrack exclusiveVoice;
    private float duckModifier = 1f;
    private float duckTarget = 1f;
    private float duckFadeSpeed;
    private int manualDuckCount;
    private float manualDuckAmount = 1f;

    MusicController(AudioSettings settings) {
        this.settings = settings;
    }

    void play(AudioId id, MusicTransition transition) {
        AudioDefinition definition = id.definition();
        if (!definition.overlay()) {
            StreamedTrack current = exclusiveSlot(definition.category());
            if (current != null && current.id == id && !current.stopping && current.music.isPlaying()) {
                return;
            }
        }
        Music music = load(definition.path());
        if (music == null) {
            return;
        }

        float fadeSeconds = transition == null ? 0f : transition.fadeSeconds();
        StreamedTrack track = new StreamedTrack(id, definition, music);

        if (!definition.overlay()) {
            StreamedTrack previous = exclusiveSlot(definition.category());
            if (previous != null) {
                dispose(previous);
                tracks.remove(previous);
            }
            assignExclusive(track);
        }

        tracks.add(track);
        music.setLooping(shouldNativeLoop(definition));
        beginFade(track, 1f, fadeSeconds);
        if (fadeSeconds <= 0f) {
            track.fade = 1f;
        }
        applyVolume(track);
        music.play();
        music.setOnCompletionListener(completed -> onCompleted(track));

        if (definition.duckMusic()) {
            track.ducking = true;
            refreshDuck(definition.duckFadeSeconds());
        }
    }

    void update(float delta) {
        updateDuck(delta);
        Iterator<StreamedTrack> iterator = tracks.iterator();
        while (iterator.hasNext()) {
            StreamedTrack track = iterator.next();
            if (track.disposed) {
                iterator.remove();
                continue;
            }
            updateFade(track, delta);
            applyLoopRange(track);
            applyVolume(track);
            if (track.stopping && track.fade <= 0f) {
                iterator.remove();
                dispose(track);
            }
        }
    }

    void stop(AudioId id, float fadeSeconds) {
        for (StreamedTrack track : new ArrayList<>(tracks)) {
            if (track.id == id) {
                stopTrack(track, fadeSeconds);
            }
        }
    }

    void stopCategory(AudioCategory category, float fadeSeconds) {
        for (StreamedTrack track : new ArrayList<>(tracks)) {
            if (track.definition.category() == category) {
                stopTrack(track, fadeSeconds);
            }
        }
    }

    void stopAll(float fadeSeconds) {
        for (StreamedTrack track : new ArrayList<>(tracks)) {
            stopTrack(track, fadeSeconds);
        }
    }

    void duck(float amount, float fadeSeconds) {
        manualDuckCount++;
        manualDuckAmount = Math.min(manualDuckAmount, AudioSettings.clamp(amount));
        refreshDuck(fadeSeconds);
    }

    void restore(float fadeSeconds) {
        if (manualDuckCount > 0) {
            manualDuckCount--;
        }
        if (manualDuckCount == 0) {
            manualDuckAmount = 1f;
        }
        refreshDuck(fadeSeconds);
    }

    void dispose() {
        for (StreamedTrack track : new ArrayList<>(tracks)) {
            dispose(track);
        }
        tracks.clear();
        exclusiveMusic = null;
        exclusiveVoice = null;
        duckModifier = 1f;
        duckTarget = 1f;
        duckFadeSpeed = 0f;
        manualDuckCount = 0;
        manualDuckAmount = 1f;
    }

    private void onCompleted(StreamedTrack track) {
        if (track.disposed || track.stopping) {
            return;
        }
        if (track.definition.hasLoopRange() || shouldNativeLoop(track.definition)) {
            return;
        }
        if (track.definition.repeatCount() == AudioDefinition.INFINITE) {
            track.music.play();
            return;
        }
        track.playsFinished++;
        if (track.playsFinished < track.definition.repeatCount()) {
            track.music.play();
            return;
        }
        stopTrack(track, 0f);
    }

    private void stopTrack(StreamedTrack track, float fadeSeconds) {
        if (track.disposed) {
            tracks.remove(track);
            return;
        }
        track.stopping = true;
        try {
            track.music.setLooping(false);
        } catch (Exception ignored) {
        }
        if (fadeSeconds <= 0f) {
            dispose(track);
            tracks.remove(track);
            return;
        }
        beginFade(track, 0f, fadeSeconds);
    }

    private void beginFade(StreamedTrack track, float target, float seconds) {
        track.fadeTarget = AudioSettings.clamp(target);
        if (seconds <= 0f) {
            track.fade = track.fadeTarget;
            track.fadeSpeed = 0f;
            return;
        }
        track.fadeSpeed = 1f / seconds;
    }

    private void updateFade(StreamedTrack track, float delta) {
        if (track.fadeSpeed <= 0f) {
            return;
        }
        float diff = track.fadeTarget - track.fade;
        float step = track.fadeSpeed * delta;
        if (Math.abs(diff) <= step) {
            track.fade = track.fadeTarget;
            return;
        }
        track.fade += Math.signum(diff) * step;
    }

    private void applyLoopRange(StreamedTrack track) {
        AudioDefinition definition = track.definition;
        if (!definition.hasLoopRange() || track.stopping || !track.music.isPlaying()) {
            return;
        }
        float position = track.music.getPosition();
        if (position + LOOP_SEEK_EPSILON >= definition.loopEnd()) {
            track.music.setPosition(definition.loopStart());
        }
    }

    private void applyVolume(StreamedTrack track) {
        if (track.disposed) {
            return;
        }
        float duck = track.definition.category() == AudioCategory.MUSIC ? duckModifier : 1f;
        float volume = settings.resolve(
            track.definition.category(),
            track.definition.defaultVolume(),
            track.fade * duck
        );
        track.music.setVolume(volume);
    }

    private void refreshDuck(float fadeSeconds) {
        float target = manualDuckCount > 0 ? manualDuckAmount : 1f;
        for (StreamedTrack track : tracks) {
            if (track.ducking && !track.disposed) {
                target = Math.min(target, track.definition.duckAmount());
            }
        }
        duckTarget = target;
        if (fadeSeconds <= 0f) {
            duckModifier = duckTarget;
            duckFadeSpeed = 0f;
            return;
        }
        duckFadeSpeed = 1f / fadeSeconds;
    }

    private void updateDuck(float delta) {
        if (duckFadeSpeed <= 0f) {
            duckModifier = duckTarget;
            return;
        }
        float diff = duckTarget - duckModifier;
        float step = duckFadeSpeed * delta;
        if (Math.abs(diff) <= step) {
            duckModifier = duckTarget;
            return;
        }
        duckModifier += Math.signum(diff) * step;
    }

    private StreamedTrack exclusiveSlot(AudioCategory category) {
        if (category == AudioCategory.VOICE) {
            return exclusiveVoice;
        }
        return exclusiveMusic;
    }

    private void assignExclusive(StreamedTrack track) {
        if (track.definition.category() == AudioCategory.VOICE) {
            exclusiveVoice = track;
            return;
        }
        exclusiveMusic = track;
    }

    private void clearExclusive(StreamedTrack track) {
        if (exclusiveMusic == track) {
            exclusiveMusic = null;
        }
        if (exclusiveVoice == track) {
            exclusiveVoice = null;
        }
    }

    private void dispose(StreamedTrack track) {
        if (track.disposed) {
            return;
        }
        boolean wasDucking = track.ducking;
        track.disposed = true;
        track.stopping = true;
        track.ducking = false;
        try {
            track.music.setLooping(false);
            track.music.stop();
        } catch (Exception ignored) {
        }
        try {
            track.music.dispose();
        } catch (Exception ignored) {
        }
        clearExclusive(track);
        if (wasDucking) {
            refreshDuck(track.definition.duckFadeSeconds());
        }
    }

    private static boolean shouldNativeLoop(AudioDefinition definition) {
        return definition.looping()
            && !definition.hasLoopRange()
            && definition.repeatCount() == AudioDefinition.INFINITE;
    }

    private static Music load(String path) {
        FileHandle source = AudioFiles.resolve(path);
        if (source == null || !source.exists() || source.length() <= 0) {
            Gdx.app.error("Audio", "Missing or empty music file: " + path);
            return null;
        }
        try {
            return Gdx.audio.newMusic(Gdx.files.internal(path));
        } catch (Exception e1) {
            try {
                FileHandle stream = cachedStreamFile(path, source);
                if (stream == null || !stream.exists() || stream.length() <= 0) {
                    Gdx.app.error("Audio", "Empty audio stream file for: " + path);
                    return null;
                }
                return Gdx.audio.newMusic(stream);
            } catch (Exception e2) {
                Gdx.app.error("Audio", "Could not load music: " + path, e2);
                return null;
            }
        }
    }

    private static FileHandle cachedStreamFile(String path, FileHandle source) {
        FileHandle existing = STREAM_CACHE.get(path);
        if (isUsable(existing)) {
            return existing;
        }

        String pid = String.valueOf(ProcessHandle.current().pid());
        String name = pid + "_" + path.replaceAll("[^a-zA-Z0-9._-]", "_");
        FileHandle cache = Gdx.files.local("audio-stream-cache/" + name);

        try {
            if (!isUsable(cache) || cache.length() != source.length()) {
                cache.parent().mkdirs();
                source.copyTo(cache);
                cache.file().deleteOnExit();
            }
            if (isUsable(cache)) {
                STREAM_CACHE.put(path, cache);
                return cache;
            }
        } catch (Exception ignored) {
        }

        return source;
    }

    private static boolean isUsable(FileHandle file) {
        try {
            return file != null && file.exists() && file.length() > 0;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static final class StreamedTrack {
        final AudioId id;
        final AudioDefinition definition;
        final Music music;
        float fade;
        float fadeTarget = 1f;
        float fadeSpeed;
        int playsFinished;
        boolean stopping;
        boolean ducking;
        boolean disposed;

        StreamedTrack(AudioId id, AudioDefinition definition, Music music) {
            this.id = id;
            this.definition = definition;
            this.music = music;
        }
    }
}
