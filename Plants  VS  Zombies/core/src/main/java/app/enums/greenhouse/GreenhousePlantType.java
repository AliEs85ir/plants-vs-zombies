package app.enums.greenhouse;

public enum GreenhousePlantType {
    MARIGOLD ("Marigold plant"),
    BOOST ("Boost plant")
    ;

    private final String label;

    GreenhousePlantType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }

    public static GreenhousePlantType parseGreenhousePlantType(String input) {
        if (input == null)
            return null;

        input = input.trim();

        for (GreenhousePlantType type : GreenhousePlantType.values()) {
            if (type.toString().equalsIgnoreCase(input))
                return type;
        }

        return null;
    }
}
