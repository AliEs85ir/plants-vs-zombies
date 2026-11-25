package graphics.general.event;

public class VisualEvent <E extends Enum<E>>{
    private final E type;
    private final float intensity;
    private final long timestamp;

    public VisualEvent(E type, float intensity) {
        this.type = type;
        this.intensity = intensity;
        this.timestamp = System.currentTimeMillis();
    }

    public VisualEvent(E type) {
        this(type, 1.0f);
    }

    public E getType() {
        return type;
    }

    public float getIntensity() {
        return intensity;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
