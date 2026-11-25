package app.enums.plant;

public enum PlantTag {
    DAY("Day"),
    NIGHT("Night"),
    SHROOM("Shroom"),
    WRAMP_UP("wramp-up"),
    PEA("Pea"),
    ICE("Ice"),
    FIRE("Fire"),
    STACK("Stack"),
    CHARGE("Charge"),
    MAGIC("Magic"),
    POISON("Poison"),
    WATER("Water"),
    AOE("AoE"),
    TRAP("Trap"),
    MOVE_ZOMBIES("moveZombies"),
    SUN("Sun"),
    EXPLOSIVE("Explosive"),
    GRAVE("Grave"),
    ICE_TERRAIN("Ice Terrain");

    private final String label;

    PlantTag(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }

    public static PlantTag fromLabel(String label) {
        if (label == null) return null;
        for (PlantTag tag : values()) {
            if (tag.label.equalsIgnoreCase(label.trim())) {
                return tag;
            }
        }
        return null;
    }
}
