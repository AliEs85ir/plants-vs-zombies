package game.entity.plant;

/**
 * Where a plant sits inside a cell stack. Damage and eating consume from
 * {@link #SHIELD} down to {@link #PLATFORM}.
 */
public enum PlantStackLayer {
    PLATFORM,
    CORE,
    REPEATABLE,
    SHIELD
}
