package app.view.game.result;

import app.controller.menu.GameMenuController;
import app.controller.menu.PlantSelectionController;
import app.enums.SeasonType;
import app.model.App;
import app.model.user.User;
import app.view.screen.ScreenManager;
import com.badlogic.gdx.scenes.scene2d.Stage;
import game.audio.AudioCatalog;
import game.audio.AudioId;
import game.audio.AudioManager;
import game.audio.MusicTransition;
import game.audio.Sfx;
import game.world.GameWorld;
import game.world.chapter.Chapter;
import game.world.chapter.ChapterId;
import game.world.levelConditions.result.ResultBalanceConfig;
import game.world.levelConditions.result.StageResult;
import graphics.GraphicsEngine;
import graphics.general.effects.EffectRequest;
import graphics.general.effects.anchers.WorldAnchor;
import plants_vs_zombies.PlantsVSZombies;

public final class StageResultPresenter {
    private final app.view.game.PlayScreen screen;
    private boolean started;
    private boolean popupShown;
    private boolean buyWinInFlight;
    private float delayRemaining;
    private StageResultPopup activePopup;

    public StageResultPresenter(app.view.game.PlayScreen screen) {
        this.screen = screen;
    }

    public void update(float delta) {
        GameWorld world = screen.getEngine() != null ? screen.getEngine().getWorld() : null;
        if (world == null || world.getStageResult() == null) {
            return;
        }
        StageResult result = world.getStageResult();
        if (!started) {
            started = true;
            delayRemaining = ResultBalanceConfigLoader.get().popupDelaySeconds;
            playMusic(result);
            spawnEffect(result);
        }
        if (popupShown) {
            return;
        }
        delayRemaining -= delta;
        if (delayRemaining <= 0f) {
            popupShown = true;
            showPopup(result);
        }
    }

    public boolean isOpen() {
        return started;
    }

    public boolean isPopupVisible() {
        return popupShown && activePopup != null && activePopup.getStage() != null;
    }

    private void playMusic(StageResult result) {
        ResultEffectConfig.Placement placement = ResultEffectConfigLoader.get().placement(result.isWin());
        AudioId music = result.isWin()
            ? AudioCatalog.victoryFor(result.getChapter())
            : AudioCatalog.gameOverFor(result.getChapter());
        AudioManager.play(music, MusicTransition.fade(placement.musicFadeSeconds));
        if (result.isWin()) {
            Sfx.play(AudioId.SFX_FINAL_FANFARE);
        } else {
            Sfx.playRandom(AudioId.VOX_DAVE_SCREAM, AudioId.VOX_DAVE_SCREAM2);
        }
    }

    private void spawnEffect(StageResult result) {
        GraphicsEngine graphics = screen.getGraphicsEngine();
        if (graphics == null || graphics.getEffectManager() == null) {
            return;
        }
        ResultEffectConfig.Placement placement = ResultEffectConfigLoader.get().placement(result.isWin());
        float x = placement.fallbackX;
        float y = placement.fallbackY;
        if (screen.getEngine() != null && screen.getEngine().getWorld() != null
            && screen.getEngine().getWorld().getGrid() != null) {
            var config = screen.getEngine().getWorld().getGrid().getConfig();
            x = config.getPlaygroundX()
                + config.getPlaygroundWidth() * placement.playgroundXFactor
                + placement.worldOffsetX;
            y = config.getPlaygroundY()
                + config.getPlaygroundHeight() * placement.playgroundYFactor
                + placement.worldOffsetY;
        }
        EffectRequest<Object> request = new EffectRequest<>(
            placement.effectId,
            null,
            new WorldAnchor(x, y),
            placement.resolvedLayer()
        );
        request.setScale(placement.scale);
        graphics.getEffectManager().spawn(request);
    }

