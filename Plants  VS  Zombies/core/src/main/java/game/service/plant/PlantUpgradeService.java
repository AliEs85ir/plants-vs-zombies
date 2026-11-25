package game.service.plant;

import app.enums.plant.PlantName;
import app.model.App;
import app.model.user.User;
import app.service.AppService;
import app.service.ProgressService;
import common.Result;
import game.entity.plant.PlantLevel;
import game.entity.plant.upgrade.EffectivePlantStats;
import game.entity.plant.upgrade.PlantLevelModifiers;
import game.entity.plant.upgrade.PlantLevelStep;
import game.entity.plant.upgrade.PlantStatPreview;
import game.entity.plant.upgrade.PlantUpgradeConfigLoader;
import game.entity.plant.upgrade.PlantUpgradePreview;
import game.entity.plant.upgrade.PlantUpgradeView;
import game.entity.registry.PlantRegistry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class PlantUpgradeService {
    public static final String PLANT_NOT_FOUND = "Plant not found.";
    public static final String NOT_ENOUGH_SEEDS = "Not enough seed packets.";
    public static final String NOT_ENOUGH_COINS = "Not enough coins.";
    public static final String NOT_ENOUGH_SEEDS_AND_COINS = "Not enough seed packets and coins.";
    public static final String MAX_LEVEL = "This plant is already at max level.";
    public static final String UPGRADE_SUCCESS = "Plant upgraded successfully.";
    public static final String UPGRADE_FAILED = "Upgrade not allowed.";
    public static final String SAVE_FAILED = "Could not save plant upgrade progress.";

    private PlantUpgradeService() {
    }

    public static int getCurrentLevel(PlantName plantName) {
        return getCurrentLevel(App.getCurrentUser(), plantName);
    }

    public static int getCurrentLevel(User user, PlantName plantName) {
        if (plantName == null) {
            return 1;
        }
        if (user == null) {
            return 1;
        }
        PlantLevel progress = user.getPlantLevel(plantName);
        int raw = progress == null ? 1 : progress.getLevel();
        return clampLevel(plantName, raw);
    }

    public static int getMaxLevel(PlantName plantName) {
        if (plantName == null) {
            return 1;
        }
        return PlantUpgradeConfigLoader.get().maxLevel(plantName);
    }

    public static int getNextLevel(PlantName plantName) {
        return getNextLevel(App.getCurrentUser(), plantName);
    }

    public static int getNextLevel(User user, PlantName plantName) {
        int current = getCurrentLevel(user, plantName);
        int max = getMaxLevel(plantName);
        return current >= max ? current : current + 1;
    }

    public static boolean isMaxLevel(PlantName plantName) {
        return isMaxLevel(App.getCurrentUser(), plantName);
    }

    public static boolean isMaxLevel(User user, PlantName plantName) {
        return plantName != null && getCurrentLevel(user, plantName) >= getMaxLevel(plantName);
    }

    public static int getRequiredSeeds(PlantName plantName) {
        return getRequiredSeeds(App.getCurrentUser(), plantName);
    }

    public static int getRequiredSeeds(User user, PlantName plantName) {
        PlantLevelStep step = nextStep(user, plantName);
        return step == null ? 0 : step.requiredSeedPackets;
    }

    public static int getRequiredCoins(PlantName plantName) {
        return getRequiredCoins(App.getCurrentUser(), plantName);
    }

    public static int getRequiredCoins(User user, PlantName plantName) {
        PlantLevelStep step = nextStep(user, plantName);
        return step == null ? 0 : step.requiredCoins;
    }

    public static boolean canUpgrade(PlantName plantName) {
        return canUpgrade(App.getCurrentUser(), plantName);
    }

    public static boolean canUpgrade(User user, PlantName plantName) {
        if (user == null || plantName == null || isMaxLevel(user, plantName)) {
            return false;
        }
        PlantLevelStep step = nextStep(user, plantName);
        if (step == null) {
            return false;
        }
        int seeds = user.getSeedBank() == null ? 0 : user.getSeedBank().getAmount(plantName);
        return seeds >= step.requiredSeedPackets && user.getCoins() >= step.requiredCoins;
    }

    /**
     * Seed-packets ready for the next upgrade level (ignores coins).
     * Matches Collection seed-bar ready / PlantUpgradeView.seedsMet() when not at max.
     */
    public static boolean hasEnoughSeedsForNextUpgrade(PlantName plantName) {
        return hasEnoughSeedsForNextUpgrade(App.getCurrentUser(), plantName);
    }

    public static boolean hasEnoughSeedsForNextUpgrade(User user, PlantName plantName) {
        if (user == null || plantName == null || isMaxLevel(user, plantName)) {
            return false;
        }
        PlantLevelStep step = nextStep(user, plantName);
        if (step == null) {
            return false;
        }
        int seeds = user.getSeedBank() == null ? 0 : user.getSeedBank().getAmount(plantName);
        return seeds >= step.requiredSeedPackets;
    }

    public static Result upgrade(PlantName plantName) {
        return upgrade(App.getCurrentUser(), plantName);
    }

    public static Result upgrade(User user, PlantName plantName) {
        if (plantName == null) {
            return Result.failure(PLANT_NOT_FOUND);
        }
        if (user == null) {
            return Result.failure(UPGRADE_FAILED);
        }
        if (isMaxLevel(user, plantName)) {
            return Result.failure(MAX_LEVEL);
        }
        PlantLevelStep step = nextStep(user, plantName);
        if (step == null) {
            return Result.failure(MAX_LEVEL);
        }

        int currentSeeds = user.getSeedBank() == null ? 0 : user.getSeedBank().getAmount(plantName);
        int currentCoins = user.getCoins();
        boolean seedsOk = currentSeeds >= step.requiredSeedPackets;
        boolean coinsOk = currentCoins >= step.requiredCoins;
        if (!seedsOk && !coinsOk) {
            return Result.failure(NOT_ENOUGH_SEEDS_AND_COINS);
        }
        if (!seedsOk) {
            return Result.failure(NOT_ENOUGH_SEEDS);
        }
        if (!coinsOk) {
            return Result.failure(NOT_ENOUGH_COINS);
        }

        PlantLevel progress = user.ensurePlantLevel(plantName);
        int previousLevel = progress.getLevel();
        int previousCoins = user.getCoins();
        int previousSeeds = currentSeeds;
        int nextLevel = getCurrentLevel(user, plantName) + 1;

        if (step.requiredSeedPackets > 0
            && (user.getSeedBank() == null
            || !user.getSeedBank().removePacket(plantName, step.requiredSeedPackets))) {
            return Result.failure(NOT_ENOUGH_SEEDS);
        }
        user.deductCoins(step.requiredCoins);
        progress.setLevel(nextLevel);
        AppService.markGameDataDirty();

        boolean saved = ProgressService.saveUserProgress(user);
        if (!saved) {
            rollback(user, plantName, previousLevel, previousCoins, previousSeeds, step.requiredSeedPackets);
            return Result.failure(SAVE_FAILED);
        }
        return Result.success(UPGRADE_SUCCESS);
    }

    public static PlantLevelModifiers getModifiers(PlantName plantName) {
        return getModifiers(App.getCurrentUser(), plantName);
    }

    public static PlantLevelModifiers getModifiers(User user, PlantName plantName) {
        if (plantName == null) {
            return PlantLevelModifiers.NONE;
        }
        return PlantUpgradeConfigLoader.get().modifiersAt(plantName, getCurrentLevel(user, plantName));
    }

    public static EffectivePlantStats getEffectiveStats(PlantName plantName) {
        return getEffectiveStats(App.getCurrentUser(), plantName);
    }

    public static EffectivePlantStats getEffectiveStats(User user, PlantName plantName) {
        return getEffectiveStatsAtLevel(plantName, getCurrentLevel(user, plantName));
    }

    public static EffectivePlantStats getEffectiveStatsAtLevel(PlantName plantName, int level) {
        int clamped = plantName == null ? 1 : clampLevel(plantName, level);
        PlantLevelModifiers modifiers = plantName == null
            ? PlantLevelModifiers.NONE
            : PlantUpgradeConfigLoader.get().modifiersAt(plantName, clamped);
        return EffectivePlantStats.from(PlantRegistry.get(plantName), modifiers);
    }

    public static PlantUpgradePreview getPreview(PlantName plantName) {
        return getPreview(App.getCurrentUser(), plantName);
    }

    public static PlantUpgradePreview getPreview(User user, PlantName plantName) {
        int currentLevel = getCurrentLevel(user, plantName);
        int maxLevel = getMaxLevel(plantName);
        boolean atMax = plantName != null && currentLevel >= maxLevel;
        int nextLevel = atMax ? currentLevel : currentLevel + 1;
        EffectivePlantStats currentStats = getEffectiveStatsAtLevel(plantName, currentLevel);
        EffectivePlantStats nextStats = atMax
            ? currentStats
            : getEffectiveStatsAtLevel(plantName, nextLevel);
        return new PlantUpgradePreview(
            currentLevel,
            nextLevel,
            maxLevel,
            atMax,
            currentStats,
            atMax ? null : nextStats,
            compareStats(currentStats, nextStats, atMax)
        );
    }

    public static PlantUpgradeView getProgress(PlantName plantName) {
        User user = App.getCurrentUser();
        int currentLevel = getCurrentLevel(plantName);
        int maxLevel = getMaxLevel(plantName);
        boolean atMax = plantName != null && currentLevel >= maxLevel;
        PlantLevelStep step = atMax ? null : nextStep(user, plantName);
        int requiredSeeds = step == null ? 0 : step.requiredSeedPackets;
        int requiredCoins = step == null ? 0 : step.requiredCoins;
        int currentSeeds = (user == null || user.getSeedBank() == null || plantName == null)
            ? 0
            : user.getSeedBank().getAmount(plantName);
        int currentCoins = user == null ? 0 : user.getCoins();
        boolean seedsMet = atMax || currentSeeds >= requiredSeeds;
        boolean coinsMet = atMax || currentCoins >= requiredCoins;
        float seedProgress = atMax ? 1f : progressRatio(currentSeeds, requiredSeeds);
        boolean canUpgrade = !atMax && seedsMet && coinsMet && step != null;
        return new PlantUpgradeView(
            plantName,
            currentLevel,
            maxLevel,
            atMax,
            canUpgrade,
            currentSeeds,
            requiredSeeds,
            currentCoins,
            requiredCoins,
            seedProgress,
            seedsMet,
            coinsMet
        );
    }

    public static float progressRatio(int current, int required) {
        if (required <= 0) {
            return 1f;
        }
        return Math.min(1f, (float) Math.max(0, current) / (float) required);
    }

    private static List<PlantStatPreview> compareStats(
        EffectivePlantStats current,
        EffectivePlantStats next,
        boolean atMax
    ) {
        Map<String, EffectivePlantStats.NumericStat> currentFields = indexById(current.numericStats());
        Map<String, EffectivePlantStats.NumericStat> nextFields = indexById(next.numericStats());
        Set<String> ids = new LinkedHashSet<>(currentFields.keySet());
        ids.addAll(nextFields.keySet());
        List<PlantStatPreview> previews = new ArrayList<>(ids.size());
        for (String id : ids) {
            EffectivePlantStats.NumericStat currentField = currentFields.get(id);
            EffectivePlantStats.NumericStat nextField = nextFields.get(id);
            if (currentField == null) {
                currentField = nextField;
            }
            if (nextField == null) {
                nextField = currentField;
            }
            if (currentField == null) {
                continue;
            }
            boolean changed = !atMax && Double.compare(currentField.comparable, nextField.comparable) != 0;
            previews.add(new PlantStatPreview(
                id,
                currentField.display,
                changed ? nextField.display : currentField.display,
                changed
            ));
        }
        return previews;
    }

    private static Map<String, EffectivePlantStats.NumericStat> indexById(
        List<EffectivePlantStats.NumericStat> stats
    ) {
        Map<String, EffectivePlantStats.NumericStat> indexed = new LinkedHashMap<>();
        if (stats == null) {
            return indexed;
        }
        for (EffectivePlantStats.NumericStat stat : stats) {
            if (stat != null && stat.id != null) {
                indexed.put(stat.id, stat);
            }
        }
        return indexed;
    }

    private static PlantLevelStep nextStep(User user, PlantName plantName) {
        if (plantName == null || isMaxLevel(user, plantName)) {
            return null;
        }
        return PlantUpgradeConfigLoader.get().stepToReach(plantName, getNextLevel(user, plantName));
    }

    private static int clampLevel(PlantName plantName, int level) {
        int max = getMaxLevel(plantName);
        return Math.max(1, Math.min(level, max));
    }

    private static void rollback(
        User user,
        PlantName plantName,
        int previousLevel,
        int previousCoins,
        int previousSeeds,
        int spentSeeds
    ) {
        user.ensurePlantLevel(plantName).setLevel(previousLevel);
        user.setCoins(previousCoins);
        if (user.getSeedBank() == null) {
            return;
        }
        int after = user.getSeedBank().getAmount(plantName);
        int missing = previousSeeds - after;
        if (missing > 0) {
            user.getSeedBank().addPacket(plantName, missing);
        } else if (missing < 0) {
            user.getSeedBank().removePacket(plantName, -missing);
        } else if (spentSeeds > 0 && after < previousSeeds) {
            user.getSeedBank().addPacket(plantName, spentSeeds);
        }
    }
}
