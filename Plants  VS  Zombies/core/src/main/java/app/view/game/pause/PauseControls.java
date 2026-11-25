package app.view.game.pause;

import app.view.ui.UiTheme;
import app.view.ui.button.ActionButton;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public final class PauseControls {
    private PauseControls() {
    }

    public static Slider slider(PauseLayout layout) {
        return slider(layout, 0f, 1f, 0.01f);
    }

    public static Slider slider(PauseLayout layout, float min, float max, float step) {
        float safeStep = step <= 0f ? 1f : step;
        float safeMin = min;
        float safeMax = max <= min ? min + safeStep : max;
        Slider slider = new Slider(safeMin, safeMax, safeStep, false, sliderStyle(layout));
        slider.setColor(layout.audio.sliderColor);
        return slider;
    }

    public static CheckBox checkBox(PauseLayout layout, String text) {
        CheckBox box = new CheckBox(text == null ? "" : text, checkBoxStyle(layout, true));
        bindHover(box, layout.audio.hoverColor);
        return box;
    }

    public static CheckBox toggle(PauseLayout layout, String text) {
        CheckBox box = new CheckBox(text == null ? "" : text, checkBoxStyle(layout, false));
        bindHover(box, layout.toggles.hoverColor);
        return box;
    }

    public static ActionButton resetButton(PauseLayout layout, Runnable action) {
        ActionButton button = new ActionButton(layout.audio.resetLabel, layout.buttons.resetStyle, layout.buttons.reset);
        button.onClick(action);
        return button;
    }

    public static void bindHover(Actor actor, Color hoverColor) {
        if (actor == null) {
            return;
        }
        Color rest = actor.getColor().cpy();
        Color hover = hoverColor == null ? rest : hoverColor.cpy();
        actor.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer != -1) {
                    return;
                }
                actor.setColor(hover);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer != -1) {
                    return;
                }
                actor.setColor(rest);
            }
        });
    }

    private static Slider.SliderStyle sliderStyle(PauseLayout layout) {
        if (layout.audio.flatSlider) {
            return flatSliderStyle(layout);
        }
        Slider.SliderStyle style = copySlider(preferredSlider(layout.audio.sliderStyle));
        applyDrawable(style, layout.audio.trackEmpty, d -> style.background = d);
        applyDrawable(style, layout.audio.trackFilled, d -> style.knobBefore = d);
        applyDrawable(style, layout.audio.knob, d -> style.knob = d);
        applyDrawable(style, layout.audio.knobOver, d -> style.knobOver = d);
        style.background = sized(style.background, 0f, layout.audio.trackHeight);
        style.knobBefore = sized(style.knobBefore, 0f, layout.audio.trackHeight);
        style.knob = sized(style.knob, layout.audio.knobSize, layout.audio.knobSize);
        style.knobOver = sized(style.knobOver, layout.audio.knobSize, layout.audio.knobSize);
        style.knobDown = sized(style.knobDown, layout.audio.knobSize, layout.audio.knobSize);
        return style;
    }

    private static Slider.SliderStyle flatSliderStyle(PauseLayout layout) {
        Slider.SliderStyle style = new Slider.SliderStyle();
        style.background = solid(layout.audio.trackEmptyColor, 8f, layout.audio.trackHeight);
        style.knobBefore = solid(layout.audio.trackFilledColor, 8f, layout.audio.trackHeight);
        Drawable knob = solid(layout.audio.knobColor, layout.audio.knobSize, layout.audio.knobSize);
        style.knob = knob;
        style.knobOver = knob;
        style.knobDown = knob;
        return style;
    }

    private static TextureRegionDrawable pixel;

    private static Drawable solid(Color color, float minWidth, float minHeight) {
        if (pixel == null) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            Texture texture = new Texture(pixmap);
            pixmap.dispose();
            pixel = new TextureRegionDrawable(new TextureRegion(texture));
        }
        TextureRegionDrawable drawable = new TextureRegionDrawable(pixel.getRegion());
        drawable.setMinWidth(minWidth);
        drawable.setMinHeight(minHeight);
        return drawable.tint(color);
    }

    private static CheckBox.CheckBoxStyle checkBoxStyle(PauseLayout layout, boolean audio) {
        String preferred = audio ? layout.audio.checkboxStyle : layout.toggles.checkboxStyle;
        CheckBox.CheckBoxStyle style = copyCheckBox(preferred);
        String on = audio ? layout.audio.checkboxOn : layout.toggles.checkboxOn;
        String off = audio ? layout.audio.checkboxOff : layout.toggles.checkboxOff;
        String onOver = audio ? layout.audio.checkboxOnOver : layout.toggles.checkboxOnOver;
        String offOver = audio ? layout.audio.checkboxOffOver : layout.toggles.checkboxOffOver;
        applyDrawable(style, on, d -> style.checkboxOn = d);
        applyDrawable(style, off, d -> style.checkboxOff = d);
        applyDrawable(style, onOver, d -> style.checkboxOnOver = d);
        applyDrawable(style, offOver, d -> style.checkboxOver = d);
        return style;
    }

    private static Slider.SliderStyle preferredSlider(String name) {
        if (name != null && UiTheme.skin().has(name, Slider.SliderStyle.class)) {
            return UiTheme.skin().get(name, Slider.SliderStyle.class);
        }
        return UiTheme.skin().get("default-horizontal", Slider.SliderStyle.class);
    }

    private static Slider.SliderStyle copySlider(Slider.SliderStyle source) {
        Slider.SliderStyle style = new Slider.SliderStyle();
        if (source == null) {
            return style;
        }
        style.background = source.background;
        style.knob = source.knob;
        style.knobBefore = source.knobBefore;
        style.knobAfter = source.knobAfter;
        style.knobOver = source.knobOver;
        style.knobDown = source.knobDown;
        style.disabledBackground = source.disabledBackground;
        style.disabledKnob = source.disabledKnob;
        style.disabledKnobBefore = source.disabledKnobBefore;
        style.disabledKnobAfter = source.disabledKnobAfter;
        return style;
    }

    private static CheckBox.CheckBoxStyle copyCheckBox(String name) {
        CheckBox.CheckBoxStyle source = UiTheme.skin().has(name, CheckBox.CheckBoxStyle.class)
            ? UiTheme.skin().get(name, CheckBox.CheckBoxStyle.class)
            : UiTheme.skin().get("default", CheckBox.CheckBoxStyle.class);
        CheckBox.CheckBoxStyle style = new CheckBox.CheckBoxStyle();
        if (source == null) {
            return style;
        }
        style.checkboxOn = source.checkboxOn;
        style.checkboxOff = source.checkboxOff;
        style.checkboxOnOver = source.checkboxOnOver;
        style.checkboxOver = source.checkboxOver;
        style.checkboxOnDisabled = source.checkboxOnDisabled;
        style.checkboxOffDisabled = source.checkboxOffDisabled;
        style.font = source.font;
        style.fontColor = source.fontColor;
        style.overFontColor = source.overFontColor;
        style.downFontColor = source.downFontColor;
        style.checkedFontColor = source.checkedFontColor;
        style.disabledFontColor = source.disabledFontColor;
        return style;
    }

    private static Drawable sized(Drawable source, float minWidth, float minHeight) {
        if (source == null) {
            return null;
        }
        if (!(source instanceof TextureRegionDrawable regionDrawable)) {
            return source;
        }
        TextureRegionDrawable copy = new TextureRegionDrawable(regionDrawable.getRegion());
        copy.setMinWidth(minWidth > 0f ? minWidth : regionDrawable.getMinWidth());
        copy.setMinHeight(minHeight > 0f ? minHeight : regionDrawable.getMinHeight());
        return copy;
    }

    private interface DrawableConsumer {
        void accept(Drawable drawable);
    }

    private static void applyDrawable(Object ignored, String name, DrawableConsumer consumer) {
        if (name == null || name.isBlank()) {
            return;
        }
        Drawable drawable = UiTheme.drawable(name);
        if (drawable != null) {
            consumer.accept(drawable);
        }
    }
}

