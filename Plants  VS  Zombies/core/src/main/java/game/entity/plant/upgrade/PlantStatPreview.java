package game.entity.plant.upgrade;

public final class PlantStatPreview {
    private final String id;
    private final String currentValue;
    private final String nextValue;
    private final boolean changed;

    public PlantStatPreview(String id, String currentValue, String nextValue, boolean changed) {
        this.id = id;
        this.currentValue = currentValue == null ? "" : currentValue;
        this.nextValue = nextValue == null ? this.currentValue : nextValue;
        this.changed = changed;
    }

    public String id() {
        return id;
    }

    public String currentValue() {
        return currentValue;
    }

    public String nextValue() {
        return nextValue;
    }

    public boolean changed() {
        return changed;
    }
}
