package app.view.hud.elements;

import app.model.App;
import app.model.user.User;
import app.service.AppService;
import app.view.adventure.AdventureHudConfig;
import app.view.adventure.AdventureHudConfigLoader;
import app.view.adventure.AdventureResourceGroup;
import app.view.mainmenu.MenuTextures;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import game.component.AssetManager;
import game.entity.plantfood.PlantFoodInventory;
import game.settings.GameSettings;
import game.world.GameWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

/**
 * In-game Coin/Gem/Plant Food/Sun using the Adventure HUD resource actors.
 */
public final class PlayHudResourceStrip extends WidgetGroup implements Disposable {
    private final AdventureHudConfig config;
    private final ObjectMap<String, Texture> textures = new ObjectMap<>();
    private final Array<BitmapFont> ownedFonts = new Array<>();
    private final List<AdventureResourceGroup> groups = new ArrayList<>();
    private final GameWorld world;
    private final IntConsumer debugAddSun;
    private float prefWidth;
    private float prefHeight;

    public PlayHudResourceStrip(GameWorld world, IntConsumer debugAddSun) {
        this.world = world;
        this.debugAddSun = debugAddSun == null ? amount -> {
        } : debugAddSun;
        this.config = AdventureHudConfigLoader.load();
        setTransform(false);
        setTouchable(Touchable.childrenOnly);
        BitmapFont errorFont = resolveFont(config.messages.font, config.messages.fontFallback);
        List<AdventureHudConfig.ResourceKindConfig> kinds = new ArrayList<>(config.resourceKinds());
        kinds.add(sunKind());
        float width = 0f;
        float height = 0f;
        for (AdventureHudConfig.ResourceKindConfig kind : kinds) {
            BitmapFont valueFont = resolveFont(config.resolvedFont(kind), config.resolvedFontFallback(kind));
            AdventureResourceGroup group = new AdventureResourceGroup(
                config,
                kind,
                texture(config.resolvedBackgroundPath(kind)),
                texture(config.resolvedIconPath(kind)),
                texture(config.resolvedPlusAssetPath(kind)),
                valueFont,
                errorFont
            );
            group.setPlusAction(() -> onPlus(group));
            groups.add(group);
            addActor(group);
            float visualWidth = config.groupContentWidth(kind) * config.resolvedGroupScale(kind);
            float visualHeight = config.backgroundDrawHeight(kind) * config.resolvedGroupScale(kind);
            width += visualWidth + config.right.resourceSpacing;
            height = Math.max(height, visualHeight);
        }
        if (!groups.isEmpty()) {
            width -= config.right.resourceSpacing;
        }
        this.prefWidth = Math.max(1f, width);
        this.prefHeight = Math.max(1f, height);
        setSize(prefWidth, prefHeight);
    }

    public void refresh() {
        User user = App.getCurrentUser();
        for (AdventureResourceGroup group : groups) {
            group.setValueText(displayValue(user, group.kind()));
        }
    }

    @Override
    public float getPrefWidth() {
        return prefWidth;
    }

    @Override
    public float getPrefHeight() {
        return prefHeight;
    }

    @Override
    public void layout() {
        float cursor = getWidth();
        float top = getHeight();
        for (int i = 0; i < groups.size(); i++) {
            AdventureResourceGroup group = groups.get(i);
            AdventureHudConfig.ResourceKindConfig kind = group.kind();
            float visualWidth = config.groupContentWidth(kind) * config.resolvedGroupScale(kind);
            float visualHeight = config.backgroundDrawHeight(kind) * config.resolvedGroupScale(kind);
            cursor -= visualWidth;
            float x = cursor + config.resolvedGroupOffsetX(kind);
            float y = top - visualHeight + config.resolvedGroupOffsetY(kind);
            group.applyLayout(x, y);
            cursor -= config.right.resourceSpacing;
        }
    }

    private String displayValue(User user, AdventureHudConfig.ResourceKindConfig kind) {
        int value = 0;
        int max = 0;
        String id = kind.id == null ? "" : kind.id;
        if ("sun".equals(id)) {
            value = world == null ? 0 : Math.max(0, world.getSunAmount());
        } else if (user != null) {
            if ("gem".equals(id)) {
                value = Math.max(0, user.getGems());
            } else if ("plantFood".equals(id)) {
                PlantFoodInventory inventory = user.getFoodInventory();
                if (inventory != null) {
                    value = inventory.getAmount();
                    max = inventory.getMaxAmount();
                }
            } else {
                value = Math.max(0, user.getCoins());
            }
        }
        return kind.formatted(value, max);
    }

    private void onPlus(AdventureResourceGroup group) {
        if (!GameSettings.gameplay().debugMode()) {
            group.showError(config.messages.debugRequired);
            return;
        }
        AdventureHudConfig.ResourceKindConfig kind = group.kind();
        String id = kind.id == null ? "" : kind.id;
        int amount = Math.max(1, kind.debugAddAmount);
        User user = App.getCurrentUser();
        if ("sun".equals(id)) {
            debugAddSun.accept(amount);
            group.clearError();
            refresh();
            return;
        }
        if (user == null) {
            group.showError(config.messages.missingUser);
            return;
        }
        if ("gem".equals(id)) {
            user.addGems(amount);
        } else if ("plantFood".equals(id)) {
            PlantFoodInventory inventory = user.getFoodInventory();
            if (inventory == null) {
                group.showError(config.messages.missingUser);
                return;
            }
            for (int i = 0; i < amount; i++) {
                inventory.addPlantFood();
            }
            AppService.markGameDataDirty();
        } else {
            user.addCoins(amount);
        }
        AppService.updateAllUsers();
        group.clearError();
        refresh();
    }

    private static AdventureHudConfig.ResourceKindConfig sunKind() {
        AdventureHudConfig.ResourceKindConfig kind = new AdventureHudConfig.ResourceKindConfig();
        kind.id = "sun";
        kind.visible = true;
        kind.iconPath = "menu/icon/sun.png";
        kind.debugAddAmount = 100;
        kind.valueFormat = "{value}";
        kind.iconScale = 1.10f;
        return kind;
    }

    private Texture texture(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        String key = path.replace('\\', '/');
        if (textures.containsKey(key)) {
            return textures.get(key);
        }
        Texture loaded = MenuTextures.load(key, "PlayHudResourceStrip");
        textures.put(key, loaded);
        return loaded;
    }

    private BitmapFont resolveFont(String name, String fallback) {
        BitmapFont font = tryFont(name);
        if (font != null) {
            return font;
        }
        font = tryFont(fallback);
        if (font != null) {
            return font;
        }
        if (AssetManager.skin != null) {
            return AssetManager.font(AssetManager.DEFAULT_FONT);
        }
        BitmapFont created = new BitmapFont();
        ownedFonts.add(created);
        return created;
    }

    private static BitmapFont tryFont(String name) {
        if (name == null || name.isBlank() || AssetManager.skin == null) {
            return null;
        }
        if (AssetManager.skin.has(name, BitmapFont.class)) {
            BitmapFont font = AssetManager.skin.getFont(name);
            font.setUseIntegerPositions(false);
            return font;
        }
        return null;
    }

    @Override
    public void dispose() {
        for (Texture texture : textures.values()) {
            if (texture != null) {
                texture.dispose();
            }
        }
        textures.clear();
        for (BitmapFont font : ownedFonts) {
            font.dispose();
        }
        ownedFonts.clear();
        groups.clear();
    }
}
