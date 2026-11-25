package app.enums.drop;

public enum DropType {
    GOLD_COIN(
        100,
        "768/INITIAL/EFFECTS/COIN_GOLD/COIN_GOLD.PAM",
        "animation",
        null,
        1f
    ),
    SILVER_COIN(
        50,
        "768/INITIAL/EFFECTS/COIN_SILVER/COIN_SILVER.PAM",
        "animation",
        null,
        1f
    ),
    DIAMOND(
        1,
        "768/INITIAL/EFFECTS/COIN_DIAMOND/COIN_DIAMOND.PAM",
        "idle",
        null,
        0.45f
    ),
    PLANT_FOOD(0, null, null, null, 1f),
    POT(0, null, null, null, 1f);

    public static final String IMAGE_PATH_PLANT_FOOD = "drops/plant_food.png";
    public static final String IMAGE_PATH_POT = "drops/plant_pot.png";

    private final int value;
    private final String pamPath;
    private final String clipName;
    private final String imagePath;
    private final float scale;

    DropType(int value, String pamPath, String clipName, String imagePath, float scale) {
        this.value = value;
        this.pamPath = pamPath;
        this.clipName = clipName;
        this.imagePath = imagePath;
        this.scale = scale;
    }

    public int getValue() {
        return value;
    }

    public String getPamPath() {
        return pamPath;
    }

    public String getClipName() {
        return clipName;
    }

    public String getImagePath() {
        return switch (this) {
            case PLANT_FOOD -> IMAGE_PATH_PLANT_FOOD;
            case POT -> IMAGE_PATH_POT;
            default -> imagePath;
        };
    }

    public float getScale() {
        return scale;
    }

    public boolean usesPam() {
        return pamPath != null && !pamPath.isBlank();
    }

    public boolean usesImage() {
        String path = getImagePath();
        return path != null && !path.isBlank();
    }
}
