package app.view.greenhouse.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GreenhouseConfig {
    public String mapPath = "Maps/greenhouse.tmx";
    public float mapPixelWidth = 1920f;
    public float mapPixelHeight = 1080f;
    public LayersConfig layers = new LayersConfig();
    public GridConfig grid = new GridConfig();
    public ArrayList<PotOffsetConfig> potOffsets = new ArrayList<>();
    public PotVisualConfig pot = new PotVisualConfig();
    public LockIconConfig lockIcon = new LockIconConfig();
    public QuestionIconConfig questionIcon = new QuestionIconConfig();
    public TimerConfig timer = new TimerConfig();
    public ActionButtonConfig actionButton = new ActionButtonConfig();
    public BillConfig bill = new BillConfig();
    public ResourceHudConfig resourceHud = new ResourceHudConfig();
    public PlantingConfig planting = new PlantingConfig();
    public GrowthConfig growth = new GrowthConfig();
    public RewardsConfig rewards = new RewardsConfig();
    public GrowthCostConfig growthCost = new GrowthCostConfig();
    public BackButtonConfig backButton = new BackButtonConfig();
    public MessagesOverlayConfig messages = new MessagesOverlayConfig();
    public InteractionConfig interaction = new InteractionConfig();
    public FormatsConfig formats = new FormatsConfig();

    public PotOffsetConfig offsetFor(int row, int col) {
        if (potOffsets != null) {
            for (PotOffsetConfig offset : potOffsets) {
                if (offset != null && offset.row == row && offset.col == col) {
                    return offset;
                }
            }
        }
        PotOffsetConfig fallback = new PotOffsetConfig();
        fallback.row = row;
        fallback.col = col;
        fallback.sanitize();
        return fallback;
    }

    public GreenhouseConfig sanitize() {
        mapPath = text(mapPath, "Maps/greenhouse.tmx");
        mapPixelWidth = positive(mapPixelWidth, 1920f);
        mapPixelHeight = positive(mapPixelHeight, 1080f);
        if (layers == null) layers = new LayersConfig();
        if (grid == null) grid = new GridConfig();
        if (potOffsets == null) potOffsets = new ArrayList<>();
        if (pot == null) pot = new PotVisualConfig();
        if (lockIcon == null) lockIcon = new LockIconConfig();
        if (questionIcon == null) questionIcon = new QuestionIconConfig();
        if (timer == null) timer = new TimerConfig();
        if (actionButton == null) actionButton = new ActionButtonConfig();
        if (bill == null) bill = new BillConfig();
        if (resourceHud == null) resourceHud = new ResourceHudConfig();
        if (planting == null) planting = new PlantingConfig();
        if (growth == null) growth = new GrowthConfig();
        if (rewards == null) rewards = new RewardsConfig();
        if (growthCost == null) growthCost = new GrowthCostConfig();
        if (backButton == null) backButton = new BackButtonConfig();
        if (messages == null) messages = new MessagesOverlayConfig();
        if (interaction == null) interaction = new InteractionConfig();
        if (formats == null) formats = new FormatsConfig();
        layers.sanitize();
        grid.sanitize();
        potOffsets.removeIf(item -> item == null);
        if (potOffsets.isEmpty()) {
            for (int row = 1; row <= grid.rows; row++) {
                for (int col = 1; col <= grid.cols; col++) {
                    PotOffsetConfig offset = new PotOffsetConfig();
                    offset.row = row;
                    offset.col = col;
                    potOffsets.add(offset);
                }
            }
        }
        for (PotOffsetConfig offset : potOffsets) {
            offset.sanitize();
        }
        pot.sanitize();
        lockIcon.sanitize();
        questionIcon.sanitize();
        timer.sanitize();
        actionButton.sanitize();
        bill.sanitize();
        resourceHud.sanitize();
        planting.sanitize();
        growth.sanitize();
        rewards.sanitize();
        growthCost.sanitize();
        backButton.sanitize();
        messages.sanitize();
        interaction.sanitize();
        formats.sanitize();
        return this;
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
        if (value < 0f) return 0f;
        if (value > 1f) return 1f;
        return value;
    }

    static double clamp01(double value, double fallback) {
        if (Double.isNaN(value)) {
            return fallback;
        }
        if (value < 0d) return 0d;
        if (value > 1d) return 1d;
        return value;
    }

    static int positive(int value, int fallback) {
        return value <= 0 ? fallback : value;
    }

    static int nonNegative(int value, int fallback) {
        return value < 0 ? fallback : value;
    }

    public static class LayersConfig {
        public int mapDepth;
        public int potDepth = 10;
        public int plantDepth = 20;
        public int timerDepth = 30;
        public int actionDepth = 40;
        public int lockDepth = 50;
        public int questionDepth = 60;
        public int hudDepth = 100;
        public int messageDepth = 200;

        public void sanitize() {
            potDepth = potDepth == 0 ? 10 : potDepth;
            plantDepth = plantDepth == 0 ? 20 : plantDepth;
            timerDepth = timerDepth == 0 ? 30 : timerDepth;
            actionDepth = actionDepth == 0 ? 40 : actionDepth;
            lockDepth = lockDepth == 0 ? 50 : lockDepth;
            questionDepth = questionDepth == 0 ? 60 : questionDepth;
            hudDepth = hudDepth == 0 ? 100 : hudDepth;
            messageDepth = messageDepth == 0 ? 200 : messageDepth;
        }
    }

    public static class GridConfig {
        public int rows = 4;
        public int cols = 5;
        public ArrayList<Integer> unlockCosts = new ArrayList<>();

        public void sanitize() {
            rows = positive(rows, 4);
            cols = positive(cols, 5);
            if (unlockCosts == null) {
                unlockCosts = new ArrayList<>();
            }
            unlockCosts.removeIf(item -> item == null || item < 0);
            while (unlockCosts.size() < rows) {
                unlockCosts.add(0);
            }
        }
    }

    public static class PotOffsetConfig {
        public int row = 1;
        public int col = 1;
        public float offsetX;
        public float offsetY;

        public void sanitize() {
            row = positive(row, 1);
            col = positive(col, 1);
            offsetX = finite(offsetX, 0f);
            offsetY = finite(offsetY, 0f);
        }
    }

    public static class PotVisualConfig {
        public String assetPath = "menu/image/pot.png";
        public float scale = 1f;
        public float rotation;
        public float alpha = 1f;
        public float width = 128f;
        public float height = 128f;
        public float offsetX;
        public float offsetY;
        public boolean visible = true;

        public void sanitize() {
            assetPath = text(assetPath, "menu/image/pot.png");
            scale = positive(scale, 1f);
            rotation = finite(rotation, 0f);
            alpha = clamp01(alpha, 1f);
            width = positive(width, 128f);
            height = positive(height, 128f);
            offsetX = finite(offsetX, 0f);
            offsetY = finite(offsetY, 0f);
        }
    }

    public static class LockIconConfig {
        public String resourceId = "IMAGE_ZEN_GARDEN_LOCKED_POT_ICON";
        public float offsetX;
        public float offsetY;
        public float width;
        public float height;
        public float scale = 1f;
        public float rotation;
        public float alpha = 1f;
        public boolean visible = true;

        public void sanitize() {
            resourceId = text(resourceId, "IMAGE_ZEN_GARDEN_LOCKED_POT_ICON");
            offsetX = finite(offsetX, 0f);
            offsetY = finite(offsetY, 0f);
            width = nonNegative(width, 0f);
            height = nonNegative(height, 0f);
            scale = positive(scale, 1f);
            rotation = finite(rotation, 0f);
            alpha = clamp01(alpha, 1f);
        }
    }

    public static class QuestionIconConfig {
        public String assetPath = "menu/icon/q.png";
        public float offsetX;
        public float offsetY = -58f;
        public float scale = 1f;
        public float rotation;
        public float alpha = 1f;
        public boolean visible = true;
        public float width = 48f;
        public float height = 48f;
        public float hoverScale = 1.08f;
        public float pressedScale = 0.94f;
        public float hoverDuration = 0.1f;
        public String easing = "sineOut";
        public String questionText = "Buy a pot from the shop to place it on this table.";
        public String lockedQuestionText = "Unlock this table with coins to make it usable.";
        public String labelStyle = "bundle_reward_multiplier";
        public String textColor = "1A1A1A";
        public String bubbleBackgroundDrawable = "image_ui_generic_guaranteed_bg_10";
        public float bubbleWidth = 260f;
        public float bubblePadX = 14f;
        public float bubblePadY = 10f;
        public float bubbleOffsetX;
        public float bubbleOffsetY = 12f;
        public float fontSize = 0.72f;
        public float minDisplaySeconds = 3f;
        public float secondsPerWord = 0.35f;
        public QuestionAnimationConfig animation = new QuestionAnimationConfig();
        public HashMap<String, String> perCellTexts = new HashMap<>();

        public void sanitize() {
            assetPath = text(assetPath, "menu/icon/q.png");
            offsetX = finite(offsetX, 0f);
            offsetY = finite(offsetY, -58f);
            scale = positive(scale, 1f);
            rotation = finite(rotation, 0f);
            alpha = clamp01(alpha, 1f);
            width = positive(width, 48f);
            height = positive(height, 48f);
            hoverScale = positive(hoverScale, 1.08f);
            pressedScale = positive(pressedScale, 0.94f);
            hoverDuration = nonNegative(hoverDuration, 0.1f);
            easing = text(easing, "sineOut");
            questionText = text(questionText, "Buy a pot from the shop to place it on this table.");
            lockedQuestionText = text(lockedQuestionText, "Unlock this table with coins to make it usable.");
            labelStyle = text(labelStyle, "default");
            textColor = text(textColor, "1A1A1A");
            bubbleBackgroundDrawable = text(bubbleBackgroundDrawable, "image_ui_generic_guaranteed_bg_10");
            bubbleWidth = positive(bubbleWidth, 260f);
            bubblePadX = nonNegative(bubblePadX, 14f);
            bubblePadY = nonNegative(bubblePadY, 10f);
            bubbleOffsetX = finite(bubbleOffsetX, 0f);
            bubbleOffsetY = finite(bubbleOffsetY, 12f);
            fontSize = positive(fontSize, 0.72f);
            minDisplaySeconds = positive(minDisplaySeconds, 3f);
            secondsPerWord = positive(secondsPerWord, 0.35f);
            if (animation == null) animation = new QuestionAnimationConfig();
            animation.sanitize();
            if (perCellTexts == null) perCellTexts = new HashMap<>();
            HashMap<String, String> cleaned = new HashMap<>();
            for (Map.Entry<String, String> entry : perCellTexts.entrySet()) {
                if (entry.getKey() == null || entry.getKey().isBlank()) continue;
                cleaned.put(entry.getKey().trim(), text(entry.getValue(), questionText));
            }
            perCellTexts = cleaned;
        }

        public String textForCell(int row, int col) {
            String key = row + "," + col;
            String value = perCellTexts.get(key);
            if (value == null || value.isBlank()) {
                return questionText;
            }
            return value;
        }

        public String textForStatus(boolean locked, int row, int col) {
            String key = row + "," + col;
            String value = perCellTexts.get(key);
            if (value != null && !value.isBlank()) {
                return value;
            }
            return locked ? lockedQuestionText : questionText;
        }
    }

    public static class QuestionAnimationConfig {
        public float riseDuration = 0.35f;
        public float fallDuration = 0.35f;
        public float riseOffsetY = 24f;
        public String easing = "sineOut";

        public void sanitize() {
            riseDuration = nonNegative(riseDuration, 0.35f);
            fallDuration = nonNegative(fallDuration, 0.35f);
            riseOffsetY = finite(riseOffsetY, 24f);
            easing = text(easing, "sineOut");
        }
    }

    public static class TimerConfig {
        public String backgroundPath = "menu/image/bg_timer.png";
        public String iconPath = "menu/icon/timer.png";
        public float backgroundWidth = 95f;
        public float backgroundHeight = 24f;
        public float backgroundScale = 1f;
        public float backgroundRotation;
        public float backgroundAlpha = 1f;
        public float backgroundOffsetX;
        public float backgroundOffsetY = 8f;
        public float iconWidth = 28f;
        public float iconHeight = 28f;
        public float iconScale = 1f;
        public float iconRotation;
        public float iconAlpha = 1f;
        public float iconOffsetX = -50f;
        public float iconOffsetY;
        public String font = "BRIANNETOD";
        public String fontFallback = "BRIANNETOD";
        public float fontSize = 0.7f;
        public String color = "fffdf5";
        public String finishedColor = "A8A8A8";
        public float textOffsetX = 8f;
        public float textOffsetY;
        public float textScale = 1f;
        public float textAlpha = 1f;
        public boolean interactive;
        public boolean visible = true;

        public void sanitize() {
            backgroundPath = text(backgroundPath, "menu/image/bg_timer.png");
            iconPath = text(iconPath, "menu/icon/timer.png");
            backgroundWidth = positive(backgroundWidth, 95f);
            backgroundHeight = positive(backgroundHeight, 24f);
            backgroundScale = positive(backgroundScale, 1f);
            backgroundRotation = finite(backgroundRotation, 0f);
            backgroundAlpha = clamp01(backgroundAlpha, 1f);
            backgroundOffsetX = finite(backgroundOffsetX, 0f);
            backgroundOffsetY = finite(backgroundOffsetY, 8f);
            iconWidth = positive(iconWidth, 28f);
            iconHeight = positive(iconHeight, 28f);
            iconScale = positive(iconScale, 1f);
            iconRotation = finite(iconRotation, 0f);
            iconAlpha = clamp01(iconAlpha, 1f);
            iconOffsetX = finite(iconOffsetX, -50f);
            iconOffsetY = finite(iconOffsetY, 0f);
            font = text(font, "BRIANNETOD");
            fontFallback = text(fontFallback, "BRIANNETOD");
            fontSize = positive(fontSize, 0.7f);
            color = text(color, "fffdf5");
            finishedColor = text(finishedColor, "A8A8A8");
            textOffsetX = finite(textOffsetX, 8f);
            textOffsetY = finite(textOffsetY, 0f);
            textScale = positive(textScale, 1f);
            textAlpha = clamp01(textAlpha, 1f);
        }
    }

    public static class ActionButtonConfig {
        public String backgroundPath = "menu/image/bg_greengouse.png";
        public String growingIconPath = "menu/icon/gem3.png";
        public String readyIconPath = "menu/icon/collect_plant.png";
        public float backgroundWidth = 95f;
        public float backgroundHeight = 24f;
        public float backgroundScale = 1f;
        public float backgroundRotation;
        public float backgroundAlpha = 1f;
        public float backgroundOffsetX;
        public float backgroundOffsetY = -52f;
        public float relativeToTimerOffsetX;
        public float relativeToTimerOffsetY = 4f;
        public float iconWidth = 36f;
        public float iconHeight = 36f;
        public float iconScale = 1f;
        public float iconOffsetX = -55f;
        public float iconOffsetY;
        public String growingText = "Grow {gems}";
        public String readyText = "Collect";
        public String font = "BRIANNETOD";
        public String fontFallback = "BRIANNETOD";
        public float fontSize = 0.75f;
        public String color = "fffdf5";
        public float textOffsetX = 10f;
        public float textOffsetY;
        public float hoverScale = 1.06f;
        public float pressedScale = 0.96f;
        public float hoverDuration = 0.1f;
        public String easing = "sineOut";
        public boolean visible = true;

        public void sanitize() {
            backgroundPath = text(backgroundPath, "menu/image/bg_greengouse.png");
            growingIconPath = text(growingIconPath, "menu/icon/gem3.png");
            readyIconPath = text(readyIconPath, "menu/icon/collect_plant.png");
            backgroundWidth = positive(backgroundWidth, 95f);
            backgroundHeight = positive(backgroundHeight, 24f);
            backgroundScale = positive(backgroundScale, 1f);
            backgroundRotation = finite(backgroundRotation, 0f);
            backgroundAlpha = clamp01(backgroundAlpha, 1f);
            backgroundOffsetX = finite(backgroundOffsetX, 0f);
            backgroundOffsetY = finite(backgroundOffsetY, -52f);
            relativeToTimerOffsetX = finite(relativeToTimerOffsetX, 0f);
            relativeToTimerOffsetY = finite(relativeToTimerOffsetY, 4f);
            iconWidth = positive(iconWidth, 36f);
            iconHeight = positive(iconHeight, 36f);
            iconScale = positive(iconScale, 1f);
            iconOffsetX = finite(iconOffsetX, -55f);
            iconOffsetY = finite(iconOffsetY, 0f);
            growingText = text(growingText, "Grow {gems}");
            readyText = text(readyText, "Collect");
            font = text(font, "BRIANNETOD");
            fontFallback = text(fontFallback, "BRIANNETOD");
            fontSize = positive(fontSize, 0.75f);
            color = text(color, "fffdf5");
            textOffsetX = finite(textOffsetX, 10f);
            textOffsetY = finite(textOffsetY, 0f);
            hoverScale = positive(hoverScale, 1.06f);
            pressedScale = positive(pressedScale, 0.96f);
            hoverDuration = nonNegative(hoverDuration, 0.1f);
            easing = text(easing, "sineOut");
        }

        public String growingLabel(int gems) {
            return growingText.replace("{gems}", String.valueOf(Math.max(0, gems)));
        }
    }

    public static class BillConfig {
        public String assetPath = "menu/icon/greenhouse_shovel.png";
        public float anchorX = 1f;
        public float anchorY;
        public float x = -40f;
        public float y = 40f;
        public float offsetX;
        public float offsetY;
        public float width = 96f;
        public float height = 96f;
        public float scale = 1f;
        public float selectedScale = 1.12f;
        public float rotation;
        public float alpha = 1f;
        public float unavailableAlpha = 0.45f;
        public float hoverScale = 1.06f;
        public float pressedScale = 0.96f;
        public float hoverDuration = 0.1f;
        public String easing = "sineOut";
        public float cooldownHours = 2f;
        public float cooldownLabelFontScale = 0.55f;
        public float cooldownLabelOffsetX;
        public float cooldownLabelOffsetY = 34f;
        public String speedIconPath = "menu/icon/speed.png";
        public float speedIconWidth = 42f;
        public float speedIconHeight = 42f;
        public float speedIconScale = 1f;
        public float speedIconOffsetX;
        public float speedIconOffsetY = -48f;
        public boolean speedIconVisible = true;
        public float speedHoverScale = 1.08f;
        public float speedPressedScale = 0.94f;
        public float speedHoverDuration = 0.1f;
        public String speedEasing = "sineOut";
        public float debugSkipMinutes = 30f;
        public int z = 110;
        public boolean visible = true;

        public void sanitize() {
            assetPath = text(assetPath, "menu/icon/greenhouse_shovel.png");
            if (Float.isNaN(anchorX)) anchorX = 1f;
            if (Float.isNaN(anchorY)) anchorY = 0f;
            x = finite(x, -40f);
            y = finite(y, 40f);
            offsetX = finite(offsetX, 0f);
            offsetY = finite(offsetY, 0f);
            width = positive(width, 96f);
            height = positive(height, 96f);
            scale = positive(scale, 1f);
            selectedScale = positive(selectedScale, 1.12f);
            rotation = finite(rotation, 0f);
            alpha = clamp01(alpha, 1f);
            unavailableAlpha = clamp01(unavailableAlpha, 0.45f);
            hoverScale = positive(hoverScale, 1.06f);
            pressedScale = positive(pressedScale, 0.96f);
            hoverDuration = nonNegative(hoverDuration, 0.1f);
            easing = text(easing, "sineOut");
            cooldownHours = positive(cooldownHours, 2f);
            cooldownLabelFontScale = positive(cooldownLabelFontScale, 0.55f);
            cooldownLabelOffsetX = finite(cooldownLabelOffsetX, 0f);
            cooldownLabelOffsetY = finite(cooldownLabelOffsetY, 34f);
            speedIconPath = text(speedIconPath, "menu/icon/speed.png");
            speedIconWidth = positive(speedIconWidth, 42f);
            speedIconHeight = positive(speedIconHeight, 42f);
            speedIconScale = positive(speedIconScale, 1f);
            speedIconOffsetX = finite(speedIconOffsetX, 0f);
            speedIconOffsetY = finite(speedIconOffsetY, -48f);
            speedHoverScale = positive(speedHoverScale, 1.08f);
            speedPressedScale = positive(speedPressedScale, 0.94f);
            speedHoverDuration = nonNegative(speedHoverDuration, 0.1f);
            speedEasing = text(speedEasing, "sineOut");
            debugSkipMinutes = positive(debugSkipMinutes, 30f);
        }
    }

    public static class PlantingConfig {
        public double marigoldProbability = 0.5d;
        public double plantFoodProbability = 0.5d;

        public void sanitize() {
            marigoldProbability = clamp01(marigoldProbability, 0.5d);
            plantFoodProbability = clamp01(plantFoodProbability, 0.5d);
        }

        public boolean probabilitiesValid() {
            return Math.abs(marigoldProbability + plantFoodProbability - 1.0d) <= 0.001d;
        }
    }

    public static class GrowthConfig {
        public float marigoldGrowthHours = 2f;
        public float plantFoodGrowthHours = 8f;

        public void sanitize() {
            marigoldGrowthHours = positive(marigoldGrowthHours, 2f);
            plantFoodGrowthHours = positive(plantFoodGrowthHours, 8f);
        }
    }

    public static class RewardsConfig {
        public int marigoldRewardCoins = 500;
        public boolean boostGrantsCoins;
        public int boostCoinReward;

        public void sanitize() {
            marigoldRewardCoins = nonNegative(marigoldRewardCoins, 500);
            boostCoinReward = nonNegative(boostCoinReward, 0);
        }
    }

    public static class GrowthCostConfig {
        public boolean enabled = true;
        public int unitCostGems = 1;
        public int timeUnitSeconds = 3600;
        public String roundingMode = "CEILING";
        public int minCost = 1;
        public int maxCost;

        public void sanitize() {
            unitCostGems = positive(unitCostGems, 1);
            timeUnitSeconds = positive(timeUnitSeconds, 3600);
            roundingMode = text(roundingMode, "CEILING").trim().toUpperCase(Locale.ROOT);
            minCost = nonNegative(minCost, 1);
            maxCost = nonNegative(maxCost, 0);
        }
    }

    public static class BackButtonConfig {
        public String assetPath = "menu/icon/back.png";
        public boolean visible = true;
        public float anchorX;
        public float anchorY = 1f;
        public float x = 28f;
        public float y = -28f;
        public float offsetX;
        public float offsetY;
        public float width = 72f;
        public float height = 72f;
        public float scale = 1f;
        public float rotation;
        public float alpha = 1f;
        public float hoverScale = 1.06f;
        public float pressedScale = 0.96f;
        public float hoverDuration = 0.1f;
        public String easing = "sineOut";
        public int z = 120;

        public void sanitize() {
            assetPath = text(assetPath, "menu/icon/back.png");
            if (Float.isNaN(anchorX)) anchorX = 0f;
            if (Float.isNaN(anchorY)) anchorY = 1f;
            x = finite(x, 28f);
            y = finite(y, -28f);
            offsetX = finite(offsetX, 0f);
            offsetY = finite(offsetY, 0f);
            width = positive(width, 72f);
            height = positive(height, 72f);
            scale = positive(scale, 1f);
            rotation = finite(rotation, 0f);
            alpha = clamp01(alpha, 1f);
            hoverScale = positive(hoverScale, 1.06f);
            pressedScale = positive(pressedScale, 0.96f);
            hoverDuration = nonNegative(hoverDuration, 0.1f);
            easing = text(easing, "sineOut");
        }
    }

    public static class MessagesOverlayConfig {
        public String font = "BRIANNETOD";
        public String fontFallback = "BRIANNETOD";
        public float fontSize = 0.7f;
        public String color = "FF6B5A";
        public String successColor = "7CFF7A";
        public float offsetX;
        public float offsetY = 40f;
        public float width = 480f;
        public float height = 36f;
        public String alignment = "center";
        public float alpha = 1f;
        public float duration = 2.8f;
        public float anchorX = 0.5f;
        /** 0 = bottom of greenhouse screen, 1 = top. */
        public float anchorY = 0f;
        /** Extra lift from the bottom edge when anchorY is near 0. Ali can tune this. */
        public float bottomOffset = 40f;
        public String rewardLabelStyle = "bundle_reward_multiplier";

        public void sanitize() {
            font = text(font, "BRIANNETOD");
            fontFallback = text(fontFallback, "BRIANNETOD");
            fontSize = positive(fontSize, 0.7f);
            color = text(color, "FF6B5A");
            successColor = text(successColor, "7CFF7A");
            offsetX = finite(offsetX, 0f);
            offsetY = finite(offsetY, 40f);
            width = positive(width, 480f);
            height = positive(height, 36f);
            alignment = text(alignment, "center");
            alpha = clamp01(alpha, 1f);
            duration = nonNegative(duration, 2.8f);
            if (Float.isNaN(anchorX)) anchorX = 0.5f;
            if (Float.isNaN(anchorY)) anchorY = 0f;
            bottomOffset = finite(bottomOffset, 40f);
            rewardLabelStyle = text(rewardLabelStyle, "bundle_reward_multiplier");
        }
    }

    public static class InteractionConfig {
        public boolean cancelOnRightClick = true;
        public boolean requireBillForPlanting = true;
        public boolean deselectBillAfterPlant = true;
        public boolean allowPlantOnEmptyOnly = true;

        public void sanitize() {
        }
    }

    public static class FormatsConfig {
        public String timerWithHours = "HH:MM:SS";
        public String timerWithoutHours = "MM:SS";

        public void sanitize() {
            timerWithHours = text(timerWithHours, "HH:MM:SS");
            timerWithoutHours = text(timerWithoutHours, "MM:SS");
        }
    }

    public static class ResourceHudConfig {
        public boolean visible = true;
        public String font = "BRIANNETOD";
        public String fontFallback = "BRIANNETOD";
        public float fontSize = 0.7f;
        public String color = "fffdf5";
        public float coinsOffsetX = 24f;
        public float coinsOffsetY = -48f;
        public float gemsOffsetX = 24f;
        public float gemsOffsetY = -78f;
        public float anchorX;
        public float anchorY = 1f;
        public String coinsFormat = "Coins: {value}";
        public String gemsFormat = "Gems: {value}";

        public void sanitize() {
            font = text(font, "BRIANNETOD");
            fontFallback = text(fontFallback, "BRIANNETOD");
            fontSize = positive(fontSize, 0.7f);
            color = text(color, "fffdf5");
            coinsOffsetX = finite(coinsOffsetX, 24f);
            coinsOffsetY = finite(coinsOffsetY, -48f);
            gemsOffsetX = finite(gemsOffsetX, 24f);
            gemsOffsetY = finite(gemsOffsetY, -78f);
            if (Float.isNaN(anchorX)) anchorX = 0f;
            if (Float.isNaN(anchorY)) anchorY = 1f;
            coinsFormat = text(coinsFormat, "Coins: {value}");
            gemsFormat = text(gemsFormat, "Gems: {value}");
        }

        public String coinsText(int value) {
            return coinsFormat.replace("{value}", String.valueOf(Math.max(0, value)));
        }

        public String gemsText(int value) {
            return gemsFormat.replace("{value}", String.valueOf(Math.max(0, value)));
        }
    }
}
