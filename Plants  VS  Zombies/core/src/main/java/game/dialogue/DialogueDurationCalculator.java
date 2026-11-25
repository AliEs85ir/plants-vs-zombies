package game.dialogue;

public final class DialogueDurationCalculator {
    private final float secondsPerWord;
    private final float minSeconds;
    private final float maxSeconds;

    public DialogueDurationCalculator() {
        this(
            DialogueConfig.SECONDS_PER_WORD,
            DialogueConfig.MIN_TALK_SECONDS,
            DialogueConfig.MAX_TALK_SECONDS
        );
    }

    public DialogueDurationCalculator(float secondsPerWord, float minSeconds, float maxSeconds) {
        this.secondsPerWord = Math.max(0f, secondsPerWord);
        this.minSeconds = Math.max(0f, minSeconds);
        this.maxSeconds = Math.max(this.minSeconds, maxSeconds);
    }

    public float calculate(String text) {
        int words = countWords(text);
        float duration = words * secondsPerWord;
        if (duration < minSeconds) {
            return minSeconds;
        }
        if (duration > maxSeconds) {
            return maxSeconds;
        }
        return duration;
    }

    public static int countWords(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        int count = 0;
        for (String token : text.trim().split("\\s+")) {
            if (!token.isEmpty()) {
                count++;
            }
        }
        return count;
    }
}
