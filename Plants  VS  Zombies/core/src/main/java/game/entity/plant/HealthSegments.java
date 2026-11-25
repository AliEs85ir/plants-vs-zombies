package game.entity.plant;

public final class HealthSegments {
    public static final int QUARTERS = 4;
    public static final int THIRDS = 3;

    private HealthSegments() {
    }

    public static int index(PlantHealth health, int segmentCount) {
        if (health == null) {
            return lastIndex(segmentCount);
        }
        return index(health.getCurrentHealth(), health.getMaxHealth(), segmentCount);
    }

    public static int index(double currentHealth, int maxHealth, int segmentCount) {
        int last = lastIndex(segmentCount);
        if (maxHealth <= 0 || currentHealth <= 0d) {
            return last;
        }
        double ratio = currentHealth / maxHealth;
        if (ratio >= 1d) {
            return 0;
        }
        int index = (int) Math.floor((1.0d - ratio) * segmentCount);
        if (index < 0) {
            return 0;
        }
        return Math.min(index, last);
    }

    public static boolean matches(PlantHealth health, int expectedSegment, int segmentCount) {
        return index(health, segmentCount) == expectedSegment;
    }

    private static int lastIndex(int segmentCount) {
        return Math.max(0, segmentCount - 1);
    }
}
