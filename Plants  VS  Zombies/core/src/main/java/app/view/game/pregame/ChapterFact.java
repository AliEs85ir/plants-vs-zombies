package app.view.game.pregame;

import game.world.chapter.Chapter;

import java.util.ArrayList;
import java.util.List;

public class ChapterFact {

    private final String text;

    public ChapterFact(String text) {
        this.text = (text != null) ? text : "";
    }

    public String getText() {
        return text;
    }

    public static List<ChapterFact> defaultFacts(Chapter chapter, int selectionCount,
                                                 int availableCount, int maxFacts) {
        List<ChapterFact> facts = new ArrayList<>();
        if (chapter == null) return facts;

        String difficulty = chapter.getDifficulty();
        if (difficulty != null && !difficulty.trim().isEmpty()) {
            facts.add(new ChapterFact(difficulty.trim().toUpperCase()));
        }

        facts.add(new ChapterFact(counted(chapter.getTotalWaves(), "WAVE", "WAVES")));

        if (chapter.getAvailableZombies() != null && !chapter.getAvailableZombies().isEmpty()) {
            facts.add(new ChapterFact(
                counted(chapter.getAvailableZombies().size(), "ZOMBIE", "ZOMBIES")));
        }

        facts.add(new ChapterFact("PICK " + selectionCount + " OF " + availableCount));

        int limit = Math.min(Math.max(1, maxFacts), facts.size());
        return new ArrayList<>(facts.subList(0, limit));
    }

    private static String counted(int amount, String singular, String plural) {
        return amount + " " + (amount == 1 ? singular : plural);
    }
}
