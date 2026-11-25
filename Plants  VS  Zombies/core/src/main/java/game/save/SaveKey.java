package game.save;

import java.util.Objects;

public final class SaveKey {
    private final String userId;
    private final String chapterName;
    private final SaveSlotKind kind;
    private final int slotIndex;

    private SaveKey(String userId, String chapterName, SaveSlotKind kind, int slotIndex) {
        this.userId = userId == null || userId.isBlank() ? "guest" : userId.trim();
        this.chapterName = chapterName == null ? "" : chapterName.trim();
        this.kind = kind == null ? SaveSlotKind.MANUAL : kind;
        this.slotIndex = Math.max(0, slotIndex);
    }

    public static SaveKey manual(String userId, String chapterName) {
        return new SaveKey(userId, chapterName, SaveSlotKind.MANUAL, 0);
    }

    public static SaveKey of(String userId, String chapterName, SaveSlotKind kind, int slotIndex) {
        return new SaveKey(userId, chapterName, kind, slotIndex);
    }

    public String userId() {
        return userId;
    }

    public String chapterName() {
        return chapterName;
    }

    public SaveSlotKind kind() {
        return kind;
    }

    public int slotIndex() {
        return slotIndex;
    }

    public String fileName() {
        return kind.name().toLowerCase() + "-" + slotIndex + ".json";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof SaveKey other)) {
            return false;
        }
        return slotIndex == other.slotIndex
            && Objects.equals(userId, other.userId)
            && Objects.equals(chapterName, other.chapterName)
            && kind == other.kind;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, chapterName, kind, slotIndex);
    }
}
