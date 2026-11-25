package app.view.adventure;

import app.model.App;
import app.model.user.User;
import app.service.AppService;
import app.validator.ValidationMessages;
import app.view.mainmenu.MenuTextures;
import app.view.quest.QuestMenuPanel;
import app.view.screen.ScreenManager;
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
import plants_vs_zombies.PlantsVSZombies;

import java.util.ArrayList;
import java.util.List;

public final class AdventureHud extends WidgetGroup implements Disposable {
    private final PlantsVSZombies game;
    private final Runnable onBack;
    private final boolean showGreenhouseButton;
    private final boolean showShopButton;
    private final boolean showCollectionButton;
    private final AdventureHudConfig config;
    private final ObjectMap<String, Texture> textures = new ObjectMap<>();
    private final Array<BitmapFont> ownedFonts = new Array<>();
    private final List<AdventureResourceGroup> resourceGroups = new ArrayList<>();
    private AdventureHudIconButton backButton;
    private AdventureHudIconButton collectionButton;
    private AdventureHudIconButton greenhouseButton;
    private AdventureHudIconButton shopButton;
    private AdventureHudIconButton questsButton;
    private QuestMenuPanel questMenu;
    private boolean leaving;

    public AdventureHud(PlantsVSZombies game, Runnable onBack) {
        this(game, onBack, true);
    }

    public AdventureHud(PlantsVSZombies game, Runnable onBack, boolean showGreenhouseButton) {
        this(game, onBack, showGreenhouseButton, true);
    }

    public AdventureHud(
        PlantsVSZombies game,
        Runnable onBack,
        boolean showGreenhouseButton,
        boolean showShopButton
    ) {
        this(game, onBack, showGreenhouseButton, showShopButton, true);
    }

    public AdventureHud(
        PlantsVSZombies game,
        Runnable onBack,
        boolean showGreenhouseButton,
        boolean showShopButton,
        boolean showCollectionButton
    ) {
        this.game = game;
        this.onBack = onBack == null ? () -> {
        } : onBack;
        this.showGreenhouseButton = showGreenhouseButton;
        this.showShopButton = showShopButton;
        this.showCollectionButton = showCollectionButton;
        this.config = AdventureHudConfigLoader.load();
        setFillParent(true);
        setTransform(false);
        setTouchable(Touchable.childrenOnly);
        build();
    }

    private void build() {
        backButton = navButton(config.left.back, "menu-adventure-back", this::goBack);
        if (showCollectionButton) {
            collectionButton = navButton(config.left.collection, "menu-adventure-collection", this::goCollection);
        }
        if (showGreenhouseButton) {
            greenhouseButton = navButton(config.left.greenhouse, "menu-adventure-greenhouse", this::goGreenhouse);
        }
        if (showShopButton) {
            shopButton = navButton(config.right.shop, "menu-adventure-shop", this::goShop);
        }
        questsButton = navButton(config.left.quests, "menu-adventure-quests", this::openQuests);
        BitmapFont errorFont = resolveFont(config.messages.font, config.messages.fontFallback);
        for (AdventureHudConfig.ResourceKindConfig kind : config.resourceKinds()) {
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
            resourceGroups.add(group);
            addActor(group);
        }
        applyZOrder();
    }

    private AdventureHudIconButton navButton(
        AdventureHudConfig.HudIconConfig style,
        String name,
        Runnable action
    ) {
        if (style == null || !style.visible) {
            return null;
        }
        Texture texture = texture(style.assetPath);
        if (texture == null) {
            return null;
        }
        AdventureHudIconButton button = new AdventureHudIconButton(texture, style, name);
        button.setAction(() -> {
            if (leaving) {
                return;
            }
            action.run();
        });
        addActor(button);
        return button;
    }

