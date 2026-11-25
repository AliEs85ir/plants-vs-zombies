package game.dialogue;

public final class DialogueConfig {
    public static final float DEFAULT_RESUME_DELAY_SECONDS = 0.8f;
    public static final float SECONDS_PER_WORD = 0.35f;
    public static final float MIN_TALK_SECONDS = 0.55f;
    public static final float MAX_TALK_SECONDS = 4.0f;

    public static final float SMALLTALK_MAX_SECONDS = 0.8f;
    public static final float MEDIUMTALK_MAX_SECONDS = 2.0f;
    public static final float BLAHBLAH_MAX_SECONDS = 3.2f;

    public static final float NPC_HORIZONTAL_INSET_FRACTION = 0.16f;
    public static final float NPC_VERTICAL_FRACTION = 0.22f;

    public static final boolean ENABLE_FIRST_CHAPTER_DIALOGUE = true;
    public static final boolean ENABLE_BOSS_INTRO_DIALOGUE = true;
    public static final boolean ENABLE_BOSS_OUTRO_DIALOGUE = true;

    public static float DAVE_BUBBLE_X = 280f;
    public static float PENNY_BUBBLE_RIGHT_MARGIN = 48f;
    public static float BUBBLE_Y = 0f;

    public static float DIALOGUE_TEXT_OFFSET_X = 0f;
    public static float DIALOGUE_TEXT_OFFSET_Y = 22f;

    public static float DAVE_POSITION_OFFSET_X = 90f;
    public static float DAVE_POSITION_OFFSET_Y = 0f;
    public static float PENNY_POSITION_OFFSET_X = 0f;
    public static float PENNY_POSITION_OFFSET_Y = 0f;
    public static float ZOMBOSS_POSITION_OFFSET_X = -40f;
    public static float ZOMBOSS_POSITION_OFFSET_Y = 10f;

    public static float BOSS_BUBBLE_WORLD_OFFSET_Y = 140f;
    public static float BOSS_BUBBLE_TAIL_X_FRACTION = 0.07f;
    public static float BOSS_BUBBLE_TAIL_Y_FRACTION = 0.32f;

    private DialogueConfig() {}
}
