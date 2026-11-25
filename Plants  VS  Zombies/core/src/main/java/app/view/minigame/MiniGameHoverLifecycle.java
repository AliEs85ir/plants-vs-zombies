package app.view.minigame;

public final class MiniGameHoverLifecycle {
    public enum State {
        NOT_HOVERED,
        HOVER_START,
        HOVER_LOOP,
        HOVER_END
    }

    private final String startClip;
    private final String loopClip;
    private final String endClip;
    private final boolean loopWhileHovered;
    private State state = State.NOT_HOVERED;
    private boolean restartClip;

    public MiniGameHoverLifecycle(MiniGameMenuConfig.HoverAnimationConfig config) {
        MiniGameMenuConfig.HoverAnimationConfig safe = config == null
            ? new MiniGameMenuConfig.HoverAnimationConfig()
            : config;
        this.startClip = text(safe.startClip);
        this.loopClip = text(safe.resolvedLoopClip());
        this.endClip = text(safe.endClip);
        this.loopWhileHovered = safe.loop;
    }

    public MiniGameHoverLifecycle(String startClip, String loopClip, String endClip, boolean loopWhileHovered) {
        this.startClip = text(startClip);
        this.loopClip = text(loopClip);
        this.endClip = text(endClip);
        this.loopWhileHovered = loopWhileHovered;
    }

    public void enter() {
        if (state == State.HOVER_START || state == State.HOVER_LOOP) {
            return;
        }
        if (!startClip.isEmpty()) {
            setState(State.HOVER_START, true);
            return;
        }
        if (!loopClip.isEmpty()) {
            setState(State.HOVER_LOOP, true);
            return;
        }
        setState(State.NOT_HOVERED, false);
    }

    public void exit() {
        if (state == State.NOT_HOVERED || state == State.HOVER_END) {
            return;
        }
        if (!endClip.isEmpty()) {
            setState(State.HOVER_END, true);
            return;
        }
        setState(State.NOT_HOVERED, false);
    }

    public void onClipFinished() {
        if (state == State.HOVER_START) {
            if (!loopClip.isEmpty()) {
                setState(State.HOVER_LOOP, !loopClip.equals(startClip));
            } else {
                setState(State.HOVER_LOOP, false);
            }
            return;
        }
        if (state == State.HOVER_END) {
            setState(State.NOT_HOVERED, false);
        }
    }

    public State state() {
        return state;
    }

    public boolean visible() {
        return state != State.NOT_HOVERED;
    }

    public String clip() {
        return switch (state) {
            case HOVER_START -> startClip;
            case HOVER_LOOP -> loopClip.isEmpty() ? startClip : loopClip;
            case HOVER_END -> endClip;
            case NOT_HOVERED -> "";
        };
    }

    public boolean loop() {
        return state == State.HOVER_LOOP && loopWhileHovered;
    }

    public boolean consumeRestart() {
        boolean restart = restartClip;
        restartClip = false;
        return restart;
    }

    private void setState(State next, boolean restart) {
        state = next;
        restartClip = restart;
    }

    private static String text(String value) {
        return value == null ? "" : value.trim();
    }
}
