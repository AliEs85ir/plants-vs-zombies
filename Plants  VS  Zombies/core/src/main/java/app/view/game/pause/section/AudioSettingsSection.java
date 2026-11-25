package app.view.game.pause.section;

import app.view.game.pause.PauseControls;
import app.view.game.pause.PauseLayout;
import app.view.game.pause.PauseSectionFrame;
import app.view.ui.button.ActionButton;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import game.audio.AudioCategory;
import game.audio.AudioManager;
import game.audio.AudioSettings;
import game.settings.GameSettings;

import java.util.ArrayList;
import java.util.List;

public class AudioSettingsSection extends Table {
    private final PauseLayout layout;
    private final List<ChannelRow> rows = new ArrayList<>();

    public AudioSettingsSection(PauseLayout layout) {
        this.layout = layout;
        top();
        PauseSectionFrame frame = new PauseSectionFrame(layout, layout.icons.audio, "AUDIO");
        Table grid = new Table();
        AudioCategory[] categories = AudioCategory.values();
        int columns = Math.max(1, layout.audio.columns);
        for (int i = 0; i < categories.length; i++) {
            ChannelRow row = buildChannel(categories[i]);
            rows.add(row);
            var cell = grid.add(row.root)
                .left()
                .expandX()
                .height(layout.audio.rowHeight);
            if (i % columns != 0) {
                cell.padLeft(layout.audio.columnSpacing);
            }
            if (i >= columns) {
                cell.padTop(layout.audio.rowSpacing);
            }
            if ((i + 1) % columns == 0) {
                grid.row();
            }
        }
        ActionButton reset = PauseControls.resetButton(layout, this::resetToDefault);
        var resetCell = grid.add(reset)
            .width(layout.buttons.reset.width)
            .height(layout.buttons.reset.height)
            .left();
        if (categories.length % columns != 0) {
            resetCell.padLeft(layout.audio.columnSpacing);
        }
        resetCell.padTop(layout.audio.rowSpacing);
        frame.body().add(grid).growX().left().top();
        add(frame).growX().top();
    }

    public void refresh() {
        AudioSettings settings = AudioManager.settings();
        for (ChannelRow row : rows) {
            row.slider.setProgrammaticChangeEvents(false);
            row.mute.setProgrammaticChangeEvents(false);
            row.slider.setValue(settings.categoryVolume(row.category));
            row.mute.setChecked(settings.isCategoryMuted(row.category));
            row.slider.setProgrammaticChangeEvents(true);
            row.mute.setProgrammaticChangeEvents(true);
        }
    }

    private void resetToDefault() {
        AudioManager.resetToDefaults();
        GameSettings.save();
        refresh();
    }

    private ChannelRow buildChannel(AudioCategory category) {
        AudioSettings settings = AudioManager.settings();
        Table root = new Table();
        root.left();
        root.add(TextSpec.of(layout.text.channel).build(labelOf(category)))
            .width(layout.audio.labelWidth)
            .left()
            .padRight(layout.audio.labelSliderGap);

        Slider slider = PauseControls.slider(layout);
        slider.setValue(settings.categoryVolume(category));
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.setCategoryVolume(category, slider.getValue());
                GameSettings.save();
            }
        });
        root.add(slider)
            .width(layout.audio.sliderWidth)
            .height(layout.audio.sliderHeight)
            .left();

        CheckBox mute = PauseControls.checkBox(layout, layout.audio.muteLabel);
        mute.getLabel().setFontScale(layout.text.mute.fontScale);
        mute.getLabel().setColor(layout.text.mute.color);
        mute.setChecked(settings.isCategoryMuted(category));
        mute.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.setCategoryMuted(category, mute.isChecked());
                GameSettings.save();
            }
        });
        root.add(mute)
            .padLeft(layout.audio.muteGap)
            .height(layout.audio.checkboxSize)
            .left();

        ChannelRow row = new ChannelRow();
        row.category = category;
        row.root = root;
        row.slider = slider;
        row.mute = mute;
        return row;
    }

    private static String labelOf(AudioCategory category) {
        return switch (category) {
            case MUSIC -> "Music";
            case SFX -> "SFX";
            case VOICE -> "Voice";
            case UI -> "UI";
        };
    }

    private static final class ChannelRow {
        private AudioCategory category;
        private Table root;
        private Slider slider;
        private CheckBox mute;
    }
}
