package app.service.collection;

import app.enums.plant.PlantCategory;
import app.enums.plant.PlantName;
import app.view.collection.data.CollectionData;
import game.entity.plant.PlantDefinition;
import game.entity.registry.PlantRegistry;
import game.service.Zombie.ZombieInfo;
import game.service.Zombie.ZombieInfoRegistry;
import game.service.plant.PlantUpgradeService;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Filters collection card lists. Does not mutate master registries or unlock/upgrade SoT.
 * Categories come from PlantCategory / ZombieInfo rarity — never hard-coded plant/zombie names.
 */
public final class CollectionFilterService {
    /** Optional test/prod override: alias -> rarity. Null means use ZombieInfoRegistry. */
    private static volatile java.util.Map<String, String> zombieCategoryOverride;

    private CollectionFilterService() {
    }

    /** Test-only / offline hook so unit tests can supply rarity without Gdx. */
    public static void setZombieCategoryOverride(java.util.Map<String, String> aliasToRarity) {
        zombieCategoryOverride = aliasToRarity;
    }

    public static void clearZombieCategoryOverride() {
        zombieCategoryOverride = null;
    }

    public static List<CollectionData.PlantCardModel> filterPlants(
        List<CollectionData.PlantCardModel> source,
        CollectionFilterState state
    ) {
        ArrayList<CollectionData.PlantCardModel> out = new ArrayList<>();
        if (source == null) {
            return out;
        }
        if (state == null) {
            out.addAll(source);
            return out;
        }
        for (CollectionData.PlantCardModel card : source) {
            if (card == null || card.plantName == null) {
                continue;
            }
            if (!matchesPlantCategory(card.plantName, state.getCategoryId())) {
                continue;
            }
            if (!matchesPlantUnlock(card.unlocked, state.getUnlockFilter())) {
                continue;
            }
            if (state.isUpgradeableOnly() && !isPlantUpgradeable(card)) {
                continue;
            }
            out.add(card);
        }
        return out;
    }

    public static List<CollectionData.ZombieCardModel> filterZombies(
        List<CollectionData.ZombieCardModel> source,
        CollectionFilterState state
    ) {
        ArrayList<CollectionData.ZombieCardModel> out = new ArrayList<>();
        if (source == null) {
            return out;
        }
        if (state == null) {
            out.addAll(source);
            return out;
        }
        for (CollectionData.ZombieCardModel card : source) {
            if (card == null || card.alias == null || card.alias.isBlank()) {
                continue;
            }
            if (!matchesZombieCategory(card.alias, state.getCategoryId())) {
                continue;
            }
            if (!matchesZombieDiscovery(card.seen, state.getUnlockFilter())) {
                continue;
            }
            out.add(card);
        }
        return out;
    }

    public static List<String> plantCategoryIds() {
        ArrayList<String> ids = new ArrayList<>();
        for (PlantCategory category : PlantCategory.values()) {
            ids.add(category.name());
        }
        return ids;
    }

    public static String plantCategoryLabel(String categoryId) {
        if (categoryId == null || categoryId.isBlank()) {
            return "";
        }
        try {
            return PlantCategory.valueOf(categoryId.trim()).getLabel();
        } catch (IllegalArgumentException ignored) {
            return categoryId;
        }
    }

    /** Distinct rarity values from zombie info SoT (classification) for the given aliases. */
    public static List<String> zombieCategoryIds(Iterable<String> aliases) {
        LinkedHashSet<String> ids = new LinkedHashSet<>();
        if (aliases != null) {
            for (String alias : aliases) {
                String rarity = zombieCategoryOf(alias);
                if (rarity != null && !rarity.isBlank()) {
                    ids.add(rarity);
                }
            }
        }
        ArrayList<String> list = new ArrayList<>(ids);
        list.sort(String.CASE_INSENSITIVE_ORDER);
        return list;
    }

    public static String zombieCategoryOf(String alias) {
        if (alias == null || alias.isBlank()) {
            return "";
        }
        java.util.Map<String, String> override = zombieCategoryOverride;
        if (override != null) {
            String rarity = override.get(alias);
            return rarity == null ? "" : rarity.trim();
        }
        try {
            ZombieInfo info = ZombieInfoRegistry.get(alias);
            if (info == null) {
                return "";
            }
            String rarity = info.getRarity();
            return rarity == null ? "" : rarity.trim();
        } catch (Throwable ignored) {
            return "";
        }
    }

    public static boolean matchesPlantCategory(PlantName plantName, String categoryId) {
        if (categoryId == null || categoryId.isBlank()) {
            return true;
        }
        PlantDefinition definition = PlantRegistry.get(plantName);
        PlantCategory category = definition == null ? null : definition.getCategory();
        return category != null && categoryId.equals(category.name());
    }

    public static boolean matchesZombieCategory(String alias, String categoryId) {
        if (categoryId == null || categoryId.isBlank()) {
            return true;
        }
        String rarity = zombieCategoryOf(alias);
        return categoryId.equalsIgnoreCase(rarity);
    }

    public static boolean matchesPlantUnlock(boolean unlocked, CollectionUnlockFilter filter) {
        if (filter == null || filter == CollectionUnlockFilter.ALL) {
            return true;
        }
        return switch (filter) {
            case UNLOCKED -> unlocked;
            case LOCKED -> !unlocked;
            default -> true;
        };
    }

    public static boolean matchesZombieDiscovery(boolean seen, CollectionUnlockFilter filter) {
        if (filter == null || filter == CollectionUnlockFilter.ALL) {
            return true;
        }
        return switch (filter) {
            case DISCOVERED -> seen;
            case UNDISCOVERED -> !seen;
            default -> true;
        };
    }

    /**
     * Upgradeable filter = seed packets sufficient for next level (not coins).
     * Uses PlantUpgradeView.seedsMet when present; otherwise PlantUpgradeService
     * hasEnoughSeedsForNextUpgrade SoT. Distinct from canUpgrade (seeds+coins).
     */
    public static boolean isPlantUpgradeable(CollectionData.PlantCardModel card) {
        if (card == null || card.plantName == null) {
            return false;
        }
        if (card.progress != null) {
            return !card.progress.atMaxLevel() && card.progress.seedsMet();
        }
        return PlantUpgradeService.hasEnoughSeedsForNextUpgrade(card.plantName);
    }

    public static Set<String> normalizeCategorySet(Iterable<String> ids) {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        if (ids == null) {
            return set;
        }
        for (String id : ids) {
            if (id != null && !id.isBlank()) {
                set.add(id.trim());
            }
        }
        return set;
    }

    public static String unlockKey(CollectionUnlockFilter filter) {
        if (filter == null) {
            return "ALL";
        }
        return filter.name();
    }

    public static CollectionUnlockFilter parseUnlock(String raw, boolean forZombies) {
        if (raw == null || raw.isBlank()) {
            return CollectionUnlockFilter.ALL;
        }
        String key = raw.trim().toUpperCase(Locale.ROOT).replace(' ', '_');
        try {
            CollectionUnlockFilter value = CollectionUnlockFilter.valueOf(key);
            if (forZombies && value.isZombieStatus()) {
                return value;
            }
            if (!forZombies && value.isPlantStatus()) {
                return value;
            }
        } catch (IllegalArgumentException ignored) {
        }
        return CollectionUnlockFilter.ALL;
    }
}
