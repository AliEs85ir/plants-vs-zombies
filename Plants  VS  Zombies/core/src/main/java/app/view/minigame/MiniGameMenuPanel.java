package app.view.minigame;

import app.view.adventure.AdventureHud;
import app.view.greenhouse.GreenhouseMapActor;
import app.view.screen.ScreenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;
import game.audio.AudioId;
import game.audio.AudioManager;
import plants_vs_zombies.PlantsVSZombies;

import java.util.ArrayList;
import java.util.List;

public final class MiniGameMenuPanel extends WidgetGroup implements Disposable {
    private final MiniGameTiledMap tiledMap;
    private final MiniGameMapSpace space;
    private final MiniGameHitTester hitTester;
    private final MiniGameHoverController hoverController;
    private final MiniGameRouter router;
    private final GreenhouseMapActor mapActor;
    private final List<SlotActor> slots = new ArrayList<>();
    private final AdventureHud hud;
    private final Vector2 pointerStage = new Vector2();
    private final Vector2 pointerLocal = new Vector2();
    private final Vector2 pointerMap = new Vector2();
    private final Rectangle slotBounds = new Rectangle();

    public MiniGameMenuPanel(PlantsVSZombies game) {
        MiniGameMenuConfig config = MiniGameMenuConfigLoader.load();
        this.tiledMap = new MiniGameTiledMap(config);
        this.space = tiledMap.space();
        List<MiniGameDefinition> definitions = tiledMap.definitions(config);
        this.hitTester = new MiniGameHitTester(space, definitions);
        this.router = new MiniGameRouter(game);
        setFillParent(true);
        setTransform(false);
        setTouchable(Touchable.childrenOnly);
        setName("menu-minigame-panel");

        this.mapActor = new GreenhouseMapActor(
            tiledMap.map(),
            tiledMap.mapPixelWidth(),
            tiledMap.mapPixelHeight()
        );
        mapActor.setTouchable(Touchable.disabled);
        addActor(mapActor);

        ArrayList<MiniGameHoverController.Entry> entries = new ArrayList<>();
        for (MiniGameDefinition definition : definitions) {
            SlotActor slot = new SlotActor(definition);
            slot.setName("menu-minigame-slot");
            slot.setTouchable(Touchable.enabled);
            slot.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    AudioManager.play(AudioId.UI_CLICK);
                    router.activate(definition);
                }
            });
            slots.add(slot);
            addActor(slot);

            ArrayList<MiniGameHoverAnimation> animations = new ArrayList<>();
            for (MiniGameMenuConfig.HoverAnimationConfig animationConfig : definition.hoverAnimations()) {
                MiniGameHoverAnimation animation = new MiniGameHoverAnimation(definition, animationConfig);
                animations.add(animation);
                addActor(animation.actor());
            }
            entries.add(new MiniGameHoverController.Entry(definition, animations));
        }
        this.hoverController = new MiniGameHoverController(entries);

        this.hud = new AdventureHud(game, () -> ScreenManager.showMainMenu(game));
        addActor(hud);
    }

    @Override
    public void act(float delta) {
        updateHover();
        super.act(delta);
        hoverController.advance();
    }

    @Override
    public void layout() {
        float width = getWidth();
        float height = getHeight();
        mapActor.setBounds(0f, 0f, width, height);
        for (SlotActor slot : slots) {
            space.toStage(slot.definition.bounds(), width, height, slotBounds);
            slot.setBounds(slotBounds.x, slotBounds.y, slotBounds.width, slotBounds.height);
        }
        hoverController.layout(space, width, height);
        hud.setBounds(0f, 0f, width, height);
        mapActor.setZIndex(0);
        int z = 1;
        for (SlotActor slot : slots) {
            slot.setZIndex(z++);
        }
        for (MiniGameHoverController.Entry entry : hoverController.entries()) {
            for (MiniGameHoverAnimation animation : entry.animations()) {
                animation.actor().setZIndex(z++);
            }
        }
        hud.setZIndex(getChildren().size);
    }

    private void updateHover() {
        if (getStage() == null) {
            return;
        }
        pointerStage.set(Gdx.input.getX(), Gdx.input.getY());
        getStage().screenToStageCoordinates(pointerStage);
        Actor hit = getStage().hit(pointerStage.x, pointerStage.y, true);
        if (blocksHover(hit)) {
            hoverController.setHovered(null);
            return;
        }
        pointerLocal.set(pointerStage);
        stageToLocalCoordinates(pointerLocal);
        space.toMap(pointerLocal.x, pointerLocal.y, getWidth(), getHeight(), pointerMap);
        hoverController.setHovered(hitTester.at(pointerMap.x, pointerMap.y));
    }

    private boolean blocksHover(Actor hit) {
        if (hit == null) {
            return false;
        }
        for (Actor current = hit; current != null; current = current.getParent()) {
            String name = current.getName();
            if (name != null && name.startsWith("menu-minigame-slot")) {
                return false;
            }
            if (current == this || current == mapActor) {
                return false;
            }
            if (name != null && name.startsWith("menu-minigame-hover")) {
                return false;
            }
            if (current == hud) {
                return true;
            }
            if (name != null && name.startsWith("menu-") && !name.startsWith("menu-minigame")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void dispose() {
        if (hud != null) {
            hud.dispose();
        }
        if (mapActor != null) {
            mapActor.dispose();
        }
        if (tiledMap != null) {
            tiledMap.dispose();
        }
        slots.clear();
    }

    private static final class SlotActor extends Widget {
        private final MiniGameDefinition definition;

        private SlotActor(MiniGameDefinition definition) {
            this.definition = definition;
        }
    }
}
