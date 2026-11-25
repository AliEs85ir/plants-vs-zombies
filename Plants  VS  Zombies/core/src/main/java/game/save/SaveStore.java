package game.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class SaveStore {
    private static final String ROOT = "saves";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public SaveResult save(SaveKey key, LevelSaveData data) {
        if (key == null || data == null) {
            return SaveResult.failure("Save data is missing.");
        }
        if (key.chapterName().isBlank()) {
            return SaveResult.failure("Chapter identity is missing.");
        }
        data.setVersion(LevelSaveData.CURRENT_VERSION);
        FileHandle file = fileFor(key);
        FileHandle temp = file.sibling(file.name() + ".tmp");
        try {
            file.parent().mkdirs();
            temp.writeString(gson.toJson(data), false, "UTF-8");
            if (file.exists()) {
                file.delete();
            }
            temp.moveTo(file);
            return SaveResult.ok(data);
        } catch (RuntimeException exception) {
            if (temp.exists()) {
                temp.delete();
            }
            Gdx.app.error("SaveStore", "Failed to write save", exception);
            return SaveResult.failure("Could not save the game.");
        }
    }

    public SaveResult load(SaveKey key) {
        if (key == null) {
            return SaveResult.failure("Save key is missing.");
        }
        FileHandle file = fileFor(key);
        if (!file.exists()) {
            return SaveResult.failure("No saved game was found.");
        }
        try {
            String json = file.readString("UTF-8");
            if (json == null || json.isBlank()) {
                return SaveResult.failure("Saved game is empty.");
            }
            LevelSaveData data = gson.fromJson(json, LevelSaveData.class);
            if (data == null) {
                return SaveResult.failure("Saved game could not be read.");
            }
            if (data.getVersion() != LevelSaveData.CURRENT_VERSION) {
                return SaveResult.failure("Saved game is not compatible.");
            }
            if (data.getChapterName() == null || data.getChapterName().isBlank()) {
                return SaveResult.failure("Saved game is incomplete.");
            }
            return SaveResult.ok(data);
        } catch (RuntimeException exception) {
            Gdx.app.error("SaveStore", "Failed to read save", exception);
            return SaveResult.failure("Saved game is damaged.");
        }
    }

    public boolean exists(SaveKey key) {
        return key != null && fileFor(key).exists();
    }

    public SaveResult delete(SaveKey key) {
        if (key == null) {
            return SaveResult.failure("Save key is missing.");
        }
        FileHandle file = fileFor(key);
        try {
            if (file.exists() && !file.delete()) {
                return SaveResult.failure("Could not remove the previous save.");
            }
            FileHandle temp = file.sibling(file.name() + ".tmp");
            if (temp.exists()) {
                temp.delete();
            }
            return SaveResult.ok();
        } catch (RuntimeException exception) {
            Gdx.app.error("SaveStore", "Failed to delete save", exception);
            return SaveResult.failure("Could not remove the previous save.");
        }
    }

    private FileHandle fileFor(SaveKey key) {
        return Gdx.files.local(ROOT + "/" + key.userId() + "/" + key.chapterName() + "/" + key.fileName());
    }
}
