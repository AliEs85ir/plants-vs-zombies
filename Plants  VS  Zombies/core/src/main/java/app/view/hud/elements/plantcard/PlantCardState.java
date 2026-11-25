package app.view.hud.elements.plantcard;

public enum PlantCardState {

    NORMAL(null),
    BOOSTED("IMAGE_UI_PACKETS_BOOST");

    private final String backgroundKeyOverride;

    PlantCardState(String backgroundKeyOverride) {
        this.backgroundKeyOverride = backgroundKeyOverride;
    }

    public String resolveBackgroundKey(String defaultKey) {
        return (backgroundKeyOverride != null) ? backgroundKeyOverride : defaultKey;
    }

    public boolean hasOverride() {
        return backgroundKeyOverride != null;
    }
}
