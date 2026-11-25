package app.view.adventure;

import app.service.ConfigFiles;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdventureHudConfigTest {
    @Test
    void sanitizeProvidesGlobalAndPerResourceDefaults() {
        AdventureHudConfig config = new AdventureHudConfig().sanitize();
        assertEquals("menu/icon/back.png", config.left.back.assetPath);
        assertEquals("menu/icon/collection.png", config.left.collection.assetPath);
        assertEquals("menu/icon/greenhouse.png", config.left.greenhouse.assetPath);
        assertEquals("menu/icon/quest.png", config.left.quests.assetPath);
        assertEquals("menu/icon/shop.png", config.right.shop.assetPath);
        assertEquals("menu/image/bg.png", config.globalResources().backgroundPath);
        assertEquals("menu/icon/coin2.png", config.right.resources.coin.iconPath);
        assertEquals("menu/icon/gem.png", config.right.resources.gem.iconPath);
        assertEquals("menu/icon/plantfood2.png", config.right.resources.plantFood.iconPath);
        assertEquals(50, config.right.resources.coin.debugAddAmount);
        assertEquals(10, config.right.resources.gem.debugAddAmount);
        assertEquals(1, config.right.resources.plantFood.debugAddAmount);
        assertEquals("3/5", config.right.resources.plantFood.formatted(3, 5));
        assertEquals(3, config.resourceKinds().size());
        assertTrue(config.left.spacing >= 0f);
        assertTrue(config.right.resourceSpacing >= 0f);
        assertTrue(ConfigFiles.resolve("ui/adventure_hud.json").isFile());
    }

    @Test
    void perResourceScaleOverridesGlobal() {
        AdventureHudConfig config = new AdventureHudConfig().sanitize();
        config.globalResources().iconScale = 1f;
        config.right.resources.coin.iconScale = 1.15f;
        config.right.resources.gem.iconScale = Float.NaN;
        config.right.resources.plantFood.iconScale = 0.9f;
        assertEquals(1.15f, config.resolvedIconScale(config.right.resources.coin), 0.001f);
        assertEquals(1f, config.resolvedIconScale(config.right.resources.gem), 0.001f);
        assertEquals(0.9f, config.resolvedIconScale(config.right.resources.plantFood), 0.001f);
    }

    @Test
    void blankHudValuesAreRestored() {
        AdventureHudConfig config = new AdventureHudConfig();
        config.left.back.assetPath = " ";
        config.right.resources.global.backgroundPath = "";
        config.messages.debugRequired = "";
        config.sanitize();
        assertEquals("menu/icon/back.png", config.left.back.assetPath);
        assertEquals("menu/image/bg.png", config.globalResources().backgroundPath);
        assertEquals("Debug Mode must be enabled.", config.messages.debugRequired);
    }
}
