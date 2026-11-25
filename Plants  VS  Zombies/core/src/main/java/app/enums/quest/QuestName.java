package app.enums.quest;

public enum QuestName {
    DAILY_SUN_COLLECTOR("Daily Sun Collector"),
    CHAPTER_HUNTER("Chapter Hunter"),
    PLANT_EXPERT("Plant Expert"),
    CACTUS_ONLY("Cactus Only"),
    ECONOMICAL_GARDENER("Economical Gardener"),
    DEFENSE_MASTER("Defense Master"),
    QUICK_ACTION("Quick Action"),
    DEMOLITION_EXPERT("Demolition Expert"),
    SYMMETRY("Symmetry"),
    FAMILY_HUNTER("Family Hunter"),
    LIMITED_BLOOM("Limited Bloom"),
    DAY_OR_NIGHT("Day or Night"),
    WIN_STREAK("Win Streak"),
    ALMOST_VICTORY("Almost Victory"),
    OCD_MASTER("OCD Master"),
    CLOUDY_DAY("Cloudy Day"),
    ONE_COLUMN_LESS("One Column Less"),
    DEFENSELESS_ROW("Defenseless Row"),
    DEFENSELESS_CROSS("Defenseless Cross"),
    LAWN_MOWER_TIME("Lawn Mower Time");

    private final String label;

    QuestName(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    public static QuestName parseQuestName(String input) {
        if (input == null) return null;
        input = input.trim();
        for (QuestName quest : QuestName.values()) {
            if (quest.toString().equalsIgnoreCase(input)) return quest;
        }
        return null;
    }
}
