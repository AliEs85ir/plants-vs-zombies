package game.entity.plant.upgrade;

import app.enums.plant.PlantName;

import java.util.LinkedHashMap;
import java.util.Map;

public final class PlantUpgradeCatalog {
    public Map<String, PlantUpgradeDefinition> plants;

    public Map<String, PlantUpgradeDefinition> plantsOrEmpty() {
        return plants == null ? Map.of() : plants;
    }

    public PlantUpgradeDefinition definition(PlantName plantName) {
        if (plantName == null || plants == null) {
            return null;
        }
        return plants.get(plantName.name());
    }

    public int maxLevel(PlantName plantName) {
        PlantUpgradeDefinition definition = definition(plantName);
        if (definition == null || definition.maxLevel < 1) {
            return 1;
        }
        return definition.maxLevel;
    }

    public PlantLevelStep stepToReach(PlantName plantName, int targetLevel) {
        PlantUpgradeDefinition definition = definition(plantName);
        if (definition == null) {
            return null;
        }
        return definition.stepForLevel(targetLevel);
    }

    public PlantLevelModifiers modifiersAt(PlantName plantName, int currentLevel) {
        PlantUpgradeDefinition definition = definition(plantName);
        if (definition == null) {
            return PlantLevelModifiers.NONE;
        }
        return PlantLevelModifiers.accumulate(definition.levelsOrEmpty(), currentLevel);
    }

    public PlantUpgradeCatalog validate() {
        if (plants == null) {
            plants = new LinkedHashMap<>();
        }
        for (Map.Entry<String, PlantUpgradeDefinition> entry : plants.entrySet()) {
            String rawName = entry.getKey();
            PlantUpgradeDefinition definition = entry.getValue();
            if (rawName == null || rawName.isBlank()) {
                throw new PlantUpgradeConfigException("plant_upgrades.json contains a plant entry with an empty name.");
            }
            PlantName plantName;
            try {
                plantName = PlantName.valueOf(rawName.trim());
            } catch (IllegalArgumentException e) {
                throw new PlantUpgradeConfigException(
                    "plant_upgrades.json references unknown plant '" + rawName + "'."
                );
            }
            if (definition == null) {
                throw new PlantUpgradeConfigException(
                    "plant_upgrades.json is missing a definition for " + plantName.name() + "."
                );
            }
            definition.sanitize();
            if (definition.maxLevel < 1) {
                throw new PlantUpgradeConfigException(
                    "plant_upgrades.json: " + plantName.name() + " has invalid maxLevel " + definition.maxLevel + "."
                );
            }
            boolean[] seen = new boolean[definition.maxLevel + 1];
            for (PlantLevelStep step : definition.levelsOrEmpty()) {
                if (step == null) {
                    throw new PlantUpgradeConfigException(
                        "plant_upgrades.json: " + plantName.name() + " has a null level step."
                    );
                }
                if (step.level < 2 || step.level > definition.maxLevel) {
                    throw new PlantUpgradeConfigException(
                        "plant_upgrades.json: " + plantName.name()
                            + " has level " + step.level + " outside 2.." + definition.maxLevel + "."
                    );
                }
                if (seen[step.level]) {
                    throw new PlantUpgradeConfigException(
                        "plant_upgrades.json: " + plantName.name()
                            + " has duplicate level " + step.level + "."
                    );
                }
                seen[step.level] = true;
                if (step.requiredSeedPackets < 0) {
                    throw new PlantUpgradeConfigException(
                        "plant_upgrades.json: " + plantName.name()
                            + " level " + step.level + " has negative requiredSeedPackets."
                    );
                }
                if (step.requiredCoins < 0) {
                    throw new PlantUpgradeConfigException(
                        "plant_upgrades.json: " + plantName.name()
                            + " level " + step.level + " has negative requiredCoins."
                    );
                }
            }
            for (int level = 2; level <= definition.maxLevel; level++) {
                if (!seen[level]) {
                    throw new PlantUpgradeConfigException(
                        "plant_upgrades.json: " + plantName.name()
                            + " is missing the step for level " + level + "."
                    );
                }
            }
        }
        return this;
    }
}
