package app.view.izombie;

import app.view.loading.InternalFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sticker catalog for the (bonus/امتیازی) animated-sticker reaction category.
 * The JSON drives which stickers exist and their display label; the actual
 * animated art is looked up in the UI skin by {@code drawableName} once the
 * artist adds it (see {@link IZombieSticker#drawableName()}).
 */
public final class IZombieStickerCatalog {
    public static final String PATH = "ui/izombie_stickers.json";

    private static List<IZombieSticker> cached;

    private IZombieStickerCatalog() {
    }

    public static List<IZombieSticker> load() {
        if (cached != null) {
            return cached;
        }
        List<IZombieSticker> loaded = readFile();
        if (loaded.isEmpty()) {
            loaded = fallback();
        }
        cached = Collections.unmodifiableList(loaded);
        return cached;
    }

    public static IZombieSticker find(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        for (IZombieSticker sticker : load()) {
            if (id.equals(sticker.id())) {
                return sticker;
            }
        }
        return null;
    }

    private static List<IZombieSticker> readFile() {
        List<IZombieSticker> stickers = new ArrayList<>();
        try {
            if (Gdx.files == null) {
                return stickers;
            }
            FileHandle file = InternalFiles.resolve(PATH);
            if (file == null || !file.exists()) {
                Gdx.app.error("IZombieStickerCatalog", "Missing " + PATH);
                return stickers;
            }
            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            JsonValue root = json.fromJson(null, file);
            if (root == null) {
                return stickers;
            }
            JsonValue array = root.get("stickers");
            if (array == null) {
                array = root;
            }
            for (JsonValue child = array.child; child != null; child = child.next) {
                String id = child.getString("id", null);
                String label = child.getString("label", null);
                String drawableName = child.getString("drawable", null);
                if (label == null || label.isBlank()) {
                    continue;
                }
                if (id == null || id.isBlank()) {
                    id = "sticker_" + stickers.size();
                }
                stickers.add(new IZombieSticker(id.trim(), label.trim(), drawableName));
            }
        } catch (Exception e) {
            Gdx.app.error("IZombieStickerCatalog", "Failed to load " + PATH, e);
        }
        return stickers;
    }

    private static List<IZombieSticker> fallback() {
        // TODO: replace with real animated sticker art + ids once available; these
        // are placeholders so the feature is wired end-to-end today.
        List<IZombieSticker> stickers = new ArrayList<>();
        stickers.add(new IZombieSticker("sticker_laugh", "LOL", null));
        stickers.add(new IZombieSticker("sticker_gg", "GG!", null));
        stickers.add(new IZombieSticker("sticker_fire", "ON FIRE", null));
        return stickers;
    }
}
