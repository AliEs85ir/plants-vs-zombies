package app.view.collection;

import app.controller.collection.CollectionController;
import app.enums.plant.PlantCategory;
import app.service.collection.CollectionFilterService;
import app.service.collection.CollectionFilterState;
import app.service.collection.CollectionUnlockFilter;
import app.view.collection.config.CollectionConfig;
import app.view.collection.data.CollectionData;
import app.view.shop.ShopUiFactory;
import app.view.ui.UiTheme;
import app.view.ui.UpwardSelectBox;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import game.component.AssetManager;

import java.util.List;

/**
 * Fixed filter chrome for Collection Plants/Zombies grids.
 * Lives outside the ScrollPane so scrolling cards does not move this panel.
 */
public final class CollectionFilterPanel extends Table {
    public interface Listener {
        void onApply();
    }

    private final CollectionConfig config;
    private final CollectionController controller;
    private final CollectionTextureCache textures;
    private final Listener listener;

    private Image filterIcon;
    private UpwardSelectBox<String> categoryBox;
    private UpwardSelectBox<String> unlockBox;
    private CheckBox upgradeableBox;
    private TextButton applyButton;
    private CollectionData.Tab tab = CollectionData.Tab.PLANTS;
    private final Array<String> categoryIds = new Array<>();
    private final Array<CollectionUnlockFilter> unlockValues = new Array<>();

    public CollectionFilterPanel(
        CollectionConfig config,
        CollectionController controller,
        CollectionTextureCache textures,
        Listener listener
    ) {
        this.config = config;
        this.controller = controller;
        this.textures = textures;
        this.listener = listener;
        setTransform(false);
        setTouchable(Touchable.childrenOnly);
        rebuildChrome();
        bindTab(CollectionData.Tab.PLANTS);
    }

    public void bindTab(CollectionData.Tab tab) {
        this.tab = tab == null ? CollectionData.Tab.PLANTS : tab;
        rebuildChrome();
        rebuildControls();
        syncFromDraft();
    }

    public CollectionData.Tab tab() {
        return tab;
    }

    private void rebuildChrome() {
        clearChildren();
        CollectionConfig.FilterPanelConfig panel = config.filterPanel;
        Drawable background = ShopUiFactory.tintedSkinDrawable(
            panel.background,
            UiTheme.BG_TAB,
            panel.alpha
        );
        if (background != null) {
            setBackground(background);
        }
        pad(panel.padTop, panel.padLeft, panel.padBottom, panel.padRight);

        Table row = new Table();
        row.defaults().center();

        Table left = new Table();
        left.defaults().padRight(panel.controlSpacing).center();
        left.padLeft(panel.contentPadLeft);

        filterIcon = createIcon();
        if (filterIcon != null) {
            left.add(filterIcon)
                .size(panel.iconSize, panel.iconSize)
                .padLeft(panel.iconPadding)
                .padRight(panel.iconPadding + panel.iconOffsetX)
                .padBottom(panel.iconOffsetY);
        }

        categoryBox = createSelectBox();
        unlockBox = createSelectBox();
        left.add(categoryBox).size(panel.dropdownWidth, panel.dropdownHeight);
        left.add(unlockBox).size(panel.dropdownWidth, panel.dropdownHeight);

        upgradeableBox = createCheckbox(config.labels.filterUpgradeableOnly);
        upgradeableBox.setVisible(false);
        left.add(upgradeableBox).padLeft(panel.checkboxLabelSpacing).left();

        applyButton = createApplyButton();
        row.add(left).left().expandX();
        row.add(applyButton)
            .size(panel.applyButtonWidth, panel.applyButtonHeight)
            .padLeft(panel.applyButtonSpacing)
            .padRight(panel.applyPadRight)
            .right();

        // Panel stays screen-centered via place(); inner content is left/right aligned.
        add(row).growX();
    }

