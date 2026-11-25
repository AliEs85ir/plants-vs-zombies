package graphics.zombie.visibility;

import graphics.general.event.ZombieVisualEventType;
import graphics.zombie.ZombieVisualState;

import java.util.List;
import java.util.Map;

public class VisibilityConfig {
    private Map<ZombieVisualEventType, Map<String, Boolean>> eventVisibility;
    private List<ConditionVisibility> conditionVisibilities;
    private Map<ZombieVisualState, Map<String, Boolean>> stateVisibility;

    public void setEventVisibility(Map<ZombieVisualEventType, Map<String, Boolean>> eventVisibility) {
        this.eventVisibility = eventVisibility;
    }

    public void setConditionVisibilities(List<ConditionVisibility> conditionVisibilities) {
        this.conditionVisibilities = conditionVisibilities;
    }

    public void setStateVisibility(Map<ZombieVisualState, Map<String, Boolean>> stateVisibility) {
        this.stateVisibility = stateVisibility;
    }

    public Map<ZombieVisualEventType, Map<String, Boolean>> getEventVisibility() {
        return eventVisibility;
    }

    public List<ConditionVisibility> getConditionVisibilities() {
        return conditionVisibilities;
    }

    public Map<ZombieVisualState, Map<String, Boolean>> getStateVisibility() {
        return stateVisibility;
    }

    public boolean hasAnyRules() {
        return (stateVisibility != null && !stateVisibility.isEmpty())
            || (conditionVisibilities != null && !conditionVisibilities.isEmpty())
            || (eventVisibility != null && !eventVisibility.isEmpty());
    }




    public static class ConditionVisibility {
        private String type;
        private Object value;
        private Map<String, Boolean> layers;
        private String phase;

        public void setType(String type) {
            this.type = type;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public void setLayers(Map<String, Boolean> layers) {
            this.layers = layers;
        }

        public String getType() {
            return type;
        }

        public Object getValue() {
            return value;
        }

        public Map<String, Boolean> getLayers() {
            return layers;
        }
        public String getPhase() {
            return phase;
        }

        public void setPhase(String phase) {
            this.phase = phase;
        }
    }

}


