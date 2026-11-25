package app.view.game.states;

import app.view.game.PlayScreen;
import app.view.hud.notification.GameplayNotifications;
import game.audio.AudioCatalog;
import game.dialogue.DialogueManager;
import game.dialogue.DialogueTriggerDispatcher;
import game.dialogue.trigger.DialogueTriggerContext;
import game.dialogue.trigger.DialogueTriggerEvent;
import game.entity.zombie.boss.boss.Zomboss;

public class ReadyState implements PlayScreenState {
    private final boolean restored;

    public ReadyState() {
        this(false);
    }

    public ReadyState(boolean restored) {
        this.restored = restored;
    }

    @Override
    public void enter(PlayScreen screen) {
        if (restored) {
            return;
        }
        screen.getHud().showNotification(GameplayNotifications.gameStarted());
        PlayScreen.shake(0.55f, 16f);
        game.audio.Sfx.play(game.audio.AudioId.SFX_READYSETPLANT);
        AudioCatalog.playLevelTheme(screen.getChapter());
        DialogueManager manager = screen.getDialogueManager();
        if (manager != null) {
            DialogueTriggerDispatcher.dispatch(
                new DialogueTriggerContext(
                    screen.getEngine().getWorld(),
                    screen.getChapter(),
                    DialogueTriggerEvent.GAMEPLAY_READY
                ),
                manager
            );
        }
    }

    @Override
    public void update(PlayScreen screen) {
        Zomboss boss = BossOutroState.findBoss(screen);
        if (boss != null && boss.isReadyForOutroDialogue()) {
            screen.getStateMachine().changeState(new BossOutroState());
        }
    }
}