    private void applyZOrder() {
        int z = 1;
        for (AdventureResourceGroup group : resourceGroups) {
            group.setZIndex(z++);
        }
        if (shopButton != null) {
            shopButton.setZIndex(Math.max(z, config.right.z));
        }
        if (backButton != null) {
            backButton.setZIndex(Math.max(z, config.left.z));
        }
        if (collectionButton != null) {
            collectionButton.setZIndex(Math.max(z, config.left.z));
        }
        if (greenhouseButton != null) {
            greenhouseButton.setZIndex(Math.max(z, config.left.z));
        }
        if (questsButton != null) {
            questsButton.setZIndex(Math.max(z, config.left.z));
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        refreshValues();
    }

    @Override
    public void layout() {
        layoutLeft();
        layoutRight();
    }

    private void layoutLeft() {
        float x = config.left.originX(getWidth());
        float top = config.left.top(getHeight());
        float spacing = config.left.spacing;
        x = placeNav(backButton, config.left.back, config.left.scale, x, top, 0f);
        x = placeNav(collectionButton, config.left.collection, config.left.scale, x, top, spacing);
        x = placeNav(greenhouseButton, config.left.greenhouse, config.left.scale, x, top, spacing);
        placeNav(questsButton, config.left.quests, config.left.scale, x, top, spacing);
    }

    private void layoutRight() {
        float cursor = config.right.originX(getWidth());
        float top = config.right.top(getHeight());
        if (shopButton != null) {
            float width = config.right.shop.drawWidth(config.right.scale);
            float height = config.right.shop.drawHeight(config.right.scale);
            cursor -= width;
            shopButton.place(
                cursor + config.right.shop.offsetX,
                top - height + config.right.shop.offsetY,
                width,
                height
            );
            cursor -= config.right.shopToResourcesSpacing;
        }
        for (AdventureResourceGroup group : resourceGroups) {
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

    private float placeNav(
        AdventureHudIconButton button,
        AdventureHudConfig.HudIconConfig style,
        float groupScale,
        float x,
        float top,
        float leadingSpacing
    ) {
        if (button == null || style == null || !style.visible) {
            return x;
        }
        x += Math.max(0f, leadingSpacing);
        float width = style.drawWidth(groupScale);
        float height = style.drawHeight(groupScale);
        button.place(x + style.offsetX, top - height + style.offsetY, width, height);
        return x + width;
    }

    private void refreshValues() {
        User user = App.getCurrentUser();
        for (AdventureResourceGroup group : resourceGroups) {
            group.setValueText(displayValue(user, group.kind()));
        }
    }

    private String displayValue(User user, AdventureHudConfig.ResourceKindConfig kind) {
        int value = 0;
        int max = 0;
        if (user != null) {
            String id = kind.id == null ? "" : kind.id;
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
        User user = App.getCurrentUser();
        if (user == null) {
            String message = config.messages.missingUser;
            if (message == null || message.isBlank()) {
                message = ValidationMessages.active().missingUser;
            }
            group.showError(message);
            return;
        }
        if (!GameSettings.gameplay().debugMode()) {
            group.showError(config.messages.debugRequired);
            return;
        }
        AdventureHudConfig.ResourceKindConfig kind = group.kind();
        int amount = Math.max(1, kind.debugAddAmount);
        String id = kind.id == null ? "" : kind.id;
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
        } else {
            user.addCoins(amount);
        }
        AppService.updateAllUsers();
        group.setValueText(displayValue(user, kind));
        group.clearError();
    }

    private void goBack() {
        leaving = true;
        closeQuestMenu();
        onBack.run();
    }

    public void setLeaving(boolean leaving) {
        this.leaving = leaving;
    }

    private void goCollection() {
        leaving = true;
        closeQuestMenu();
        ScreenManager.showCollection(game);
    }

    private void goGreenhouse() {
        leaving = true;
        closeQuestMenu();
        ScreenManager.showGreenhouse(game);
    }

    private void goShop() {
        leaving = true;
        closeQuestMenu();
        ScreenManager.showShop(game);
    }

    private void openQuests() {
        if (questMenu == null) {
            questMenu = new QuestMenuPanel();
            if (getStage() != null) {
                getStage().addActor(questMenu);
            } else {
                addActor(questMenu);
            }
        } else if (questMenu.getStage() == null && getStage() != null) {
            getStage().addActor(questMenu);
        }
        questMenu.open();
    }

    private void closeQuestMenu() {
        if (questMenu != null) {
            questMenu.close();
        }
    }

    private Texture texture(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        String key = path.replace('\\', '/');
        if (textures.containsKey(key)) {
            return textures.get(key);
        }
        Texture loaded = MenuTextures.load(key, "AdventureHud");
        textures.put(key, loaded);
        return loaded;
    }

    private BitmapFont resolveFont(String name, String fallback) {
        BitmapFont font = tryFont(name);
        if (font != null) {
            return font;
        }
        if (name != null) {
            font = tryFont(name.replace('-', '_'));
            if (font != null) {
                return font;
            }
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
        closeQuestMenu();
        if (questMenu != null) {
            questMenu.remove();
            questMenu.dispose();
            questMenu = null;
        }
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
        resourceGroups.clear();
    }
}
