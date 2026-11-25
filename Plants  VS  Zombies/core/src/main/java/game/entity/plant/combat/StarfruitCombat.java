package game.entity.plant.combat;

/**
 * Single source of truth for Starfruit's five star directions (normal and plant food).
 */
public final class StarfruitCombat {
    public record Shot(double angleDegrees, float offsetX, float offsetY) {
    }

    public static final Shot[] SHOTS = {
        new Shot(180, -55f, 5f),
        new Shot(252, -17f, -50f),
        new Shot(324, 55f, -35f),
        new Shot(396, 50f, 22f),
        new Shot(468, -15f, 45f)
    };

    private StarfruitCombat() {
    }
}
