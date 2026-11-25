package app.service.collection;

import app.enums.plant.PlantCategory;
import app.enums.plant.PlantName;
import app.view.collection.data.CollectionData;
import game.entity.plant.upgrade.PlantUpgradeView;
import game.entity.registry.PlantDefinitionLoader;
import game.entity.registry.PlantRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollectionFilterServiceTest {

    @BeforeAll
    static void loadPlantDefinitions() {
        PlantDefinitionLoader.load();
    }

    @BeforeEach
    void setUp() {
        CollectionFilterService.clearZombieCategoryOverride();
    }

    @AfterEach
    void tearDown() {
        CollectionFilterService.clearZombieCategoryOverride();
    }

    @Test
    void plantsAllReturnsFullList() {
        List<CollectionData.PlantCardModel> source = samplePlants();
        CollectionFilterState state = CollectionFilterState.forPlants();
        state.applyDraft();

        List<CollectionData.PlantCardModel> filtered = CollectionFilterService.filterPlants(source, state);

        assertEquals(source.size(), filtered.size());
    }

    @Test
    void plantsCategoryFilterUsesRegistrySoT() {
        List<CollectionData.PlantCardModel> source = samplePlants();
        CollectionFilterState state = CollectionFilterState.forPlants();
        state.setDraftCategoryId(PlantCategory.SUN_PRODUCER.name());
        state.applyDraft();

        List<CollectionData.PlantCardModel> filtered = CollectionFilterService.filterPlants(source, state);

        assertFalse(filtered.isEmpty());
        for (CollectionData.PlantCardModel card : filtered) {
            assertEquals(PlantCategory.SUN_PRODUCER, PlantRegistry.get(card.plantName).getCategory());
        }
        assertTrue(filtered.stream().anyMatch(c -> c.plantName == PlantName.SUNFLOWER));
        assertTrue(filtered.stream().noneMatch(c -> c.plantName == PlantName.PEASHOOTER));
    }

    @Test
    void plantsUnlockAndLockedFilters() {
        List<CollectionData.PlantCardModel> source = samplePlants();
        CollectionFilterState unlocked = CollectionFilterState.forPlants();
        unlocked.setDraftUnlockFilter(CollectionUnlockFilter.UNLOCKED);
        unlocked.applyDraft();
        CollectionFilterState locked = CollectionFilterState.forPlants();
        locked.setDraftUnlockFilter(CollectionUnlockFilter.LOCKED);
        locked.applyDraft();

        List<CollectionData.PlantCardModel> onlyUnlocked = CollectionFilterService.filterPlants(source, unlocked);
        List<CollectionData.PlantCardModel> onlyLocked = CollectionFilterService.filterPlants(source, locked);

        assertEquals(2, onlyUnlocked.size());
        assertTrue(onlyUnlocked.stream().allMatch(c -> c.unlocked));
        assertEquals(2, onlyLocked.size());
        assertTrue(onlyLocked.stream().allMatch(c -> !c.unlocked));
    }

    @Test
    void plantsUpgradeableOnlyKeepsSeedsReadyForNextLevel() {
        List<CollectionData.PlantCardModel> source = samplePlants();
        CollectionFilterState state = CollectionFilterState.forPlants();
        state.setDraftUpgradeableOnly(true);
        state.applyDraft();

        List<CollectionData.PlantCardModel> filtered = CollectionFilterService.filterPlants(source, state);

        // SUNFLOWER + PEASHOOTER have seedsMet; CHERRY_BOMB lacks seeds; WALL_NUT at max.
        assertEquals(2, filtered.size());
        assertTrue(filtered.stream().anyMatch(c -> c.plantName == PlantName.SUNFLOWER));
        assertTrue(filtered.stream().anyMatch(c -> c.plantName == PlantName.PEASHOOTER));
        assertTrue(filtered.stream().noneMatch(c -> c.plantName == PlantName.CHERRY_BOMB));
        assertTrue(filtered.stream().noneMatch(c -> c.plantName == PlantName.WALL_NUT));
        assertTrue(filtered.stream().allMatch(CollectionFilterService::isPlantUpgradeable));
    }

    @Test
    void plantsUpgradeableOnlyIgnoresCoinsGatedCanUpgrade() {
        // seedsMet true, canUpgrade false (coins not met) still passes upgradeable filter
        PlantUpgradeView progress = new PlantUpgradeView(
            PlantName.SUNFLOWER,
            1,
            10,
            false,
            false,
            20,
            10,
            0,
            1000,
            1f,
            true,
            false
        );
        CollectionData.PlantCardModel card = new CollectionData.PlantCardModel(
            PlantName.SUNFLOWER, true, progress, "", progress.currentLevel()
        );
        assertTrue(CollectionFilterService.isPlantUpgradeable(card));
        assertFalse(progress.canUpgrade());
        assertTrue(progress.seedsMet());
    }

    @Test
    void plantsUpgradeableOnlyExcludesMaxEvenIfSeedsMetFlagTrue() {
        PlantUpgradeView progress = new PlantUpgradeView(
            PlantName.WALL_NUT,
            10,
            10,
            true,
            false,
            99,
            0,
            0,
            0,
            1f,
            true,
            true
        );
        CollectionData.PlantCardModel card = new CollectionData.PlantCardModel(
            PlantName.WALL_NUT, true, progress, "", progress.currentLevel()
        );
        assertFalse(CollectionFilterService.isPlantUpgradeable(card));
    }

    @Test
    void plantsAndCombinationCategoryUnlockUpgradeable() {
        List<CollectionData.PlantCardModel> source = samplePlants();
        CollectionFilterState state = CollectionFilterState.forPlants();
        state.setDraftCategoryId(PlantCategory.SHOOTER.name());
        state.setDraftUnlockFilter(CollectionUnlockFilter.UNLOCKED);
        state.setDraftUpgradeableOnly(true);
        state.applyDraft();

        List<CollectionData.PlantCardModel> filtered = CollectionFilterService.filterPlants(source, state);

        assertEquals(1, filtered.size());
        assertEquals(PlantName.PEASHOOTER, filtered.get(0).plantName);
    }

    @Test
    void plantsEmptyResultWhenNoMatch() {
        List<CollectionData.PlantCardModel> source = samplePlants();
        CollectionFilterState state = CollectionFilterState.forPlants();
        state.setDraftCategoryId(PlantCategory.EXPLOSIVE.name());
        state.setDraftUnlockFilter(CollectionUnlockFilter.UNLOCKED);
        state.setDraftUpgradeableOnly(true);
        state.applyDraft();

        List<CollectionData.PlantCardModel> filtered = CollectionFilterService.filterPlants(source, state);

        assertTrue(filtered.isEmpty());
    }

    @Test
    void selectionDoesNotApplyUntilApplyDraft() {
        List<CollectionData.PlantCardModel> source = samplePlants();
        CollectionFilterState state = CollectionFilterState.forPlants();
        state.setDraftUnlockFilter(CollectionUnlockFilter.LOCKED);

        assertEquals(source.size(), CollectionFilterService.filterPlants(source, state).size());
        state.applyDraft();
        assertEquals(2, CollectionFilterService.filterPlants(source, state).size());
    }

    @Test
    void zombiesDiscoveryFilters() {
        List<CollectionData.ZombieCardModel> source = sampleZombies();
        CollectionFilterState discovered = CollectionFilterState.forZombies();
        discovered.setDraftUnlockFilter(CollectionUnlockFilter.DISCOVERED);
        discovered.applyDraft();
        CollectionFilterState undiscovered = CollectionFilterState.forZombies();
        undiscovered.setDraftUnlockFilter(CollectionUnlockFilter.UNDISCOVERED);
        undiscovered.applyDraft();

        assertEquals(2, CollectionFilterService.filterZombies(source, discovered).size());
        assertEquals(1, CollectionFilterService.filterZombies(source, undiscovered).size());
        assertEquals("Basic Egypt", CollectionFilterService.filterZombies(source, undiscovered).get(0).alias);
    }

    @Test
    void zombiesCategoryAndDiscoveryAndEmpty() {
        CollectionFilterService.setZombieCategoryOverride(Map.of(
            "Basic Egypt", "Common",
            "Gargantuar1", "Epic",
            "Canon1", "Epic"
        ));
        List<CollectionData.ZombieCardModel> source = sampleZombies();

        CollectionFilterState state = CollectionFilterState.forZombies();
        state.setDraftCategoryId("Epic");
        state.setDraftUnlockFilter(CollectionUnlockFilter.DISCOVERED);
        state.applyDraft();
        List<CollectionData.ZombieCardModel> filtered = CollectionFilterService.filterZombies(source, state);
        assertEquals(2, filtered.size());
        assertTrue(filtered.stream().allMatch(c -> c.seen));
        assertTrue(filtered.stream().allMatch(c -> "Epic".equalsIgnoreCase(CollectionFilterService.zombieCategoryOf(c.alias))));

        state.setDraftUnlockFilter(CollectionUnlockFilter.UNDISCOVERED);
        state.applyDraft();
        assertTrue(CollectionFilterService.filterZombies(source, state).isEmpty());
    }

    @Test
    void zombieFilterStateIgnoresUpgradeableFlag() {
        CollectionFilterState state = CollectionFilterState.forZombies();
        assertFalse(state.supportsUpgradeable());
        state.setDraftUpgradeableOnly(true);
        assertFalse(state.isDraftUpgradeableOnly());
        state.applyDraft();
        assertFalse(state.isUpgradeableOnly());
    }

    @Test
    void plantAndZombieStatesAreIndependent() {
        CollectionFilterState plants = CollectionFilterState.forPlants();
        CollectionFilterState zombies = CollectionFilterState.forZombies();
        plants.setDraftCategoryId(PlantCategory.MELEE.name());
        plants.setDraftUnlockFilter(CollectionUnlockFilter.LOCKED);
        plants.setDraftUpgradeableOnly(true);
        plants.applyDraft();
        zombies.setDraftCategoryId("Rare");
        zombies.setDraftUnlockFilter(CollectionUnlockFilter.DISCOVERED);
        zombies.applyDraft();

        assertEquals(PlantCategory.MELEE.name(), plants.getCategoryId());
        assertEquals(CollectionUnlockFilter.LOCKED, plants.getUnlockFilter());
        assertTrue(plants.isUpgradeableOnly());
        assertEquals("Rare", zombies.getCategoryId());
        assertEquals(CollectionUnlockFilter.DISCOVERED, zombies.getUnlockFilter());
        assertFalse(zombies.isUpgradeableOnly());
    }

    private static List<CollectionData.PlantCardModel> samplePlants() {
        List<CollectionData.PlantCardModel> cards = new ArrayList<>();
        cards.add(card(PlantName.SUNFLOWER, true, false, true));
        cards.add(card(PlantName.PEASHOOTER, true, false, true));
        cards.add(card(PlantName.CHERRY_BOMB, false, false, false));
        cards.add(card(PlantName.WALL_NUT, false, true, false));
        return cards;
    }

    private static CollectionData.PlantCardModel card(
        PlantName name,
        boolean unlocked,
        boolean atMax,
        boolean seedsReady
    ) {
        int requiredSeeds = atMax ? 0 : 10;
        int currentSeeds = seedsReady ? Math.max(requiredSeeds, 10) : 0;
        float seedProgress = atMax || requiredSeeds <= 0
            ? 1f
            : Math.min(1f, (float) currentSeeds / (float) requiredSeeds);
        boolean seedsMet = atMax || currentSeeds >= requiredSeeds;
        boolean canUpgrade = !atMax && unlocked && seedsMet;
        PlantUpgradeView progress = new PlantUpgradeView(
            name,
            atMax ? 10 : 1,
            10,
            atMax,
            canUpgrade,
            currentSeeds,
            requiredSeeds == 0 ? 10 : requiredSeeds,
            0,
            1000,
            seedProgress,
            seedsMet && !atMax,
            false
        );
        return new CollectionData.PlantCardModel(name, unlocked, progress, "", progress.currentLevel());
    }

    private static List<CollectionData.ZombieCardModel> sampleZombies() {
        List<CollectionData.ZombieCardModel> cards = new ArrayList<>();
        cards.add(new CollectionData.ZombieCardModel("Basic Egypt", false));
        cards.add(new CollectionData.ZombieCardModel("Gargantuar1", true));
        cards.add(new CollectionData.ZombieCardModel("Canon1", true));
        return cards;
    }
}
