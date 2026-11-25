package graphics.general.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class PamPathResolver {

    private static final String[] PACKS = {"INITIAL", "FULL"};
    private static final Map<String, String> FOLDER_ALIASES = Map.of(
        "ROTOBAGA", "ROTORUTABAGA",
        "TWINSUNFLOWER", "SUNFLOWER_TWIN",
        "SUNFLOWERTWIN", "SUNFLOWER_TWIN",
        "ICEBERGLETTUCE", "ICEBURG",
        "ICEBERG", "ICEBURG"
    );

    private static volatile PamPathResolver instance;

    private final Path imagesRoot;
    private final Map<String, List<String>> byFileName = new HashMap<>();
    private final Map<String, List<String>> byFolder = new HashMap<>();
    private final Map<String, List<String>> byCompactFolder = new HashMap<>();
    private boolean indexed;

    public PamPathResolver(Path imagesRoot) {
        this.imagesRoot = imagesRoot;
    }

    public static PamPathResolver get() {
        PamPathResolver current = instance;
        if (current == null) {
            synchronized (PamPathResolver.class) {
                current = instance;
                if (current == null) {
                    current = createDefault();
                    instance = current;
                }
            }
        }
        return current;
    }

    static PamPathResolver createDefault() {
        Path path = Path.of("assets", "IMAGES");
        FileHandle images = Gdx.files.internal("assets").child("IMAGES");
        if (images.exists() && images.file() != null && images.file().isDirectory()) {
            path = images.file().toPath();
        }
        return new PamPathResolver(path);
    }

    public String resolve(String configuredPath) {
        if (configuredPath == null || configuredPath.isBlank()) {
            return configuredPath;
        }
        String path = configuredPath.replace('\\', '/');
        if (exists(path)) {
            return path;
        }
        String swapped = swapPack(path);
        if (swapped != null && exists(swapped)) {
            return swapped;
        }
        ensureIndex();
        String fileName = fileNameOf(path);
        String chosen = choose(byFileName.get(fileName.toUpperCase(Locale.ROOT)), path);
        if (chosen != null) {
            return chosen;
        }
        String folder = folderNameOf(path);
        chosen = choose(byFolder.get(folder.toUpperCase(Locale.ROOT)), path);
        if (chosen != null) {
            return chosen;
        }
        chosen = choose(byCompactFolder.get(compact(folder)), path);
        if (chosen != null) {
            return chosen;
        }
        String alias = FOLDER_ALIASES.get(compact(folder));
        if (alias != null) {
            chosen = choose(byFolder.get(alias), path);
            if (chosen == null) {
                chosen = choose(byCompactFolder.get(compact(alias)), path);
            }
            if (chosen != null) {
                return chosen;
            }
        }
        chosen = findByDistance(folder, path);
        if (chosen != null) {
            return chosen;
        }
        chosen = findByPrefix(folder, path);
        if (chosen != null) {
            return chosen;
        }
        return path;
    }

    public boolean exists(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return false;
        }
        Path file = imagesRoot;
        for (String part : relativePath.split("/")) {
            if (!part.isEmpty()) {
                file = file.resolve(part);
            }
        }
        return Files.isRegularFile(file);
    }

    private String findByDistance(String folder, String configuredPath) {
        String compactFolder = compact(folder);
        String unique = null;
        for (Map.Entry<String, List<String>> entry : byCompactFolder.entrySet()) {
            if (levenshtein(compactFolder, entry.getKey()) > 1) {
                continue;
            }
            String chosen = choose(entry.getValue(), configuredPath);
            if (chosen == null) {
                continue;
            }
            if (unique != null && !unique.equals(chosen)) {
                return null;
            }
            unique = chosen;
        }
        return unique;
    }

    private String findByPrefix(String folder, String configuredPath) {
        String compactFolder = compact(folder);
        if (compactFolder.length() < 10) {
            return null;
        }
        String unique = null;
        for (Map.Entry<String, List<String>> entry : byCompactFolder.entrySet()) {
            String candidate = entry.getKey();
            int shorter = Math.min(compactFolder.length(), candidate.length());
            if (shorter < 10) {
                continue;
            }
            boolean match = compactFolder.startsWith(candidate) || candidate.startsWith(compactFolder);
            if (!match) {
                continue;
            }
            String chosen = choose(entry.getValue(), configuredPath);
            if (chosen == null) {
                continue;
            }
            if (unique != null && !unique.equals(chosen)) {
                return null;
            }
            unique = chosen;
        }
        return unique;
    }

    private String choose(List<String> candidates, String configuredPath) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }
        if (candidates.size() == 1) {
            return candidates.get(0);
        }
        String pack = packOf(configuredPath);
        if (pack != null) {
            for (String candidate : candidates) {
                if (pack.equals(packOf(candidate))) {
                    return candidate;
                }
            }
        }
        for (String preferredPack : PACKS) {
            for (String candidate : candidates) {
                if (preferredPack.equals(packOf(candidate))) {
                    return candidate;
                }
            }
        }
        return candidates.get(0);
    }

    private void ensureIndex() {
        if (indexed) {
            return;
        }
        synchronized (this) {
            if (indexed) {
                return;
            }
            for (String pack : PACKS) {
                Path plantRoot = imagesRoot.resolve(Path.of("768", pack, "PLANT"));
                indexPlantRoot(plantRoot, "768/" + pack + "/PLANT");
            }
            indexed = true;
        }
    }

    private void indexPlantRoot(Path plantRoot, String relativePrefix) {
        if (!Files.isDirectory(plantRoot)) {
            return;
        }
        try (DirectoryStream<Path> folders = Files.newDirectoryStream(plantRoot)) {
            for (Path folder : folders) {
                if (!Files.isDirectory(folder)) {
                    continue;
                }
                String folderName = folder.getFileName().toString();
                try (DirectoryStream<Path> files = Files.newDirectoryStream(folder, "*.PAM")) {
                    for (Path pam : files) {
                        String relative = relativePrefix + "/" + folderName + "/" + pam.getFileName();
                        byFileName.computeIfAbsent(pam.getFileName().toString().toUpperCase(Locale.ROOT), key -> new ArrayList<>()).add(relative);
                        byFolder.computeIfAbsent(folderName.toUpperCase(Locale.ROOT), key -> new ArrayList<>()).add(relative);
                        byCompactFolder.computeIfAbsent(compact(folderName), key -> new ArrayList<>()).add(relative);
                    }
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to index plant PAM files under " + plantRoot, e);
        }
    }

    private static String swapPack(String path) {
        if (path.contains("/INITIAL/")) {
            return path.replaceFirst("/INITIAL/", "/FULL/");
        }
        if (path.contains("/FULL/")) {
            return path.replaceFirst("/FULL/", "/INITIAL/");
        }
        return null;
    }

    private static String packOf(String path) {
        if (path.contains("/INITIAL/")) {
            return "INITIAL";
        }
        if (path.contains("/FULL/")) {
            return "FULL";
        }
        return null;
    }

    private static String fileNameOf(String path) {
        int slash = path.lastIndexOf('/');
        return slash >= 0 ? path.substring(slash + 1) : path;
    }

    private static String folderNameOf(String path) {
        int last = path.lastIndexOf('/');
        if (last <= 0) {
            return "";
        }
        int previous = path.lastIndexOf('/', last - 1);
        return previous >= 0 ? path.substring(previous + 1, last) : path.substring(0, last);
    }

    static String compact(String name) {
        if (name == null) {
            return "";
        }
        return name.replace("_", "").replace("-", "").toUpperCase(Locale.ROOT);
    }

    static int levenshtein(String left, String right) {
        int n = left.length();
        int m = right.length();
        int[] previous = new int[m + 1];
        int[] current = new int[m + 1];
        for (int j = 0; j <= m; j++) {
            previous[j] = j;
        }
        for (int i = 1; i <= n; i++) {
            current[0] = i;
            for (int j = 1; j <= m; j++) {
                int cost = left.charAt(i - 1) == right.charAt(j - 1) ? 0 : 1;
                current[j] = Math.min(
                    Math.min(current[j - 1] + 1, previous[j] + 1),
                    previous[j - 1] + cost
                );
            }
            int[] swap = previous;
            previous = current;
            current = swap;
        }
        return previous[m];
    }
}
