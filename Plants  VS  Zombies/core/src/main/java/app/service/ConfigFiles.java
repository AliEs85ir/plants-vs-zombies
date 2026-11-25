package app.service;

import java.io.File;

public final class ConfigFiles {
    private ConfigFiles() {
    }

    public static File resolve(String path) {
        if (path == null || path.isBlank()) {
            return new File("");
        }
        String normalized = path.replace('\\', '/');
        while (normalized.startsWith("./")) {
            normalized = normalized.substring(2);
        }
        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        File[] candidates = new File[] {
            new File(normalized),
            new File("assets", normalized),
            new File("..", "assets/" + normalized),
            new File("../assets", normalized)
        };
        for (File candidate : candidates) {
            if (candidate.isFile()) {
                return candidate;
            }
        }
        return new File("assets", normalized);
    }
}
