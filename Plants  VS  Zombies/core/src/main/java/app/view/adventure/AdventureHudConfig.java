package app.view.adventure;

import app.view.mainmenu.config.MainMenuEasings;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdventureHudConfig {
    public LeftHudConfig left = new LeftHudConfig();
    public RightHudConfig right = new RightHudConfig();
    public HudMessageConfig messages = new HudMessageConfig();

    public AdventureHudConfig sanitize() {
        if (left == null) {
            left = new LeftHudConfig();
        }
        if (right == null) {
            right = new RightHudConfig();
        }
        if (messages == null) {
            messages = new HudMessageConfig();
        }
        left.sanitize();
        right.sanitize();
        messages.sanitize();
        return this;
    }

    public List<ResourceKindConfig> resourceKinds() {
        return right.resources.orderedKinds();
    }

    public ResourceGroupGlobalConfig globalResources() {
        return right.resources.global;
    }

    public float resolvedBackgroundWidth(ResourceKindConfig kind) {
        return overridePositive(kind.backgroundWidth, globalResources().backgroundWidth);
    }

    public float resolvedBackgroundHeight(ResourceKindConfig kind) {
        return overridePositive(kind.backgroundHeight, globalResources().backgroundHeight);
    }

    public float resolvedBackgroundScale(ResourceKindConfig kind) {
        return overridePositive(kind.backgroundScale, globalResources().backgroundScale);
    }

    public float resolvedBackgroundRotation(ResourceKindConfig kind) {
        return overrideFinite(kind.backgroundRotation, globalResources().backgroundRotation);
    }

    public float resolvedBackgroundAlpha(ResourceKindConfig kind) {
        return clamp01(overrideFinite(kind.backgroundAlpha, globalResources().backgroundAlpha), 1f);
    }

    public float resolvedBackgroundOffsetX(ResourceKindConfig kind) {
        return overrideFinite(kind.backgroundOffsetX, globalResources().backgroundOffsetX);
    }

    public float resolvedBackgroundOffsetY(ResourceKindConfig kind) {
        return overrideFinite(kind.backgroundOffsetY, globalResources().backgroundOffsetY);
    }

    public int resolvedBackgroundZ(ResourceKindConfig kind) {
        return kind.backgroundZ != Integer.MIN_VALUE ? kind.backgroundZ : globalResources().backgroundZ;
    }

    public String resolvedBackgroundPath(ResourceKindConfig kind) {
        return overrideText(kind.backgroundPath, globalResources().backgroundPath);
    }

    public float resolvedIconWidth(ResourceKindConfig kind) {
        return overridePositive(kind.iconWidth, globalResources().iconWidth);
    }

    public float resolvedIconHeight(ResourceKindConfig kind) {
        return overridePositive(kind.iconHeight, globalResources().iconHeight);
    }

    public float resolvedIconScale(ResourceKindConfig kind) {
        return overridePositive(kind.iconScale, globalResources().iconScale);
    }

    public float resolvedIconRotation(ResourceKindConfig kind) {
        return overrideFinite(kind.iconRotation, globalResources().iconRotation);
    }

    public float resolvedIconAlpha(ResourceKindConfig kind) {
        return clamp01(overrideFinite(kind.iconAlpha, globalResources().iconAlpha), 1f);
    }

    public float resolvedIconOffsetX(ResourceKindConfig kind) {
        return overrideFinite(kind.iconOffsetX, globalResources().iconOffsetX);
    }

    public float resolvedIconOffsetY(ResourceKindConfig kind) {
        return overrideFinite(kind.iconOffsetY, globalResources().iconOffsetY);
    }

    public int resolvedIconZ(ResourceKindConfig kind) {
        return kind.iconZ != Integer.MIN_VALUE ? kind.iconZ : globalResources().iconZ;
    }

    public String resolvedIconPath(ResourceKindConfig kind) {
        return overrideText(kind.iconPath, "");
    }

    public String resolvedPlusAssetPath(ResourceKindConfig kind) {
        return overrideText(kind.plusAssetPath, globalResources().plusAssetPath);
    }

    public float resolvedPlusWidth(ResourceKindConfig kind) {
        return overridePositive(kind.plusWidth, globalResources().plusWidth);
    }

    public float resolvedPlusHeight(ResourceKindConfig kind) {
        return overridePositive(kind.plusHeight, globalResources().plusHeight);
    }

    public float resolvedPlusScale(ResourceKindConfig kind) {
        return overridePositive(kind.plusScale, globalResources().plusScale);
    }

    public float resolvedPlusRotation(ResourceKindConfig kind) {
        return overrideFinite(kind.plusRotation, globalResources().plusRotation);
    }

    public float resolvedPlusAlpha(ResourceKindConfig kind) {
        return clamp01(overrideFinite(kind.plusAlpha, globalResources().plusAlpha), 1f);
    }

    public float resolvedPlusOffsetX(ResourceKindConfig kind) {
        return overrideFinite(kind.plusOffsetX, globalResources().plusOffsetX);
    }

    public float resolvedPlusOffsetY(ResourceKindConfig kind) {
        return overrideFinite(kind.plusOffsetY, globalResources().plusOffsetY);
    }

    public int resolvedPlusZ(ResourceKindConfig kind) {
        return kind.plusZ != Integer.MIN_VALUE ? kind.plusZ : globalResources().plusZ;
    }

    public float resolvedPlusHoverScale(ResourceKindConfig kind) {
        return overridePositive(kind.plusHoverScale, globalResources().plusHoverScale);
    }

    public float resolvedPlusPressedScale(ResourceKindConfig kind) {
        return overridePositive(kind.plusPressedScale, globalResources().plusPressedScale);
    }

    public float resolvedPlusHoverDuration(ResourceKindConfig kind) {
        return overrideNonNegative(kind.plusHoverDuration, globalResources().plusHoverDuration);
    }

    public Interpolation resolvedPlusInterpolation(ResourceKindConfig kind) {
        String easing = overrideText(kind.plusEasing, globalResources().plusEasing);
        return MainMenuEasings.parse(easing);
    }

    public String resolvedFont(ResourceKindConfig kind) {
        return overrideText(kind.font, globalResources().font);
    }

    public String resolvedFontFallback(ResourceKindConfig kind) {
        return overrideText(kind.fontFallback, globalResources().fontFallback);
    }

    public float resolvedFontSize(ResourceKindConfig kind) {
        return overridePositive(kind.fontSize, globalResources().fontSize);
    }

    public String resolvedColor(ResourceKindConfig kind) {
        return overrideText(kind.color, globalResources().color);
    }

    public float resolvedTextOffsetX(ResourceKindConfig kind) {
        return overrideFinite(kind.textOffsetX, globalResources().textOffsetX);
    }

    public float resolvedTextOffsetY(ResourceKindConfig kind) {
        return overrideFinite(kind.textOffsetY, globalResources().textOffsetY);
    }

    public float resolvedTextScale(ResourceKindConfig kind) {
        return overridePositive(kind.textScale, globalResources().textScale);
    }

    public float resolvedTextAlpha(ResourceKindConfig kind) {
        return clamp01(overrideFinite(kind.textAlpha, globalResources().textAlpha), 1f);
    }

    public int resolvedTextAlign(ResourceKindConfig kind) {
        return textAlign(overrideText(kind.textAlignment, globalResources().textAlignment));
    }

    public int resolvedValueZ(ResourceKindConfig kind) {
        return kind.valueZ != Integer.MIN_VALUE ? kind.valueZ : globalResources().valueZ;
    }

    public float resolvedGroupScale(ResourceKindConfig kind) {
        return overridePositive(kind.groupScale, globalResources().groupScale) * right.scale;
    }

    public float resolvedGroupRotation(ResourceKindConfig kind) {
        return overrideFinite(kind.groupRotation, globalResources().groupRotation);
    }

    public float resolvedGroupOffsetX(ResourceKindConfig kind) {
        return overrideFinite(kind.groupOffsetX, globalResources().groupOffsetX);
    }

    public float resolvedGroupOffsetY(ResourceKindConfig kind) {
        return overrideFinite(kind.groupOffsetY, globalResources().groupOffsetY);
    }

    public float resolvedGroupAlpha(ResourceKindConfig kind) {
        return clamp01(overrideFinite(kind.groupAlpha, globalResources().groupAlpha), 1f);
    }

    public float resolvedPadding(ResourceKindConfig kind) {
        return overrideNonNegative(kind.padding, globalResources().padding);
    }

    public float backgroundDrawWidth(ResourceKindConfig kind) {
        return resolvedBackgroundWidth(kind) * resolvedBackgroundScale(kind);
    }

    public float backgroundDrawHeight(ResourceKindConfig kind) {
        return resolvedBackgroundHeight(kind) * resolvedBackgroundScale(kind);
    }

    public float iconDrawWidth(ResourceKindConfig kind) {
        return resolvedIconWidth(kind) * resolvedIconScale(kind);
    }

    public float iconDrawHeight(ResourceKindConfig kind) {
        return resolvedIconHeight(kind) * resolvedIconScale(kind);
    }

    public float plusDrawWidth(ResourceKindConfig kind) {
        return resolvedPlusWidth(kind) * resolvedPlusScale(kind);
    }

    public float plusDrawHeight(ResourceKindConfig kind) {
        return resolvedPlusHeight(kind) * resolvedPlusScale(kind);
    }

    public float groupContentWidth(ResourceKindConfig kind) {
        float bgW = backgroundDrawWidth(kind);
        float iconW = iconDrawWidth(kind);
        float plusW = plusDrawWidth(kind);
        float iconX = resolvedIconOffsetX(kind);
        float plusX = bgW - plusW + resolvedPlusOffsetX(kind);
        float left = Math.min(0f, iconX);
        float right = Math.max(bgW, plusX + plusW);
        return Math.max(1f, right - left) + resolvedPadding(kind) * 2f;
    }

    public float groupContentHeight(ResourceKindConfig kind) {
        float bgH = backgroundDrawHeight(kind);
        float iconH = iconDrawHeight(kind);
        float plusH = plusDrawHeight(kind);
        float top = Math.max(bgH, Math.max(
            (bgH - iconH) * 0.5f + resolvedIconOffsetY(kind) + iconH,
            (bgH - plusH) * 0.5f + resolvedPlusOffsetY(kind) + plusH
        ));
        float bottom = Math.min(0f, Math.min(
            (bgH - iconH) * 0.5f + resolvedIconOffsetY(kind),
            (bgH - plusH) * 0.5f + resolvedPlusOffsetY(kind)
        ));
        return Math.max(1f, top - bottom) + resolvedPadding(kind) * 2f;
    }

    public static Color color(String hex, Color fallback) {
        return AdventureMenuConfig.color(hex, fallback);
    }

    public static int textAlign(String value) {
        if (value == null || value.isBlank()) {
            return Align.center;
        }
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "left" -> Align.left;
            case "right" -> Align.right;
            default -> Align.center;
        };
    }

    static String text(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value;
    }

    static float positive(float value, float fallback) {
        if (Float.isNaN(value) || value <= 0f) {
            return fallback;
        }
        return value;
    }

    static float nonNegative(float value, float fallback) {
        if (Float.isNaN(value) || value < 0f) {
            return fallback;
        }
        return value;
    }

    static float finite(float value, float fallback) {
        if (Float.isNaN(value) || Float.isInfinite(value)) {
            return fallback;
        }
        return value;
    }

    static float clamp01(float value, float fallback) {
        if (Float.isNaN(value)) {
            return fallback;
        }
        if (value < 0f) {
            return 0f;
        }
        if (value > 1f) {
            return 1f;
        }
        return value;
    }

    static float overridePositive(float value, float fallback) {
        if (Float.isNaN(value) || value <= 0f) {
            return fallback;
        }
        return value;
    }

    static float overrideNonNegative(float value, float fallback) {
        if (Float.isNaN(value) || value < 0f) {
            return fallback;
        }
        return value;
    }

    static float overrideFinite(float value, float fallback) {
        if (Float.isNaN(value) || Float.isInfinite(value)) {
            return fallback;
        }
        return value;
    }

    static String overrideText(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value;
    }

    public static class LeftHudConfig {
        public float anchorX;
        public float anchorY = 1f;
        public float x = 28f;
        public float y = -28f;
        public float offsetX;
        public float offsetY;
        public float spacing = 14f;
        public float verticalSpacing = 10f;
        public float scale = 1f;
        public int z = 20;
        public HudIconConfig back = HudIconConfig.withPath("menu/icon/back.png");
        public HudIconConfig collection = HudIconConfig.withPath("menu/icon/collection.png");
        public HudIconConfig greenhouse = HudIconConfig.withPath("menu/icon/greenhouse.png");
        public HudIconConfig quests = HudIconConfig.withPath("menu/icon/quest.png");

        public void sanitize() {
            if (Float.isNaN(anchorX)) {
                anchorX = 0f;
            }
            if (Float.isNaN(anchorY)) {
                anchorY = 1f;
            }
            x = finite(x, 28f);
            y = finite(y, -28f);
            offsetX = finite(offsetX, 0f);
            offsetY = finite(offsetY, 0f);
            spacing = finite(spacing, 14f);
            verticalSpacing = finite(verticalSpacing, 10f);
            scale = positive(scale, 1f);
            if (back == null) {
                back = HudIconConfig.withPath("menu/icon/back.png");
            }
            if (collection == null) {
                collection = HudIconConfig.withPath("menu/icon/collection.png");
            }
            if (greenhouse == null) {
                greenhouse = HudIconConfig.withPath("menu/icon/greenhouse.png");
            }
            if (quests == null) {
                quests = HudIconConfig.withPath("menu/icon/quest.png");
            }
            back.sanitize("menu/icon/back.png");
            collection.sanitize("menu/icon/collection.png");
            greenhouse.sanitize("menu/icon/greenhouse.png");
            quests.sanitize("menu/icon/quest.png");
        }

        public float originX(float areaWidth) {
            return areaWidth * anchorX + x + offsetX;
        }

        public float top(float areaHeight) {
            return areaHeight * anchorY + y + offsetY;
        }
    }

    public static class RightHudConfig {
        public float anchorX = 1f;
        public float anchorY = 1f;
        public float x = -24f;
        public float y = -28f;
        public float offsetX;
        public float offsetY;
        public float shopToResourcesSpacing = 18f;
        public float resourceSpacing = 12f;
        public float scale = 1f;
        public int z = 20;
        public HudIconConfig shop = HudIconConfig.withPath("menu/icon/shop.png");
        public ResourceHudConfig resources = new ResourceHudConfig();

        public void sanitize() {
            if (Float.isNaN(anchorX)) {
                anchorX = 1f;
            }
            if (Float.isNaN(anchorY)) {
                anchorY = 1f;
            }
            x = finite(x, -24f);
            y = finite(y, -28f);
            offsetX = finite(offsetX, 0f);
            offsetY = finite(offsetY, 0f);
            shopToResourcesSpacing = finite(shopToResourcesSpacing, 18f);
            resourceSpacing = finite(resourceSpacing, 12f);
            scale = positive(scale, 1f);
            if (shop == null) {
                shop = HudIconConfig.withPath("menu/icon/shop.png");
            }
            if (resources == null) {
                resources = new ResourceHudConfig();
            }
            shop.sanitize("menu/icon/shop.png");
            resources.sanitize();
        }

        public float originX(float areaWidth) {
            return areaWidth * anchorX + x + offsetX;
        }

        public float top(float areaHeight) {
            return areaHeight * anchorY + y + offsetY;
        }
    }

    public static class HudIconConfig {
        public String assetPath = "";
        public boolean visible = true;
        public float width = 72f;
        public float height = 72f;
        public float scale = 1f;
        public float rotation;
        public float alpha = 1f;
        public float offsetX;
        public float offsetY;
        public float hoverScale = 1.06f;
        public float pressedScale = 0.96f;
        public float hoverDuration = 0.1f;
        public String easing = "sineOut";
        public int z = 1;

        public static HudIconConfig withPath(String path) {
            HudIconConfig config = new HudIconConfig();
            config.assetPath = path;
            return config;
        }

        public void sanitize(String fallbackPath) {
            assetPath = text(assetPath, fallbackPath);
            width = positive(width, 72f);
            height = positive(height, 72f);
            scale = positive(scale, 1f);
            rotation = finite(rotation, 0f);
            alpha = clamp01(alpha, 1f);
            offsetX = finite(offsetX, 0f);
            offsetY = finite(offsetY, 0f);
            hoverScale = positive(hoverScale, 1.06f);
            pressedScale = positive(pressedScale, 0.96f);
            hoverDuration = nonNegative(hoverDuration, 0.1f);
            if (easing == null || easing.isBlank()) {
                easing = "sineOut";
            }
        }

        public Interpolation interpolation() {
            return MainMenuEasings.parse(easing);
        }

        public float drawWidth(float groupScale) {
            return width * scale * groupScale;
        }

        public float drawHeight(float groupScale) {
            return height * scale * groupScale;
        }
    }

    public static class ResourceHudConfig {
        public ResourceGroupGlobalConfig global = new ResourceGroupGlobalConfig();
        public ResourceKindConfig coin = ResourceKindConfig.coin();
        public ResourceKindConfig gem = ResourceKindConfig.gem();
        public ResourceKindConfig plantFood = ResourceKindConfig.plantFood();
        public ArrayList<ResourceKindConfig> extra = new ArrayList<>();

        public void sanitize() {
            if (global == null) {
                global = new ResourceGroupGlobalConfig();
            }
            if (coin == null) {
                coin = ResourceKindConfig.coin();
            }
            if (gem == null) {
                gem = ResourceKindConfig.gem();
            }
            if (plantFood == null) {
                plantFood = ResourceKindConfig.plantFood();
            }
            if (extra == null) {
                extra = new ArrayList<>();
            }
            global.sanitize();
            coin.sanitize("coin", "menu/icon/coin2.png", 50, "{value}");
            gem.sanitize("gem", "menu/icon/gem.png", 10, "{value}");
            plantFood.sanitize("plantFood", "menu/icon/plantfood2.png", 1, "{current}/{max}");
            extra.removeIf(item -> item == null);
            for (ResourceKindConfig item : extra) {
                item.sanitize(item.id, item.iconPath, item.debugAddAmount, item.valueFormat);
            }
        }

        public List<ResourceKindConfig> orderedKinds() {
            List<ResourceKindConfig> kinds = new ArrayList<>();
            if (coin != null && coin.visible) {
                kinds.add(coin);
            }
            if (gem != null && gem.visible) {
                kinds.add(gem);
            }
            if (plantFood != null && plantFood.visible) {
                kinds.add(plantFood);
            }
            for (ResourceKindConfig item : extra) {
                if (item != null && item.visible) {
                    kinds.add(item);
                }
            }
            return kinds;
        }
    }

    public static class ResourceGroupGlobalConfig {
        public String backgroundPath = "menu/image/bg.png";
        public float backgroundWidth = 200f;
        public float backgroundHeight = 50f;
        public float backgroundScale = 1f;
        public float backgroundRotation;
        public float backgroundAlpha = 1f;
        public float backgroundOffsetX;
        public float backgroundOffsetY;
        public int backgroundZ;
        public float iconWidth = 52f;
        public float iconHeight = 52f;
        public float iconScale = 1f;
        public float iconRotation;
        public float iconAlpha = 1f;
        public float iconOffsetX = -22f;
        public float iconOffsetY;
        public int iconZ = 2;
        public String plusAssetPath = "menu/icon/plus.png";
        public float plusWidth = 40f;
        public float plusHeight = 40f;
        public float plusScale = 1f;
        public float plusRotation;
        public float plusAlpha = 1f;
        public float plusOffsetX = 14f;
        public float plusOffsetY;
        public int plusZ = 3;
        public float plusHoverScale = 1.08f;
        public float plusPressedScale = 0.94f;
        public float plusHoverDuration = 0.1f;
        public String plusEasing = "sineOut";
        public String font = "BRIANNETOD";
        public String fontFallback = "BRIANNETOD";
        public float fontSize = 0.95f;
        public String color = "fffdf5";
        public float textOffsetX = 8f;
        public float textOffsetY;
        public float textScale = 1f;
        public float textAlpha = 1f;
        public String textAlignment = "center";
        public int valueZ = 4;
        public float groupScale = 1f;
        public float groupRotation;
        public float groupOffsetX;
        public float groupOffsetY;
        public float groupAlpha = 1f;
        public float padding;

        public void sanitize() {
            backgroundPath = text(backgroundPath, "menu/image/bg.png");
            backgroundWidth = positive(backgroundWidth, 200f);
            backgroundHeight = positive(backgroundHeight, 50f);
            backgroundScale = positive(backgroundScale, 1f);
            backgroundRotation = finite(backgroundRotation, 0f);
            backgroundAlpha = clamp01(backgroundAlpha, 1f);
            backgroundOffsetX = finite(backgroundOffsetX, 0f);
            backgroundOffsetY = finite(backgroundOffsetY, 0f);
            iconWidth = positive(iconWidth, 52f);
            iconHeight = positive(iconHeight, 52f);
            iconScale = positive(iconScale, 1f);
            iconRotation = finite(iconRotation, 0f);
            iconAlpha = clamp01(iconAlpha, 1f);
            iconOffsetX = finite(iconOffsetX, -22f);
            iconOffsetY = finite(iconOffsetY, 0f);
            plusAssetPath = text(plusAssetPath, "menu/icon/plus.png");
            plusWidth = positive(plusWidth, 40f);
            plusHeight = positive(plusHeight, 40f);
            plusScale = positive(plusScale, 1f);
            plusRotation = finite(plusRotation, 0f);
            plusAlpha = clamp01(plusAlpha, 1f);
            plusOffsetX = finite(plusOffsetX, 14f);
            plusOffsetY = finite(plusOffsetY, 0f);
            plusHoverScale = positive(plusHoverScale, 1.08f);
            plusPressedScale = positive(plusPressedScale, 0.94f);
            plusHoverDuration = nonNegative(plusHoverDuration, 0.1f);
            if (plusEasing == null || plusEasing.isBlank()) {
                plusEasing = "sineOut";
            }
            font = text(font, "BRIANNETOD");
            fontFallback = text(fontFallback, "BRIANNETOD");
            fontSize = positive(fontSize, 0.95f);
            color = text(color, "fffdf5");
            textOffsetX = finite(textOffsetX, 8f);
            textOffsetY = finite(textOffsetY, 0f);
            textScale = positive(textScale, 1f);
            textAlpha = clamp01(textAlpha, 1f);
            textAlignment = text(textAlignment, "center");
            groupScale = positive(groupScale, 1f);
            groupRotation = finite(groupRotation, 0f);
            groupOffsetX = finite(groupOffsetX, 0f);
            groupOffsetY = finite(groupOffsetY, 0f);
            groupAlpha = clamp01(groupAlpha, 1f);
            padding = nonNegative(padding, 0f);
        }
    }

    public static class ResourceKindConfig {
        public String id = "";
        public boolean visible = true;
        public String iconPath = "";
        public int debugAddAmount;
        public String valueFormat = "";
        public String backgroundPath = "";
        public float backgroundWidth = Float.NaN;
        public float backgroundHeight = Float.NaN;
        public float backgroundScale = Float.NaN;
        public float backgroundRotation = Float.NaN;
        public float backgroundAlpha = Float.NaN;
        public float backgroundOffsetX = Float.NaN;
        public float backgroundOffsetY = Float.NaN;
        public int backgroundZ = Integer.MIN_VALUE;
        public float iconWidth = Float.NaN;
        public float iconHeight = Float.NaN;
        public float iconScale = Float.NaN;
        public float iconRotation = Float.NaN;
        public float iconAlpha = Float.NaN;
        public float iconOffsetX = Float.NaN;
        public float iconOffsetY = Float.NaN;
        public int iconZ = Integer.MIN_VALUE;
        public String plusAssetPath = "";
        public float plusWidth = Float.NaN;
        public float plusHeight = Float.NaN;
        public float plusScale = Float.NaN;
        public float plusRotation = Float.NaN;
        public float plusAlpha = Float.NaN;
        public float plusOffsetX = Float.NaN;
        public float plusOffsetY = Float.NaN;
        public int plusZ = Integer.MIN_VALUE;
        public float plusHoverScale = Float.NaN;
        public float plusPressedScale = Float.NaN;
        public float plusHoverDuration = Float.NaN;
        public String plusEasing = "";
        public String font = "";
        public String fontFallback = "";
        public float fontSize = Float.NaN;
        public String color = "";
        public float textOffsetX = Float.NaN;
        public float textOffsetY = Float.NaN;
        public float textScale = Float.NaN;
        public float textAlpha = Float.NaN;
        public String textAlignment = "";
        public int valueZ = Integer.MIN_VALUE;
        public float groupScale = Float.NaN;
        public float groupRotation = Float.NaN;
        public float groupOffsetX = Float.NaN;
        public float groupOffsetY = Float.NaN;
        public float groupAlpha = Float.NaN;
        public float padding = Float.NaN;

        public static ResourceKindConfig coin() {
            ResourceKindConfig kind = new ResourceKindConfig();
            kind.id = "coin";
            kind.iconPath = "menu/icon/coin2.png";
            kind.debugAddAmount = 50;
            kind.valueFormat = "{value}";
            kind.iconScale = 1.15f;
            return kind;
        }

        public static ResourceKindConfig gem() {
            ResourceKindConfig kind = new ResourceKindConfig();
            kind.id = "gem";
            kind.iconPath = "menu/icon/gem.png";
            kind.debugAddAmount = 10;
            kind.valueFormat = "{value}";
            return kind;
        }

        public static ResourceKindConfig plantFood() {
            ResourceKindConfig kind = new ResourceKindConfig();
            kind.id = "plantFood";
            kind.iconPath = "menu/icon/plantfood2.png";
            kind.debugAddAmount = 1;
            kind.valueFormat = "{current}/{max}";
            kind.iconScale = 0.9f;
            return kind;
        }

        public void sanitize(String fallbackId, String fallbackIcon, int fallbackAmount, String fallbackFormat) {
            id = text(id, fallbackId).trim().toLowerCase(Locale.ROOT);
            if ("plant_food".equals(id) || "plantfood".equals(id)) {
                id = "plantFood";
            }
            iconPath = text(iconPath, fallbackIcon);
            if (debugAddAmount <= 0) {
                debugAddAmount = Math.max(1, fallbackAmount);
            }
            valueFormat = text(valueFormat, fallbackFormat);
            if (plusAssetPath == null) {
                plusAssetPath = "";
            }
            if (backgroundPath == null) {
                backgroundPath = "";
            }
            if (font == null) {
                font = "";
            }
            if (fontFallback == null) {
                fontFallback = "";
            }
            if (color == null) {
                color = "";
            }
            if (textAlignment == null) {
                textAlignment = "";
            }
            if (plusEasing == null) {
                plusEasing = "";
            }
        }

        public String formatted(int value, int max) {
            String format = valueFormat == null || valueFormat.isBlank() ? "{value}" : valueFormat;
            return format
                .replace("{current}", String.valueOf(Math.max(0, value)))
                .replace("{max}", String.valueOf(Math.max(0, max)))
                .replace("{value}", String.valueOf(Math.max(0, value)));
        }
    }

    public static class HudMessageConfig {
        public String debugRequired = "Debug Mode must be enabled.";
        public String missingUser = "User not found.";
        public String font = "BRIANNETOD";
        public String fontFallback = "BRIANNETOD";
        public float fontSize = 0.62f;
        public String color = "FF6B5A";
        public float offsetX;
        public float offsetY = -8f;
        public float width = 240f;
        public float height = 28f;
        public String alignment = "center";
        public float alpha = 1f;
        public float duration = 2.4f;

        public void sanitize() {
            debugRequired = text(debugRequired, "Debug Mode must be enabled.");
            missingUser = text(missingUser, "User not found.");
            font = text(font, "BRIANNETOD");
            fontFallback = text(fontFallback, "BRIANNETOD");
            fontSize = positive(fontSize, 0.62f);
            color = text(color, "FF6B5A");
            offsetX = finite(offsetX, 0f);
            offsetY = finite(offsetY, -8f);
            width = positive(width, 240f);
            height = positive(height, 28f);
            alignment = text(alignment, "center");
            alpha = clamp01(alpha, 1f);
            duration = nonNegative(duration, 2.4f);
        }

        public int textAlign() {
            return AdventureHudConfig.textAlign(alignment);
        }
    }
}
