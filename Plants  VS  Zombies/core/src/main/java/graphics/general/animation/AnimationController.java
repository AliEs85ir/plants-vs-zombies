package graphics.general.animation;

import graphics.general.effects.EffectCue;
import pvz.libpvz.pam.ClipRef;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AnimationController<V extends AnimationVariant<?, ?>> {

    private V currentVariant;
    private ClipRef currentClip;
    private String currentPamPath;
    private float stateTime;
    private float timeScale;
    private boolean finished;

    // NEW: edge-detection
    private boolean justStarted;
    private boolean justFinished;

    // NEW: cue state, one flag per cue
    private boolean[] cueFired;
    private int loopCount;
    private final List<EffectCue> cueQueue = new ArrayList<>();

    public AnimationController() {
        this.stateTime = 0.0f;
        this.finished = false;
        this.timeScale = 1f;
    }

    public void update(float delta) {
        boolean startedThisFrame = justStarted;
        justStarted = false;
        justFinished = false;


        if (finished) return;
        this.stateTime += delta * timeScale;

        if (currentClip != null &&
            currentVariant != null &&
            currentVariant.isLoop() &&
            currentClip.duration > 0f)
        {
            int newLoops = (int) (this.stateTime / currentClip.duration);
            if (newLoops > loopCount) {
                loopCount = newLoops;
                resetPerLoopCues();
            }
        }
        checkAnimationCompletion();
        boolean finishedThisFrame = justFinished;
        evaluateCues(startedThisFrame, finishedThisFrame);
    }

    // Now uses V instead of AnimationVariant<?, ?>
    public boolean changeVariant(V newVariant, ClipRef newClip, String pamPath) {
        if (newVariant == null) return false;
        if (isSameVariant(newVariant) && Objects.equals(this.currentClip, newClip)) return false;

        this.currentVariant = newVariant;
        this.currentClip = newClip;
        this.currentPamPath = pamPath;
        this.stateTime = 0.0f;
        this.finished = false;

        this.cueFired = new boolean[newVariant.getEffectCues().size()];
        this.loopCount = 0;
        this.cueQueue.clear();
        this.justStarted = true;
        this.justFinished = false;

        return true;
    }

    private void evaluateCues(boolean startedThisFrame, boolean finishedThisFrame) {
        if (currentVariant == null) {
            return;
        }
        List<EffectCue> cues = currentVariant.getEffectCues();
        for (int i = 0; i < cues.size(); i++) {
            if (cueFired[i]) {
                continue;
            }
            EffectCue cue = cues.get(i);
            switch (cue.getTrigger()) {
                case ON_START:
                    if (startedThisFrame) {
                        fire(i, cue);
                    }
                    break;
                case ON_FINISH:
                    if (finishedThisFrame) {
                        fire(i, cue);
                    }
                    break;
                case AFTER_DELAY:
                    if (stateTime >= cue.getDelay()) {
                        fire(i, cue);
                    }
                    break;
                case AT_NORMALIZED_TIME:
                    if (currentClip != null && stateTime >= cue.getNormalizedTime() * currentClip.duration) {
                        fire(i, cue);
                    }
                    break;
                default:
                    break;
            }
        }
    }


    private void fire(int index, EffectCue cue) {
        cueFired[index] = true;
        cueQueue.add(cue);
    }

    private void resetPerLoopCues() {
        if (currentVariant == null) {
            return;
        }
        List<EffectCue> cues = currentVariant.getEffectCues();
        for (int i = 0; i < cues.size(); i++) {
            if (cues.get(i).isPerLoop()) {
                cueFired[i] = false;
            }
        }
    }

    public List<EffectCue> pollReadyCues() {
        if (cueQueue.isEmpty()) {
            return List.of();
        }
        List<EffectCue> out = new ArrayList<>(cueQueue);
        cueQueue.clear();
        return out;
    }

    private void checkAnimationCompletion() {
        if (isNonLoopingFinished()) {
            this.finished = true;
            this.justFinished = true;
        }
    }

    private boolean isNonLoopingFinished() {
        return currentVariant != null
            && !currentVariant.isLoop()
            && currentClip != null
            && stateTime >= currentClip.duration ;
    }

    private boolean isSameVariant(V newVariant) {
        return currentVariant != null && currentVariant.equals(newVariant);
    }

    public V getCurrentVariant() { return currentVariant; }
    public ClipRef getCurrentClip() { return currentClip; }
    public int getLoopCount() { return loopCount; }
    public String getCurrentPamPath() { return currentPamPath; }
    public float getStateTime() { return stateTime; }
    public boolean isFinished() { return finished; }
    public void setFinished(boolean finished) { this.finished = finished; }
    public void setTimeScale(float timeScale) {this.timeScale = Math.max(0f, timeScale);}
    public float getTimeScale() {return timeScale;}
    public void pauseAnimation() {this.timeScale = 0f;}
    public void resumeAnimation() {this.timeScale = 1f;}
}