    private void rebuildControls() {
        boolean plants = tab == CollectionData.Tab.PLANTS;
        CollectionFilterState state = plants ? controller.plantFilterState() : controller.zombieFilterState();
        CollectionConfig.FilterPanelConfig panel = config.filterPanel;

        categoryIds.clear();
        Array<String> categoryLabels = new Array<>();
        categoryLabels.add(config.labels.filterAllCategories);
        categoryIds.add(null);
        if (plants) {
            for (PlantCategory category : PlantCategory.values()) {
                categoryIds.add(category.name());
                categoryLabels.add(category.getLabel());
            }
        } else {
            List<String> rarities = CollectionFilterService.zombieCategoryIds(controller.zombies());
            for (String rarity : rarities) {
                categoryIds.add(rarity);
                categoryLabels.add(rarity);
            }
        }
        categoryBox.setItems(categoryLabels);

        unlockValues.clear();
        Array<String> unlockLabels = new Array<>();
        if (plants) {
            addUnlock(unlockLabels, CollectionUnlockFilter.ALL, config.labels.filterUnlockAll);
            addUnlock(unlockLabels, CollectionUnlockFilter.UNLOCKED, config.labels.filterUnlockUnlocked);
            addUnlock(unlockLabels, CollectionUnlockFilter.LOCKED, config.labels.filterUnlockLocked);
        } else {
            addUnlock(unlockLabels, CollectionUnlockFilter.ALL, config.labels.filterDiscoverAll);
            addUnlock(unlockLabels, CollectionUnlockFilter.DISCOVERED, config.labels.filterDiscoverDiscovered);
            addUnlock(unlockLabels, CollectionUnlockFilter.UNDISCOVERED, config.labels.filterDiscoverUndiscovered);
        }
        unlockBox.setItems(unlockLabels);

        boolean showUpgrade = plants && state.supportsUpgradeable();
        upgradeableBox.setVisible(showUpgrade);
        upgradeableBox.setDisabled(!showUpgrade);
        if (!showUpgrade && upgradeableBox.getParent() != null) {
            upgradeableBox.remove();
        }
        categoryBox.setSize(panel.dropdownWidth, panel.dropdownHeight);
        unlockBox.setSize(panel.dropdownWidth, panel.dropdownHeight);
    }

    private void addUnlock(Array<String> labels, CollectionUnlockFilter value, String label) {
        unlockValues.add(value);
        labels.add(label);
    }

    private void syncFromDraft() {
        CollectionFilterState state = tab == CollectionData.Tab.PLANTS
            ? controller.plantFilterState()
            : controller.zombieFilterState();

        int categoryIndex = 0;
        String draftCategory = state.getDraftCategoryId();
        for (int i = 0; i < categoryIds.size; i++) {
            String id = categoryIds.get(i);
            if (draftCategory == null && id == null) {
                categoryIndex = i;
                break;
            }
            if (draftCategory != null && draftCategory.equals(id)) {
                categoryIndex = i;
                break;
            }
        }
        categoryBox.setSelectedIndex(Math.max(0, Math.min(categoryIndex, categoryBox.getItems().size - 1)));

        CollectionUnlockFilter draftUnlock = state.getDraftUnlockFilter();
        int unlockIndex = 0;
        for (int i = 0; i < unlockValues.size; i++) {
            if (unlockValues.get(i) == draftUnlock) {
                unlockIndex = i;
                break;
            }
        }
        unlockBox.setSelectedIndex(Math.max(0, Math.min(unlockIndex, unlockBox.getItems().size - 1)));

        upgradeableBox.setChecked(state.isDraftUpgradeableOnly());
        wireDraftListeners(state);
    }

