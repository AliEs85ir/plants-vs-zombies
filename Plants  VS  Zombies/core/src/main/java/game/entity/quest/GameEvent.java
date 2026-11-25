package game.entity.quest;

import app.enums.quest.EventType;

public record GameEvent(
    EventType type,
    int amount,
    String plantName,
    String familyType,
    int chapter,
    int timeInSeconds,
    int difficulty,
    boolean isFirstColNoMower
) {
    public GameEvent(EventType type, int amount) {
        this(type, amount, null, null, -1, -1, -1, false);
    }

    public boolean plantIs(String name) {
        return name != null && name.equalsIgnoreCase(plantName);
    }

    public boolean familyIs(String family) {
        return family != null && family.equalsIgnoreCase(familyType);
    }

    public boolean chapterIs(int targetChapter) {
        return targetChapter != -1 && chapter == targetChapter;
    }

    public boolean withinSeconds(int maxSeconds) {
        if (maxSeconds < 0) {
            return true;
        }
        return timeInSeconds >= 0 && timeInSeconds < maxSeconds;
    }

    public boolean difficultyIs(int targetDifficulty) {
        return targetDifficulty < 0 || difficulty == targetDifficulty;
    }
}
