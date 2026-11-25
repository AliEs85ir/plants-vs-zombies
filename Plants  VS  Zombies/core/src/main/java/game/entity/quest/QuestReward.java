package game.entity.quest;

import app.enums.quest.RewardType;

public record QuestReward(RewardType type, int amount, String targetId) {

    public QuestReward(RewardType type, int amount) {
        this(type, amount, "CURRENCY");
    }
}
