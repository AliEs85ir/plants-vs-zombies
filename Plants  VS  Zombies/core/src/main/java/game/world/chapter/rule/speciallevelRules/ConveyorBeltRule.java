package game.world.chapter.rule.speciallevelRules;

import app.enums.plant.PlantName;
import game.component.physic.PhysicService;
import game.engine.GameSimulationEngine;
import game.engine.system.special_levels.ConveyorBeltSystem;
import game.service.special_levels.ConveyorBeltConfig;
import game.service.special_levels.ConveyorBeltService;
import game.world.GameWorld;
import game.world.chapter.Chapter;
import game.world.chapter.rule.ChapterRule;
import graphics.GraphicsEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConveyorBeltRule implements ChapterRule {

    private final ConveyorBeltConfig config;
    private final List<PlantName> explicitPool;
    private ConveyorBeltService service;

    public ConveyorBeltRule() {
        this(null, ConveyorBeltConfig.defaults());
    }

    public ConveyorBeltRule(List<PlantName> plantPool) {
        this(plantPool, ConveyorBeltConfig.defaults());
    }

    public ConveyorBeltRule(ConveyorBeltConfig config) {
        this(null, config);
    }

    public ConveyorBeltRule(List<PlantName> plantPool, ConveyorBeltConfig config) {
        this.explicitPool = (plantPool != null) ? new ArrayList<>(plantPool) : null;
        this.config = (config != null) ? config : ConveyorBeltConfig.defaults();
    }

    @Override
    public void onChapterStart(GameWorld world, GraphicsEngine graphicsEngine, PhysicService physicService) {
        if (world != null) {
            world.addEvent("Conveyor Belt engaged. Plants will arrive on the belt.");
        }
    }

    @Override
    public void onTick(GameWorld world, long currentTick) {
    }

    @Override
    public void onEngineStart(GameSimulationEngine engine) {
        GameWorld world = engine.getGameWorld();
        List<PlantName> pool = resolveInitialPool(world);
        this.service = new ConveyorBeltService(world, pool, config);
        engine.addSystem(new ConveyorBeltSystem(this.service));
    }

    @Override
    public boolean allowsManualPlantSelection() {
        return false;
    }

    public ConveyorBeltService getService() {
        return service;
    }

    public ConveyorBeltConfig getConfig() {
        return config;
    }

    public void applyPlantPool(List<PlantName> selectedPlants) {
        applyPlantPool(selectedPlants, 0L);
    }

    public void applyPlantPool(List<PlantName> selectedPlants, long currentTick) {
        if (service == null) return;
        List<PlantName> pool = filterAgainstAllowed(selectedPlants, service.getPlantPool());
        if (pool.isEmpty()) {
            pool = new ArrayList<>(service.getPlantPool());
        }
        if (pool.isEmpty() && explicitPool != null) {
            pool = new ArrayList<>(explicitPool);
        }
        service.setPlantPool(pool);
        service.startAt(currentTick);
    }

    private List<PlantName> resolveInitialPool(GameWorld world) {
        if (explicitPool != null && !explicitPool.isEmpty()) {
            return explicitPool;
        }
        if (world != null && world.getCurrentChapter() != null) {
            Chapter chapter = world.getCurrentChapter();
            if (chapter.getAvailablePlants() != null) {
                return chapter.getAvailablePlants();
            }
        }
        return Collections.emptyList();
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
