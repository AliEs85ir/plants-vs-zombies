package graphics.general.effects;

public enum EffectTrigger {
    /** Fire the frame the clip starts. */
    ON_START,
    /** Fire the frame a non-looping clip finishes. */
    ON_FINISH,
    /** Fire once the clip has played `normalizedTime` (0..1) of its duration. */
    AT_NORMALIZED_TIME,
    /** Fire once `delay` seconds have passed from clip start. */
    AFTER_DELAY
}
