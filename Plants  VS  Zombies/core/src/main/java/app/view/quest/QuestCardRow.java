package app.view.quest;

import app.view.mainmenu.news.NewsUiFonts;
import app.view.ui.SkinPanel;
import app.view.ui.UiTheme;
import app.view.ui.button.ActionButton;
import app.view.ui.button.ButtonTheme;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import game.audio.AudioId;
import game.audio.AudioManager;
import game.component.AssetManager;
import game.entity.quest.Quest;
import game.entity.quest.QuestCondition;

import java.util.function.Function;

public final class QuestCardRow extends Table {
    private final Quest quest;
    private final QuestMenuConfig config;
    private final QuestMenuConfig.QuestPresentationConfig presentation;
    private final Label titleLabel;
    private final ProgressBar progressBar;
    private final Label progressLabel;
    private final ActionButton claimButton;
    private final Runnable onClaimed;

    public QuestCardRow(
        Quest quest,
        QuestMenuConfig config,
        Function<String, Texture> textures,
        Runnable onClaimed
    ) {
        this.quest = quest;
        this.config = config;
        this.presentation = config.presentationFor(quest);
        this.onClaimed = onClaimed == null ? () -> {
        } : onClaimed;

        QuestMenuConfig.CardConfig card = config.card;
        String background = presentation.resolvedBackground(card);
        SkinPanel root = new SkinPanel(card.backgroundDrawable, card.backgroundFallback);
        Drawable customBackground = looksLikeTexturePath(background)
            ? textureBackground(textures.apply(background))
            : namedBackground(background, card.backgroundDrawable, card.backgroundFallback);
        if (customBackground != null) {
            root.setBackground(customBackground);
        }
        root.padded(card.padTop, card.padLeft, card.padBottom, card.padRight);

        titleLabel = new Label(presentation.resolvedTitle(quest), new Label.LabelStyle(
            NewsUiFonts.resolve(card.titleFont, card.titleFontFallback),
            QuestMenuConfig.color(card.titleColor, Color.valueOf("2A1C10"))
        ));
        titleLabel.setAlignment(QuestMenuConfig.textAlign(card.titleAlignment));
        titleLabel.setFontScale(card.titleFontSize);
        titleLabel.setWrap(true);

        progressBar = createProgressBar(config.resolvedProgressStyle(quest, presentation));
        progressLabel = new Label("0/0", new Label.LabelStyle(
            NewsUiFonts.resolve(card.progressLabelFont, card.progressLabelFontFallback),
            QuestMenuConfig.color(card.progressLabelColor, Color.WHITE)
        ));
        progressLabel.setAlignment(Align.center);
        progressLabel.setFontScale(card.progressLabelFontSize);

        Stack progressStack = new Stack();
        progressStack.add(progressBar);
        Table progressLabelTable = new Table();
        progressLabelTable.add(progressLabel).grow();
        progressStack.add(progressLabelTable);

        ButtonTheme claimTheme = new ButtonTheme().size(card.claimWidth, card.claimHeight);
        claimTheme.labelScale = card.claimLabelScale;
        claimButton = new ActionButton(
            card.claimText,
            UiTheme.buttonStyleOrDefault(card.claimLockedStyle),
            claimTheme
        );
        claimButton.onClick(this::claim);

        Actor left = imageOrNull(textures.apply(presentation.leftImagePath), card.iconSize);
        Actor reward = imageOrNull(textures.apply(presentation.rightImagePath), card.rewardSize);

        Table middle = new Table();
        middle.add(titleLabel).growX().left().padBottom(4f);
        middle.row();
        Table progressRow = new Table();
        progressRow.add(progressStack).growX().height(card.progressHeight);
        if (reward != null) {
            progressRow.add(reward).size(card.rewardSize).padLeft(card.progressToRewardGap);
        }
        middle.add(progressRow).growX();

        if (left != null) {
            root.add(left).size(card.iconSize).padRight(card.iconToTextGap);
        }
        root.add(middle).growX();
        root.add(claimButton).width(card.claimWidth).height(card.claimHeight).padLeft(8f);

        add(root).grow();
        refresh();
    }

    public Quest quest() {
        return quest;
    }

    public void refresh() {
        QuestCondition condition = quest.getCondition();
        int current = condition == null ? 0 : Math.max(0, condition.getCurrentProgress());
        int required = condition == null ? 1 : Math.max(1, condition.getRequiredProgress());
        progressBar.setRange(0f, required);
        progressBar.setValue(Math.min(current, required));
        progressLabel.setText(current + "/" + required);

        boolean ready = quest.isReadyToClaim();
        String style = ready ? config.card.claimReadyStyle : config.card.claimLockedStyle;
        claimButton.setStyle(AssetManager.skin.get(
            UiTheme.buttonStyleOrDefault(style),
            com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle.class
        ));
        claimButton.setEnabledState(ready);
        claimButton.setText(config.card.claimText);
    }

    private void claim() {
        if (!quest.isReadyToClaim()) {
            AudioManager.play(AudioId.UI_ERROR);
            return;
        }
        AudioManager.play(AudioId.UI_CLICK);
        onClaimed.run();
    }

    private ProgressBar createProgressBar(String styleName) {
        Skin skin = AssetManager.skin;
        String style = resolveProgressStyle(skin, styleName);
        ProgressBar bar;
        try {
            bar = new ProgressBar(0f, 1f, 1f, false, skin, style);
        } catch (Exception ignored) {
            bar = new ProgressBar(0f, 1f, 1f, false, skin);
        }
        bar.setAnimateDuration(0.12f);
        return bar;
    }

    private static String resolveProgressStyle(Skin skin, String requested) {
        if (hasProgressStyle(skin, requested)) {
            return requested;
        }
        if (hasProgressStyle(skin, "xp_green")) {
            return "xp_green";
        }
        if (hasProgressStyle(skin, "default-horizontal")) {
            return "default-horizontal";
        }
        return "default-horizontal";
    }

    private static boolean hasProgressStyle(Skin skin, String name) {
        return skin != null && name != null && !name.isBlank()
            && skin.has(name, ProgressBar.ProgressBarStyle.class);
    }

    private static Drawable textureBackground(Texture texture) {
        if (texture == null) {
            return null;
        }
        int split = Math.max(12, Math.min(texture.getWidth(), texture.getHeight()) / 4);
        NinePatchDrawable drawable = new NinePatchDrawable(new NinePatch(
            new TextureRegion(texture),
            split,
            split,
            split,
            split
        ));
        drawable.setMinWidth(0f);
        drawable.setMinHeight(0f);
        drawable.setLeftWidth(0f);
        drawable.setRightWidth(0f);
        drawable.setTopHeight(0f);
        drawable.setBottomHeight(0f);
        return drawable;
    }

    private static Drawable namedBackground(String background, String current, String fallback) {
        if (background == null || background.isBlank() || background.equals(current)) {
            return null;
        }
        return UiTheme.drawableOrFallback(background, fallback);
    }

    private static boolean looksLikeTexturePath(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        String path = value.replace('\\', '/').toLowerCase();
        return path.contains("/") || path.endsWith(".png") || path.endsWith(".jpg")
            || path.endsWith(".jpeg") || path.endsWith(".webp");
    }

    private static Actor imageOrNull(Texture texture, float size) {
        if (texture == null) {
            return null;
        }
        Image image = new Image(new TextureRegionDrawable(new TextureRegion(texture)));
        image.setScaling(Scaling.fit);
        image.setSize(size, size);
        return image;
    }
}
