package app.view.mainmenu.settings;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import game.settings.GameSettings;
import game.settings.GameplaySettings;

public final class SettingsDebugSection extends Table {
    public SettingsDebugSection(SettingsMenuConfig.DebugConfig config) {
        left().top();
        Label title = new Label(config.titleText, new Label.LabelStyle(
            SettingsUiFonts.resolve(config.titleFont, config.titleFontFallback),
            SettingsMenuConfig.color(config.titleColor, Color.valueOf("F4E7B0"))
        ));
        title.setFontScale(config.titleFontSize);
        title.setAlignment(SettingsMenuConfig.textAlign(config.titleAlignment));
        add(title).left().padLeft(config.titleOffsetX).padBottom(config.titleCheckboxGap + config.titleOffsetY);
        row();

        GameplaySettings gameplay = GameSettings.gameplay();
        CheckBox box = SettingsControls.checkBox(
            config.checkboxLabel,
            config.checkboxStyle,
            SettingsMenuConfig.color(config.checkboxHoverColor, Color.valueOf("FFF4C0"))
        );
        box.getLabel().setStyle(new Label.LabelStyle(
            SettingsUiFonts.resolve(config.checkboxLabelFont, "BRIANNETOD"),
            SettingsMenuConfig.color(config.checkboxLabelColor, Color.valueOf("F2E7C4"))
        ));
        box.getLabel().setFontScale(config.checkboxLabelFontSize);
        if (box.getImageCell() != null) {
            box.getImageCell().size(config.checkboxSize);
        }
        box.setChecked(gameplay.debugMode());
        box.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameplay.setDebugMode(box.isChecked());
                GameSettings.save();
            }
        });
        add(box).left().height(Math.max(config.checkboxSize, 32f));
    }
}
