package app.enums.minigame;

public enum WalnutPlantType {
    BOWLING_WALLNUT("Bowling Wallnut"),
    EXPLODE_O_NUT("Explode O' Nut"),
    GIANT_WALLNUT("Giant Wallnut");

    private final String label;

    WalnutPlantType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }

    public static WalnutPlantType fromLabel(String label) {
        if (label == null) return null;
        for (WalnutPlantType type : values()) {
            if (type.label.equalsIgnoreCase(label.trim())) {
                return type;
            }
        }
        return null;
    }
}
