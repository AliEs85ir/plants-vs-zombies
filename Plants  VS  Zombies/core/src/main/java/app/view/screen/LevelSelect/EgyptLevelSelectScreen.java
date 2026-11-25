package app.view.screen.LevelSelect;

import app.controller.game.izombie.IZombieNetworkController;
import app.enums.SeasonType;
import app.view.game.pause.LevelContinuePopup;
import app.view.game.pause.PauseLayout;
import app.view.levelselect.LevelSelectNodeActor;
import app.view.levelselect.LevelSelectPanel;
import app.view.levelselect.LevelSelectProgress;
import app.view.screen.BaseScreen;
import app.view.screen.ScreenManager;
import common.Result;
import game.audio.AudioId;
import game.audio.AudioManager;
import game.component.AssetManager;
import game.save.SaveResult;
import game.save.SaveService;
import game.world.chapter.ChapterId;
import game.world.minigame.izombie.IZombieRules;
import plants_vs_zombies.PlantsVSZombies;

public class EgyptLevelSelectScreen extends BaseScreen {
    private final SeasonType season;
    private LevelSelectPanel panel;
    private boolean enteringLevel;

    public EgyptLevelSelectScreen(PlantsVSZombies game) {
        this(game, SeasonType.ANCIENT_EGYPT);
    }

    public EgyptLevelSelectScreen(PlantsVSZombies game, SeasonType season) {
        super(game);
        this.season = season == null ? SeasonType.ANCIENT_EGYPT : season;
    }

    @Override
    protected AudioId menuMusicId() {
        return AudioId.WORLD_MAP;
    }

    @Override
    public void show() {
        super.show();
        enteringLevel = false;
        panel = new LevelSelectPanel(game, season, this::enterLevel);
        stage.addActor(panel);
    }

    private void enterLevel(int levelNumber) {
        if (enteringLevel || panel == null) {
            return;
        }
        LevelSelectNodeActor node = panel.node(levelNumber);
        if (node == null || node.state() == LevelSelectProgress.NodeState.LOCKED) {
            AudioManager.play(AudioId.UI_ERROR);
            return;
        }
        enteringLevel = true;
        String chapterName = new ChapterId(season, levelNumber).toName();
        boolean hasSave = SaveService.hasSave(chapterName);
        final LevelContinuePopup[] holder = new LevelContinuePopup[1];
        holder[0] = new LevelContinuePopup(
            "Level " + levelNumber,
            PauseLayout.defaults(),
            hasSave,
            new LevelContinuePopup.Callback() {
                @Override
                public void onContinueSaved() {
                    SaveResult loaded = SaveService.load(SaveService.keyFor(chapterName));
                    if (!loaded.success()) {
                        enteringLevel = false;
                        holder[0].showError(loaded.message());
                        return;
                    }
                    SaveService.prepareRestore(loaded.data());
                    Result result = ScreenManager.startChapter(chapterName);
                    if (openIZombieMatchIfNeeded(result, chapterName, holder[0])) {
                        return;
                    }
                    if (!result.success()) {
                        SaveService.consumePendingRestore();
                        enteringLevel = false;
                        holder[0].showError(result.message());
                    }
                }

                @Override
                public void onNewGame() {
                    SaveService.clearPendingRestore();
                    SaveService.delete(chapterName);
                    Result result = ScreenManager.startChapter(chapterName);
                    if (openIZombieMatchIfNeeded(result, chapterName, holder[0])) {
                        return;
                    }
                    if (!result.success()) {
                        enteringLevel = false;
                        holder[0].showError(result.message());
                    }
                }

                @Override
                public void onCancel() {
                    enteringLevel = false;
                }
            }
        );
        stage.addActor(holder[0]);
    }

    private boolean openIZombieMatchIfNeeded(Result result, String chapterName, LevelContinuePopup continuePopup) {
        if (!IZombieRules.isPendingMatchmaking(result)) {
            return false;
        }
        IZombieNetworkController controller = IZombieNetworkController.get();
        if (controller == null) {
            enteringLevel = false;
            continuePopup.showError("Online play is not available.");
            return true;
        }
        continuePopup.close();
        controller.openMatchPopup(stage, chapterName, () -> enteringLevel = false);
        return true;
    }

    @Override
    public void render(float delta) {
        AssetManager.pump();
        super.render(delta);
    }

    @Override
    public void dispose() {
        if (panel != null) {
            panel.dispose();
            panel = null;
        }
        super.dispose();
    }
}
