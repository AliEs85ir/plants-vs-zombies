package app.view.hud.notification;

import app.enums.asset.GameIcon;
import app.view.game.PlayScreen;
import app.view.hud.notification.emoji_sticker.EmojiAssetLoader;
import app.view.hud.notification.emoji_sticker.StickerAssetLoader;
import app.view.ui.UiColor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.component.AssetManager;
import plants_vs_zombies.PlantsVSZombies;

public final class GameplayNotifications {
    public static final String SCORING_BONUS_EFFECT_ID = "scoring bonus";
    public static final String SCORING_BONUS_PAM_PATH =
        "768/FULL/EFFECTS/ZOMBIE_CORPORATE_CONSULTANT_EFFECT/ZOMBIE_CORPORATE_CONSULTANT_EFFECT.PAM";
    public static final String SCORING_BONUS_PAM_CLIP = "animation";
    public static final float SCORING_BONUS_DURATION_SECONDS = 2.1f;

    private GameplayNotifications() {}

    public static NotificationSpec gameStarted() {
        return NotificationSpec.of("STARTING: Ready, Set, Plant!")
            .icon(GameIcon.IMAGE_NOTES_ZOMBIENOTE_TUTORIAL)
            .iconScale(0.3f)
            .iconPlacement(IconPlacement.LEFT)
            .size(NotificationSize.XXLARGE)
            .textSize(TextSize.LARGE)
            .lineCount(1)
            .color(UiColor.BLACK)
            .priority(NotificationPriority.NORMAL);
    }

    public static NotificationSpec minigameLevel(int level, int total) {
        return NotificationSpec.of("Level " + level + " of " + total)
            .icon(GameIcon.IMAGE_NOTES_ZOMBIENOTE_TUTORIAL)
            .iconScale(0.3f)
            .iconPlacement(IconPlacement.LEFT)
            .size(NotificationSize.LARGE)
            .textSize(TextSize.LARGE)
            .lineCount(1)
            .color(UiColor.GOLD)
            .priority(NotificationPriority.HIGH);
    }

    public static NotificationSpec waveStarted(int waveNumber) {
        return NotificationSpec.of("Wave " + waveNumber)
            .icon(GameIcon.IMAGE_NOTES_ZOMBIENOTE_DARK)
            .iconScale(0.3f)
            .iconPlacement(IconPlacement.LEFT)
            .size(NotificationSize.MEDIUM)
            .textSize(TextSize.LARGE)
            .lineCount(1)
            .color(UiColor.CYAN)
            .priority(NotificationPriority.NORMAL);
    }

    public static NotificationSpec finalWave() {
        return NotificationSpec.of("A huge wave of zombies is approaching!")
            .icon(GameIcon.IMAGE_UI_HUD_INGAME_NOTIFICATION_ICON)
            .iconScale(0.3f)
            .iconPlacement(IconPlacement.LEFT)
            .size(NotificationSize.LARGE)
            .textSize(TextSize.LARGE)
            .lineCount(2)
            .color(UiColor.CRIMSON)
            .priority(NotificationPriority.HIGH);
    }

    public static NotificationSpec shovelNoTarget() {
        return NotificationSpec.of("There is no plant to remove.")
            .icon(GameIcon.IMAGE_UI_HUD_INGAME_NOTIFICATION_ICON)
            .iconScale(0.3f)
            .iconPlacement(IconPlacement.LEFT)
            .size(NotificationSize.MEDIUM)
            .textSize(TextSize.LARGE)
            .lineCount(1)
            .color(UiColor.CRIMSON)
            .priority(NotificationPriority.NORMAL);
    }

    public static NotificationSpec shovelNotAllowed() {
        return NotificationSpec.of("This plant cannot be removed.")
            .icon(GameIcon.IMAGE_UI_HUD_INGAME_NOTIFICATION_ICON)
            .iconScale(0.3f)
            .iconPlacement(IconPlacement.LEFT)
            .size(NotificationSize.MEDIUM)
            .textSize(TextSize.LARGE)
            .lineCount(1)
            .color(UiColor.CRIMSON)
            .priority(NotificationPriority.NORMAL);
    }

    public static NotificationSpec scoringBonus(String text) {
        return NotificationSpec.of(text)
            .icon(GameIcon.IMAGE_UI_HUD_INGAME_NOTIFICATION_ICON)
            .iconScale(0.55f)
            .iconPlacement(IconPlacement.LEFT)
            .pam(SCORING_BONUS_PAM_PATH, SCORING_BONUS_PAM_CLIP, 84f, 168f)
            .font(AssetManager.DEFAULT_FONT)
            .size(NotificationSize.XXLARGE)
            .textSize(TextSize.XXXXLARGE)
            .lineCount(1)
            .anchor(NotificationAnchor.TOP_CENTER)
            .screenPadding(24f, 24f, 72f, 168f)
            .innerPadding(10f)
            .color(UiColor.GOLD)
            .priority(NotificationPriority.HIGH)
            .duration(SCORING_BONUS_DURATION_SECONDS)
            .animation(NotificationAnimationSpec.scoringPop());
    }

