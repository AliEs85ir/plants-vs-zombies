package app.view.game.pause.section;

import app.view.game.pause.PauseLayout;
import app.view.game.pause.PauseSectionFrame;
import app.view.ui.SkinPanel;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import game.component.AssetManager;
import game.world.GameWorld;
import game.world.chapter.Chapter;

public class GameInformationSection extends Table {
    private final PauseLayout layout;
    private Label plantsValue;
    private Label waveValue;
    private Label zombiesValue;
    private Label plantsUsedValue;
    private Label mowersValue;

    public GameInformationSection(PauseLayout layout) {
        this.layout = layout;
        top();
        PauseSectionFrame frame = new PauseSectionFrame(layout, layout.icons.status, "GAME STATUS");
        Table fields = new Table();
        plantsValue = addField(fields, layout.stats.plantsIcon, "Plants", true);
        waveValue = addField(fields, layout.stats.waveIcon, "Wave", false);
        zombiesValue = addField(fields, layout.stats.zombiesIcon, "Zombies", false);
        plantsUsedValue = addField(fields, layout.stats.plantsUsedIcon, "Planted", false);
        mowersValue = addField(fields, layout.stats.mowersIcon, "Mowers", false);
        frame.body().add(fields).growX().left();
        add(frame).growX().top();
    }

    public void refresh(GameWorld world) {
        if (world == null) {
            plantsValue.setText("0");
            waveValue.setText("0 / 0");
            zombiesValue.setText("0");
            plantsUsedValue.setText("0");
            mowersValue.setText("0 / 0");
            return;
        }
        Chapter chapter = world.getCurrentChapter();
        int totalWaves = chapter == null ? 0 : chapter.getTotalWaves();
        plantsValue.setText(String.valueOf(world.getAlivePlantCount()));
        waveValue.setText(world.getCurrentWaveNumber() + " / " + totalWaves);
        zombiesValue.setText(String.valueOf(world.getTotalDeadZombies()));
        plantsUsedValue.setText(String.valueOf(world.getTotalPlantsPlanted()));
        mowersValue.setText(world.getUsedLawnMowerCount() + " / " + world.getTotalLawnMowerCount());
    }

    private Label addField(Table fields, String iconId, String title, boolean first) {
        SkinPanel chip = new SkinPanel(layout.backgrounds.statField);
        chip.pad(0f, layout.stats.chipPadX, 0f, layout.stats.chipPadX);
        Image icon = icon(iconId);
        if (icon != null) {
            chip.add(icon).size(layout.stats.iconSize).padRight(layout.stats.iconTextGap);
        }
        chip.add(TextSpec.of(layout.text.statLabel).build(title))
            .padRight(layout.stats.labelValueGap);
        Label value = TextSpec.of(layout.text.statValue).color(layout.text.infoColor).build("0");
        chip.add(value).left();
        var cell = fields.add(chip).height(layout.stats.fieldHeight).left().expandX();
        if (!first) {
            cell.padLeft(layout.stats.fieldSpacing);
        }
        return value;
    }

    private Image icon(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        TextureRegion region = AssetManager.textureBank.region(id);
        if (region == null) {
            return null;
        }
        return new Image(new TextureRegionDrawable(region));
    }
}
