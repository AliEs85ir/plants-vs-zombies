package app.view.izombie;

public final class IZombieReaction {
    public enum Kind { TEXT, EMOJI, STICKER }

    private final Kind kind;
    private final String id;
    private final String content;

    private IZombieReaction(Kind kind, String id, String content) {
        this.kind = kind;
        this.id = id;
        this.content = content;
    }

    public static IZombieReaction text(IZombieQuickMessage message) {
        return new IZombieReaction(Kind.TEXT, message.id(), message.text());
    }

    public static IZombieReaction emoji(IZombieQuickEmoji emoji) {
        return new IZombieReaction(Kind.EMOJI, emoji.id(), emoji.glyph());
    }

    public static IZombieReaction sticker(IZombieSticker sticker) {
        return new IZombieReaction(Kind.STICKER, sticker.id(), sticker.label());
    }

    public Kind kind() {
        return kind;
    }

    public String id() {
        return id;
    }

    public String content() {
        return content;
    }

    public String wireKind() {
        return kind.name();
    }
}
