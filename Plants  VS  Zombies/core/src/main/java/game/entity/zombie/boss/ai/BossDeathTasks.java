package game.entity.zombie.boss.ai;

import app.view.game.PlayScreen;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import game.dialogue.DialogueConfig;
import game.entity.zombie.boss.boss.Zomboss;
import graphics.general.event.ZombieVisualEventType;

public final class BossDeathTasks {
    private BossDeathTasks() {}

    public static final class PlayDeathClip<T extends Zomboss> extends LeafTask<T> {
        private ZombieVisualEventType event;

        public PlayDeathClip(ZombieVisualEventType event) {
            this.event = event;
        }

        @Override
        public Status execute() {
            getObject().pushEvent(event);
            if (event == ZombieVisualEventType.EGYPT_DIE1
                || event == ZombieVisualEventType.MAMMOTH_DIE
                || event == ZombieVisualEventType.SHARK_DIE) {
                PlayScreen.shake(0.75f, 22f);
            }
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<T> copyTo(Task<T> task) {
            ((PlayDeathClip<T>) task).event = event;
            return task;
        }
    }

    public static final class StartDeathTalk<T extends Zomboss> extends LeafTask<T> {
        private ZombieVisualEventType event;

        public StartDeathTalk(ZombieVisualEventType event) {
            this.event = event;
        }

        @Override
        public Status execute() {
            T boss = getObject();
            boss.pushEvent(event);
            boss.beginOutroDialogue();
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<T> copyTo(Task<T> task) {
            ((StartDeathTalk<T>) task).event = event;
            return task;
        }
    }

    public static final class WaitForOutroDialogue<T extends Zomboss> extends LeafTask<T> {
        @Override
        public Status execute() {
            T boss = getObject();
            if (boss.isOutroDialogueFinished()) {
                return Status.SUCCEEDED;
            }
            if (!DialogueConfig.ENABLE_BOSS_OUTRO_DIALOGUE) {
                boss.finishOutroDialogue();
                return Status.SUCCEEDED;
            }
            return Status.RUNNING;
        }

        @Override
        protected Task<T> copyTo(Task<T> task) {
            return task;
        }
    }

    public static final class FinishDeathSequence<T extends Zomboss> extends LeafTask<T> {
        @Override
        public Status execute() {
            getObject().completeDeathSequence();
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<T> copyTo(Task<T> task) {
            return task;
        }
    }
}
