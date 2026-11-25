package game.service.drop;

public final class DropSettings {
    public static final float NOTHING_CHANCE = 0.16f;
    public static final float SILVER_CHANCE = 0.16f;
    public static final float GOLD_CHANCE = 0.16f;
    public static final float PLANT_FOOD_CHANCE = 0.16f;
    public static final float POT_CHANCE = 0.16f;
    public static final float DIAMOND_CHANCE = 0.2f;

    /**
     * BETTER :
     * NOTHING -> 0.7
     * SILVER -> 0.15
     * GOLD -> 0.08
     * PLANT FOOD -> 0.04
     * POT -> 0.02
     * DIAMOND -> 0.01
      */

    public static final float LAUNCH_SECONDS = 0.55f;
    public static final float LAUNCH_HEIGHT = 72f;
    public static final float MAX_HORIZONTAL_DRIFT = 28f;
    public static final float BOB_AMPLITUDE = 8f;
    public static final float BOB_SPEED = 3.6f;
    public static final float LIFETIME_SECONDS = 10f;
    public static final float COLLECT_RADIUS = 44f;
    public static final float SPAWN_Y_OFFSET = 36f;

    private DropSettings() {
    }
}
