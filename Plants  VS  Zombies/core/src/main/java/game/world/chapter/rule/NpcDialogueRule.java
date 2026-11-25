package game.world.chapter.rule;

import game.component.physic.PhysicService;
import game.dialogue.DialogueConfig;
import game.dialogue.DialogueLine;
import game.dialogue.trigger.BossChapterTrigger;
import game.dialogue.trigger.BossDeathTrigger;
import game.dialogue.trigger.DialogueTrigger;
import game.dialogue.trigger.DialogueTriggerContext;
import game.dialogue.trigger.GameStartTrigger;
import game.engine.GameSimulationEngine;
import game.world.GameWorld;
import graphics.GraphicsEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NpcDialogueRule implements ChapterRule {
    private final DialogueTrigger trigger;
    private final List<DialogueLine> dialogues;
    private final float resumeDelaySeconds;
    private final boolean enabled;
    private final boolean spawnNpcs;
    private boolean consumed;

    public NpcDialogueRule(DialogueTrigger trigger, List<DialogueLine> dialogues, boolean enabled) {
        this(trigger, dialogues, DialogueConfig.DEFAULT_RESUME_DELAY_SECONDS, enabled);
    }

    public NpcDialogueRule(
        DialogueTrigger trigger,
        List<DialogueLine> dialogues,
        float resumeDelaySeconds,
        boolean enabled
    ) {
        this(trigger, dialogues, resumeDelaySeconds, enabled, true);
    }

    public NpcDialogueRule(
        DialogueTrigger trigger,
        List<DialogueLine> dialogues,
        float resumeDelaySeconds,
        boolean enabled,
        boolean spawnNpcs
    ) {
        this.trigger = trigger;
        this.dialogues = dialogues != null
            ? Collections.unmodifiableList(new ArrayList<>(dialogues))
            : List.of();
        this.resumeDelaySeconds = Math.max(0f, resumeDelaySeconds);
        this.enabled = enabled;
        this.spawnNpcs = spawnNpcs;
    }

    public static NpcDialogueRule gameStart(List<DialogueLine> dialogues, boolean enabled) {
        return new NpcDialogueRule(new GameStartTrigger(), dialogues, enabled);
    }

    public static NpcDialogueRule bossIntro(List<DialogueLine> dialogues, boolean enabled) {
        return new NpcDialogueRule(new BossChapterTrigger(), dialogues, enabled);
    }

    public static NpcDialogueRule bossOutro(List<DialogueLine> dialogues, boolean enabled) {
        return new NpcDialogueRule(
            new BossDeathTrigger(),
            dialogues,
            DialogueConfig.DEFAULT_RESUME_DELAY_SECONDS,
            enabled,
            false
        );
    }

    public boolean spawnsNpcs() {
        return spawnNpcs;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean consumeIfShouldFire(DialogueTriggerContext context) {
        if (!enabled || consumed || trigger == null || !trigger.shouldFire(context)) {
            return false;
        }
        if (dialogues.isEmpty()) {
            consumed = true;
            return false;
        }
        consumed = true;
        return true;
    }

    public List<DialogueLine> getDialogues() {
        return dialogues;
    }

    public float getResumeDelaySeconds() {
        return resumeDelaySeconds;
    }

    public boolean isConsumed() {
        return consumed;
    }

    @Override
    public void onChapterStart(GameWorld world, GraphicsEngine graphicsEngine, PhysicService physicService) {}

    @Override
    public void onTick(GameWorld world, long currentTick) {}

    @Override
    public void onEngineStart(GameSimulationEngine engine) {}
}
