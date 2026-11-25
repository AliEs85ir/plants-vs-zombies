package app.view.izombie;

public final class IZombieQuickEmoji {
    private final String id;
    private final String glyph;
    private final String drawableName;

    public IZombieQuickEmoji(String id, String glyph, String drawableName) {
        this.id = id;
        this.glyph = glyph;
        this.drawableName = drawableName;
    }

    public String id() {
        return id;
    }

    /** Text fallback used only when the real icon (see {@link #drawableName()}) isn't found. */
    public String glyph() {
        return glyph;
    }

    /** Asset id of the emoji's real icon image, looked up via AssetManager.textureBank. May be null. */
    public String drawableName() {
        return drawableName;
    }
}
