package graphics.general.animation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PamPathResolverTest {

    @TempDir
    Path imagesRoot;

    @Test
    void keepsPathWhenFileExists() throws Exception {
        Path pam = createPam("768", "FULL", "ROTORUTABAGA", "ROTORUTABAGA.PAM");
        PamPathResolver resolver = new PamPathResolver(imagesRoot);
        String configured = "768/FULL/PLANT/ROTORUTABAGA/ROTORUTABAGA.PAM";
        assertEquals(configured, resolver.resolve(configured));
        assertTrue(resolver.exists(configured));
        assertTrue(Files.isRegularFile(pam));
    }

    @Test
    void swapsInitialToFullWhenFolderNameMatches() throws Exception {
        createPam("768", "FULL", "TALLNUT", "TALLNUT.PAM");
        PamPathResolver resolver = new PamPathResolver(imagesRoot);
        String resolved = resolver.resolve("768/INITIAL/PLANT/TALLNUT/TALLNUT.PAM");
        assertEquals("768/FULL/PLANT/TALLNUT/TALLNUT.PAM", resolved);
        assertTrue(resolver.exists(resolved));
    }

    @Test
    void resolvesRotobagaAliasToRotorutabaga() throws Exception {
        createPam("768", "FULL", "ROTORUTABAGA", "ROTORUTABAGA.PAM");
        PamPathResolver resolver = new PamPathResolver(imagesRoot);
        String resolved = resolver.resolve("768/FULL/PLANT/ROTOBAGA/ROTOBAGA.PAM");
        assertEquals("768/FULL/PLANT/ROTORUTABAGA/ROTORUTABAGA.PAM", resolved);
        assertTrue(resolver.exists(resolved));
    }

    @Test
    void resolvesCompactFolderNameWithUnderscore() throws Exception {
        createPam("768", "FULL", "PRIMAL_POTATOMINE", "PRIMAL_POTATOMINE.PAM");
        PamPathResolver resolver = new PamPathResolver(imagesRoot);
        String resolved = resolver.resolve("768/INITIAL/PLANT/PRIMALPOTATOMINE/PRIMALPOTATOMINE.PAM");
        assertEquals("768/FULL/PLANT/PRIMAL_POTATOMINE/PRIMAL_POTATOMINE.PAM", resolved);
    }

    @Test
    void resolvesOneCharacterFolderTypo() throws Exception {
        createPam("768", "INITIAL", "KERNALPULT", "KERNALPULT.PAM");
        PamPathResolver resolver = new PamPathResolver(imagesRoot);
        String resolved = resolver.resolve("768/INITIAL/PLANT/KERNELPULT/KERNELPULT.PAM");
        assertEquals("768/INITIAL/PLANT/KERNALPULT/KERNALPULT.PAM", resolved);
    }

    @Test
    void leavesTrulyMissingPathUnresolved() {
        PamPathResolver resolver = new PamPathResolver(imagesRoot);
        String configured = "768/INITIAL/PLANT/CATTAIL/CATTAIL.PAM";
        assertEquals(configured, resolver.resolve(configured));
        assertFalse(resolver.exists(configured));
    }

    @Test
    void resolvesRealRepositoryAssetsWhenPresent() {
        Path images = repositoryImages();
        Assumptions.assumeTrue(images != null && Files.isDirectory(images));
        PamPathResolver resolver = new PamPathResolver(images);
        assertEquals(
            "768/FULL/PLANT/ROTORUTABAGA/ROTORUTABAGA.PAM",
            resolver.resolve("768/FULL/PLANT/ROTOBAGA/ROTOBAGA.PAM")
        );
        assertEquals(
            "768/FULL/PLANT/TALLNUT/TALLNUT.PAM",
            resolver.resolve("768/INITIAL/PLANT/TALLNUT/TALLNUT.PAM")
        );
        assertTrue(resolver.exists("768/INITIAL/PLANT/PEASHOOTER/PEASHOOTER.PAM"));
        assertFalse(resolver.exists("768/INITIAL/PLANT/CATTAIL/CATTAIL.PAM"));
    }

    private static Path repositoryImages() {
        Path fromCore = Path.of("..", "assets", "IMAGES");
        if (Files.isDirectory(fromCore)) {
            return fromCore;
        }
        Path fromRoot = Path.of("assets", "IMAGES");
        if (Files.isDirectory(fromRoot)) {
            return fromRoot;
        }
        return null;
    }

    private Path createPam(String resolution, String pack, String folder, String fileName) throws Exception {
        Path dir = imagesRoot.resolve(Path.of(resolution, pack, "PLANT", folder));
        Files.createDirectories(dir);
        Path pam = dir.resolve(fileName);
        Files.writeString(pam, "pam");
        return pam;
    }
}
