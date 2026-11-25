package app.enums.plant;

public enum PlantCategory {
    SUN_PRODUCER("Sun Producer"),
    SHOOTER("Shooter"),
    HOMING("Homing"),
    STRIKE_THROUGH("Strike-through"),
    LOBBER("Lobber"),
    EXPLOSIVE("Explosive"),
    MELEE("Melee"),
    WALL_NUT("Wall-nut"),
    MODIFIER("Modifier");

    private final String label;

    PlantCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }

    public static PlantCategory fromLabel(String label) {
        if (label == null) return null;
        for (PlantCategory cat : values()) {
            if (cat.label.equalsIgnoreCase(label.trim())) {
                return cat;
            }
        }
        return null;
    }
}
