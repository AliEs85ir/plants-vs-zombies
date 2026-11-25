package app.view.minigame;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class MiniGameHitTesterTest {
    @Test
    void hitUsesTiledBoundsAndLastOverlappingObjectWins() {
        MiniGameDefinition first = definition("first", new Rectangle(0f, 0f, 40f, 40f));
        MiniGameDefinition second = definition("second", new Rectangle(20f, 0f, 40f, 40f));
        MiniGameMapSpace space = new MiniGameMapSpace(100f, 100f, new Rectangle(0f, 0f, 100f, 100f));
        MiniGameHitTester tester = new MiniGameHitTester(space, List.of(first, second));
        assertSame(first, tester.at(10f, 10f));
        assertSame(second, tester.at(30f, 10f));
        assertSame(second, tester.at(50f, 10f));
        assertNull(tester.at(90f, 90f));
    }

    @Test
    void pointsOutsideMapBoundsAreIgnoredEvenIfAMiniGameRectWouldMatch() {
        MiniGameDefinition game = definition("outside-map", new Rectangle(0f, 0f, 50f, 50f));
        MiniGameMapSpace space = new MiniGameMapSpace(100f, 100f, new Rectangle(60f, 60f, 20f, 20f));
        MiniGameHitTester tester = new MiniGameHitTester(space, List.of(game));
        assertNull(tester.at(10f, 10f));
        assertNull(tester.at(70f, 70f));
    }

    @Test
    void mapAndStageTransformsAreInverses() {
        MiniGameMapSpace space = new MiniGameMapSpace(1920f, 1080f, new Rectangle(0f, 0f, 1920f, 1080f));
        Vector2 map = space.toMap(640f, 360f, 1280f, 720f, new Vector2());
        assertEquals(960f, map.x, 0.001f);
        assertEquals(540f, map.y, 0.001f);
        Vector2 stage = space.toStage(960f, 540f, 1280f, 720f, new Vector2());
        assertEquals(640f, stage.x, 0.001f);
        assertEquals(360f, stage.y, 0.001f);
        Rectangle stageRect = space.toStage(new Rectangle(100f, 200f, 300f, 400f), 1280f, 720f, new Rectangle());
        assertEquals(100f * (1280f / 1920f), stageRect.x, 0.001f);
        assertEquals(200f * (720f / 1080f), stageRect.y, 0.001f);
        assertEquals(300f * (1280f / 1920f), stageRect.width, 0.001f);
        assertEquals(400f * (720f / 1080f), stageRect.height, 0.001f);
        assertEquals(1280f / 1920f, space.uniformScale(1280f, 720f), 0.001f);
    }

    private static MiniGameDefinition definition(String id, Rectangle bounds) {
        return new MiniGameDefinition(id, bounds, "none", "", true, List.of());
    }
}
