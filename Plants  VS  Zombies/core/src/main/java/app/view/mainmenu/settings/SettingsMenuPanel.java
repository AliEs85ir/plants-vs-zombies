package app.view.mainmenu.settings;

import app.controller.menu.SettingsMenuController;
import app.view.ui.ScrollPanes;
import app.view.ui.Solid;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import game.audio.AudioManager;
import game.settings.GameSettings;

public class SettingsMenuPanel extends WidgetGroup {
    private final SettingsMenuConfig config;
    private final SettingsAudioSection audioSection;
    private final ScrollPane scrollPane;

    public SettingsMenuPanel(Skin skin) {
        this.config = SettingsMenuConfigLoader.load();
        setFillParent(false);
        setTransform(false);
        setTouchable(Touchable.childrenOnly);

        SettingsMenuController controller = new SettingsMenuController();
        audioSection = new SettingsAudioSection(config.audio, config.resetAudio, this::resetAudio);
        Table content = new Table();
        content.top().left();
        boolean first = true;
        for (String section : config.layout.orderedSections()) {
            Table actor = switch (section.trim().toLowerCase()) {
                case "difficulty" -> new SettingsDifficultySection(config.difficulty, controller);
                case "audio" -> audioSection;
                case "debug" -> new SettingsDebugSection(config.debug);
                default -> null;
            };
            if (actor == null) {
                continue;
            }
            if (!first) {
                addSectionSeparator(content);
            }
            content.add(actor).growX().top();
            content.row();
            first = false;
        }

        scrollPane = ScrollPanes.verticalHidden(content);
        addActor(scrollPane);
    }

    @Override
    public void layout() {
        float width = getWidth();
        float height = getHeight();
        float contentWidth = config.layout.contentWidth > 0f
            ? Math.min(config.layout.contentWidth, width - config.layout.paddingLeft - config.layout.paddingRight)
            : Math.max(80f, width - config.layout.paddingLeft - config.layout.paddingRight);
        int align = SettingsMenuConfig.align(config.layout.contentAlignment, Align.top | Align.center);
        float x = alignedX(width, contentWidth, align) + config.layout.paddingLeft + config.layout.offsetX;
        if ((align & Align.center) == Align.center && (align & Align.left) == 0 && (align & Align.right) == 0) {
            x = ((width - contentWidth) * 0.5f) + config.layout.offsetX;
        }
        float y = config.layout.paddingBottom + config.layout.offsetY;
        float paneHeight = Math.max(80f, height - config.layout.paddingTop - config.layout.paddingBottom);
        scrollPane.setBounds(x, y, Math.max(1f, contentWidth), paneHeight);
    }

    private void addSectionSeparator(Table content) {
        SettingsMenuConfig.SeparatorConfig separator = config.layout.separator;
        float extra = config.layout.sectionSpacing;
        if (separator != null && separator.enabled) {
            Image line = new Image(separatorDrawable(separator));
            content.add(line)
                .growX()
                .height(Math.max(1f, separator.height))
                .padTop(separator.topPadding)
                .padBottom(separator.bottomPadding);
            content.row();
            return;
        }
        content.add().growX().height(0f).padTop(extra);
        content.row();
    }

    private static Drawable separatorDrawable(SettingsMenuConfig.SeparatorConfig separator) {
        Color color = SettingsMenuConfig.color(separator.color, new Color(1f, 1f, 1f, 0.25f));
        return Solid.drawable(color, 1f, Math.max(1f, separator.height));
    }

    private void resetAudio() {
        AudioManager.resetToDefaults();
        GameSettings.save();
        audioSection.refresh();
    }

    private static float alignedX(float areaWidth, float itemWidth, int align) {
        if ((align & Align.right) != 0) {
            return areaWidth - itemWidth;
        }
        if ((align & Align.left) != 0) {
            return 0f;
        }
        return (areaWidth - itemWidth) * 0.5f;
    }
}
