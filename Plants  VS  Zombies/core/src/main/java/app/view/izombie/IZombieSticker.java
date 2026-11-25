package app.view.izombie;

public final class IZombieSticker {
    private final String id;
    private final String label;
    private final String drawableName;

    public IZombieSticker(String id, String label, String drawableName) {
        this.id = id;
        this.label = label;
        this.drawableName = drawableName;
    }

    public String id() {
        return id;
    }

    public String label() {
        return label;
    }

    /** Skin drawable name for the sticker art. May be null while art has not been added yet. */
    public String drawableName() {
        return drawableName;
    }
}
