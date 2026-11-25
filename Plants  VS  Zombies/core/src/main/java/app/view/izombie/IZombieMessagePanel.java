package app.view.izombie;

import app.view.hud.notification.emoji_sticker.AnimatedIconImage;
import app.view.hud.notification.emoji_sticker.EmojiAssetLoader;
import app.view.hud.notification.emoji_sticker.StickerAssetLoader;
import app.view.ui.ScrollPanes;
import app.view.ui.SkinPanel;
import app.view.ui.UiTheme;
import app.view.ui.text.TextAlign;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import game.audio.AudioId;
import game.audio.AudioManager;

import java.util.List;
import java.util.function.Consumer;

public final class IZombieMessagePanel extends WidgetGroup {
    private static final float PANEL_WIDTH = 340f;
    private static final float SLIDE_IN = 0.22f;
    private static final float SLIDE_OUT = 0.16f;

    private final Table drawer;
    private final Table tabBar = new Table();
    private final Table content = new Table();
    private boolean opened;
    private boolean closing;

    public IZombieMessagePanel(Consumer<IZombieReaction> onSelect) {
        setFillParent(true);
        setTouchable(Touchable.childrenOnly);

        Table backdrop = new Table();
        backdrop.setFillParent(true);
        backdrop.setTouchable(Touchable.enabled);
        backdrop.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                close();
            }
        });
        addActor(backdrop);

        drawer = new Table();
        drawer.setTouchable(Touchable.enabled);
        drawer.top();
        drawer.pad(16f, 12f, 16f, 16f);

        buildTabBar(onSelect);
        drawer.add(tabBar).growX().padBottom(10f).row();
        drawer.add(content).grow();
        showMessages(onSelect);

        addActor(drawer);
    }

    private void buildTabBar(Consumer<IZombieReaction> onSelect) {
        tabBar.add(tabButton("Messages", () -> showMessages(onSelect))).growX();
        tabBar.add(tabButton("Emoji", () -> showEmoji(onSelect))).growX();
        tabBar.add(tabButton("Stickers", () -> showStickers(onSelect))).growX();
    }

    private SkinPanel tabButton(String label, Runnable onClick) {
        SkinPanel button = new SkinPanel(UiTheme.BG_TAB, UiTheme.BG_PANEL);
        button.padded(8f, 6f, 8f, 6f);
        button.add(TextSpec.of(UiTheme.LBL_DEFAULT).align(TextAlign.CENTER).build(label)).growX();
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AudioManager.play(AudioId.UI_CLICK);
                onClick.run();
            }
        });
        return button;
    }

    private void showMessages(Consumer<IZombieReaction> onSelect) {
        List<IZombieQuickMessage> messages = IZombieQuickMessageCatalog.load();
        Table list = new Table();
        list.top().left();
        for (IZombieQuickMessage message : messages) {
            SkinPanel row = new SkinPanel(UiTheme.BG_TAB, UiTheme.BG_PANEL);
            row.padded(10f, 12f, 10f, 12f);
            Label label = TextSpec.of(UiTheme.LBL_DEFAULT)
                .align(TextAlign.LEFT)
                .wrap(true)
                .build(message.text());
            row.add(label).growX();
            row.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    AudioManager.play(AudioId.UI_CLICK);
                    if (onSelect != null) {
                        onSelect.accept(IZombieReaction.text(message));
                    }
                    close();
                }
            });
            list.add(row).growX().padBottom(8f).row();
        }
        setContent(list);
    }

    private void showEmoji(Consumer<IZombieReaction> onSelect) {
        List<IZombieQuickEmoji> emojis = IZombieQuickEmojiCatalog.load();

        Table grid = new Table();
        grid.top().left();

        int columns = 4;
        int index = 0;

        for (IZombieQuickEmoji emoji : emojis) {
            SkinPanel tile = new SkinPanel(
                UiTheme.BG_TAB,
                UiTheme.BG_PANEL
            );

            tile.padded(8f, 8f, 8f, 8f);
            tile.setTouchable(Touchable.enabled);

            TextureRegion region = EmojiAssetLoader.getRegion(
                emoji.drawableName()
            );

            Gdx.app.log(
                "IZombieEmoji",
                "id=" + emoji.id()
                    + ", drawable=" + emoji.drawableName()
                    + ", loaded=" + (region != null)
            );

            if (region != null) {
                Image image = new Image(region);
                image.setScaling(Scaling.fit);

                tile.add(image)
                    .size(48f, 48f)
                    .center();
            } else {
                Label label = TextSpec
                    .of(UiTheme.LBL_MEDIUM)
                    .align(TextAlign.CENTER)
                    .build(
                        emoji.glyph() == null
                            ? "?"
                            : emoji.glyph()
                    );

                tile.add(label)
                    .size(48f, 48f)
                    .center();
            }

            tile.addListener(new ClickListener() {
                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {
                    AudioManager.play(AudioId.UI_CLICK);

                    if (onSelect != null) {
                        onSelect.accept(
                            IZombieReaction.emoji(emoji)
                        );
                    }

                    close();
                }
            });

            grid.add(tile)
                .size(72f, 72f)
                .pad(4f);

            index++;

            if (index % columns == 0) {
                grid.row();
            }
        }

        setContent(grid);
    }

    private void showStickers(Consumer<IZombieReaction> onSelect) {
        List<IZombieSticker> stickers = IZombieStickerCatalog.load();
        Gdx.app.log("IZombieMessagePanel", "showStickers called. Total stickers in catalog: " + stickers.size());

        Table grid = new Table();
        grid.top().left();

        int columns = 3;
        int index = 0;

        for (IZombieSticker sticker : stickers) {
            Gdx.app.log("IZombieMessagePanel", "Checking sticker -> id=" + sticker.id()
                + ", label=" + sticker.label()
                + ", drawable=" + sticker.drawableName());

            SkinPanel tile = new SkinPanel(
                UiTheme.BG_TAB,
                UiTheme.BG_PANEL
            );
            tile.padded(6f, 6f, 6f, 6f);
            tile.setTouchable(Touchable.enabled);

            Animation<TextureRegion> animation = StickerAssetLoader.getAnimation(
                sticker.drawableName(),
                0.033f
            );

            if (animation != null) {
                AnimatedIconImage animatedImage = new AnimatedIconImage(animation);
                animatedImage.setScaling(Scaling.fit);

                tile.add(animatedImage)
                    .size(64f, 64f)
                    .center();
            } else {
                Gdx.app.log("IZombieMessagePanel", "Fallback to text label for sticker: " + sticker.id());
                Label label = TextSpec.of(UiTheme.LBL_DEFAULT)
                    .align(TextAlign.CENTER)
                    .build(sticker.label());

                tile.add(label)
                    .size(64f, 64f)
                    .center();
            }

            tile.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    AudioManager.play(AudioId.UI_CLICK);
                    if (onSelect != null) {
                        onSelect.accept(IZombieReaction.sticker(sticker));
                    }
                    close();
                }
            });

            grid.add(tile)
                .size(80f, 80f)
                .pad(4f);

            index++;
            if (index % columns == 0) {
                grid.row();
            }
        }

        setContent(grid);
    }

    private void setContent(Table list) {
        content.clearChildren();
        ScrollPane scroll = ScrollPanes.vertical(list, 8f);
        content.add(scroll).grow();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        drawer.setSize(PANEL_WIDTH, getHeight());
        if (!opened && getWidth() > 1f) {
            opened = true;
            drawer.setPosition(getWidth(), 0f);
            drawer.addAction(Actions.moveTo(
                getWidth() - PANEL_WIDTH,
                0f,
                SLIDE_IN,
                Interpolation.pow2Out
            ));
        }
    }

    public void close() {
        if (closing) {
            return;
        }
        closing = true;
        drawer.clearActions();
        drawer.addAction(Actions.sequence(
            Actions.moveTo(getWidth(), 0f, SLIDE_OUT, Interpolation.pow2In),
            Actions.run(this::remove)
        ));
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        Actor hit = super.hit(x, y, touchable);
        if (hit == null && touchable && getTouchable() == Touchable.disabled) {
            return null;
        }
        return hit;
    }
}
