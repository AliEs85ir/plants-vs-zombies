package graphics.zombie.visibility;

import game.entity.zombie.Zombie;
import graphics.general.event.VisualEvent;
import graphics.general.event.ZombieVisualEventType;
import graphics.zombie.ZombieRenderInstance;
import graphics.zombie.ZombieVisualState;
import graphics.zombie.visibility.conditions.VisibilityConditionEvaluator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class VisibilitySystem {
    private final Zombie zombie;
    private final ZombieRenderInstance renderInstance;
    private final VisibilityConfig config;

    private final Map<String, Boolean> currentVisibility = new HashMap<>();

    public VisibilitySystem(Zombie zombie, ZombieRenderInstance renderInstance, VisibilityConfig config) {
        this.zombie = zombie;
        this.renderInstance = renderInstance;
        this.config = config;
    }

    public void update() {
        currentVisibility.clear();

        // 1. Apply state-based visibility (lowest priority)
        applyStateVisibility();

        // 2. Apply condition-based visibility (medium priority)
        applyConditionVisibility();

        // 3. Apply event-based visibility (highest priority)
        applyEventVisibility();
    }

    private void applyStateVisibility() {
        ZombieVisualState currentState = renderInstance.getCurrentVisualState();

        if (config.getStateVisibility() != null) {
            Map<String, Boolean> stateLayers = config.getStateVisibility().get(currentState);
            if (stateLayers != null) {
                currentVisibility.putAll(stateLayers);
            }
        }
    }

    private void applyConditionVisibility() {
        if (config.getConditionVisibilities() == null) return;

        for (VisibilityConfig.ConditionVisibility condition : config.getConditionVisibilities()) {
            VisibilityConditionEvaluator evaluator = VisibilityConditionRegistry.getEvaluator(condition.getType());
            if (evaluator != null && evaluator.evaluate(zombie, condition)) {
                currentVisibility.putAll(condition.getLayers());
            }
        }
    }

    private void applyEventVisibility() {
        VisualEvent<ZombieVisualEventType> currentEvent = renderInstance.getCurrentEvent();
        if (currentEvent != null && config.getEventVisibility() != null) {
            Map<String, Boolean> eventLayers = config.getEventVisibility().get(currentEvent.getType());
            if (eventLayers != null) {
                currentVisibility.putAll(eventLayers);
            }
        }
    }

    public Map<String, Boolean> getVisibilityMap() {
        return Collections.unmodifiableMap(currentVisibility);
    }
}
