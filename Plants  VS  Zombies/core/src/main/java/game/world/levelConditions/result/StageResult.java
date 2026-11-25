package game.world.levelConditions.result;

import app.enums.SeasonType;
import app.enums.plant.PlantName;
import game.entity.quest.Quest;
import game.world.chapter.Chapter;
import game.world.levelConditions.conditions.LevelOutcome;

import java.util.ArrayList;
import java.util.List;

public final class StageResult {
    private final LevelOutcome outcome;
    private final Chapter chapter;
    private final StageRunStatistics statistics;
    private final boolean firstClear;
    private final boolean boughtWin;
    private final List<PlantName> newlyUnlockedPlants;
    private final List<String> newlyDiscoveredZombies;
    private final List<Quest> changedQuests;
    private final List<StageRewardGrant> rewards;
    private final SeasonType seasonType;
    private final int stageNumber;
    private final Integer nextStageNumber;

    private StageResult(Builder builder) {
        this.outcome = builder.outcome;
        this.chapter = builder.chapter;
        this.statistics = builder.statistics != null
            ? builder.statistics
            : new StageRunStatistics(0, 0, 0, 0d);
        this.firstClear = builder.firstClear;
        this.boughtWin = builder.boughtWin;
        this.newlyUnlockedPlants = List.copyOf(builder.newlyUnlockedPlants);
        this.newlyDiscoveredZombies = List.copyOf(builder.newlyDiscoveredZombies);
        this.changedQuests = List.copyOf(builder.changedQuests);
        this.rewards = List.copyOf(builder.rewards);
        this.seasonType = builder.seasonType;
        this.stageNumber = builder.stageNumber;
        this.nextStageNumber = builder.nextStageNumber;
    }

    public LevelOutcome getOutcome() {
        return outcome;
    }

    public boolean isWin() {
        return outcome == LevelOutcome.PLAYER_WON;
    }

    public boolean isLose() {
        return outcome == LevelOutcome.PLAYER_LOST;
    }

    public Chapter getChapter() {
        return chapter;
    }

    public StageRunStatistics getStatistics() {
        return statistics;
    }

    public boolean isFirstClear() {
        return firstClear;
    }

    public boolean isBoughtWin() {
        return boughtWin;
    }

    public List<PlantName> getNewlyUnlockedPlants() {
        return newlyUnlockedPlants;
    }

    public List<String> getNewlyDiscoveredZombies() {
        return newlyDiscoveredZombies;
    }

    public List<Quest> getChangedQuests() {
        return changedQuests;
    }

    public List<StageRewardGrant> getRewards() {
        return rewards;
    }

    public SeasonType getSeasonType() {
        return seasonType;
    }

    public int getStageNumber() {
        return stageNumber;
    }

    public Integer getNextStageNumber() {
        return nextStageNumber;
    }

    public boolean hasNextStage() {
        return nextStageNumber != null && nextStageNumber > 0;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private LevelOutcome outcome = LevelOutcome.NONE;
        private Chapter chapter;
        private StageRunStatistics statistics;
        private boolean firstClear;
        private boolean boughtWin;
        private final List<PlantName> newlyUnlockedPlants = new ArrayList<>();
        private final List<String> newlyDiscoveredZombies = new ArrayList<>();
        private final List<Quest> changedQuests = new ArrayList<>();
        private final List<StageRewardGrant> rewards = new ArrayList<>();
        private SeasonType seasonType;
        private int stageNumber;
        private Integer nextStageNumber;

        public Builder outcome(LevelOutcome outcome) {
            this.outcome = outcome;
            return this;
        }

        public Builder chapter(Chapter chapter) {
            this.chapter = chapter;
            return this;
        }

        public Builder statistics(StageRunStatistics statistics) {
            this.statistics = statistics;
            return this;
        }

        public Builder firstClear(boolean firstClear) {
            this.firstClear = firstClear;
            return this;
        }

        public Builder boughtWin(boolean boughtWin) {
            this.boughtWin = boughtWin;
            return this;
        }

        public Builder newlyUnlockedPlants(List<PlantName> plants) {
            newlyUnlockedPlants.clear();
            if (plants != null) {
                newlyUnlockedPlants.addAll(plants);
            }
            return this;
        }

        public Builder newlyDiscoveredZombies(List<String> zombies) {
            newlyDiscoveredZombies.clear();
            if (zombies != null) {
                newlyDiscoveredZombies.addAll(zombies);
            }
            return this;
        }

        public Builder changedQuests(List<Quest> quests) {
            changedQuests.clear();
            if (quests != null) {
                changedQuests.addAll(quests);
            }
            return this;
        }

        public Builder rewards(List<StageRewardGrant> grants) {
            rewards.clear();
            if (grants != null) {
                rewards.addAll(grants);
            }
            return this;
        }

        public Builder seasonType(SeasonType seasonType) {
            this.seasonType = seasonType;
            return this;
        }

        public Builder stageNumber(int stageNumber) {
            this.stageNumber = stageNumber;
            return this;
        }

        public Builder nextStageNumber(Integer nextStageNumber) {
            this.nextStageNumber = nextStageNumber;
            return this;
        }

        public StageResult build() {
            return new StageResult(this);
        }
    }
}
