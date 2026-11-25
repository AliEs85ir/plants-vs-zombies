package app.view.levelselect;

import app.enums.SeasonType;
import app.view.adventure.AdventureHud;
import app.view.loading.CoverImage;
import app.view.mainmenu.MenuTextures;
import app.view.screen.ScreenManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import plants_vs_zombies.PlantsVSZombies;

import java.util.function.IntConsumer;

public final class LevelSelectPanel extends WidgetGroup implements Disposable {
    private final LevelSelectMenuConfig config;
    private final LevelSelectMenuConfig.ChapterConfig chapter;
    private final SeasonType season;
    private final CoverImage background = new CoverImage();
    private final Array<Texture> textures = new Array<>();
    private final LevelSelectCentralGroup centralGroup;
    private final LevelSelectNodeActor[] nodes;
    private final LevelSelectLayoutMath.Slot[] slots;
    private final AdventureHud hud;
    private final int levelCount;
    private float time;

    public LevelSelectPanel(PlantsVSZombies game, SeasonType season, IntConsumer onLevelChosen) {
        this.config = LevelSelectMenuConfigLoader.load();
        this.season = season;
        this.chapter = config.chapterForSeason(season);
        this.levelCount = chapter == null
            ? 0
            : LevelSelectProgress.totalLevels(chapter.seasonType(), chapter.levelCount);
        setFillParent(true);
        setTransform(false);
        setTouchable(Touchable.childrenOnly);
        Texture backgroundTexture = load(chapter == null ? "" : chapter.resolvedBackgroundPath());
        if (backgroundTexture != null && chapter != null) {
            background.setTexture(
                backgroundTexture,
                chapter.backgroundLayout.sourceWidth > 0f
                    ? chapter.backgroundLayout.sourceWidth
                    : backgroundTexture.getWidth(),
                chapter.backgroundLayout.sourceHeight > 0f
                    ? chapter.backgroundLayout.sourceHeight
                    : backgroundTexture.getHeight()
            );
            background.setOffsets(chapter.backgroundLayout.offsetX, chapter.backgroundLayout.offsetY);
        }
        background.setFillParent(true);
        background.setTouchable(Touchable.disabled);
        addActor(background);
        centralGroup = chapter == null ? null : new LevelSelectCentralGroup(config, chapter);
        if (centralGroup != null) {
            addActor(centralGroup);
        }
        nodes = new LevelSelectNodeActor[Math.max(0, levelCount)];
        slots = new LevelSelectLayoutMath.Slot[nodes.length];
        for (int i = 0; i < nodes.length; i++) {
            slots[i] = new LevelSelectLayoutMath.Slot();
            int levelNumber = i + 1;
            LevelSelectNodeActor node = new LevelSelectNodeActor(chapter.level, config.hover);
            node.applyState(LevelSelectProgress.state(season, levelNumber, levelCount), true);
            node.setClickAction(() -> {
                if (onLevelChosen != null) {
                    onLevelChosen.accept(levelNumber);
                }
            });
            nodes[i] = node;
            addActor(node);
        }
        hud = new AdventureHud(game, () -> ScreenManager.showGameScreen(game));
        addActor(hud);
    }

    public LevelSelectMenuConfig.ChapterConfig chapter() {
        return chapter;
    }

    public int levelCount() {
        return levelCount;
    }

    public LevelSelectNodeActor node(int levelNumber) {
        if (levelNumber < 1 || levelNumber > nodes.length) {
            return null;
        }
        return nodes[levelNumber - 1];
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        time += Math.max(0f, delta);
        layoutMap(delta);
    }

    @Override
    public void layout() {
        layoutMap(0f);
    }

    private void layoutMap(float delta) {
        background.setBounds(0f, 0f, getWidth(), getHeight());
        float cx = config.layout.resolvedCentralX(getWidth());
        float cy = config.layout.resolvedCentralY(getHeight());
        if (centralGroup != null) {
            centralGroup.update(delta, cx, cy);
        }
        LevelSelectLayoutMath.compute(slots, levelCount, config, getWidth(), getHeight());
        for (int i = 0; i < nodes.length; i++) {
            LevelSelectNodeActor node = nodes[i];
            node.updateHover(delta);
            node.applySlot(slots[i], time, config.layout.levelHitPad);
        }
        int z = 1;
        int[] order = LevelSelectLayoutMath.drawOrder(slots, nodes.length);
        boolean centralPlaced = centralGroup == null;
        for (int index : order) {
            if (!centralPlaced && slots[index] != null && slots[index].depth >= config.layout.centralGroupDepth) {
                centralGroup.setZIndex(z++);
                centralPlaced = true;
            }
            nodes[index].setZIndex(z++);
        }
        if (!centralPlaced) {
            centralGroup.setZIndex(z++);
        }
        hud.setBounds(0f, 0f, getWidth(), getHeight());
        hud.setZIndex(z);
    }

    private Texture load(String path) {
        Texture texture = MenuTextures.load(path, "LevelSelectPanel");
        if (texture != null) {
            textures.add(texture);
        }
        return texture;
    }

    @Override
    public void dispose() {
        hud.dispose();
        for (Texture texture : textures) {
            texture.dispose();
        }
        textures.clear();
    }
}
