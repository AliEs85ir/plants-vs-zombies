package app.view.network;

import app.view.mainmenu.profile.ProfileAvatars;
import app.view.mainmenu.profile.ProfileUiFonts;
import app.view.ui.UiTheme;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.ray3k.tenpatch.TenPatchDrawable;

public final class NetworkRankSlot extends Table {
    NetworkRankSlot(
        NetworkMenuConfig.RankingConfig config,
        int rank,
        String nickname,
        int score,
        String pictureId,
        boolean you,
        NetworkRankSort sort,
        float scaleX,
        float scaleY
    ) {
        setTransform(false);
        setTouchable(Touchable.disabled);
        Drawable background = slotBackground(config.slotBackground);
        if (background != null) {
            setBackground(background);
        }
        pad(
            config.slotPadTop * scaleY,
            config.slotPadLeft * scaleX,
            config.slotPadBottom * scaleY,
            config.slotPadRight * scaleX
        );

        Color accent = you
            ? NetworkMenuConfig.color(config.youColor, Color.valueOf("8A4A12"))
            : NetworkMenuConfig.color(config.rankColor, Color.valueOf("5A2E0A"));
        Color nameColor = you
            ? accent
            : NetworkMenuConfig.color(config.nameColor, Color.valueOf("3A2410"));
        Color scoreColor = NetworkMenuConfig.color(config.scoreColor, Color.valueOf("6B3A12"));

        Label rankLabel = label(
            String.valueOf(rank),
            config.rankFont,
            config.rankFontFallback,
            config.rankFontSize,
            accent
        );
        rankLabel.setAlignment(Align.center);
        add(rankLabel).width(config.rankWidth * scaleX).center().growY();

        Image avatar = new Image();
        avatar.setScaling(Scaling.fit);
        avatar.setTouchable(Touchable.disabled);
        TextureRegion region = ProfileAvatars.region(pictureId);
        if (region != null) {
            avatar.setDrawable(new TextureRegionDrawable(region));
        }
        float avatarSize = config.avatarSize * Math.min(scaleX, scaleY);
        add(avatar).size(avatarSize).padLeft(2f * scaleX).padRight(config.avatarGap * scaleX).center();

        Table text = new Table();
        text.left();
        Label nameLabel = label(
            nickname == null || nickname.isBlank() ? "Player" : nickname,
            config.nameFont,
            config.nameFontFallback,
            config.nameFontSize,
            nameColor
        );
        nameLabel.setAlignment(Align.left);
        nameLabel.setEllipsis(true);
        Label scoreLabel = label(
            config.formatScore(score, sort),
            config.scoreFont,
            config.scoreFontFallback,
            config.scoreFontSize,
            scoreColor
        );
        scoreLabel.setAlignment(Align.left);
        text.add(nameLabel).left().growX();
        text.row();
        text.add(scoreLabel).left().growX().padTop(1f * scaleY);
        add(text).growX().growY().left();
    }

    private static Label label(String text, String font, String fallback, float scale, Color color) {
        BitmapFont bitmapFont = ProfileUiFonts.resolve(font, fallback);
        Label.LabelStyle style = new Label.LabelStyle(bitmapFont, color);
        Label label = new Label(text, style);
        label.setFontScale(Math.max(0.1f, scale));
        return label;
    }

    private static Drawable slotBackground(String name) {
        Drawable source = UiTheme.drawableOrFallback(name, UiTheme.BG_MULTIPLIER);
        if (source instanceof TenPatchDrawable tenPatch) {
            TenPatchDrawable copy = new TenPatchDrawable(tenPatch);
            zeroMinSize(copy);
            return copy;
        }
        if (source instanceof NinePatchDrawable nine) {
            NinePatchDrawable copy = new NinePatchDrawable(nine);
            zeroMinSize(copy);
            return copy;
        }
        if (source instanceof TextureRegionDrawable region) {
            TextureRegionDrawable copy = new TextureRegionDrawable(region);
            zeroMinSize(copy);
            return copy;
        }
        return source;
    }

    private static void zeroMinSize(Drawable drawable) {
        drawable.setMinWidth(0f);
        drawable.setMinHeight(0f);
        drawable.setLeftWidth(0f);
        drawable.setRightWidth(0f);
        drawable.setTopHeight(0f);
        drawable.setBottomHeight(0f);
    }
}
