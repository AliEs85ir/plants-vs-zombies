package game.world.seasons;

public class Color {

    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
    public static final String RESET = "\u001B[0m";

    public static final String BRIGHT_BLACK = "\u001B[90m";
    public static final String BRIGHT_RED = "\u001B[91m";
    public static final String BRIGHT_GREEN = "\u001B[92m";
    public static final String BRIGHT_YELLOW = "\u001B[93m";
    public static final String BRIGHT_BLUE = "\u001B[94m";
    public static final String BRIGHT_MAGENTA = "\u001B[95m";
    public static final String BRIGHT_CYAN = "\u001B[96m";
    public static final String BRIGHT_WHITE = "\u001B[97m";

    public static final String PURPLE = "\u001B[38;5;93m";
    public static final String ORANGE = "\u001B[38;5;214m";


    public static final String PURPLE_ALT = "\u001B[38;5;129m";
    public static final String ORANGE_ALT = "\u001B[38;5;208m";

    public static String rgb(int r, int g, int b) {
        return String.format("\u001B[38;2;%d;%d;%dm", r, g, b);
    }


    public static final String PURPLE_RGB = rgb(128, 0, 128);
    public static final String DARK_PURPLE = rgb(75, 0, 130);
    public static final String ORANGE_RGB = rgb(255, 165, 0);
    public static final String DARK_ORANGE = rgb(255, 140, 0);

}
