package app.view.game.pause;

import app.controller.menu.PlantSelectionController;
import app.enums.GameState;
import app.model.App;
import app.view.game.PlayScreen;
import app.view.screen.ScreenManager;
import game.audio.AudioId;
import game.audio.AudioManager;
import game.engine.pause.PauseReason;
import game.save.SaveResult;
import game.save.SaveService;
import game.world.chapter.rule.scoringgame.ScoringSession;
import plants_vs_zombies.PlantsVSZombies;

public class PauseMenuController implements PauseMenuPopup.PauseMenuActions {
    private final PlayScreen screen;
    private final PauseLayout layout;
    private PauseMenuPopup popup;
    private boolean open;
    private boolean closing;

    public PauseMenuController(PlayScreen screen) {
        this(screen, PauseLayout.defaults());
    }

    public PauseMenuController(PlayScreen screen, PauseLayout layout) {
        this.screen = screen;
        this.layout = layout == null ? PauseLayout.defaults() : layout;
    }

    public void open() {
        if (open || closing || screen == null || screen.getEngine() == null) {
            return;
        }
        GameState state = screen.getEngine().getWorld().getGameState();
        if (state == GameState.WON || state == GameState.LOST) {
            return;
        }
        screen.getEngine().pause(PauseReason.PAUSE_MENU);
        screen.getHud().setGameplayHudInteractive(false);
        ensurePopup();
        popup.refresh(screen.getEngine().getWorld());
        popup.present(screen.getHud().getStage());
        AudioManager.play(AudioId.SFX_PAUSE);
        open = true;
    }

    public void close() {
        if (!open || closing) {
            return;
        }
        closing = true;
        if (popup != null && popup.isVisible()) {
            popup.dismiss(this::finishClose);
            return;
        }
        finishClose();
    }

    public boolean isOpen() {
        return open || closing;
    }

    @Override
    public void onContinue() {
        close();
    }

    @Override
    public void onSaveAndExit() {
        SaveResult result = SaveService.saveCurrent();
        if (!result.success()) {
            AudioManager.play(AudioId.UI_ERROR);
            if (popup != null) {
                popup.showError(result.message());
            }
            return;
        }
        if (App.getCurrentUser() != null && App.getCurrentUser().getQuestManager() != null) {
            App.getCurrentUser().getQuestManager().saveQuests();
        }
        leaveLevel();
        ScreenManager.showLevelSelectFor(PlantsVSZombies.getPvz(), screen.getChapter());
    }

    @Override
    public void onRestart() {
        leaveLevel();
        PlantSelectionController.restartCurrentLevel();
    }

    private void leaveLevel() {
        AudioManager.stopGameplayAudio();
        ScoringSession.finish();
        closing = true;
        if (popup != null) {
            popup.dismiss();
        }
        finishClose();
    }

    private void finishClose() {
        restoreHud();
        screen.getEngine().resume(PauseReason.PAUSE_MENU);
        open = false;
        closing = false;
    }

    private void restoreHud() {
        if (screen.getHud() != null) {
            screen.getHud().setGameplayHudInteractive(true);
        }
    }

    private void ensurePopup() {
        if (popup == null) {
            popup = new PauseMenuPopup(layout, this);
        }
    }
}
