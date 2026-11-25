package app.view.minigame;

import app.service.ConfigFiles;
import com.badlogic.gdx.math.Rectangle;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MiniGameMenuConfigTest {
    @Test
    void sanitizeProvidesTiledPathFourGamesAndTwoDefaultAnimations() {
        MiniGameMenuConfig config = new MiniGameMenuConfig().sanitize();
        assertEquals("Maps/mini_game.tmx", config.mapPath);
        assertEquals("bg", config.bgLayer);
        assertEquals("map", config.mapLayer);
        assertEquals("miniGames", config.miniGamesLayer);
        assertEquals(4, config.games.size());
        assertEquals("Walnut Bowling", config.games.get(0).id);
        assertEquals("start_chapter", config.games.get(0).action);
        assertEquals("WALLNUT_BOWLING", config.games.get(0).chapter);
        assertEquals("Vase Breaker", config.games.get(1).id);
        assertEquals("Plant zombies", config.games.get(2).id);
        assertEquals("PLANT_ZOMBIES", config.games.get(2).chapter);
        assertEquals("Beghouled", config.games.get(3).id);
        assertEquals("BEGHOULED", config.games.get(3).chapter);
        assertEquals("start_chapter", config.games.get(3).action);
        assertEquals(2, config.defaultHoverAnimations.size());
        assertEquals("electricity", config.defaultHoverAnimations.get(0).id);
        assertEquals("animation", config.defaultHoverAnimations.get(0).clip);
        assertEquals("cloud", config.defaultHoverAnimations.get(1).id);
        assertEquals("start", config.defaultHoverAnimations.get(1).startClip);
        assertEquals("attack", config.defaultHoverAnimations.get(1).loopClip);
        assertEquals("death", config.defaultHoverAnimations.get(1).endClip);
        assertTrue(config.backButton.visible);
        assertSame(config.games.get(0), config.gameFor("Walnut Bowling"));
        assertSame(config.games.get(2), config.gameFor("plant zombies"));
    }

    @Test
    void independentHoverListsAreCopiedPerGame() {
        MiniGameMenuConfig config = new MiniGameMenuConfig().sanitize();
        MiniGameTiledMap.ObjectRef walnut = new MiniGameTiledMap.ObjectRef(
            "Walnut Bowling",
            new Rectangle(10f, 20f, 30f, 40f)
        );
        MiniGameTiledMap.ObjectRef vase = new MiniGameTiledMap.ObjectRef(
            "Vase Breaker",
            new Rectangle(50f, 20f, 30f, 40f)
        );
        List<MiniGameDefinition> definitions = MiniGameDefinition.bind(List.of(walnut, vase), config);
        assertEquals(2, definitions.size());
        assertEquals(2, definitions.get(0).hoverAnimations().size());
        assertEquals(2, definitions.get(1).hoverAnimations().size());
        assertNotSame(definitions.get(0).hoverAnimations(), definitions.get(1).hoverAnimations());
        definitions.get(0).hoverAnimations().get(0).scale = 3f;
        assertEquals(1f, definitions.get(1).hoverAnimations().get(0).scale, 0.001f);
    }

    @Test
    void unknownTiledObjectStillBecomesADefinitionWithoutHardcodedCount() {
        MiniGameMenuConfig config = new MiniGameMenuConfig().sanitize();
        MiniGameTiledMap.ObjectRef extra = new MiniGameTiledMap.ObjectRef(
            "New MiniGame",
            new Rectangle(1f, 2f, 3f, 4f)
        );
        List<MiniGameDefinition> definitions = MiniGameDefinition.bind(List.of(extra), config);
        assertEquals(1, definitions.size());
        assertEquals("New MiniGame", definitions.get(0).id());
        assertEquals("none", definitions.get(0).action());
        assertEquals(2, definitions.get(0).hoverAnimations().size());
        assertFalse(definitions.get(0).clickable());
        assertEquals(2.5f, definitions.get(0).centerX(), 0.001f);
        assertEquals(4f, definitions.get(0).centerY(), 0.001f);
    }

    @Test
    void emptyHoverAnimationListMeansNoHoverAnimations() {
        MiniGameMenuConfig config = new MiniGameMenuConfig().sanitize();
        MiniGameMenuConfig.GameConfig game = config.gameFor("Beghouled");
        game.hoverAnimations = new java.util.ArrayList<>();
        MiniGameTiledMap.ObjectRef object = new MiniGameTiledMap.ObjectRef(
            "Beghouled",
            new Rectangle(0f, 0f, 10f, 10f)
        );
        List<MiniGameDefinition> definitions = MiniGameDefinition.bind(List.of(object), config);
        assertTrue(definitions.get(0).hoverAnimations().isEmpty());
    }

    @Test
    void animationScaleRotationAndAlphaStayInConfiguration() {
        MiniGameMenuConfig.HoverAnimationConfig animation = MiniGameMenuConfig.electricity();
        animation.offsetX = 12f;
        animation.offsetY = -8f;
        animation.scale = 0.5f;
        animation.scaleX = 1.4f;
        animation.scaleY = 0.8f;
        animation.rotation = -15f;
        animation.alpha = 0.5f;
        animation.sanitize();
        assertEquals(12f, animation.offsetX, 0.001f);
        assertEquals(-8f, animation.offsetY, 0.001f);
        assertEquals(0.5f, animation.scale, 0.001f);
        assertEquals(0.7f, animation.resolvedScaleX(), 0.001f);
        assertEquals(0.4f, animation.resolvedScaleY(), 0.001f);
        assertEquals(-15f, animation.rotation, 0.001f);
        assertEquals(0.5f, animation.alpha, 0.001f);
        MiniGameMenuConfig.HoverAnimationConfig copy = animation.copy();
        copy.scale = 9f;
        assertEquals(0.5f, animation.scale, 0.001f);
    }

    @Test
    void jsonConfigDrivesHoverScaleForEveryTiledGame() {
        assertTrue(ConfigFiles.resolve("ui/mini_game_menu.json").isFile());
        MiniGameMenuConfig config = MiniGameMenuConfigLoader.load();
        assertEquals("Maps/mini_game.tmx", config.mapPath);
        assertEquals(4, config.games.size());
        assertEquals(3.2f, config.defaultHoverAnimations.get(0).scale, 0.001f);
        MiniGameMenuConfig.GameConfig walnut = config.gameFor("Walnut Bowling");
        MiniGameMenuConfig.GameConfig vase = config.gameFor("Vase Breaker");
        assertEquals("WALLNUT_BOWLING", walnut.chapter);
        assertEquals("VASE_BREAKER", vase.chapter);
        MiniGameMenuConfig.GameConfig beghouled = config.gameFor("Beghouled");
        assertEquals("BEGHOULED", beghouled.chapter);
        assertTrue(beghouled.clickable());
        MiniGameTiledMap.ObjectRef object = new MiniGameTiledMap.ObjectRef(
            "Walnut Bowling",
            new Rectangle(0f, 0f, 10f, 20f)
        );
        List<MiniGameDefinition> definitions = MiniGameDefinition.bind(List.of(object), config);
        assertEquals(3.2f, definitions.get(0).hoverAnimations().get(0).scale, 0.001f);
        assertEquals(3.2f, definitions.get(0).hoverAnimations().get(0).resolvedScaleX(), 0.001f);
        assertEquals("plantfood_end", definitions.get(0).hoverAnimations().get(0).clip);
        assertEquals("attack", definitions.get(0).hoverAnimations().get(1).loopClip);
        assertEquals("death", definitions.get(0).hoverAnimations().get(1).endClip);
    }
}
