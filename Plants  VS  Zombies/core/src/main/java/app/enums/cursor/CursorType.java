package app.enums.cursor;

public enum CursorType {
    DEFAULT("cursor/default.png", 0.50f, 0.06f),
    HOVER("cursor/hover.png", 0.50f, 0.06f),
    PRESSED("cursor/pressed.png", 0.50f, 0.06f),
    PLANTING("cursor/planting.png", 0.90f, 0.08f),
    SHOVEL("cursor/shovel.png", 0.14f, 0.10f),
    TARGET("cursor/target.png", 0.50f, 0.50f),
    PLANT_FOOD("cursor/plantfood.png", 0.50f, 0.50f);

    private final String assetPath;
    private final float hotspotX;
    private final float hotspotY;

    CursorType(String assetPath, float hotspotX, float hotspotY) {
        this.assetPath = assetPath;
        this.hotspotX = hotspotX;
        this.hotspotY = hotspotY;
    }

    public String assetPath() {
        return assetPath;
    }

    public float hotspotX() {
        return hotspotX;
    }

    public float hotspotY() {
        return hotspotY;
    }
}
