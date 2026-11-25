package app.view.izombie;

import app.view.loading.InternalFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class IZombieQuickMessageCatalog {
    public static final String PATH = "ui/izombie_messages.json";

    private static List<IZombieQuickMessage> cached;

    private IZombieQuickMessageCatalog() {
    }

    public static List<IZombieQuickMessage> load() {
        if (cached != null) {
            return cached;
        }
        List<IZombieQuickMessage> loaded = readFile();
        if (loaded.isEmpty()) {
            loaded = fallback();
        }
        cached = Collections.unmodifiableList(loaded);
        return cached;
    }

    public static IZombieQuickMessage find(String id, String text) {
        List<IZombieQuickMessage> messages = load();
        if (id != null && !id.isBlank()) {
            for (IZombieQuickMessage message : messages) {
                if (id.equals(message.id())) {
                    return message;
                }
            }
        }
        if (text != null && !text.isBlank()) {
            for (IZombieQuickMessage message : messages) {
                if (text.equals(message.text())) {
                    return message;
                }
            }
            return new IZombieQuickMessage("custom", text.trim());
        }
        return null;
    }

    private static List<IZombieQuickMessage> readFile() {
        List<IZombieQuickMessage> messages = new ArrayList<>();
        try {
            if (Gdx.files == null) {
                return messages;
            }
            FileHandle file = InternalFiles.resolve(PATH);
            if (file == null || !file.exists()) {
                Gdx.app.error("IZombieQuickMessageCatalog", "Missing " + PATH);
                return messages;
            }
            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            JsonValue root = json.fromJson(null, file);
            if (root == null) {
                return messages;
            }
            JsonValue array = root.get("messages");
            if (array == null) {
                array = root;
            }
            for (JsonValue child = array.child; child != null; child = child.next) {
                String id = child.getString("id", null);
                String text = child.getString("text", null);
                if (text == null || text.isBlank()) {
                    continue;
                }
                if (id == null || id.isBlank()) {
                    id = "msg_" + messages.size();
                }
                messages.add(new IZombieQuickMessage(id.trim(), text.trim()));
            }
        } catch (Exception e) {
            Gdx.app.error("IZombieQuickMessageCatalog", "Failed to load " + PATH, e);
        }
        return messages;
    }

    private static List<IZombieQuickMessage> fallback() {
        List<IZombieQuickMessage> messages = new ArrayList<>();
        messages.add(new IZombieQuickMessage("nice_try", "Nice try!"));
        messages.add(new IZombieQuickMessage("well_played", "Well played!"));
        messages.add(new IZombieQuickMessage("gg", "GG"));
        return messages;
    }
}
