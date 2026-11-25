package app.view.network;

import app.view.mainmenu.profile.ProfileUiFonts;
import app.view.ui.UiTheme;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.ray3k.tenpatch.TenPatchDrawable;
import game.audio.AudioId;
import game.audio.AudioManager;

import java.util.function.Consumer;

public final class NetworkSortSelect extends WidgetGroup {
    private final NetworkMenuConfig.RankingConfig ranking;
    private final NetworkMenuConfig.SortSelectConfig config;
    private final Table button;
    private final Label buttonLabel;
    private final Table dropdown;
    private final InputListener closeOnOutside = new InputListener() {
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            Actor target = event.getTarget();
            if (open && target != null && !target.isDescendantOf(NetworkSortSelect.this)) {
                setOpen(false);
            }
            return false;
        }
    };

    private NetworkRankSort selected;
    private Consumer<NetworkRankSort> onChanged = sort -> {
    };
    private boolean open;
    private float padScaleX = 1f;
    private float padScaleY = 1f;

    public NetworkSortSelect(NetworkMenuConfig.RankingConfig ranking) {
        this.ranking = ranking;
        this.config = ranking.sort;
        this.selected = config.initialSort();
        setName("menu-network-sort");
        setTransform(false);
        setTouchable(Touchable.childrenOnly);

        button = new Table();
        button.setName("menu-network-sort-button");
        button.setTransform(false);
        button.setTouchable(Touchable.enabled);
        buttonLabel = label(
            ranking.labelFor(selected),
            config.font,
            config.fontFallback,
            config.fontSize,
            NetworkMenuConfig.color(config.fontColor, Color.valueOf("F4E7B0"))
        );
        button.add(buttonLabel).grow().center();
        addActor(button);

        dropdown = new Table();
        dropdown.setName("menu-network-sort-list");
        dropdown.setTransform(false);
        dropdown.setVisible(false);
        dropdown.setTouchable(Touchable.disabled);
        dropdown.top();
        addActor(dropdown);

        ClickListener toggle = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AudioManager.play(AudioId.UI_CLICK);
                setOpen(!open);
            }
        };
        button.addListener(toggle);
        buttonLabel.addListener(toggle);
        rebuildOptions();
    }

    public void setOnChanged(Consumer<NetworkRankSort> onChanged) {
        this.onChanged = onChanged != null ? onChanged : sort -> {
        };
    }

    public NetworkRankSort selected() {
        return selected;
    }

    public void place(float x, float y, float width, float height, float scaleX, float scaleY) {
        float nextX = scaleX > 0f ? scaleX : 1f;
        float nextY = scaleY > 0f ? scaleY : 1f;
        boolean scaleChanged = Math.abs(padScaleX - nextX) > 0.01f || Math.abs(padScaleY - nextY) > 0.01f;
        padScaleX = nextX;
        padScaleY = nextY;
        setBounds(x, y, Math.max(1f, width), Math.max(1f, height));
        if (scaleChanged) {
            rebuildOptions();
        } else {
            layoutChildren();
        }
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean value) {
        if (open == value) {
            return;
        }
        open = value;
        dropdown.setVisible(open);
        dropdown.setTouchable(open ? Touchable.childrenOnly : Touchable.disabled);
        layoutChildren();
        Stage stage = getStage();
        if (stage == null) {
            return;
        }
        stage.removeCaptureListener(closeOnOutside);
        if (open) {
            stage.addCaptureListener(closeOnOutside);
        }
    }

    @Override
    protected void setStage(Stage stage) {
        Stage previous = getStage();
        if (previous != null) {
            previous.removeCaptureListener(closeOnOutside);
        }
        super.setStage(stage);
        if (stage != null && open) {
            stage.addCaptureListener(closeOnOutside);
        }
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        layoutChildren();
    }

    private void select(NetworkRankSort sort) {
        NetworkRankSort next = sort == null ? NetworkRankSort.MIOPOINT : sort;
        boolean changed = selected != next;
        selected = next;
        buttonLabel.setText(ranking.labelFor(selected));
        setOpen(false);
        rebuildOptions();
        if (changed) {
            onChanged.accept(selected);
        }
    }

    private void rebuildOptions() {
        dropdown.clearChildren();
        Color idle = NetworkMenuConfig.color(config.optionFontColor, Color.valueOf("F4E7B0"));
        Color active = NetworkMenuConfig.color(config.selectedColor, Color.valueOf("FFE08A"));
        float optionHeight = config.optionHeight * padScaleY;
        float spacing = config.optionSpacing * padScaleY;
        NetworkRankSort[] sorts = NetworkRankSort.values();
        for (int i = 0; i < sorts.length; i++) {
            NetworkRankSort sort = sorts[i];
            boolean chosen = sort == selected;
            Table slot = new Table();
            slot.setTransform(false);
            slot.setTouchable(Touchable.enabled);
            Drawable background = slotBackground(config.optionSlotBackground);
            if (background != null) {
                slot.setBackground(background);
            }
            slot.pad(
                config.optionPadTop * padScaleY,
                config.optionPadLeft * padScaleX,
                config.optionPadBottom * padScaleY,
                config.optionPadRight * padScaleX
            );
            Label option = label(
                ranking.labelFor(sort),
                config.optionFont,
                config.optionFontFallback,
                config.optionFontSize,
                chosen ? active : idle
            );
            option.setTouchable(Touchable.disabled);
            slot.add(option).grow().center();
            slot.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    AudioManager.play(AudioId.UI_CLICK);
                    select(sort);
                }
            });
            float bottom = i < sorts.length - 1 ? spacing : 0f;
            dropdown.add(slot).growX().height(optionHeight).padBottom(bottom);
            dropdown.row();
        }
        layoutChildren();
    }

    private void layoutChildren() {
        float width = getWidth();
        float height = getHeight();
        button.setBounds(0f, 0f, width, height);
        button.invalidateHierarchy();

        float optionHeight = config.optionHeight * padScaleY;
        float spacing = config.optionSpacing * padScaleY;
        int count = NetworkRankSort.values().length;
        float listHeight = optionHeight * count + spacing * Math.max(0, count - 1);
        dropdown.setBounds(0f, height, width, Math.max(1f, listHeight));
        dropdown.invalidateHierarchy();
        if (open) {
            dropdown.toFront();
        }
    }

    private static Label label(String text, String font, String fallback, float scale, Color color) {
        BitmapFont bitmapFont = ProfileUiFonts.resolve(font, fallback);
        Label label = new Label(text, new Label.LabelStyle(bitmapFont, color));
        label.setFontScale(Math.max(0.1f, scale));
        label.setAlignment(Align.center);
        return label;
    }

    private static Drawable slotBackground(String name) {
        Drawable source = UiTheme.drawableOrFallback(name, UiTheme.BG_REWARD);
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
