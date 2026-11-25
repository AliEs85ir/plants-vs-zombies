package game.world.chapter.rule.speciallevelRules;

import app.enums.plant.PlantName;
import game.component.physic.PhysicService;
import game.engine.GameSimulationEngine;
import game.world.GameWorld;
import game.world.chapter.Chapter;
import game.world.chapter.rule.ChapterRule;
import graphics.GraphicsEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

public class VaseBreakerRule implements ChapterRule {

    private final List<PlantName> explicitPool;
    private final List<PlantName> plantPool;

    public VaseBreakerRule() {
        this(null);
    }

    public VaseBreakerRule(List<PlantName> plantPool) {
        this.explicitPool = (plantPool != null) ? new ArrayList<>(plantPool) : null;
        this.plantPool = new ArrayList<>();
        if (this.explicitPool != null) {
            setPlantPool(this.explicitPool);
        }
    }

    @Override
    public void onChapterStart(GameWorld world, GraphicsEngine graphicsEngine, PhysicService physicService) {
        if (world != null) {
            world.addEvent("Vasebreaker: plants arrive only from broken vases.");
        }
    }

    @Override
    public void onTick(GameWorld world, long currentTick) {
    }

    @Override
    public void onEngineStart(GameSimulationEngine engine) {
        if (plantPool.isEmpty()) {
            setPlantPool(resolveInitialPool(engine != null ? engine.getGameWorld() : null));
        }
    }

    @Override
    public boolean allowsManualPlantSelection() {
        return false;
    }

    public List<PlantName> getPlantPool() {
        return Collections.unmodifiableList(plantPool);
    }

    public void applyPlantPool(List<PlantName> selectedPlants) {
        List<PlantName> pool = filterAgainstAllowed(selectedPlants, resolveFallbackPool());
        if (pool.isEmpty()) {
            pool = new ArrayList<>(resolveFallbackPool());
        }
        if (pool.isEmpty() && explicitPool != null) {
            pool = new ArrayList<>(explicitPool);
        }
        setPlantPool(pool);
    }

    private List<PlantName> resolveInitialPool(GameWorld world) {
        if (explicitPool != null && !explicitPool.isEmpty()) {
            return explicitPool;
        }
        return resolveChapterPlants(world);
    }

    private List<PlantName> resolveFallbackPool() {
        if (!plantPool.isEmpty()) {
            return plantPool;
        }
        if (explicitPool != null && !explicitPool.isEmpty()) {
            return explicitPool;
        }
        return Collections.emptyList();
    }

    private static List<PlantName> resolveChapterPlants(GameWorld world) {
        if (world != null && world.getCurrentChapter() != null) {
            Chapter chapter = world.getCurrentChapter();
            if (chapter.getAvailablePlants() != null) {
                return chapter.getAvailablePlants();
            }
        }
        return Collections.emptyList();
    }

    private void setPlantPool(List<PlantName> pool) {
        plantPool.clear();
        if (pool == null) {
            return;
        }
        plantPool.addAll(new LinkedHashSet<>(pool));
        plantPool.removeIf(name -> name == null);
    }

    private static List<PlantName> filterAgainstAllowed(List<PlantName> selected, List<PlantName> allowed) {
        if (selected == null || selected.isEmpty()) {
            return Collections.emptyList();
        }
        List<PlantName> filtered = new ArrayList<>();
        for (PlantName plant : selected) {
            if (plant != null && (allowed == null || allowed.isEmpty() || allowed.contains(plant))) {
                if (!filtered.contains(plant)) {
                    filtered.add(plant);
                }
            }
        }
        return filtered;
    }
}
