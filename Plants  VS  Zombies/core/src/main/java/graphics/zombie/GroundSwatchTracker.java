package graphics.zombie;

import com.badlogic.gdx.math.Rectangle;
import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.pam.PamPlayer;

public final class GroundSwatchTracker {
    public static final String PART_NAME = "ground_swatch";

    private ClipRef bakedFor;
    private float[] swatchX;
    private float clipDuration;
    private int lastFrame = -1;

    public void syncClip(PamPlayer player, ClipRef clip) {
        if (clip == bakedFor) {
            return;
        }
        bakedFor = clip;
        lastFrame = -1;
        swatchX = bake(player, clip);
        clipDuration = clip != null ? clip.duration : 0f;
    }

    public boolean isReady() {
        return swatchX != null && swatchX.length > 1 && clipDuration > 0f;
    }

    public float consumeWorldDeltaX(float stateTime) {
        if (!isReady()) {
            return 0f;
        }
        int frame = frameIndex(stateTime);
        if (lastFrame < 0) {
            lastFrame = frame;
            return 0f;
        }
        if (frame < lastFrame) {
            lastFrame = frame;
            return 0f;
        }
        float delta = swatchX[lastFrame] - swatchX[frame];
        lastFrame = frame;
        return delta;
    }

    static GroundSwatchTracker withCurve(float[] curve, float duration) {
        GroundSwatchTracker tracker = new GroundSwatchTracker();
        tracker.swatchX = curve;
        tracker.clipDuration = duration;
        tracker.lastFrame = -1;
        return tracker;
    }

    private int frameIndex(float stateTime) {
        int span = swatchX.length;
        float wrapped = stateTime % clipDuration;
        if (wrapped < 0f) {
            wrapped += clipDuration;
        }
        int frame = (int) Math.floor(wrapped / clipDuration * span);
        if (frame < 0) {
            return 0;
        }
        if (frame >= span) {
            return span - 1;
        }
        return frame;
    }

    private static float[] bake(PamPlayer player, ClipRef clip) {
        if (player == null || clip == null) {
            return null;
        }
        Rectangle[] frames = player.partBoundsByFrame(clip, PART_NAME);
        if (frames == null || frames.length < 2) {
            return null;
        }
        float[] xs = new float[frames.length];
        Float last = null;
        int valid = 0;
        for (int i = 0; i < frames.length; i++) {
            Rectangle bounds = frames[i];
            if (bounds != null) {
                last = bounds.x + bounds.width * 0.5f;
                valid++;
            }
            xs[i] = last != null ? last : 0f;
        }
        return valid >= 2 ? xs : null;
    }
}
