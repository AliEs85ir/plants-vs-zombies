package game.entity.plant;

public enum PlantRole {

    SUN,
    OFFENSE,
    DEFENSE,
    INSTANT,
    SUPPORT;

    public static PlantRole parse(String raw) {
        if (raw == null || raw.isEmpty()) return null;
        try {
            return valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
