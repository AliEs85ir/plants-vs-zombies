package app.view.greenhouse;

import app.controller.greenhouse.GreenhouseUiController;
import app.enums.greenhouse.GreenhouseInteractionState;
import app.enums.greenhouse.PotStatus;
import app.model.greenhouse.GreenhousePot;
import app.service.greenhouse.GreenhouseGameplaySettings;
import app.view.adventure.AdventureHud;
import app.view.greenhouse.config.GreenhouseConfig;
import app.view.greenhouse.config.GreenhouseConfigLoader;
import app.view.greenhouse.config.GreenhousePlantVisualsConfig;
import app.view.greenhouse.config.GreenhousePlantVisualsConfigLoader;
import app.view.mainmenu.MenuTextures;
import app.view.screen.ScreenManager;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import common.Result;
import game.component.AssetManager;
import plants_vs_zombies.PlantsVSZombies;

import java.util.ArrayList;
import java.util.List;

public final class GreenhousePanel extends WidgetGroup implements Disposable {
    private final PlantsVSZombies game;
    private final GreenhouseConfig config;
    private final GreenhousePlantVisualsConfig plantVisuals;
    private final GreenhouseUiController controller;
    private final GreenhouseMapCells mapCells;
    private final GreenhouseMapActor mapActor;
    private final List<GreenhouseCellView> cellViews = new ArrayList<>();
    private final ObjectMap<String, Texture> textures = new ObjectMap<>();
    private final Array<BitmapFont> ownedFonts = new Array<>();
    private final AdventureHud hud;
    private GreenhouseBillButton billButton;
    private GreenhouseMessageToast messageToast;
    private boolean built;

    public GreenhousePanel(PlantsVSZombies game) {
        this.game = game;
        this.config = GreenhouseConfigLoader.load();
        GreenhouseGameplaySettings.from(this.config);
        this.plantVisuals = GreenhousePlantVisualsConfigLoader.load();
        this.controller = new GreenhouseUiController();
        this.mapCells = new GreenhouseMapCells(config.mapPath, config.mapPixelWidth, config.mapPixelHeight);
        this.mapActor = new GreenhouseMapActor(mapCells.map(), mapCells.mapPixelWidth(), mapCells.mapPixelHeight());
        this.hud = new AdventureHud(game, () -> ScreenManager.showGameScreen(game), false);
        setFillParent(true);
        setTransform(false);
        setTouchable(Touchable.childrenOnly);
        build();
    }

    public boolean isShovelSelected() {
        return controller.getInteractionState() == GreenhouseInteractionState.SHOVEL_SELECTED;
    }

    public GreenhouseUiController controller() {
        return controller;
    }

