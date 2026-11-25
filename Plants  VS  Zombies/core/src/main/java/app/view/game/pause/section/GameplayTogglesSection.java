package app.view.game.pause.section;

import app.view.game.pause.PauseControls;
import app.view.game.pause.PauseLayout;
import app.view.game.pause.PauseSectionFrame;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import game.settings.GameSettings;
import game.settings.GameplaySettings;

public class GameplayTogglesSection extends Table {
    public GameplayTogglesSection(PauseLayout layout) {
        top();
        PauseSectionFrame frame = new PauseSectionFrame(layout, layout.icons.gameplay, "GAMEPLAY");
        GameplaySettings gameplay = GameSettings.gameplay();
        Table row = new Table();
        row.left();
        row.add(buildToggle(layout, "Notifications", gameplay.notificationsEnabled(), gameplay::setNotificationsEnabled))
            .left()
            .height(layout.toggles.rowHeight);
        row.add(buildToggle(layout, "Screen Shake", gameplay.screenShakeEnabled(), gameplay::setScreenShakeEnabled))
            .left()
            .height(layout.toggles.rowHeight)
            .padLeft(layout.toggles.itemSpacing);
        row.add(buildToggle(layout, "Debug Mode", gameplay.debugMode(), gameplay::setDebugMode))
            .left()
            .height(layout.toggles.rowHeight)
            .padLeft(layout.toggles.itemSpacing);
        row.add(buildToggle(layout, "Grid Overlay", gameplay.gridOverlayEnabled(), gameplay::setGridOverlayEnabled))
            .left()
            .height(layout.toggles.rowHeight)
            .padLeft(layout.toggles.itemSpacing);
        frame.body().add(row).growX().left();
        frame.body().row();
        frame.body().add(buildSpeedCounter(layout, gameplay))
            .growX()
            .left()
            .padTop(layout.toggles.speedRowGap);
        add(frame).growX().top();
    }

    private Table buildSpeedCounter(PauseLayout layout, GameplaySettings gameplay) {
        Table speedRow = new Table();
        speedRow.left();
        speedRow.add(TextSpec.of(layout.text.toggle).build("Game Speed"))
            .left()
            .padRight(layout.toggles.speedLabelGap);

        Slider slider = PauseControls.slider(
            layout,
            GameplaySettings.MIN_GAME_SPEED,
            GameplaySettings.MAX_GAME_SPEED,
            1f
        );
        slider.setValue(gameplay.gameSpeed());

        Label valueLabel = TextSpec.of(layout.text.toggle).build(formatSpeed(gameplay.gameSpeed()));
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int speed = Math.round(slider.getValue());
                gameplay.setGameSpeed(speed);
                slider.setProgrammaticChangeEvents(false);
                slider.setValue(gameplay.gameSpeed());
                slider.setProgrammaticChangeEvents(true);
                valueLabel.setText(formatSpeed(gameplay.gameSpeed()));
                GameSettings.save();
            }
        });

        speedRow.add(slider)
            .width(layout.toggles.speedSliderWidth)
            .height(layout.toggles.speedSliderHeight)
            .left();
        speedRow.add(valueLabel)
            .padLeft(layout.toggles.speedValueGap)
            .left();
        return speedRow;
    }

    private static String formatSpeed(int speed) {
        return "x" + speed;
    }

    private CheckBox buildToggle(PauseLayout layout, String title, boolean initial, BooleanConsumer consumer) {
        CheckBox box = PauseControls.toggle(layout, title);
        box.getImageCell().size(layout.toggles.checkboxSize);
        box.getLabelCell().padLeft(layout.toggles.labelGap);
        box.getLabel().setFontScale(layout.text.toggle.fontScale);
        box.getLabel().setColor(layout.text.toggle.color);
        box.setChecked(initial);
        box.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                consumer.accept(box.isChecked());
                GameSettings.save();
            }
        });
        return box;
    }

    private interface BooleanConsumer {
        void accept(boolean value);
    }
}
