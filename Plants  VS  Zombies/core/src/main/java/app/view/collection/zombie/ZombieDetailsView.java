package app.view.collection.zombie;

import app.view.collection.CollectionIdlePreview;
import app.view.collection.CollectionStatTable;
import app.view.collection.StatPreviewRow;
import app.view.collection.config.CollectionConfig;
import app.view.collection.data.CollectionData;
import app.view.ui.UiTheme;
import app.view.ui.text.TextAlign;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import game.component.AssetManager;

import java.util.ArrayList;
import java.util.List;

public final class ZombieDetailsView extends Table {
    public interface Listener {
        void onPrevious();

        void onNext();
    }

    private final CollectionConfig config;
    private final Listener listener;
    private final CollectionIdlePreview preview;
    private final Label nameLabel;
    private final Label descriptionLabel;
    private final Table statsTable;

    public ZombieDetailsView(CollectionConfig config, Listener listener) {
        this.config = config;
        this.listener = listener;
        setFillParent(true);
        preview = new CollectionIdlePreview(config);
        nameLabel = TextSpec.of(UiTheme.LBL_DEFAULT)
            .align(TextAlign.CENTER)
            .color(config.colors.resolve(config.colors.title, UiTheme.ACCENT))
            .scale(config.details.titleScale)
            .build("");
        nameLabel.setAlignment(Align.center);
        descriptionLabel = TextSpec.of(UiTheme.LBL_DEFAULT)
            .align(TextAlign.TOP_LEFT)
            .color(config.colors.resolve(config.colors.body, UiTheme.TEXT_PRIMARY))
            .scale(config.details.bodyScale)
            .wrap(true)
            .build("");
        descriptionLabel.setAlignment(Align.topLeft);
        statsTable = new Table();
        statsTable.top().left();

        ImageButton previousButton = imageButton(config.details.arrowPreviousId, config.details.arrowPreviousDownId);
        ImageButton nextButton = imageButton(config.details.arrowNextId, config.details.arrowNextDownId);
        previousButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (listener != null) {
                    listener.onPrevious();
                }
            }
        });
        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (listener != null) {
                    listener.onNext();
                }
            }
        });

        top().center();
        pad(config.details.padTop, config.details.padLeft, config.details.padBottom, config.details.padRight);
        add(nameLabel).expandX().center().padBottom(config.details.titlePad).row();

        Table left = new Table();
        left.top().center();
        left.add(preview).size(config.details.previewWidth, config.details.previewHeight);

        Table right = new Table();
        right.top().left();
        right.add(statsTable)
            .left()
            .width(config.details.infoWidth)
            .padTop(config.details.statsPadTop)
            .row();
        right.add(descriptionLabel)
            .left()
            .width(config.details.infoWidth)
            .height(Math.max(1, config.details.zombieDescriptionLines) * config.details.descriptionLineHeight)
            .padTop(config.details.descriptionPadTop)
            .row();

        Table middle = new Table();
        middle.center().top();
        middle.add(previousButton).size(config.details.arrowSize).padRight(config.details.arrowPad);
        middle.add(left).top().padRight(config.details.columnGap);
        middle.add(right).top();
        middle.add(nextButton).size(config.details.arrowSize).padLeft(config.details.arrowPad);
        add(middle).expand().center();
    }

    public void bind(CollectionData.ZombieDetailsModel model) {
        if (model == null) {
            setVisible(false);
            return;
        }
        setVisible(true);
        preview.showZombie(model.alias, model.seen, model.detailsBackgroundId);
        nameLabel.setText(model.displayName);
        descriptionLabel.setText(model.description);
        List<StatPreviewRow> rows = new ArrayList<>();
        if (model.seen) {
            List<CollectionConfig.StatConfig> configs = config.zombieStats == null
                ? List.of()
                : config.zombieStats;
            for (CollectionConfig.StatConfig statConfig : configs) {
                if (statConfig == null || statConfig.id == null) {
                    continue;
                }
                String value = switch (statConfig.id) {
                    case "hp" -> model.hp;
                    case "speed" -> model.speed;
                    default -> "";
                };
                if (value == null || value.isBlank()) {
                    continue;
                }
                StatPreviewRow row = new StatPreviewRow(config);
                row.bindPlain(statConfig, statConfig.title, value, statConfig.icon);
                rows.add(row);
            }
        }
        CollectionStatTable.fill(statsTable, config, rows);
    }

    private static ImageButton imageButton(String normalId, String hoverId) {
        TextureRegion normal = AssetManager.textureBank == null ? null : AssetManager.textureBank.region(normalId);
        TextureRegion hover = AssetManager.textureBank == null ? null : AssetManager.textureBank.region(hoverId);
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        if (normal != null) {
            style.imageUp = new TextureRegionDrawable(normal);
        }
        if (hover != null) {
            style.imageOver = new TextureRegionDrawable(hover);
            style.imageDown = new TextureRegionDrawable(hover);
        }
        ImageButton button = new ImageButton(style);
        button.setName("menu-collection-nav");
        return button;
    }
}