    private void build() {
        if (built) {
            return;
        }
        built = true;
        mapActor.setZIndex(config.layers.mapDepth);
        addActor(mapActor);

        BitmapFont timerFont = resolveFont(config.timer.font, config.timer.fontFallback);
        BitmapFont actionFont = resolveFont(config.actionButton.font, config.actionButton.fontFallback);
        BitmapFont messageFont = resolveFont(config.messages.font, config.messages.fontFallback);
        BitmapFont billFont = resolveFont(config.timer.font, config.timer.fontFallback);

        Texture potTexture = texture(config.pot.assetPath);
        TextureRegion lockRegion = lockRegion();
        Texture questionTexture = texture(config.questionIcon.assetPath);
        Texture timerBg = texture(config.timer.backgroundPath);
        Texture timerIcon = texture(config.timer.iconPath);
        Texture actionBg = texture(config.actionButton.backgroundPath);
        Texture growingIcon = texture(config.actionButton.growingIconPath);
        Texture readyIcon = texture(config.actionButton.readyIconPath);

        for (GreenhouseMapCells.CellInfo cell : mapCells.cells()) {
            GreenhouseCellView view = new GreenhouseCellView(
                cell.row,
                cell.col,
                config,
                plantVisuals,
                potTexture,
                lockRegion,
                questionTexture,
                timerBg,
                timerIcon,
                actionBg,
                growingIcon,
                readyIcon,
                timerFont,
                actionFont
            );
            view.setOnPotClick(this::handlePotClick);
            view.setOnActionClick(this::handleActionClick);
            view.setOnQuestionClick(this::handleQuestionClick);
            cellViews.add(view);
            addActor(view);
        }

        billButton = new GreenhouseBillButton(
            config.bill,
            texture(config.bill.assetPath),
            texture(config.bill.speedIconPath),
            billFont
        );
        billButton.setAction(this::handleBillClick);
        billButton.setSpeedAction(this::handleBillSpeedClick);
        addActor(billButton);

        messageToast = new GreenhouseMessageToast(config.messages, messageFont);
        addActor(messageToast);

        addActor(hud);

        addCaptureListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == Input.Buttons.RIGHT) {
                    Result result = controller.handleRightClickCancel();
                    if (result.success()) {
                        showResult(result);
                    }
                    event.stop();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void layout() {
        float width = getWidth();
        float height = getHeight();
        mapActor.setBounds(0f, 0f, width, height);

        for (GreenhouseCellView view : cellViews) {
            GreenhouseMapCells.CellInfo info = findCell(view.row(), view.col());
            if (info == null) {
                continue;
            }
            GreenhouseConfig.PotOffsetConfig offset = config.offsetFor(info.row, info.col);
            float stageX = (info.mapCenterX / mapCells.mapPixelWidth()) * width
                + offset.offsetX
                + config.pot.offsetX;
            float stageY = (info.mapCenterY / mapCells.mapPixelHeight()) * height
                + offset.offsetY
                + config.pot.offsetY;
            view.setPosition(stageX - view.getOriginX(), stageY - view.getOriginY());
        }

        if (billButton != null) {
            billButton.layoutInParent(width, height);
            billButton.setZIndex(Math.max(config.layers.hudDepth, config.bill.z));
        }
        if (messageToast != null) {
            messageToast.layoutInParent(width, height);
            messageToast.setZIndex(config.layers.messageDepth);
        }
        if (hud != null) {
            hud.setBounds(0f, 0f, width, height);
            hud.setZIndex(getChildren().size);
        }
        mapActor.setZIndex(config.layers.mapDepth);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        refresh();
    }

    private void refresh() {
        for (GreenhouseCellView view : cellViews) {
            GreenhousePot pot = controller.getGreenhouse().getPot(view.col(), view.row());
            int cost = controller.growthCostFor(view.col(), view.row());
            view.refresh(pot, cost);
        }
        if (billButton != null) {
            billButton.refresh(
                controller.getBillAvailability(),
                controller.getBillService().remainingCooldown()
            );
        }
    }

    private void handleBillClick() {
        if (controller.getInteractionState() == GreenhouseInteractionState.SHOVEL_SELECTED) {
            showResult(controller.cancelBill());
            return;
        }
        showResult(controller.selectBill());
    }

    private void handleBillSpeedClick() {
        showResult(controller.debugSkipBillCooldown());
    }

    private void handlePotClick(GreenhouseCellView cell) {
        if (controller.getInteractionState() != GreenhouseInteractionState.SHOVEL_SELECTED) {
            return;
        }
        showResult(controller.plantWithBill(cell.col(), cell.row()));
    }

    private void handleActionClick(GreenhouseCellView cell) {
        GreenhousePot pot = controller.getGreenhouse().getPot(cell.col(), cell.row());
        if (pot == null) {
            return;
        }
        if (pot.isPlantReady() || pot.getPotStatus() == PotStatus.READY) {
            showResult(controller.collect(cell.col(), cell.row()));
        } else if (pot.getPotStatus() == PotStatus.GROWING || pot.getPlant() != null) {
            showResult(controller.instantGrow(cell.col(), cell.row()));
        }
    }

    private void handleQuestionClick(GreenhouseCellView cell, String fallbackText) {
        Result result = controller.onQuestionClick(cell.col(), cell.row());
        String text = result.message() == null || result.message().isBlank() ? fallbackText : result.message();
        if (result.success()) {
            cell.showQuestionBubble(text);
        } else {
            showResult(result);
        }
    }

    private void showResult(Result result) {
        if (result == null || result.message() == null || result.message().isBlank()) {
            return;
        }
        if (result.success()) {
            messageToast.showSuccess(result.message());
        } else {
            messageToast.showError(result.message());
        }
    }

    private GreenhouseMapCells.CellInfo findCell(int row, int col) {
        for (GreenhouseMapCells.CellInfo info : mapCells.cells()) {
            if (info.row == row && info.col == col) {
                return info;
            }
        }
        return null;
    }

    private TextureRegion lockRegion() {
        if (AssetManager.textureBank == null) {
            return null;
        }
        try {
            return AssetManager.textureBank.region(config.lockIcon.resourceId);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private Texture texture(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        String key = path.replace('\\', '/');
        if (textures.containsKey(key)) {
            return textures.get(key);
        }
        Texture loaded = MenuTextures.load(key, "GreenhousePanel");
        textures.put(key, loaded);
        return loaded;
    }

    private BitmapFont resolveFont(String name, String fallback) {
        BitmapFont font = tryFont(name);
        if (font != null) {
            return font;
        }
        if (name != null) {
            font = tryFont(name.replace('-', '_'));
            if (font != null) {
                return font;
            }
        }
        font = tryFont(fallback);
        if (font != null) {
            return font;
        }
        if (AssetManager.skin != null) {
            return AssetManager.font(AssetManager.DEFAULT_FONT);
        }
        BitmapFont created = new BitmapFont();
        ownedFonts.add(created);
        return created;
    }

    private static BitmapFont tryFont(String name) {
        if (name == null || name.isBlank() || AssetManager.skin == null) {
            return null;
        }
        if (AssetManager.skin.has(name, BitmapFont.class)) {
            BitmapFont font = AssetManager.skin.getFont(name);
            font.setUseIntegerPositions(false);
            return font;
        }
        return null;
    }

    @Override
    public void dispose() {
        if (hud != null) {
            hud.dispose();
        }
        for (Texture texture : textures.values()) {
            if (texture != null) {
                texture.dispose();
            }
        }
        textures.clear();
        for (BitmapFont font : ownedFonts) {
            font.dispose();
        }
        ownedFonts.clear();
        if (mapActor != null) {
            mapActor.dispose();
        }
        if (mapCells != null) {
            mapCells.dispose();
        }
        cellViews.clear();
    }
}
