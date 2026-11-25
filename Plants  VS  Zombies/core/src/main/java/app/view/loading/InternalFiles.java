package app.view.loading;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.File;

public final class InternalFiles {
    private InternalFiles() {
    }

    public static FileHandle resolve(String path) {
        if (path == null || path.isBlank()) {
            return Gdx.files.internal("");
        }
        String normalized = path.replace('\\', '/');
        while (normalized.startsWith("./")) {
            normalized = normalized.substring(2);
        }
        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }

        FileHandle liveAssets = Gdx.files.local("assets/" + normalized);
        if (liveAssets.exists()) {
            return liveAssets;
        }
        FileHandle absoluteAssets = Gdx.files.absolute(new File("assets", normalized).getAbsolutePath());
        if (absoluteAssets.exists()) {
            return absoluteAssets;
        }
        FileHandle local = Gdx.files.local(normalized);
        if (local.exists()) {
            return local;
        }
        FileHandle direct = Gdx.files.internal(normalized);
        if (direct.exists()) {
            return direct;
        }
        FileHandle nested = Gdx.files.internal("assets/" + normalized);
        if (nested.exists()) {
            return nested;
        }
        return liveAssets.exists() ? liveAssets : direct;
    }
}
