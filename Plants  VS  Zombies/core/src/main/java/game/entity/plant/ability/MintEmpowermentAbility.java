package game.entity.plant.ability;

import app.enums.plant.PlantCategory;
import app.enums.plant.PlantGameplayPhase;
import app.enums.plant.PlantState;
import game.engine.time.GameClock;
import game.entity.plant.Plant;
import game.entity.plant.upgrade.PlantLevelModifiers;
import game.entity.plant.upgrade.PlantUpgradeKeys;
import game.service.plant.PlantFoodService;
import game.service.plant.PlantService;
import game.service.plant.PlantUpgradeService;
import game.world.GameWorld;
import game.world.playground.Cell;
import graphics.general.event.PlantVisualEventType;
import graphics.plant.PlantVisualState;

import java.util.ArrayList;
import java.util.List;

/**
 * Single mint family-empowerment ability. Target category comes from the mint's
 * PlantDefinition category; empowerment is a one-shot board snapshot that
 * triggers the existing plant-food API.
 */
public class MintEmpowermentAbility extends AbstractPlantAbility {
    public static final int MINT_DURATION_TICKS = 4 * GameClock.TICKS_PER_SECOND;

    public enum Phase {
        PLANTED,
        INTRO,
        LOOP,
        OUTRO,
        REMOVED
    }

    private Phase phase = Phase.PLANTED;
    private boolean empowered;
    private boolean finished;
    private long startTick = -1L;
    private long outroStartTick = -1L;
    private long lastProcessedTick = Long.MIN_VALUE;
    private int snapshotSize;

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null || finished) {
            return;
        }
        if (currentTick == lastProcessedTick) {
            return;
        }
        lastProcessedTick = currentTick;

        if (!isMintAlive(plant)) {
            abortWithoutCorrupting();
            return;
        }

        if (phase == Phase.PLANTED) {
            startTick = currentTick;
            empowerOnce(plant, plantService, currentTick);
            enterIntro(plant);
        }

        if (phase == Phase.INTRO && introComplete(plant, currentTick)) {
            enterLoop(plant);
        }

        if ((phase == Phase.INTRO || phase == Phase.LOOP)
            && currentTick - startTick >= lifetimeTicks(plant)) {
            enterOutro(plant, currentTick);
        }

        if (phase == Phase.OUTRO && outroComplete(plant, currentTick)) {
            finish(plant, plantService, currentTick);
        }
    }

    @Override
    public boolean handleDeath(Plant plant, PlantService plantService, long currentTick) {
        abortWithoutCorrupting();
        return false;
    }

    private void empowerOnce(Plant plant, PlantService plantService, long currentTick) {
        if (empowered) {
            return;
        }
        empowered = true;
        GameWorld world = worldOf(plantService);
        List<Plant> board = world != null ? world.getPlants() : List.of();
        List<Plant> snapshot = snapshotFamily(plant, board);
        snapshotSize = snapshot.size();
        for (Plant target : snapshot) {
            PlantFoodService.activateExistingEffect(target, currentTick);
        }
    }

    public static List<Plant> snapshotFamily(Plant mint, Iterable<Plant> plants) {
        List<Plant> snapshot = new ArrayList<>();
        PlantCategory category = empoweredCategory(mint);
        if (category == null || plants == null) {
            return snapshot;
        }
        for (Plant candidate : plants) {
            if (isEligibleTarget(mint, candidate, category)) {
                snapshot.add(candidate);
            }
        }
        return snapshot;
    }

    public static PlantCategory empoweredCategory(Plant mint) {
        if (mint == null || mint.getDefinition() == null) {
            return null;
        }
        return mint.getDefinition().getCategory();
    }

    public static boolean isEligibleTarget(Plant mint, Plant candidate, PlantCategory category) {
        if (candidate == null || candidate == mint || category == null) {
            return false;
        }
        if (!isActiveOnBoard(candidate)) {
            return false;
        }
        return candidate.getDefinition() != null
            && candidate.getDefinition().getCategory() == category;
    }

    public static boolean isActiveOnBoard(Plant plant) {
        if (plant == null || !plant.isAlive() || plant.isDead()) {
            return false;
        }
        if (plant.getState() == PlantState.DISABLED) {
            return false;
        }
        Cell cell = plant.getCell();
        if (cell == null) {
            return true;
        }
        List<Plant> occupants = cell.getPlants();
        if (occupants == null || occupants.isEmpty()) {
            return cell.getCurrentPlant() == plant;
        }
        return occupants.contains(plant);
    }

    public int lifetimeTicks(Plant mint) {
        int extra = 0;
        if (mint != null && mint.getDefinition() != null && mint.getDefinition().getName() != null) {
            PlantLevelModifiers modifiers = PlantUpgradeService.getModifiers(mint.getDefinition().getName());
            extra = Math.max(0, modifiers.ticks(PlantUpgradeKeys.EXTRA_MINT_DURATION));
        }
        return MINT_DURATION_TICKS + extra;
    }

    private void enterIntro(Plant plant) {
        phase = Phase.INTRO;
        plant.setGameplayPhase(PlantGameplayPhase.ATTACKING);
        PlantAbilitySupport.play(plant, PlantVisualEventType.ATTACK_TRIGGERED);
    }

    private void enterLoop(Plant plant) {
        if (phase != Phase.INTRO) {
            return;
        }
        phase = Phase.LOOP;
        plant.setGameplayPhase(PlantGameplayPhase.DEFAULT);
    }

    private void enterOutro(Plant plant, long currentTick) {
        if (phase == Phase.OUTRO || phase == Phase.REMOVED) {
            return;
        }
        phase = Phase.OUTRO;
        outroStartTick = currentTick;
        plant.setGameplayPhase(PlantGameplayPhase.ATTACKING);
        PlantAbilitySupport.play(plant, PlantVisualEventType.ATTACK_2_TRIGGERED);
    }

    private boolean introComplete(Plant plant, long currentTick) {
        if (PlantAbilitySupport.instanceOf(plant) != null
            && PlantAbilitySupport.isClipFinished(plant, PlantVisualState.ATTACK)) {
            return true;
        }
        return currentTick > startTick;
    }

    private boolean outroComplete(Plant plant, long currentTick) {
        if (PlantAbilitySupport.instanceOf(plant) == null) {
            return currentTick > outroStartTick;
        }
        if (PlantAbilitySupport.isClipFinished(plant, PlantVisualState.ATTACK_2)) {
            return true;
        }
        return currentTick - outroStartTick >= 5 * GameClock.TICKS_PER_SECOND;
    }

    private void finish(Plant plant, PlantService plantService, long currentTick) {
        if (finished) {
            return;
        }
        finished = true;
        phase = Phase.REMOVED;
        setActive(false);
        PlantAbilitySupport.consumeLater(plant, plantService, currentTick, 0);
    }

    private void abortWithoutCorrupting() {
        finished = true;
        phase = Phase.REMOVED;
        setActive(false);
    }

    private static boolean isMintAlive(Plant plant) {
        return plant != null && plant.isAlive() && !plant.isDead();
    }

    private static GameWorld worldOf(PlantService plantService) {
        if (plantService != null && plantService.getGameWorld() != null) {
            return plantService.getGameWorld();
        }
        return null;
    }

    public boolean hasEmpowered() {
        return empowered;
    }

    public boolean isFinished() {
        return finished;
    }

    public Phase getPhase() {
        return phase;
    }

    public int getSnapshotSize() {
        return snapshotSize;
    }
}
