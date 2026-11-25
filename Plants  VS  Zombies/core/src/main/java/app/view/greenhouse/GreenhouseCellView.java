package app.view.greenhouse;

import app.enums.greenhouse.GreenhousePlantType;
import app.enums.plant.PlantName;
import app.enums.greenhouse.PotStatus;
import app.model.greenhouse.BoostPlant;
import app.model.greenhouse.GreenhousePlant;
import app.model.greenhouse.GreenhousePot;
import app.model.greenhouse.MarigoldPlant;
import app.view.game.pregame.visual.PlantIdlePreviewActor;
import app.view.game.pregame.visual.PlantIdlePreviewConfigLoader;
import app.view.game.pregame.visual.providers.IdlePlantVisualProvider;
import app.view.greenhouse.config.GreenhouseConfig;
import app.view.greenhouse.config.GreenhousePlantVisualsConfig;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import game.component.AssetManager;
import graphics.general.animation.PamClipResolver;
import graphics.general.animation.PamPathResolver;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class GreenhouseCellView extends WidgetGroup {
    private final int row;
    private final int col;
    private final GreenhouseConfig config;
    private final GreenhousePlantVisualsConfig plantVisuals;
    private final IdlePlantVisualProvider plantProvider = new IdlePlantVisualProvider();
    private final Image potImage;
    private final Image lockImage;
    private final GreenhouseQuestionActor questionActor;
    private final GreenhouseTimerGroup timerGroup;
    private final GreenhouseActionButton actionButton;
    private Actor plantActor;
    private String currentPlantKey = "";
    private boolean questionLockedTarget;
    private Consumer<GreenhouseCellView> onPotClick = cell -> {
    };
    private Consumer<GreenhouseCellView> onActionClick = cell -> {
    };
    private BiConsumer<GreenhouseCellView, String> onQuestionClick = (cell, text) -> {
    };

    public GreenhouseCellView(
        int row,
        int col,
        GreenhouseConfig config,
        GreenhousePlantVisualsConfig plantVisuals,
        Texture potTexture,
        TextureRegion lockRegion,
        Texture questionTexture,
        Texture timerBackground,
        Texture timerIcon,
        Texture actionBackground,
        Texture growingIcon,
        Texture readyIcon,
        BitmapFont timerFont,
        BitmapFont actionFont
    ) {
        this.row = row;
        this.col = col;
        this.config = config;
        this.plantVisuals = plantVisuals == null ? new GreenhousePlantVisualsConfig().sanitize() : plantVisuals;
        setTransform(true);
        setTouchable(Touchable.childrenOnly);

        float potW = Math.max(1f, config.pot.width * config.pot.scale);
        float potH = Math.max(1f, config.pot.height * config.pot.scale);
        setSize(potW, potH);
        setOrigin(Align.center);

        potImage = new Image();
        if (potTexture != null) {
            potImage.setDrawable(new TextureRegionDrawable(new TextureRegion(potTexture)));
        }
        potImage.setSize(potW, potH);
        potImage.setOrigin(Align.center);
        potImage.setRotation(config.pot.rotation);
        potImage.getColor().a = config.pot.alpha;
        potImage.setPosition(0f, 0f);
        potImage.setName("greenhouse-pot");
        potImage.setVisible(false);
        potImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onPotClick.accept(GreenhouseCellView.this);
            }
        });
        addActor(potImage);

        Image hitArea = new Image();
        hitArea.setSize(potW, potH);
        hitArea.setPosition(0f, 0f);
        hitArea.setName("greenhouse-cell");
        hitArea.setTouchable(Touchable.enabled);
        hitArea.getColor().a = 0f;
        hitArea.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onPotClick.accept(GreenhouseCellView.this);
            }
        });
        addActor(hitArea);
        hitArea.toBack();

        lockImage = new Image();
        if (lockRegion != null) {
            lockImage.setDrawable(new TextureRegionDrawable(lockRegion));
            float lockW = config.lockIcon.width > 0f
                ? config.lockIcon.width * config.lockIcon.scale
                : lockRegion.getRegionWidth() * config.lockIcon.scale;
            float lockH = config.lockIcon.height > 0f
                ? config.lockIcon.height * config.lockIcon.scale
                : lockRegion.getRegionHeight() * config.lockIcon.scale;
            if (lockW <= 0f) {
                lockW = potW * 0.5f;
            }
            if (lockH <= 0f) {
                lockH = potH * 0.5f;
            }
            lockImage.setSize(lockW, lockH);
        } else {
            float lockW = config.lockIcon.width > 0f ? config.lockIcon.width * config.lockIcon.scale : potW * 0.4f;
            float lockH = config.lockIcon.height > 0f ? config.lockIcon.height * config.lockIcon.scale : potH * 0.4f;
            lockImage.setSize(lockW, lockH);
        }
        lockImage.setOrigin(Align.center);
        lockImage.setRotation(config.lockIcon.rotation);
        lockImage.getColor().a = config.lockIcon.alpha;
        lockImage.setPosition(
            config.lockIcon.offsetX + (potW - lockImage.getWidth()) * 0.5f,
            config.lockIcon.offsetY + (potH - lockImage.getHeight()) * 0.5f
        );
        lockImage.setName("greenhouse-lock");
        lockImage.setVisible(false);
        lockImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onPotClick.accept(GreenhouseCellView.this);
            }
        });
        addActor(lockImage);

        questionActor = new GreenhouseQuestionActor(config.questionIcon, questionTexture);
        questionActor.setOnClick(() -> onQuestionClick.accept(
            this,
            config.questionIcon.textForStatus(questionLockedTarget, row, col)
        ));
        questionActor.setVisible(false);
        addActor(questionActor);

        timerGroup = new GreenhouseTimerGroup(
            config.timer,
            config.formats,
            timerBackground,
            timerIcon,
            timerFont
        );
        timerGroup.setVisible(false);
        addActor(timerGroup);

        actionButton = new GreenhouseActionButton(
            config.actionButton,
            actionBackground,
            growingIcon,
            readyIcon,
            actionFont
        );
        actionButton.setAction(() -> onActionClick.accept(this));
        actionButton.setVisible(false);
        addActor(actionButton);

        layoutOverlays(potW, potH);
        applyDepths();
    }

    private void layoutOverlays(float potW, float potH) {
        questionActor.setPosition(
            (potW - questionActor.getWidth()) * 0.5f + questionActor.offsetX(),
            questionActor.offsetY()
        );

        float timerX = (potW - timerGroup.getWidth()) * 0.5f + timerGroup.offsetX();
        float timerY = (potH - timerGroup.getHeight()) * 0.5f + timerGroup.offsetY();
        timerGroup.setPosition(timerX, timerY);

        float buttonX = timerGroup.getX()
            + timerGroup.getWidth() * 0.5f
            - actionButton.getWidth() * 0.5f
            + config.actionButton.relativeToTimerOffsetX;
        float buttonY = timerGroup.getY()
            - actionButton.getHeight()
            + config.actionButton.relativeToTimerOffsetY;
        actionButton.setPosition(buttonX, buttonY);
    }

    public int row() {
        return row;
    }

    public int col() {
        return col;
    }

    public void setOnPotClick(Consumer<GreenhouseCellView> onPotClick) {
        this.onPotClick = onPotClick != null ? onPotClick : cell -> {
        };
    }

    public void setOnActionClick(Consumer<GreenhouseCellView> onActionClick) {
        this.onActionClick = onActionClick != null ? onActionClick : cell -> {
        };
    }

    public void setOnQuestionClick(BiConsumer<GreenhouseCellView, String> onQuestionClick) {
        this.onQuestionClick = onQuestionClick != null ? onQuestionClick : (cell, text) -> {
        };
    }

    public void showQuestionBubble(String text) {
        questionActor.showText(text);
    }

    public void refresh(GreenhousePot pot, int growthCostGems) {
        if (pot == null) {
            setVisible(false);
            return;
        }
        setVisible(true);
        PotStatus status = pot.getPotStatus();
        boolean hasPot = pot.hasPhysicalPot();
        boolean locked = status == PotStatus.LOCKED;
        boolean noPot = status == PotStatus.NO_POT;
        boolean hasPlant = pot.getPlant() != null
            && (status == PotStatus.GROWING || status == PotStatus.READY || pot.isPlantReady());

        potImage.setVisible(config.pot.visible && hasPot);
        potImage.setTouchable(hasPot ? Touchable.enabled : Touchable.disabled);

        lockImage.setVisible(config.lockIcon.visible && locked);
        lockImage.setTouchable(locked ? Touchable.enabled : Touchable.disabled);

        questionLockedTarget = locked;
        boolean showQuestion = config.questionIcon.visible && (noPot || locked);
        questionActor.setVisible(showQuestion);
        questionActor.setTouchable(showQuestion ? Touchable.enabled : Touchable.disabled);

        layoutOverlays(getWidth(), getHeight());

        if (hasPlant) {
            ensurePlantActor(pot.getPlant());
            if (plantActor != null) {
                plantActor.setVisible(true);
            }
            boolean ready = pot.isPlantReady() || status == PotStatus.READY;
            timerGroup.setVisible(config.timer.visible);
            timerGroup.refresh(pot.getPlant().getRemainingTime(), ready);
            actionButton.setVisible(config.actionButton.visible);
            actionButton.setState(
                ready ? GreenhouseActionButton.State.READY : GreenhouseActionButton.State.GROWING,
                growthCostGems
            );
        } else {
            if (plantActor != null) {
                plantActor.setVisible(false);
            }
            timerGroup.setVisible(false);
            actionButton.setVisible(false);
        }
        applyDepths();
    }

    private void ensurePlantActor(GreenhousePlant plant) {
        String key = resolvePlantKey(plant);
        GreenhousePlantVisualsConfig.PlantVisualConfig visual = plantVisuals.resolve(key);
        if (plantActor != null && key.equals(currentPlantKey)) {
            layoutPlant(visual);
            return;
        }
        if (plantActor != null) {
            plantActor.remove();
            plantActor = null;
        }
        currentPlantKey = key;
        plantActor = createCollectionIdleActor(resolvePlantName(plant), visual);
        if (plantActor == null) {
            currentPlantKey = "";
            return;
        }
        plantActor.setName("greenhouse-plant");
        plantActor.setTouchable(Touchable.disabled);
        plantActor.getColor().a = visual.alpha;
        plantActor.setRotation(visual.rotation);
        layoutPlant(visual);
        addActor(plantActor);
        applyDepths();
    }

    // Reuses Collection idle source (IdlePlantVisualProvider / plant_idle_preview PAM+clip).
    // Greenhouse visuals only supply size/spacing (scale, offsetX/Y, alpha, rotation).
    private Actor createCollectionIdleActor(
        PlantName plantName,
        GreenhousePlantVisualsConfig.PlantVisualConfig visual
    ) {
        float size = plantSlotSize(visual);
        if (plantName != null) {
            return plantProvider.createVisual(plantName, size, size);
        }
        PlantIdlePreviewConfigLoader.PlantIdlePreviewConfig idle =
            new PlantIdlePreviewConfigLoader.PlantIdlePreviewConfig();
        idle.pamPath = visual.pamPath;
        idle.clip = visual.clip;
        idle.loop = visual.loop;
        idle.scale = 1f;
        idle.offsetX = 0f;
        idle.offsetY = 0f;
        PamPathResolver paths = PamPathResolver.get();
        if (idle.pamPath != null && !idle.pamPath.isEmpty()) {
            idle.pamPath = paths.resolve(idle.pamPath);
        }
        if (!paths.exists(idle.pamPath)) {
            return null;
        }
        try {
            AssetManager.player.loadSync(idle.pamPath);
        } catch (RuntimeException loadFailed) {
            return null;
        }
        String resolvedClip = PamClipResolver.resolve(AssetManager.player, idle.pamPath, idle.clip);
        if (resolvedClip == null) {
            return null;
        }
        idle.clip = resolvedClip;
        PlantIdlePreviewActor actor = new PlantIdlePreviewActor(idle);
        actor.setSize(size, size);
        return actor;
    }

    private void layoutPlant(GreenhousePlantVisualsConfig.PlantVisualConfig visual) {
        if (plantActor == null || visual == null) {
            return;
        }
        float size = plantSlotSize(visual);
        plantActor.setSize(size, size);
        float cx = getWidth() * 0.5f + visual.offsetX;
        float cy = getHeight() * 0.5f + visual.offsetY;
        plantActor.setPosition(cx - size * 0.5f, cy - size * 0.5f);
        plantActor.setRotation(visual.rotation);
        plantActor.getColor().a = visual.alpha;
    }

    private float plantSlotSize(GreenhousePlantVisualsConfig.PlantVisualConfig visual) {
        float base = Math.max(1f, Math.min(getWidth(), getHeight()));
        float scale = visual == null ? 0.7f : visual.scale;
        return Math.max(1f, base * scale);
    }

    private static String resolvePlantKey(GreenhousePlant plant) {
        if (plant instanceof MarigoldPlant) {
            return "MARIGOLD";
        }
        if (plant instanceof BoostPlant boost) {
            PlantName name = boost.getPlant();
            return name == null ? "MARIGOLD" : name.name();
        }
        if (plant != null && plant.getGreenhousePlantType() == GreenhousePlantType.MARIGOLD) {
            return "MARIGOLD";
        }
        return plant == null ? "MARIGOLD" : plant.getPlantName();
    }

    private static PlantName resolvePlantName(GreenhousePlant plant) {
        if (plant instanceof BoostPlant boost) {
            return boost.getPlant();
        }
        if (plant instanceof MarigoldPlant) {
            return null;
        }
        if (plant == null || plant.getPlantName() == null) {
            return null;
        }
        try {
            return PlantName.valueOf(plant.getPlantName().trim().toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private void applyDepths() {
        setActorDepth(potImage, config.layers.potDepth);
        setActorDepth(lockImage, config.layers.lockDepth);
        setActorDepth(questionActor, config.layers.questionDepth);
        if (plantActor != null) {
            setActorDepth(plantActor, config.layers.plantDepth);
        }
        setActorDepth(timerGroup, config.layers.timerDepth);
        setActorDepth(actionButton, config.layers.actionDepth);
    }

    private void setActorDepth(Actor actor, int depth) {
        if (actor == null || actor.getParent() != this) {
            return;
        }
        actor.setZIndex(Math.max(0, depth));
    }
}
