package game.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

final class AudioFiles {
    private AudioFiles() {
    }

    static FileHandle resolve(String path) {
        FileHandle direct = Gdx.files.internal(path);
        if (direct.exists()) {
            return direct;
        }
        FileHandle nested = Gdx.files.internal("assets/" + path);
        if (nested.exists()) {
            return nested;
        }
        return direct;
    }
}
