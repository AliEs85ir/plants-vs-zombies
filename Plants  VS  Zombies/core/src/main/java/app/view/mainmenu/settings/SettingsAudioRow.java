package app.view.mainmenu.settings;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import game.audio.AudioCategory;
import game.audio.AudioManager;
import game.settings.GameSettings;

public final class SettingsAudioRow extends Table {
    private final Slider slider;
    private final CheckBox mute;
    private final VolumeBinding binding;

    public SettingsAudioRow(SettingsMenuConfig.AudioConfig config, String title, VolumeBinding binding) {
        this.binding = binding;
        left();
        Label label = new Label(title, new Label.LabelStyle(
            SettingsUiFonts.resolve(config.titleFontRow, config.titleFontRowFallback),
            SettingsMenuConfig.color(config.titleColorRow, Color.valueOf("F2E7C4"))
        ));
        label.setFontScale(config.titleFontRowSize);
        label.setAlignment(SettingsMenuConfig.textAlign(config.titleAlignmentRow));
        add(label).width(config.titleWidth).left().padRight(config.titleSliderGap);

        slider = SettingsControls.slider(
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
        slider.setValue(binding.volume());
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                binding.setVolume(slider.getValue());
                GameSettings.save();
            }
        });
        add(slider).width(config.sliderWidth).height(config.sliderHeight).left();

        mute = SettingsControls.checkBox(
            config.muteLabel,
            config.muteStyle,
            SettingsMenuConfig.color(config.muteHoverColor, Color.valueOf("FFF4C0"))
        );
        mute.getLabel().setStyle(new Label.LabelStyle(
            SettingsUiFonts.resolve(config.muteLabelFont, "BRIANNETOD"),
            SettingsMenuConfig.color(config.muteLabelColor, Color.valueOf("E4DCC4"))
        ));
        mute.getLabel().setFontScale(config.muteLabelFontSize);
        if (mute.getImageCell() != null) {
            mute.getImageCell().size(config.muteSize);
        }
        mute.setChecked(binding.muted());
        mute.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                binding.setMuted(mute.isChecked());
                GameSettings.save();
            }
        });
        add(mute).padLeft(config.sliderMuteGap).height(Math.max(config.muteSize, config.rowHeight)).left();
    }

    public void refresh() {
        slider.setProgrammaticChangeEvents(false);
        mute.setProgrammaticChangeEvents(false);
        slider.setValue(binding.volume());
        mute.setChecked(binding.muted());
        slider.setProgrammaticChangeEvents(true);
        mute.setProgrammaticChangeEvents(true);
    }

    public interface VolumeBinding {
        float volume();

        void setVolume(float volume);

        boolean muted();

        void setMuted(boolean muted);
    }

    public static VolumeBinding masterBinding() {
        return new VolumeBinding() {
            @Override
            public float volume() {
                return AudioManager.settings().masterVolume();
            }

            @Override
            public void setVolume(float volume) {
                AudioManager.setMasterVolume(volume);
            }

            @Override
            public boolean muted() {
                return AudioManager.settings().muted();
            }

            @Override
            public void setMuted(boolean muted) {
                AudioManager.setMuted(muted);
            }
        };
    }

    public static VolumeBinding categoryBinding(AudioCategory category) {
        return new VolumeBinding() {
            @Override
            public float volume() {
                return AudioManager.settings().categoryVolume(category);
            }

            @Override
            public void setVolume(float volume) {
                AudioManager.setCategoryVolume(category, volume);
            }

            @Override
            public boolean muted() {
                return AudioManager.isCategoryMuted(category);
            }

            @Override
            public void setMuted(boolean muted) {
                AudioManager.setCategoryMuted(category, muted);
            }
        };
    }
}