    private void showPopup(StageResult result) {
        Stage stage = screen.getHud() != null ? screen.getHud().getStage() : screen.getStage();
        if (stage == null) {
            return;
        }
        if (result.isWin()) {
            activePopup = new WinResultPopup(result, new WinResultPopup.Callbacks() {
                @Override
                public void onReplay() {
                    closeAndRestart();
                }

                @Override
                public void onBackToStageSelect() {
                    closeAndBackToSelect(result.getChapter());
                }

                @Override
                public void onNextStage() {
                    closeAndNext(result);
                }
            });
        } else {
            activePopup = new LoseResultPopup(result, new LoseResultPopup.Callbacks() {
                @Override
                public void onRetry() {
                    closeAndRestart();
                }

                @Override
                public void onBuyWin() {
                    handleBuyWin();
                }

                @Override
                public void onBackToStageSelect() {
                    closeAndBackToSelect(result.getChapter());
                }
            });
        }
        activePopup.present(stage);
    }

    private void handleBuyWin() {
        if (buyWinInFlight) {
            return;
        }
        GameWorld world = screen.getEngine().getWorld();
        StageResult current = world.getStageResult();
        if (current == null || !current.isLose()) {
            return;
        }
        User user = App.getCurrentUser();
        if (user == null) {
            return;
        }
        ResultBalanceConfig balance = ResultBalanceConfigLoader.get();
        int cost = balance.loseSkipDiamondCost;
        if (user.getGems() < cost) {
            if (activePopup instanceof LoseResultPopup losePopup) {
                losePopup.showError(balance.notEnoughDiamondsWithCost());
            }
            AudioManager.play(AudioId.UI_ERROR);
            return;
        }
        buyWinInFlight = true;
        if (!user.tryDeductGems(cost)) {
            buyWinInFlight = false;
            if (activePopup instanceof LoseResultPopup losePopup) {
                losePopup.showError(balance.notEnoughDiamonds);
            }
            return;
        }
        StageResult winResult = world.convertLoseToWin();
        if (winResult == null || !winResult.isWin()) {
            user.addGems(cost);
            buyWinInFlight = false;
            if (activePopup instanceof LoseResultPopup losePopup) {
                losePopup.showError(balance.convertFailed);
            }
            AudioManager.play(AudioId.UI_ERROR);
            return;
        }
        if (activePopup != null) {
            activePopup.close();
            activePopup = null;
        }
        popupShown = true;
        started = true;
        playMusic(winResult);
        spawnEffect(winResult);
        activePopup = new WinResultPopup(winResult, new WinResultPopup.Callbacks() {
            @Override
            public void onReplay() {
                closeAndRestart();
            }

            @Override
            public void onBackToStageSelect() {
                closeAndBackToSelect(winResult.getChapter());
            }

            @Override
            public void onNextStage() {
                closeAndNext(winResult);
            }
        });
        Stage stage = screen.getHud() != null ? screen.getHud().getStage() : screen.getStage();
        activePopup.present(stage);
        buyWinInFlight = false;
    }

    private void closeAndRestart() {
        if (activePopup != null) {
            activePopup.close();
            activePopup = null;
        }
        PlantSelectionController.restartCurrentLevel();
    }

    private void closeAndBackToSelect(Chapter chapter) {
        if (activePopup != null) {
            activePopup.close();
            activePopup = null;
        }
        ScreenManager.showLevelSelectFor(PlantsVSZombies.getPvz(), chapter);
    }

    private void closeAndNext(StageResult result) {
        if (activePopup != null) {
            activePopup.close();
            activePopup = null;
        }
        if (!result.hasNextStage() || result.getSeasonType() == null
            || result.getSeasonType() == SeasonType.MINIGAME) {
            closeAndBackToSelect(result.getChapter());
            return;
        }
        ChapterId nextId = new ChapterId(result.getSeasonType(), result.getNextStageNumber());
        String name = nextId.toName();
        new GameMenuController().startChapterFromUi(name);
    }
}
