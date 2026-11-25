package app.view.game.states;

import app.view.game.PlayScreen;
import game.dialogue.DialogueManager;
import game.dialogue.DialogueTriggerDispatcher;
import game.dialogue.trigger.DialogueTriggerContext;
import game.dialogue.trigger.DialogueTriggerEvent;
import game.entity.zombie.Zombie;
import game.entity.zombie.boss.boss.Zomboss;

public class BossOutroState implements PlayScreenState {
    @Override
    public void enter(PlayScreen screen) {
        Zomboss boss = findBoss(screen);
        DialogueManager manager = screen.getDialogueManager();
        if (manager == null) {
            finishOutro(boss);
            return;
        }
        DialogueTriggerDispatcher.dispatch(
            new DialogueTriggerContext(
                screen.getEngine().getWorld(),
                screen.getChapter(),
                DialogueTriggerEvent.BOSS_DEFEATED
            ),
            manager
        );
        if (!manager.isActive()) {
            finishOutro(boss);
        }
    }

    @Override
    public void update(PlayScreen screen) {
        DialogueManager manager = screen.getDialogueManager();
        if (manager != null && manager.isActive()) {
            return;
        }
        finishOutro(findBoss(screen));
    }

    private void finishOutro(Zomboss boss) {
        if (boss != null && !boss.isOutroDialogueFinished()) {
            boss.finishOutroDialogue();
        }
    }

    static Zomboss findBoss(PlayScreen screen) {
        if (screen.getEngine() == null || screen.getEngine().getWorld() == null) {
            return null;
        }
        for (Zombie zombie : screen.getEngine().getWorld().getZombies()) {
            if (zombie instanceof Zomboss boss) {
                return boss;
            }
        }
        return null;
    }
}
