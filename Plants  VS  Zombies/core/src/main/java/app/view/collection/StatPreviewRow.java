package app.view.collection;

import app.view.collection.config.CollectionConfig;
import app.view.ui.UiTheme;
import app.view.ui.text.TextAlign;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import game.component.AssetManager;
import game.entity.plant.upgrade.PlantStatPreview;

public final class StatPreviewRow extends Table {
    private final CollectionConfig config;
    private final Image icon = new Image();
    private final Label titleLabel;
    private final Label currentLabel;
    private final Label separatorLabel;
    private final Label nextLabel;
    private final Label plainLabel;
    private final Table valueRow = new Table();

    public StatPreviewRow(CollectionConfig config) {
        this.config = config;
        icon.setScaling(Scaling.fit);
        titleLabel = text(
            config.colors.resolve(config.colors.statTitle, UiTheme.TEXT_SECONDARY),
            config.details.statTitleScale,
            "");
        titleLabel.setAlignment(Align.left);
        currentLabel = text(config.colors.resolve(config.colors.currentValue, UiTheme.TEXT_STAT),
            config.details.valueScale, "");
        currentLabel.setAlignment(Align.left);
        separatorLabel = text(config.colors.resolve(config.colors.separator, UiTheme.TEXT_MUTED),
            config.details.valueScale, config.details.previewSeparator);
        nextLabel = text(config.colors.resolve(config.colors.nextValue, UiTheme.TEXT_SUCCESS),
            config.details.valueScale, "");
        plainLabel = text(config.colors.resolve(config.colors.currentValue, UiTheme.TEXT_STAT),
            config.details.valueScale, "");
        plainLabel.setAlignment(Align.left);
        separatorLabel.setFontScale(config.details.previewSeparatorScale);

        top().left();
        valueRow.left();

        Table textColumn = new Table();
        textColumn.left().top();
        textColumn.add(titleLabel).left().growX().row();
        textColumn.add(valueRow).left().growX();

        add(icon).size(config.details.statIconSize).padRight(config.details.statIconPad).top();
        add(textColumn).left().growX().top();
    }

    public void bindPreview(CollectionConfig.StatConfig statConfig, PlantStatPreview preview, String fallbackTitle) {
        String title = statConfig != null && statConfig.title != null && !statConfig.title.isBlank()
            ? statConfig.title
            : fallbackTitle;
        applyIcon(statConfig == null ? "" : statConfig.icon);
        titleLabel.setText(title == null ? "" : title);
        if (preview != null && preview.changed()) {
            showUpgradeValue(preview.currentValue(), preview.nextValue());
        } else {
            showPlainValue(preview == null ? "" : preview.currentValue());
        }
        setVisible(true);
    }

    public void bindPlain(CollectionConfig.StatConfig statConfig, String title, String value, String iconId) {
        String resolvedTitle = statConfig != null && statConfig.title != null && !statConfig.title.isBlank()
            ? statConfig.title
            : title;
        String resolvedIcon = (statConfig != null && statConfig.icon != null && !statConfig.icon.isBlank())
            ? statConfig.icon
            : iconId;
        applyIcon(resolvedIcon);
        titleLabel.setText(resolvedTitle == null ? "" : resolvedTitle);
        showPlainValue(value);
        setVisible(true);
    }

    private void showPlainValue(String value) {
        valueRow.clearChildren();
        valueRow.left();
        plainLabel.setText(value == null ? "" : value);
        valueRow.add(plainLabel).left();
    }

    private void showUpgradeValue(String current, String next) {
        valueRow.clearChildren();
        valueRow.left();
        currentLabel.setText(current == null ? "" : current);
        nextLabel.setText(next == null ? "" : next);
        separatorLabel.setText(config.details.previewSeparator);
        valueRow.add(currentLabel).left();
        valueRow.add(separatorLabel)
            .padLeft(config.details.previewValueGap)
            .padRight(config.details.previewValueGap)
            .left();
        valueRow.add(nextLabel).left();
    }

    private void applyIcon(String iconId) {
        TextureRegion region = (iconId == null || iconId.isBlank() || AssetManager.textureBank == null)
            ? null
            : AssetManager.textureBank.region(iconId);
        if (region == null) {
            icon.setDrawable(null);
            icon.setVisible(false);
            return;
        }
        icon.setVisible(true);
        icon.setDrawable(new TextureRegionDrawable(region));
    }

    private static Label text(Color color, float scale, String value) {
        return TextSpec.of(UiTheme.LBL_DEFAULT)
            .align(TextAlign.LEFT)
            .color(color)
            .scale(scale)
            .build(value);
    }
}