    public static NotificationSpec iZombieMessage(String text) {
        return NotificationSpec.of(text)
            .icon(GameIcon.IMAGE_NOTES_ZOMBIENOTE_TUTORIAL)
            .iconScale(0.3f)
            .iconPlacement(IconPlacement.LEFT)
            .size(NotificationSize.LARGE)
            .textSize(TextSize.LARGE)
            .lineCount(2)
            .anchor(NotificationAnchor.TOP_RIGHT)
            .color(UiColor.BLACK)
            .priority(NotificationPriority.HIGH)
            .duration(3.2f);
    }

    public static NotificationSpec iZombieEmoji(String drawableName, String glyphFallback) {
        TextureRegion region = EmojiAssetLoader.getRegion(drawableName);

        if (region != null) {
            return NotificationSpec.of("  ")
                .iconRegion(region, 100f)
                .iconScale(0.6f)
                .iconPlacement(IconPlacement.LEFT)
                .size(NotificationSize.MEDIUM)
                .textSize(TextSize.LARGE)
                .lineCount(1)
                .anchor(NotificationAnchor.TOP_RIGHT)
                .color(UiColor.BLACK)
                .priority(NotificationPriority.HIGH)
                .duration(2.2f);
        }

        return NotificationSpec.of(glyphFallback == null ? "  " : glyphFallback)
            .icon(GameIcon.IMAGE_NOTES_ZOMBIENOTE_TUTORIAL)
            .iconScale(0.3f)
            .iconPlacement(IconPlacement.LEFT)
            .size(NotificationSize.MEDIUM)
            .textSize(TextSize.LARGE)
            .lineCount(1)
            .anchor(NotificationAnchor.TOP_RIGHT)
            .color(UiColor.BLACK)
            .priority(NotificationPriority.HIGH)
            .duration(2.2f);
    }

    public static NotificationSpec iZombieSticker(String drawableName, String labelFallback) {
        Animation<TextureRegion> animation = (drawableName != null && !drawableName.isBlank())
            ? StickerAssetLoader.getAnimation(drawableName, 0.033f)
            : null;

        if (animation != null) {
            return NotificationSpec.of("  ")
                .iconAnimation(animation, 120f)
                .iconScale(0.85f)
                .iconPlacement(IconPlacement.LEFT)
                .size(NotificationSize.LARGE)
                .textSize(TextSize.LARGE)
                .lineCount(1)
                .anchor(NotificationAnchor.TOP_RIGHT)
                .color(UiColor.BLACK)
                .priority(NotificationPriority.HIGH)
                .duration(3.5f);
        }

        return NotificationSpec.of(labelFallback == null ? "  " : labelFallback)
            .icon(GameIcon.IMAGE_NOTES_ZOMBIENOTE_TUTORIAL)
            .iconScale(0.45f)
            .iconPlacement(IconPlacement.LEFT)
            .size(NotificationSize.LARGE)
            .textSize(TextSize.LARGE)
            .lineCount(1)
            .anchor(NotificationAnchor.TOP_RIGHT)
            .color(UiColor.BLACK)
            .priority(NotificationPriority.HIGH)
            .duration(3.2f);
    }

    public static NotificationSpec seasonHazard(String text) {
        return NotificationSpec.of(text)
            .font(AssetManager.HOUSE_OF_TERROR_FONT)
            .size(NotificationSize.XXLARGE)
            .textSize(TextSize.XXXXXXLARGE)
            .lineCount(1)
            .anchor(NotificationAnchor.TOP_CENTER)
            .screenPadding(28f, 28f, 72f, 200f)
            .innerPadding(8f)
            .color(UiColor.RED)
            .priority(NotificationPriority.HIGH)
            .duration(1.8f)
            .animation(NotificationAnimationSpec.fadeOnly(0.16f, 0.9f));
    }

    public static void show(NotificationSpec spec) {
        PlantsVSZombies game = PlantsVSZombies.getPvz();
        if (game == null || spec == null) {
            return;
        }
        Screen screen = game.getScreen();
        if (screen instanceof PlayScreen playScreen && playScreen.getHud() != null) {
            playScreen.getHud().showNotification(spec);
        }
    }

    public static void showSeasonHazard(String text) {
        show(seasonHazard(text));
    }
}
