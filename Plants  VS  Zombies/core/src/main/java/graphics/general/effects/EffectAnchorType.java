package graphics.general.effects;

/**
 * Where an effect should be positioned.
 * SELF   -> follows the entity that emitted the cue.
 * TARGET -> follows an explicitly provided target (gameplay-driven).
 * WORLD  -> fixed world coordinates, ignores offsets as world pos.
 */

public enum EffectAnchorType {
    SELF,
    TARGET,
    WORLD,
    KINEMATIC
}