    private void wireDraftListeners(CollectionFilterState state) {
        categoryBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int index = categoryBox.getSelectedIndex();
                String id = index >= 0 && index < categoryIds.size ? categoryIds.get(index) : null;
                state.setDraftCategoryId(id);
            }
        });
        unlockBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int index = unlockBox.getSelectedIndex();
                CollectionUnlockFilter filter = index >= 0 && index < unlockValues.size
                    ? unlockValues.get(index)
                    : CollectionUnlockFilter.ALL;
                state.setDraftUnlockFilter(filter);
            }
        });
        upgradeableBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                state.setDraftUpgradeableOnly(upgradeableBox.isChecked());
            }
        });
        applyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (tab == CollectionData.Tab.PLANTS) {
                    controller.applyPlantFilters();
                } else {
                    controller.applyZombieFilters();
                }
                if (listener != null) {
                    listener.onApply();
                }
            }
        });
    }

    private Image createIcon() {
        Texture texture = textures.get(config.filterPanel.iconPath);
        if (texture == null) {
            return null;
        }
        Image image = new Image(new TextureRegionDrawable(new TextureRegion(texture)));
        image.setTouchable(Touchable.disabled);
        return image;
    }

    private UpwardSelectBox<String> createSelectBox() {
        CollectionConfig.FilterPanelConfig panel = config.filterPanel;
        SelectBox.SelectBoxStyle style = new SelectBox.SelectBoxStyle();
        if (AssetManager.skin != null) {
            try {
                SelectBox.SelectBoxStyle base = AssetManager.skin.get(SelectBox.SelectBoxStyle.class);
                if (base != null) {
                    style = new SelectBox.SelectBoxStyle(base);
                }
            } catch (RuntimeException ignored) {
            }
        }
        UpwardSelectBox<String> box = new UpwardSelectBox<>(style, panel.popupOffset);
        box.setAlignment(Align.center);
        box.setSize(panel.dropdownWidth, panel.dropdownHeight);
        return box;
    }

    private CheckBox createCheckbox(String label) {
        CheckBox.CheckBoxStyle style = new CheckBox.CheckBoxStyle();
        if (AssetManager.skin != null) {
            try {
                CheckBox.CheckBoxStyle base = AssetManager.skin.get(CheckBox.CheckBoxStyle.class);
                if (base != null) {
                    style = new CheckBox.CheckBoxStyle(base);
                }
            } catch (RuntimeException ignored) {
            }
        }
        if (style.font == null && AssetManager.skin != null) {
            try {
                style.font = AssetManager.skin.getFont("default-font");
            } catch (RuntimeException ignored) {
            }
        }
        CheckBox box = new CheckBox(label == null ? "" : label, style);
        box.getLabel().setColor(UiTheme.TEXT_PRIMARY);
        if (config.filterPanel.checkboxSize > 0f && box.getStyle().checkboxOn != null) {
            // visual size driven mainly by drawable mins; pad for hit area
            box.pad(2f);
        }
        return box;
    }

    private TextButton createApplyButton() {
        CollectionConfig.FilterPanelConfig panel = config.filterPanel;
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        if (AssetManager.skin != null) {
            try {
                TextButton.TextButtonStyle base = AssetManager.skin.get("green", TextButton.TextButtonStyle.class);
                if (base == null) {
                    base = AssetManager.skin.get(TextButton.TextButtonStyle.class);
                }
                if (base != null) {
                    style = new TextButton.TextButtonStyle(base);
                }
            } catch (RuntimeException ignored) {
            }
        }
        if (style.font == null && AssetManager.skin != null) {
            try {
                style.font = AssetManager.skin.getFont("default-font");
            } catch (RuntimeException ignored) {
            }
        }
        TextButton button = new TextButton(config.labels.filterApply, style);
        button.setSize(panel.applyButtonWidth, panel.applyButtonHeight);
        return button;
    }

    public void place(float parentWidth, float parentHeight) {
        CollectionConfig.FilterPanelConfig panel = config.filterPanel;
        float width = panel.width > 0f ? panel.width : Math.min(parentWidth * 0.92f, 920f);
        float height = panel.height > 0f ? panel.height : 64f;
        setSize(width, height);
        float x = (parentWidth - width) * 0.5f + panel.xOffset;
        float y = panel.bottomOffset;
        setPosition(x, y);
    }
}
