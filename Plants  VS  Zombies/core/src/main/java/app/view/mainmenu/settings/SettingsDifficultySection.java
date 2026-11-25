package app.view.mainmenu.settings;

import app.controller.menu.SettingsMenuController;
import app.model.App;
import app.model.user.User;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import common.Result;

public final class SettingsDifficultySection extends Table {
    public SettingsDifficultySection(SettingsMenuConfig.DifficultyConfig config, SettingsMenuController controller) {
        left().top();
        Label title = new Label(config.titleText, new Label.LabelStyle(
            SettingsUiFonts.resolve(config.titleFont, config.titleFontFallback),
            SettingsMenuConfig.color(config.titleColor, Color.valueOf("F4E7B0"))
        ));
        title.setFontScale(config.titleFontSize);
        title.setAlignment(SettingsMenuConfig.textAlign(config.titleAlignment));
        add(title)
            .left()
            .padLeft(config.titleOffsetX)
            .padBottom(config.titleSliderGap + config.titleOffsetY);
        row();

        User user = App.getCurrentUser();
        int current = user != null
            ? clamp(user.getDifficultyLevel(), (int) config.sliderMinValue, (int) config.sliderMaxValue)
            : Math.round(config.sliderDefaultValue);

        Slider slider = SettingsControls.slider(
            config.sliderMinValue,
            config.sliderMaxValue,
            config.sliderStep,
            config.sliderUseFlatStyle,
            config.sliderStyle,
            SettingsMenuConfig.color(config.sliderTrackEmptyColor, Color.valueOf("1414148C")),
            SettingsMenuConfig.color(config.sliderTrackFilledColor, Color.valueOf("E6B83D")),
            SettingsMenuConfig.color(config.sliderKnobColor, Color.valueOf("FFF4D1")),
            SettingsMenuConfig.color(config.sliderColor, Color.WHITE),
            config.sliderTrackHeight,
            config.sliderKnobSize,
            SettingsMenuConfig.color(config.hoverColor, Color.valueOf("FFF4C0"))
        );
        slider.setValue(current);

        Label valueLabel = new Label(config.formattedValue(current), new Label.LabelStyle(
            SettingsUiFonts.resolve(config.valueLabelFont, config.valueLabelFontFallback),
            SettingsMenuConfig.color(config.valueLabelColor, Color.valueOf("F2E7C4"))
        ));
        valueLabel.setFontScale(config.valueLabelFontSize);
        valueLabel.setVisible(config.valueLabelVisible);

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int level = Math.round(slider.getValue());
                Result result = controller.changeDifficulty(level);
                if (result.success()) {
                    valueLabel.setText(config.formattedValue(level));
                }
            }
        });

        Table sliderRow = new Table();
        sliderRow.left();
        sliderRow.add(slider)
            .width(config.sliderWidth)
            .height(config.sliderHeight)
            .padLeft(config.sliderOffsetX)
            .padBottom(config.sliderOffsetY)
            .left();
        sliderRow.add(valueLabel).padLeft(config.valueLabelGap).left();
        add(sliderRow).left();
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
