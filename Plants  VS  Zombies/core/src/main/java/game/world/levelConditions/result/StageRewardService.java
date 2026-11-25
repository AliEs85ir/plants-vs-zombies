package game.world.levelConditions.result;

import app.enums.SeasonType;
import app.enums.plant.PlantName;
import app.model.user.StageProgress;
import app.model.user.User;
import game.world.chapter.ChapterId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public final class StageRewardService {
    private static final Random RANDOM = new Random();

    private StageRewardService() {
    }

    public static List<StageRewardGrant> grantFirstClearRewards(User user, SeasonType season, int stage) {
        if (user == null || season == null || season == SeasonType.MINIGAME || stage < 1) {
            return List.of();
        }
        StageProgress progress = user.getGameProgress().getOrCreateStageProgress(season, stage);
        if (progress.isFirstClearRewardClaimed()) {
            return List.of();
        }

        List<StageRewardCatalog.RewardSpec> specs = StageRewardCatalog.rewardsFor(season, stage);
        if (specs.isEmpty()) {
            progress.markFirstClearRewardClaimed();
            return List.of();
        }

        List<StageRewardGrant> grants = new ArrayList<>();
        Set<PlantName> usedSeedPlants = EnumSet.noneOf(PlantName.class);

        for (StageRewardCatalog.RewardSpec spec : specs) {
            switch (spec.kind()) {
                case COINS -> {
                    user.addCoins(spec.amount());
                    grants.add(StageRewardGrant.coins(spec.amount()));
                }
                case DIAMONDS -> {
                    user.addGems(spec.amount());
                    grants.add(StageRewardGrant.diamonds(spec.amount()));
                }
                case GREENHOUSE_TABLE -> {
                    if (user.getGreenhouse() != null && user.getGreenhouse().unlockTableViaShop()) {
                        grants.add(StageRewardGrant.greenhouseTable());
                    }
                }
                case GREENHOUSE_POT -> {
                    if (user.getGreenhouse() != null && user.getGreenhouse().isPotAvailable(1)) {
                        user.getGreenhouse().addPot();
                        grants.add(StageRewardGrant.greenhousePot());
                    }
                }
                case SEED_PACKETS_RANDOM_UNLOCKED -> {
                    PlantName plant = pickUnlockedPlant(user, usedSeedPlants, spec.uniqueAmongBatch());
                    if (plant != null && user.getSeedBank() != null) {
                        user.getSeedBank().addPacket(plant, spec.amount());
                        if (spec.uniqueAmongBatch()) {
                            usedSeedPlants.add(plant);
                        }
                        grants.add(StageRewardGrant.seeds(plant, spec.amount()));
                    }
                }
                case UNLOCK_RANDOM_LOCKED_PLANTS -> {
                    List<PlantName> unlocked = unlockRandomLockedPlants(user, spec.amount());
                    for (PlantName plant : unlocked) {
                        grants.add(StageRewardGrant.plantUnlock(plant));
                    }
                }
            }
        }

        progress.markFirstClearRewardClaimed();
        return grants;
    }

    private static PlantName pickUnlockedPlant(User user, Set<PlantName> excluded, boolean unique) {
        List<PlantName> pool = new ArrayList<>();
        for (PlantName plant : user.getUnlockedPlants()) {
            if (plant == null) {
                continue;
            }
            if (unique && excluded.contains(plant)) {
                continue;
            }
            pool.add(plant);
        }
        if (pool.isEmpty()) {
            for (PlantName plant : user.getUnlockedPlants()) {
                if (plant != null) {
                    pool.add(plant);
                }
            }
        }
        if (pool.isEmpty()) {
            return null;
        }
        Collections.shuffle(pool, RANDOM);
        return pool.get(0);
    }

    private static List<PlantName> unlockRandomLockedPlants(User user, int count) {
        List<PlantName> locked = new ArrayList<>();
        for (PlantName plant : PlantName.values()) {
            if (!user.isUnlocked(plant)) {
                locked.add(plant);
            }
        }
        Collections.shuffle(locked, RANDOM);
        int limit = Math.min(count, locked.size());
        List<PlantName> selected = new ArrayList<>(locked.subList(0, limit));
        for (PlantName plant : selected) {
            user.unlockPlant(plant);
        }
        return selected;
    }

    public static ChapterId chapterId(SeasonType season, int stage) {
        return new ChapterId(season, stage);
    }
}
