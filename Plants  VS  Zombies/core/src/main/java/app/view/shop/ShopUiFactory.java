package app.view.shop;

import app.view.mainmenu.config.MainMenuEasings;
import app.view.shop.config.ShopConfig;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import app.view.ui.UiTheme;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.ray3k.tenpatch.TenPatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import game.component.AssetManager;

public final class ShopUiFactory {
    private ShopUiFactory() {
    }

    public static Label label(String text, BitmapFont font, Color color, int alignment) {
        Label.LabelStyle style = new Label.LabelStyle(font, color);
        Label label = new Label(text, style);
        label.setAlignment(alignment);
        return label;
    }

    public static Label label(String text, BitmapFont font, Color color) {
        return label(text, font, color, Align.left);
    }

    public static TextButton styledButton(
        ShopConfig.ButtonStyleConfig style,
        BitmapFont font,
        ShopTextureCache textures,
        ShopConfig.InteractionConfig interaction
    ) {
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        Texture bgTexture = textures.get(style.backgroundPath);
        if (bgTexture != null) {
            TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(bgTexture));
            buttonStyle.up = drawable;
            buttonStyle.down = drawable;
            buttonStyle.over = drawable;
        }
        TextButton button = new TextButton(style.label, buttonStyle);
        button.getLabel().setAlignment(Align.center);
        button.getLabel().setFontScale(style.fontSize);
        button.getLabel().setColor(parseColor(style.fontColor, Color.WHITE));
        float width = style.width;
        float height = style.height;
        if (style.keepAspect && bgTexture != null && bgTexture.getHeight() > 0) {
            float aspect = bgTexture.getWidth() / (float) bgTexture.getHeight();
            width = height * aspect;
        }
        button.setSize(width, height);
        button.setTransform(true);
        button.setOrigin(Align.center);
        attachHoverScale(button, interaction);
        return button;
    }

    public static SelectBox<String> styledSelectBox(
        BitmapFont font,
        Color fontColor,
        ShopConfig.SelectBoxStyleConfig style
    ) {
        SelectBox.SelectBoxStyle boxStyle = new SelectBox.SelectBoxStyle();
        if (AssetManager.skin != null) {
            try {
                SelectBox.SelectBoxStyle base = AssetManager.skin.get(SelectBox.SelectBoxStyle.class);
                if (base != null) {
                    boxStyle = new SelectBox.SelectBoxStyle(base);
                }
            } catch (RuntimeException ignored) {
            }
        }
        if (boxStyle.listStyle == null) {
            boxStyle.listStyle = new List.ListStyle();
        }
        if (boxStyle.scrollStyle == null) {
            boxStyle.scrollStyle = new ScrollPane.ScrollPaneStyle();
        }
        boxStyle.font = font;
        boxStyle.fontColor = fontColor;
        if (boxStyle.listStyle != null) {
            boxStyle.listStyle = new List.ListStyle(boxStyle.listStyle);
            boxStyle.listStyle.font = font;
            boxStyle.listStyle.fontColorSelected = fontColor;
            boxStyle.listStyle.fontColorUnselected = fontColor;
        }
        SelectBox<String> selectBox = new SelectBox<>(boxStyle);
        selectBox.setName("shop-select");
        selectBox.setAlignment(Align.center);
        selectBox.setSize(style.width, style.height);
        return selectBox;
    }

    public static void addCenteredButton(Table table, TextButton button, float padTop) {
        table.add(button)
            .size(button.getWidth(), button.getHeight())
            .growX()
            .center()
            .padTop(padTop)
            .row();
    }

    public static ScrollPane.ScrollPaneStyle thinScrollStyle(float barWidth, Color knobColor, Color trackColor) {
        ScrollPane.ScrollPaneStyle style = new ScrollPane.ScrollPaneStyle();
        style.vScroll = solidDrawable(Math.max(1f, barWidth), trackColor);
        style.vScrollKnob = solidDrawable(Math.max(1f, barWidth), knobColor);
        style.hScroll = solidDrawable(Math.max(1f, barWidth), trackColor);
        style.hScrollKnob = solidDrawable(Math.max(1f, barWidth), knobColor);
        return style;
    }

    private static TextureRegionDrawable solidDrawable(float width, Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(texture));
        drawable.setMinWidth(width);
        drawable.setMinHeight(width);
        return drawable;
    }

    public static TextButton plainButton(String label, BitmapFont font, float fontScale) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.WHITE;
        TextButton button = new TextButton(label, style);
        button.getLabel().setFontScale(fontScale);
        return button;
    }

    public static void attachHoverScale(Actor actor, ShopConfig.InteractionConfig interaction) {
        if (interaction == null) {
            return;
        }
        attachHoverScale(actor, interaction, interaction.hoverScale);
    }

    public static void attachHoverScale(Actor actor, ShopConfig.InteractionConfig interaction, float hoverScale) {
        if (actor == null || interaction == null) {
            return;
        }
        float duration = interaction.hoverDuration;
        if (hoverScale <= 1f || duration <= 0f) {
            return;
        }
        actor.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer != -1) {
                    return;
                }
                actor.clearActions();
                actor.addAction(Actions.scaleTo(hoverScale, hoverScale, duration, MainMenuEasings.parse(interaction.easing)));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer != -1) {
                    return;
                }
                actor.clearActions();
                actor.addAction(Actions.scaleTo(1f, 1f, duration, MainMenuEasings.parse(interaction.easing)));
            }
        });
    }

    public static BitmapFont resolveFont(String name, String fallback) {
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
        return new BitmapFont();
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

    public static Drawable tintedSkinDrawable(String name, String fallback, float alpha) {
        Drawable source = UiTheme.drawableOrFallback(name, fallback);
        if (source == null) {
            return null;
        }
        float tintAlpha = Math.max(0f, Math.min(1f, alpha));
        if (source instanceof TenPatchDrawable tenPatch) {
            TenPatchDrawable copy = new TenPatchDrawable(tenPatch);
            copy.setMinWidth(0f);
            copy.setMinHeight(0f);
            if (tintAlpha < 0.999f) {
                copy = copy.tint(new Color(1f, 1f, 1f, tintAlpha));
                copy.setMinWidth(0f);
                copy.setMinHeight(0f);
            }
            return copy;
        }
        if (source instanceof TextureRegionDrawable region) {
            TextureRegionDrawable copy = new TextureRegionDrawable(region);
            copy.setMinWidth(0f);
            copy.setMinHeight(0f);
            if (tintAlpha < 0.999f) {
                return copy.tint(new Color(1f, 1f, 1f, tintAlpha));
            }
            return copy;
        }
        return source;
    }

    public static Color parseColor(String hex, Color fallback) {
        if (hex == null || hex.isBlank()) {
            return fallback.cpy();
        }
        try {
            return Color.valueOf(hex.startsWith("#") ? hex : "#" + hex);
        } catch (RuntimeException exception) {
            return fallback.cpy();
        }
    }
}
