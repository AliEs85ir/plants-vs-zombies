package app.view.mainmenu.settings;

import app.view.ui.UiTheme;
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

public final class SettingsControls {
    private SettingsControls() {
    }

    public static Slider slider(
        float min,
        float max,
        float step,
        boolean flat,
        String styleName,
        Color trackEmpty,
        Color trackFilled,
        Color knob,
        Color sliderColor,
        float trackHeight,
        float knobSize,
        Color hoverColor
    ) {
        Slider.SliderStyle style = flat
            ? flatSliderStyle(trackEmpty, trackFilled, knob, trackHeight, knobSize)
            : skinSliderStyle(styleName, trackHeight, knobSize);
        Slider slider = new Slider(min, max, step, false, style);
        slider.setColor(sliderColor);
        bindHover(slider, hoverColor);
        return slider;
    }

    public static CheckBox checkBox(String text, String styleName, Color hoverColor) {
        CheckBox box = new CheckBox(text == null ? "" : text, checkBoxStyle(styleName));
        bindHover(box, hoverColor);
        return box;
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

    private static Slider.SliderStyle flatSliderStyle(
        Color trackEmpty,
        Color trackFilled,
        Color knob,
        float trackHeight,
        float knobSize
    ) {
        Slider.SliderStyle style = new Slider.SliderStyle();
        style.background = solid(trackEmpty, 8f, trackHeight);
        style.knobBefore = solid(trackFilled, 8f, trackHeight);
        Drawable knobDrawable = solid(knob, knobSize, knobSize);
        style.knob = knobDrawable;
        style.knobOver = knobDrawable;
        style.knobDown = knobDrawable;
        return style;
    }

    private static Slider.SliderStyle skinSliderStyle(String styleName, float trackHeight, float knobSize) {
        Slider.SliderStyle source = preferredSlider(styleName);
        Slider.SliderStyle style = copySlider(source);
        style.background = sized(style.background, 0f, trackHeight);
        style.knobBefore = sized(style.knobBefore, 0f, trackHeight);
        style.knob = sized(style.knob, knobSize, knobSize);
        style.knobOver = sized(style.knobOver, knobSize, knobSize);
        style.knobDown = sized(style.knobDown, knobSize, knobSize);
        return style;
    }

    private static CheckBox.CheckBoxStyle checkBoxStyle(String name) {
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
}
