package graphics.general.animation;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PamClipResolverTest {

    @Test
    void keepsRequestedClipWhenPresent() {
        List<String> clips = List.of("idle1_1", "idle1_2", "attack", "plantfood", "water");
        assertEquals("idle1_1", PamClipResolver.resolve("idle1_1", clips));
    }

    @Test
    void prefersExactIdleOverPrefixedIdles() {
        List<String> clips = List.of("idle1_1", "idle", "attack");
        assertEquals("idle", PamClipResolver.resolve("missing", clips));
    }

    @Test
    void selectsFirstIdlePrefixedClipWhenIdleIsAbsent() {
        List<String> clips = List.of("idle1_1", "idle1_2", "idle2_1", "attack", "plantfood", "water");
        assertEquals("idle1_1", PamClipResolver.resolve("idle", clips));
    }

    @Test
    void usesFirstClipWhenNoIdleVariantExists() {
        List<String> clips = List.of("attack", "plantfood");
        assertEquals("attack", PamClipResolver.resolve("idle", clips));
    }

    @Test
    void returnsNullWhenAnimationHasNoClips() {
        assertNull(PamClipResolver.resolve("idle", List.of()));
        assertNull(PamClipResolver.resolve("idle", null));
    }
}
