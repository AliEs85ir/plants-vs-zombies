package app.view.quest;

import app.view.loading.InternalFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public final class QuestMenuConfigLoader {
    public static final String CONFIG_PATH = "ui/quest_menu.json";

    private QuestMenuConfigLoader() {
    }

    public static QuestMenuConfig load() {
        return load(CONFIG_PATH);
    }

    public static QuestMenuConfig load(String path) {
        FileHandle file = InternalFiles.resolve(path);
        if (file == null || !file.exists()) {
            Gdx.app.error("QuestMenuConfigLoader", "Missing config: " + path);
            return new QuestMenuConfig().sanitize();
        }
        try {
            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            json.setElementType(QuestMenuConfig.class, "quests", QuestMenuConfig.QuestPresentationConfig.class);
            json.setElementType(QuestMenuConfig.class, "progressStyles", String.class);
            QuestMenuConfig config = json.fromJson(QuestMenuConfig.class, file);
            if (config == null) {
                Gdx.app.error("QuestMenuConfigLoader", "Could not parse " + path);
                return new QuestMenuConfig().sanitize();
            }
            return config.sanitize();
        } catch (Exception exception) {
            Gdx.app.error("QuestMenuConfigLoader", "Failed to load " + path, exception);
            return new QuestMenuConfig().sanitize();
        }
    }
}
