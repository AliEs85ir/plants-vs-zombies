package game.service.quest;

import app.enums.plant.PlantCategory;
import app.enums.plant.PlantName;
import app.enums.quest.QuestCategory;
import app.enums.quest.QuestName;
import app.model.App;
import app.model.user.User;
import app.service.AppService;
import app.service.ProgressService;
import game.entity.plant.PlantDefinition;
import game.entity.quest.GameEvent;
import game.entity.quest.LevelFacts;
import game.entity.quest.Quest;
import game.entity.quest.QuestReward;
import game.entity.registry.PlantRegistry;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

public class QuestManager {
    private static final long PERSIST_INTERVAL_NANOS = 1_000_000_000L;
    private static final EnumSet<PlantCategory> COMBAT_FAMILIES = EnumSet.of(
        PlantCategory.SHOOTER,
        PlantCategory.HOMING,
        PlantCategory.STRIKE_THROUGH,
        PlantCategory.LOBBER,
        PlantCategory.EXPLOSIVE,
        PlantCategory.MELEE
    );

    private final List<Quest> activeQuests;
    private final List<Quest> completedQuests;
    private LocalDate lastResetDate;
    private final Random random;
    private transient long lastPersistNanos;

    public QuestManager() {
        this.activeQuests = new ArrayList<>();
        this.completedQuests = new ArrayList<>();
        this.lastResetDate = LocalDate.now();
        this.random = new Random();
    }

    public void addQuest(Quest quest) {
        activeQuests.add(quest);
    }

    public void fireEvent(GameEvent event) {
        checkDailyReset();
        boolean changed = false;
        for (Quest quest : activeQuests) {
            int before = progressOf(quest);
            quest.updateProgress(event);
            if (progressOf(quest) != before) {
                changed = true;
            }
        }
        if (changed) {
            persistProgress(false);
        }
    }

    public void evaluateLevelEnd(LevelFacts finalState) {
        checkDailyReset();
        boolean changed = false;
        for (Quest quest : activeQuests) {
            int before = progressOf(quest);
            quest.evaluateEndState(finalState);
            if (progressOf(quest) != before) {
                changed = true;
            }
        }
        if (changed) {
            persistProgress(false);
        }
    }

    public boolean claimQuest(Quest quest) {
        if (quest == null || !activeQuests.contains(quest) || !quest.isReadyToClaim()) {
            return false;
        }
        grantReward(quest);
        if (quest.checkAndAdvanceTier()) {
            activeQuests.remove(quest);
            completedQuests.add(quest);
            announceCompletion(quest);
        }
        saveQuests();
        return true;
    }

    private void announceCompletion(Quest quest) {
        String message = String.format(">>> %s is complete <<<", quest.getName().toString());
        if (App.getCurrentGameEngine() != null && App.getCurrentGameEngine().getGameWorld() != null) {
            App.getCurrentGameEngine().getGameWorld().addEvent(message);
        }
        User user = App.getCurrentUser();
        if (user != null) {
            app.service.news.NewsPublisher.onQuestCompleted(user, quest);
        }
    }

    private void grantReward(Quest quest) {
        if (quest == null || quest.getRewards() == null) return;

        User currentUser = App.getCurrentUser();
        if (currentUser == null) return;

        for (QuestReward reward : quest.getRewards()) {
            if (reward == null || reward.type() == null) continue;

            switch (reward.type()) {
                case COIN:
                    currentUser.addCoins(reward.amount());
                    break;
                case GEM:
                    currentUser.addGems(reward.amount());
                    break;
                case SEED:
                    currentUser.addCoins(reward.amount() * 10);
                    break;
                case PLANT_UNLOCK:
                    currentUser.unlockRandomPlant();
                    break;
            }
        }
    }

    public void checkDailyReset() {
        LocalDate today = LocalDate.now();
        if (today.isAfter(lastResetDate)) {
            resetDailyQuests();
            lastResetDate = today;
            saveQuests();
        }
    }

