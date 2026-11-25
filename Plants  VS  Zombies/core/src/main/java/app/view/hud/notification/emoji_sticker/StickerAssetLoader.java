package app.view.hud.notification.emoji_sticker;

import app.view.loading.InternalFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class StickerAssetLoader {
    private static final String TAG = "StickerAssetLoader";
    private static final Map<String, Animation<TextureRegion>> CACHE = new HashMap<>();

    private StickerAssetLoader() {}

    public static Animation<TextureRegion> getAnimation(String folderPath, float frameDuration) {
        if (folderPath == null || folderPath.isBlank()) {
            Gdx.app.log(TAG, "folderPath is null or blank");
            return null;
        }

        String key = folderPath.trim();
        if (CACHE.containsKey(key)) {
            return CACHE.get(key);
        }

        Gdx.app.log(TAG, "Attempting to load animation from: " + key);

        FileHandle dir = null;
        try {
            dir = InternalFiles.resolve(key);
        } catch (Throwable ignored) {}

        if (dir == null || !dir.exists()) {
            dir = Gdx.files.internal(key);
        }

        if (dir == null || !dir.exists()) {
            dir = Gdx.files.local(key);
        }

        Gdx.app.log(TAG, "Resolved dir path: " + (dir != null ? dir.file().getAbsolutePath() : "NULL"));

        if (dir == null || !dir.exists()) {
            Gdx.app.error(TAG, "Directory does NOT exist: " + key);
            return null;
        }

        FileHandle[] files = dir.list((d, name) -> name.toLowerCase().endsWith(".png"));

        // اگر dir.list کار نکرد، از جاوا استاندارد دایرکتوری را بخوانیم
        if (files == null || files.length == 0) {
            java.io.File javaDir = dir.file();
            if (javaDir.isDirectory()) {
                java.io.File[] javaFiles = javaDir.listFiles((d, name) -> name.toLowerCase().endsWith(".png"));
                if (javaFiles != null && javaFiles.length > 0) {
                    files = new FileHandle[javaFiles.length];
                    for (int i = 0; i < javaFiles.length; i++) {
                        files[i] = new FileHandle(javaFiles[i]);
                    }
                }
            }
        }

        if (files == null || files.length == 0) {
            Gdx.app.error(TAG, "No PNG files found in: " + dir.file().getAbsolutePath());
            return null;
        }

        Gdx.app.log(TAG, "Found " + files.length + " frames in: " + key);

        Arrays.sort(files, Comparator.comparing(FileHandle::name));

        Array<TextureRegion> regions = new Array<>(files.length);
        for (FileHandle f : files) {
            try {
                Texture tex = new Texture(f);
                tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                regions.add(new TextureRegion(tex));
            } catch (Exception e) {
                Gdx.app.error(TAG, "Failed to load texture frame: " + f.path(), e);
            }
        }

        if (regions.size == 0) {
            Gdx.app.error(TAG, "All frame textures failed to load for: " + key);
            return null;
        }

        Animation<TextureRegion> anim = new Animation<>(frameDuration, regions, Animation.PlayMode.LOOP);
        CACHE.put(key, anim);
        Gdx.app.log(TAG, "Successfully created animation with " + regions.size + " frames for " + key);
        return anim;
    }
}
