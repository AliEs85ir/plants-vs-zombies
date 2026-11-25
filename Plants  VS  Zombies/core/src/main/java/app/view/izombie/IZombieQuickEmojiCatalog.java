package app.view.izombie;

import app.view.loading.InternalFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class IZombieQuickEmojiCatalog {
    public static final String PATH = "ui/izombie_emojis.json";

    private static List<IZombieQuickEmoji> cached;

    private IZombieQuickEmojiCatalog() {
    }

    public static List<IZombieQuickEmoji> load() {
        if (cached != null) {
            return cached;
        }
        List<IZombieQuickEmoji> loaded = readFile();
        if (loaded.isEmpty()) {
            loaded = fallback();
        }
        cached = Collections.unmodifiableList(loaded);
        return cached;
    }

    public static IZombieQuickEmoji find(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        for (IZombieQuickEmoji emoji : load()) {
            if (id.equals(emoji.id())) {
                return emoji;
            }
        }
        return null;
    }

    private static List<IZombieQuickEmoji> readFile() {
        List<IZombieQuickEmoji> emojis = new ArrayList<>();
        try {
            if (Gdx.files == null) {
                return emojis;
            }
            FileHandle file = InternalFiles.resolve(PATH);
            if (file == null || !file.exists()) {
                Gdx.app.error("IZombieQuickEmojiCatalog", "Missing " + PATH);
                return emojis;
            }
            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            JsonValue root = json.fromJson(null, file);
            if (root == null) {
                return emojis;
            }
            JsonValue array = root.get("emojis");
            if (array == null) {
                array = root;
            }
            for (JsonValue child = array.child; child != null; child = child.next) {
                String id = child.getString("id", null);
                String glyph = child.getString("glyph", null);
                String drawableName = child.getString("drawable", null);
                if ((glyph == null || glyph.isBlank()) && (drawableName == null || drawableName.isBlank())) {
                    continue;
                }
                if (id == null || id.isBlank()) {
                    id = "emoji_" + emojis.size();
                }
                emojis.add(new IZombieQuickEmoji(
                    id.trim(),
                    glyph == null ? null : glyph.trim(),
                    drawableName == null ? null : drawableName.trim()
                ));
            }
        } catch (Exception e) {
            Gdx.app.error("IZombieQuickEmojiCatalog", "Failed to load " + PATH, e);
        }
        return emojis;
    }

    private static List<IZombieQuickEmoji> fallback() {
        List<IZombieQuickEmoji> emojis = new ArrayList<>();
        emojis.add(new IZombieQuickEmoji("emoji_laugh", ":)", null));
        emojis.add(new IZombieQuickEmoji("emoji_thumbsup", "+1", null));
        emojis.add(new IZombieQuickEmoji("emoji_fire", "hot", null));
        return emojis;
    }
    public static void clearCache() {
        cached = null;
    }
}