    public void resetDailyQuests() {
        activeQuests.removeIf(quest -> quest.getCategory() == QuestCategory.DAILY);
        completedQuests.removeIf(quest -> quest.getCategory() == QuestCategory.DAILY);

        PlantName randomPlant = getRandomCombatPlant();
        PlantCategory randomCategoryForLimited = getRandomPlantCategory();
        PlantCategory randomCategoryForHunter = getRandomCombatFamily();
        int randomCol = random.nextInt(9);
        int randomRow = random.nextInt(5);
        int randomCross = random.nextInt(5);

        addQuest(QuestFactory.createDailySunCollector());
        addQuest(QuestFactory.createCactusOnly());
        addQuest(QuestFactory.createDemolitionExpert());
        addQuest(QuestFactory.createSymmetry());
        addQuest(QuestFactory.createLimitedBloom(randomCategoryForLimited));
        addQuest(QuestFactory.createWinStreak());
        addQuest(QuestFactory.createAlmostVictory());
        addQuest(QuestFactory.createOcdMaster());
        addQuest(QuestFactory.createCloudyDay());
        addQuest(QuestFactory.createOneColumnLess(randomCol));
        addQuest(QuestFactory.createDefenselessRow(randomRow));
        addQuest(QuestFactory.createDefenselessCross(randomCross));
        addQuest(QuestFactory.createPlantExpert(randomPlant));
        addQuest(QuestFactory.createFamilyHunter(randomCategoryForHunter));
    }

    public void ensureEventQuests() {
        boolean added = false;
        added |= addIfAbsent(QuestName.ECONOMICAL_GARDENER, () -> QuestFactory.createEconomicalGardener(3));
        added |= addIfAbsent(QuestName.DEFENSE_MASTER, QuestFactory::createDefenseMaster);
        added |= addIfAbsent(QuestName.QUICK_ACTION, QuestFactory::createQuickAction);
        added |= addIfAbsent(QuestName.DAY_OR_NIGHT, QuestFactory::createDayOrNight);
        added |= addIfAbsent(QuestName.LAWN_MOWER_TIME, () -> QuestFactory.createLawnMowerTime(10));
        if (added) {
            persistProgress(true);
        }
    }

    private boolean addIfAbsent(QuestName name, Supplier<Quest> factory) {
        if (name == null || factory == null || hasQuest(name)) {
            return false;
        }
        addQuest(factory.get());
        return true;
    }

    private boolean hasQuest(QuestName name) {
        if (name == null) {
            return false;
        }
        for (Quest quest : activeQuests) {
            if (quest != null && quest.getName() == name) {
                return true;
            }
        }
        for (Quest quest : completedQuests) {
            if (quest != null && quest.getName() == name) {
                return true;
            }
        }
        return false;
    }

    private PlantName getRandomCombatPlant() {
        List<PlantName> combatPlants = new ArrayList<>();
        for (PlantDefinition definition : PlantRegistry.getPlantDefinitions()) {
            if (definition == null || definition.getName() == null || !isCombatFamily(definition.getCategory())) {
                continue;
            }
            combatPlants.add(definition.getName());
        }
        if (combatPlants.isEmpty()) {
            return PlantName.PEASHOOTER;
        }
        return combatPlants.get(random.nextInt(combatPlants.size()));
    }

    private PlantCategory getRandomPlantCategory() {
        PlantCategory[] values = PlantCategory.values();
        return values[random.nextInt(values.length)];
    }

    private PlantCategory getRandomCombatFamily() {
        PlantCategory[] combat = COMBAT_FAMILIES.toArray(new PlantCategory[0]);
        return combat[random.nextInt(combat.length)];
    }

    private static boolean isCombatFamily(PlantCategory category) {
        return category != null && COMBAT_FAMILIES.contains(category);
    }

    public List<Quest> getActiveQuests() {
        return activeQuests;
    }

