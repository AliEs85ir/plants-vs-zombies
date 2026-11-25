package game.dialogue;

import app.view.dialogue.DialogueOverlay;
import app.view.game.GameViewport;
import game.audio.AudioCatalog;
import game.audio.AudioId;
import game.audio.AudioManager;
import game.audio.Sfx;
import game.engine.RealTimeGameEngine;
import game.engine.pause.PauseReason;
import game.entity.zombie.Zombie;
import game.entity.zombie.boss.boss.Zomboss;
import graphics.GraphicsEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DialogueManager implements NpcController.Listener {
    private enum State {
        IDLE,
        ENTERING,
        SHOWING_LINE,
        LEAVING,
        WAITING_BEFORE_RESUME
    }

    private final RealTimeGameEngine engine;
    private final DialogueOverlay overlay;
    private final NpcController npcController;
    private final DialogueDurationCalculator durationCalculator;
    private final TalkAnimationPicker talkAnimationPicker;

    private State state = State.IDLE;
    private List<DialogueLine> dialogues = List.of();
    private int currentIndex;
    private float resumeDelaySeconds;
    private float resumeElapsed;
    private Speaker leavingSpeaker;
    private Speaker musicSpeaker;
    private boolean spawnNpcs = true;
    private final GameViewport viewport;
    public DialogueManager(
        RealTimeGameEngine engine,
        GraphicsEngine graphicsEngine,
        GameViewport viewport,
        DialogueOverlay overlay
    ) {
        this.engine = engine;
        this.overlay = overlay;
        this.npcController = new NpcController(graphicsEngine, viewport);
        this.viewport = viewport;
        this.npcController.setListener(this);
        this.durationCalculator = new DialogueDurationCalculator();
        this.talkAnimationPicker = new TalkAnimationPicker();
    }

    public void start(List<DialogueLine> lines, float resumeDelaySeconds) {
        start(lines, resumeDelaySeconds, true);
    }

    public void start(List<DialogueLine> lines, float resumeDelaySeconds, boolean spawnNpcs) {
        if (isActive() || lines == null || lines.isEmpty()) {
            return;
        }
        this.dialogues = Collections.unmodifiableList(new ArrayList<>(lines));
        this.currentIndex = 0;
        this.resumeDelaySeconds = Math.max(0f, resumeDelaySeconds);
        this.resumeElapsed = 0f;
        this.leavingSpeaker = null;
        this.musicSpeaker = null;
        this.spawnNpcs = spawnNpcs;
        engine.pause(PauseReason.DIALOG);
        overlay.beginSession();
        syncDialogueMusic(lines.get(0).getSpeaker());
        if (spawnNpcs) {
            enterCurrentSpeaker();
        } else {
            showCurrentLine();
        }
    }

    public void update(float delta) {
        if (state == State.IDLE) {
            return;
        }
        npcController.update(delta);
        if (state == State.WAITING_BEFORE_RESUME) {
            resumeElapsed += delta;
            if (resumeElapsed >= resumeDelaySeconds) {
                finishSession();
            }
        }
    }

    public void requestAdvance() {
        if (state != State.SHOWING_LINE) {
            return;
        }
        Speaker current = dialogues.get(currentIndex).getSpeaker();
        if (isLastLineForSpeaker(current)) {
            overlay.hideLine();
            if (!spawnNpcs) {
                beginResumeDelay();
                return;
            }
            leavingSpeaker = current;
            state = State.LEAVING;
            npcController.leave(current);
            return;
        }
        if (spawnNpcs) {
            npcController.idle(current);
        }
        currentIndex++;
        presentCurrentLine();
    }

    public boolean isActive() {
        return state != State.IDLE;
    }

    public boolean isBlockingGameplayInput() {
        return isActive();
    }

    public void dispose() {
        npcController.reset();
        overlay.endSession();
        stopDialogueMusic();
        dialogues = List.of();
        currentIndex = 0;
        leavingSpeaker = null;
        musicSpeaker = null;
        state = State.IDLE;
    }

    @Override
    public void onEnterFinished(Speaker speaker) {
        if (state != State.ENTERING) {
            return;
        }
        showCurrentLine();
    }

    @Override
    public void onLeaveFinished(Speaker speaker) {
        if (state != State.LEAVING || speaker != leavingSpeaker) {
            return;
        }
        leavingSpeaker = null;
        currentIndex++;
        if (currentIndex >= dialogues.size()) {
            beginResumeDelay();
            return;
        }
        presentCurrentLine();
    }

    @Override
    public void onTalkDurationFinished(Speaker speaker) {
    }

    private void enterCurrentSpeaker() {
        DialogueLine line = dialogues.get(currentIndex);
        overlay.hideLine();
        state = State.ENTERING;
        npcController.enter(line.getSpeaker());
    }

    private void presentCurrentLine() {
        if (!spawnNpcs) {
            showCurrentLine();
            return;
        }
        Speaker speaker = dialogues.get(currentIndex).getSpeaker();
        if (npcController.hasNpc(speaker)) {
            showCurrentLine();
            return;
        }
        enterCurrentSpeaker();
    }

    private void showCurrentLine() {
        DialogueLine line = dialogues.get(currentIndex);
        syncDialogueMusic(line.getSpeaker());
        Sfx.dialogueLine(line.getSpeaker(), line.getText());
        if (spawnNpcs) {
            float duration = durationCalculator.calculate(line.getText());
            String clip = talkAnimationPicker.pick(line.getSpeaker(), duration);
            npcController.talk(line.getSpeaker(), clip, duration);
            overlay.showLine(line);
        } else {
            Zomboss boss = findBoss();
            if (boss != null && boss.getPosition() != null) {
                overlay.showLine(
                    line,
                    (float) boss.getPosition().getX(),
                    (float) boss.getPosition().getY(),
                    viewport
                );
            } else {
                overlay.showLine(line);
            }
        }
        state = State.SHOWING_LINE;
    }

    private Zomboss findBoss() {
        if (engine == null || engine.getWorld() == null) {
            return null;
        }
        for (Zombie zombie : engine.getWorld().getZombies()) {
            if (zombie instanceof Zomboss boss) {
                return boss;
            }
        }
        return null;
    }

    private boolean isLastLineForSpeaker(Speaker speaker) {
        for (int i = currentIndex + 1; i < dialogues.size(); i++) {
            if (dialogues.get(i).getSpeaker() == speaker) {
                return false;
            }
        }
        return true;
    }

    private void beginResumeDelay() {
        overlay.hideLine();
        state = State.WAITING_BEFORE_RESUME;
        resumeElapsed = 0f;
    }

    private void finishSession() {
        npcController.reset();
        overlay.endSession();
        restoreLevelThemeAfterDialogue();
        dialogues = List.of();
        currentIndex = 0;
        leavingSpeaker = null;
        musicSpeaker = null;
        state = State.IDLE;
        engine.resume(PauseReason.DIALOG);
    }

    private void syncDialogueMusic(Speaker speaker) {
        if (speaker == null || speaker == musicSpeaker) {
            return;
        }
        musicSpeaker = speaker;
        if (speaker == Speaker.CRAZY_DAVE) {
            AudioManager.play(AudioId.CRAZY_DAVE_DIALOGUE);
            AudioManager.stop(AudioId.ZOMBOSS_DIALOGUE, 0.25f);
            return;
        }
        if (speaker == Speaker.ZOMBOSS) {
            AudioManager.play(AudioId.ZOMBOSS_DIALOGUE);
            AudioManager.stop(AudioId.CRAZY_DAVE_DIALOGUE, 0.25f);
        }
    }

    private void stopDialogueMusic() {
        musicSpeaker = null;
        AudioManager.stop(AudioId.CRAZY_DAVE_DIALOGUE, 0f);
        AudioManager.stop(AudioId.ZOMBOSS_DIALOGUE, 0f);
    }

    private void restoreLevelThemeAfterDialogue() {
        stopDialogueMusic();
        if (engine == null || engine.getWorld() == null) {
            return;
        }
        if (plants_vs_zombies.PlantsVSZombies.getPvz() == null
            || !(plants_vs_zombies.PlantsVSZombies.getPvz().getScreen() instanceof app.view.game.PlayScreen)) {
            return;
        }
        AudioCatalog.playLevelTheme(engine.getWorld().getCurrentChapter());
    }
}
