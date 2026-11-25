package game.world.chapter.meta;

import app.enums.plant.PlantName;
import game.entity.plant.PlantDefinition;
import game.entity.plant.PlantRole;
import game.entity.registry.PlantRegistry;
import game.service.plant.PlantInfoRegistry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public final class RecommendedPlantResolver {

    private static final Map<PlantRole, Float> ROLE_WEIGHTS = new EnumMap<>(PlantRole.class);

    static {
        ROLE_WEIGHTS.put(PlantRole.SUN, 0.28f);
        ROLE_WEIGHTS.put(PlantRole.OFFENSE, 0.40f);
        ROLE_WEIGHTS.put(PlantRole.DEFENSE, 0.16f);
        ROLE_WEIGHTS.put(PlantRole.INSTANT, 0.12f);
        ROLE_WEIGHTS.put(PlantRole.SUPPORT, 0.04f);
    }

    private RecommendedPlantResolver() {}

    public static List<PlantName> resolve(List<PlantName> configured,
                                          List<PlantName> availablePlants,
                                          int selectionCount) {
        List<PlantName> pool = distinct(availablePlants);
        if (pool.isEmpty() || selectionCount <= 0) {
            return List.of();
        }

        int target = Math.min(selectionCount, pool.size());
        List<PlantName> result = new ArrayList<>();

        if (configured != null) {
            for (PlantName plant : distinct(configured)) {
                if (pool.contains(plant) && result.size() < target) {
                    result.add(plant);
                }
            }
        }

        if (result.size() == target) {
            return List.copyOf(result);
        }

        for (PlantName plant : deriveBalanced(pool, target)) {
            if (result.size() >= target) break;
            if (!result.contains(plant)) {
                result.add(plant);
            }
        }

        for (PlantName plant : pool) {
            if (result.size() >= target) break;
            if (!result.contains(plant)) {
                result.add(plant);
            }
        }

        return List.copyOf(result);
    }

    private static List<PlantName> deriveBalanced(List<PlantName> pool, int target) {
        Map<PlantRole, List<PlantName>> byRole = new EnumMap<>(PlantRole.class);
        for (PlantRole role : PlantRole.values()) {
            byRole.put(role, new ArrayList<>());
        }
        for (PlantName plant : pool) {
            byRole.get(PlantInfoRegistry.get(plant).getRole()).add(plant);
        }

        Comparator<PlantName> byEfficiency = Comparator.comparingDouble(
            RecommendedPlantResolver::efficiencyScore).reversed();
        for (List<PlantName> group : byRole.values()) {
            group.sort(byEfficiency);
        }

        List<PlantName> picked = new ArrayList<>();
        for (Map.Entry<PlantRole, Float> entry : ROLE_WEIGHTS.entrySet()) {
            List<PlantName> group = byRole.get(entry.getKey());
            if (group.isEmpty()) continue;

            int quota = Math.round(target * entry.getValue());
            if (quota <= 0 && entry.getKey() == PlantRole.SUN) {
                quota = 1;
            }
            for (int i = 0; i < Math.min(quota, group.size()); i++) {
                picked.add(group.get(i));
            }
        }

        picked.sort(byEfficiency);
        return picked;
    }

    private static double efficiencyScore(PlantName plant) {
        PlantDefinition definition = PlantRegistry.get(plant);
        if (definition == null) return 0d;

        int cost = Math.max(definition.getSunCost(), 1);
        double value = definition.getAttackDamage() * 2d + definition.getMaxHealth() * 0.25d;
        return value / cost;
    }

    private static List<PlantName> distinct(List<PlantName> source) {
        if (source == null) return List.of();
        return new ArrayList<>(new LinkedHashSet<>(source));
    }
}
