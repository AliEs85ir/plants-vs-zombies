package app.view.loading;

public final class LoadingProgressModel {
    private float actual;
    private float displayed;
    private boolean complete;
    private boolean failed;

    public void setActual(float value) {
        actual = clamp01(value);
        if (!complete && !failed) {
            actual = Math.min(actual, 0.999f);
        }
    }

    public void markComplete() {
        complete = true;
        failed = false;
        actual = 1f;
    }

    public void markFailed() {
        failed = true;
        complete = false;
    }

    public float update(float delta, float lerpSpeed, float incompleteCap) {
        float cap = complete ? 1f : Math.min(0.999f, incompleteCap);
        float target = Math.min(actual, cap);
        if (complete) {
            target = 1f;
        }
        float speed = Math.max(0.01f, lerpSpeed);
        float t = 1f - (float) Math.exp(-speed * Math.max(0f, delta));
        displayed += (target - displayed) * t;
        if (complete && displayed > 0.999f) {
            displayed = 1f;
        }
        if (!complete) {
            displayed = Math.min(displayed, cap);
        }
        return displayed;
    }

    public float displayed() {
        return displayed;
    }

    public float actual() {
        return actual;
    }

    public boolean isComplete() {
        return complete;
    }

    public boolean isFailed() {
        return failed;
    }

    private static float clamp01(float value) {
        if (value < 0f) {
            return 0f;
        }
        if (value > 1f) {
            return 1f;
        }
        return value;
    }
}
