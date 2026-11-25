package app.view.izombie;

public final class IZombieQuickMessage {
    private final String id;
    private final String text;

    public IZombieQuickMessage(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public String id() {
        return id;
    }

    public String text() {
        return text;
    }
}
