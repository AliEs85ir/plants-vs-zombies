package app.view.hud.notification;

public class WordCountDurationPolicy implements DurationPolicy {
    private static final float BASE_SECONDS = 1.6f;
    private static final float SECONDS_PER_WORD = 0.4f;
    private static final float MIN_SECONDS = 2.0f;
    private static final float MAX_SECONDS = 8.0f;

    @Override
    public float durationSeconds(String text) {
        int words = countWords(text);
        float duration = BASE_SECONDS + words * SECONDS_PER_WORD;
        if (duration < MIN_SECONDS) {
            return MIN_SECONDS;
        }
        if (duration > MAX_SECONDS) {
            return MAX_SECONDS;
        }
        return duration;
    }

    private int countWords(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        int count = 0;
        for (String part : text.trim().split("\\s+")) {
            if (!part.isEmpty()) {
                count++;
            }
        }
        return count;
    }
}