    public List<Quest> getCompletedQuests() {
        return completedQuests;
    }

    public List<Quest> getQuestsByPriority() {
        List<Quest> sorted = new ArrayList<>(activeQuests);
        sorted.sort((q1, q2) -> q1.getPriority().compareTo(q2.getPriority()));
        return sorted;
    }

    public List<Quest> getQuestsByCategory(QuestCategory category) {
        List<Quest> filtered = new ArrayList<>();
        for (Quest quest : activeQuests) {
            if (quest.getCategory() == category) {
                filtered.add(quest);
            }
        }
        return filtered;
    }

    public LocalDate getLastResetDate() {
        return lastResetDate;
    }

    public void setLastResetDate(LocalDate lastResetDate) {
        this.lastResetDate = lastResetDate;
    }

    public void saveQuests() {
        persistProgress(true);
        AppService.syncGameDataIfDirty();
    }

    public static QuestManager merged(QuestManager left, QuestManager right) {
        if (left == null) {
            if (right != null) {
                right.ensureEventQuests();
            }
            return right;
        }
        if (right == null) {
            left.ensureEventQuests();
            return left;
        }
        LocalDate reset = laterDate(left.lastResetDate, right.lastResetDate);
        QuestManager result = new QuestManager();
        result.setLastResetDate(reset);
        Map<QuestName, Quest> chosen = new LinkedHashMap<>();
        absorb(chosen, left, reset);
        absorb(chosen, right, reset);
        for (Quest quest : chosen.values()) {
            if (quest.isCompleted()) {
                result.completedQuests.add(quest);
            } else {
                result.activeQuests.add(quest);
            }
        }
        result.ensureEventQuests();
        return result;
    }

    private void persistProgress(boolean force) {
        User user = App.getCurrentUser();
        if (user == null) {
            return;
        }
        long now = System.nanoTime();
        if (!force && lastPersistNanos != 0L && now - lastPersistNanos < PERSIST_INTERVAL_NANOS) {
            AppService.markGameDataDirty();
            return;
        }
        lastPersistNanos = now;
        ProgressService.saveUserProgress(user);
        AppService.markGameDataDirty();
    }

    private static int progressOf(Quest quest) {
        if (quest == null || quest.getCondition() == null) {
            return 0;
        }
        return quest.getCondition().getCurrentProgress();
    }

    private static void absorb(Map<QuestName, Quest> chosen, QuestManager source, LocalDate chosenReset) {
        if (source == null) {
            return;
        }
        for (Quest quest : source.activeQuests) {
            consider(chosen, quest, source.lastResetDate, chosenReset);
        }
        for (Quest quest : source.completedQuests) {
            consider(chosen, quest, source.lastResetDate, chosenReset);
        }
    }

    private static void consider(
        Map<QuestName, Quest> chosen,
        Quest quest,
        LocalDate sourceReset,
        LocalDate chosenReset
    ) {
        if (quest == null || quest.getName() == null) {
            return;
        }
        if (quest.getCategory() == QuestCategory.DAILY
            && sourceReset != null
            && chosenReset != null
            && sourceReset.isBefore(chosenReset)) {
            return;
        }
        Quest existing = chosen.get(quest.getName());
        if (existing == null || isFurtherAlong(quest, existing)) {
            chosen.put(quest.getName(), quest);
        }
    }

    private static boolean isFurtherAlong(Quest candidate, Quest current) {
        if (candidate.isCompleted() != current.isCompleted()) {
            return candidate.isCompleted();
        }
        if (candidate.getCurrentTierIndex() != current.getCurrentTierIndex()) {
            return candidate.getCurrentTierIndex() > current.getCurrentTierIndex();
        }
        return progressOf(candidate) > progressOf(current);
    }

    private static LocalDate laterDate(LocalDate left, LocalDate right) {
        if (left == null) {
            return right;
        }
        if (right == null) {
            return left;
        }
        return right.isAfter(left) ? right : left;
    }
}
