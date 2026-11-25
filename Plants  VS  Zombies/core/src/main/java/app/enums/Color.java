package app.enums;

public enum Color {
    RED("Red"),
    GREEN("Green"),
    BLUE("Blue"),
    YELLOW("Yellow"),
    CYAN("Cyan"),
    MAGENTA("Magenta"),
    ORANGE("Orange"),
    PURPLE("Purple"),
    PINK("Pink"),
    BROWN("Brown"),
    BLACK("Black"),
    WHITE("White"),
    GRAY("Gray"),
    LIME("Lime"),
    INDIGO("Indigo"),
    VIOLET("Violet"),
    TEAL("Teal"),
    MAROON("Maroon"),
    NAVY("Navy"),
    OLIVE("Olive")
    ;

    private final String label;

    Color(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }

    public static Color parseColor(String input) {
        if (input == null)
            return null;

        input = input.trim();

        for (Color color : Color.values()) {
            if (color.toString().equalsIgnoreCase(input))
                return color;
        }

        return null;
    }
}
