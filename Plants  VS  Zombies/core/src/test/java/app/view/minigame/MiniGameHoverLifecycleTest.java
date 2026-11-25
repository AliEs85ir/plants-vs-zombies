package app.view.minigame;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MiniGameHoverLifecycleTest {
    @Test
    void cloudUsesStartLoopThenDeathWithoutRestartingTheLoop() {
        MiniGameHoverLifecycle lifecycle = new MiniGameHoverLifecycle("start", "attack", "death", true);
        assertEquals(MiniGameHoverLifecycle.State.NOT_HOVERED, lifecycle.state());
        lifecycle.enter();
        assertEquals(MiniGameHoverLifecycle.State.HOVER_START, lifecycle.state());
        assertEquals("start", lifecycle.clip());
        assertFalse(lifecycle.loop());
        assertTrue(lifecycle.consumeRestart());
        assertFalse(lifecycle.consumeRestart());

        lifecycle.enter();
        assertEquals(MiniGameHoverLifecycle.State.HOVER_START, lifecycle.state());
        assertFalse(lifecycle.consumeRestart());

        lifecycle.onClipFinished();
        assertEquals(MiniGameHoverLifecycle.State.HOVER_LOOP, lifecycle.state());
        assertEquals("attack", lifecycle.clip());
        assertTrue(lifecycle.loop());
        assertTrue(lifecycle.consumeRestart());

        lifecycle.onClipFinished();
        assertEquals(MiniGameHoverLifecycle.State.HOVER_LOOP, lifecycle.state());
        assertFalse(lifecycle.consumeRestart());

        lifecycle.exit();
        assertEquals(MiniGameHoverLifecycle.State.HOVER_END, lifecycle.state());
        assertEquals("death", lifecycle.clip());
        assertFalse(lifecycle.loop());
        assertTrue(lifecycle.consumeRestart());

        lifecycle.onClipFinished();
        assertEquals(MiniGameHoverLifecycle.State.NOT_HOVERED, lifecycle.state());
        assertFalse(lifecycle.visible());

        lifecycle.enter();
        assertEquals(MiniGameHoverLifecycle.State.HOVER_START, lifecycle.state());
        assertEquals("start", lifecycle.clip());
        assertTrue(lifecycle.consumeRestart());
    }

    @Test
    void leavingDuringStartGoesToDeathWithoutReplayingStart() {
        MiniGameHoverLifecycle lifecycle = new MiniGameHoverLifecycle("start", "attack", "death", true);
        lifecycle.enter();
        lifecycle.consumeRestart();
        lifecycle.exit();
        assertEquals(MiniGameHoverLifecycle.State.HOVER_END, lifecycle.state());
        assertEquals("death", lifecycle.clip());
        assertTrue(lifecycle.consumeRestart());
    }

    @Test
    void simpleLoopClipSkipsStartAndHidesImmediatelyWhenThereIsNoDeath() {
        MiniGameHoverLifecycle lifecycle = new MiniGameHoverLifecycle("", "animation", "", true);
        lifecycle.enter();
        assertEquals(MiniGameHoverLifecycle.State.HOVER_LOOP, lifecycle.state());
        assertEquals("animation", lifecycle.clip());
        assertTrue(lifecycle.loop());
        assertTrue(lifecycle.consumeRestart());
        lifecycle.enter();
        assertFalse(lifecycle.consumeRestart());
        lifecycle.exit();
        assertEquals(MiniGameHoverLifecycle.State.NOT_HOVERED, lifecycle.state());
        assertFalse(lifecycle.visible());
    }

    @Test
    void reenteringDuringDeathRestartsFromStart() {
        MiniGameHoverLifecycle lifecycle = new MiniGameHoverLifecycle("start", "attack", "death", true);
        lifecycle.enter();
        lifecycle.onClipFinished();
        lifecycle.exit();
        assertEquals(MiniGameHoverLifecycle.State.HOVER_END, lifecycle.state());
        lifecycle.enter();
        assertEquals(MiniGameHoverLifecycle.State.HOVER_START, lifecycle.state());
        assertEquals("start", lifecycle.clip());
        assertTrue(lifecycle.consumeRestart());
    }
}
