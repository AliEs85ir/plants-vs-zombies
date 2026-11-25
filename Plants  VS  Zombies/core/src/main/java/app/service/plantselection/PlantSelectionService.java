package app.service.plantselection;

import app.enums.plant.PlantName;
import app.model.App;
import app.model.game.PendingGameSession;
import game.entity.plant.Plant;
import game.entity.plant.PlantCooldown;
import game.entity.plant.PlantDefinition;
import game.entity.plant.ability.PlantAbility;
import game.entity.registry.PlantRegistry;
import game.service.plant.PlantFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlantSelectionService {

    private static final int TABLE_WIDTH = 100;

    private String padRight(String s, int width) {
        if (s.length() >= width) return s.substring(0, width);
        return s + " ".repeat(width - s.length());
    }

    private String truncate(String s, int maxLength) {
        if (s.length() <= maxLength) return s;
        return s.substring(0, maxLength - 3) + "...";
    }

    public String showAllPlants() {
        Map<PlantName, PlantDefinition> definitions = PlantRegistry.getDefinitions();
        if (definitions == null || definitions.isEmpty()) {
            return "╔" + "═".repeat(TABLE_WIDTH - 2) + "╗\n" +
                    "║" + padRight("No plants registered.", TABLE_WIDTH - 2) + "║\n" +
                    "╚" + "═".repeat(TABLE_WIDTH - 2) + "╝";
        }

        StringBuilder sb = new StringBuilder();
        String borderTop = "╔" + "═".repeat(TABLE_WIDTH - 2) + "╗\n";
        String separator = "╠" + "═".repeat(TABLE_WIDTH - 2) + "╣\n";
        String borderBottom = "╚" + "═".repeat(TABLE_WIDTH - 2) + "╝";

        sb.append(borderTop);
        sb.append("║").append(padRight("🌱  PLANT_PLANT COLLECTION  🌱", TABLE_WIDTH - 2)).append("║\n");
        sb.append(separator);

        int index = 1;
        for (PlantDefinition def : definitions.values()) {
            Plant plant = PlantFactory.create(def.getName());
            String name = def.getName() != null ? def.getName().name() : "Unknown";
            String line1 = String.format("%-2d. %-20s │ Cost: %-4d │ HP: %-5d │ Cooldown: %-4d │ Damage: %-4d",
                    index++,
                    truncate(name, 20),
                    plant.getSunCost(),
                    plant.getHealth().getMaxHealth(),
                    PlantCooldown.getCoolDown(plant.getDefinition().getName()),
                    plant.getAttackDamage()
            );
            sb.append("║").append(padRight(line1, TABLE_WIDTH - 2)).append("║\n");

            String type = def.getName() != null ? def.getName().name() : "N/A";
            String category = def.getCategory() != null ? def.getCategory().name() : "N/A";
            String line2 = String.format("Type: %-12s │ Category: %-16s",
                    truncate(type, 12),
                    truncate(category, 16)
            );
            sb.append("║").append(padRight(line2, TABLE_WIDTH - 2)).append("║\n");

            String tagsStr = "None";
            if (def.getTags() != null && !def.getTags().tags().isEmpty()) {
                tagsStr = def.getTags().toString();
            }
            String line3 = String.format("Tags: %-68s", truncate(tagsStr, 68));
            sb.append("║").append(padRight(line3, TABLE_WIDTH - 2)).append("║\n");

            String abilitiesStr = "None";
            if (def.getAbilities() != null && !def.getAbilities().isEmpty()) {
                List<String> abilityNames = new ArrayList<>();
                for (PlantAbility ability : def.getAbilities()) {
                    abilityNames.add(ability.getClass().getSimpleName());
                }
                abilitiesStr = String.join(", ", abilityNames);
            }
            String line4 = String.format("Abilities: %-60s", truncate(abilitiesStr, 60));
            sb.append("║").append(padRight(line4, TABLE_WIDTH - 2)).append("║\n");

            if (index - 1 < definitions.size()) {
                sb.append(separator);
            }
        }

        sb.append(borderBottom);
        return sb.toString();
    }

    public String showAvailablePlants() {
        PendingGameSession session = App.getPendingGameSession();
        List<PlantName> availablePlants = new ArrayList<>();

        if (session.getSelectedChapter() != null) {
            availablePlants = session.getSelectedChapter().getAvailablePlants();
        }
        if (availablePlants == null || availablePlants.isEmpty()) {
            return "╔" + "═".repeat(TABLE_WIDTH - 2) + "╗\n" +
                    "║" + padRight("No plants available.", TABLE_WIDTH - 2) + "║\n" +
                    "╚" + "═".repeat(TABLE_WIDTH - 2) + "╝";
        }

        StringBuilder sb = new StringBuilder();
        String borderTop = "╔" + "═".repeat(TABLE_WIDTH - 2) + "╗\n";
        String separator = "╠" + "═".repeat(TABLE_WIDTH - 2) + "╣\n";
        String borderBottom = "╚" + "═".repeat(TABLE_WIDTH - 2) + "╝";

        sb.append(borderTop);
        sb.append("║").append(padRight("🌱  PLANT_PLANT COLLECTION  🌱", TABLE_WIDTH - 2)).append("║\n");
        sb.append(separator);

        int index = 1;
        for (PlantName plantName : availablePlants) {
            PlantDefinition def = PlantRegistry.get(plantName);
            Plant plant = PlantFactory.create(def.getName());
            if (def == null) continue;

            String name = def.getName() != null ? def.getName().name() : "Unknown";
            String line1 = String.format("%-2d. %-20s │ Cost: %-4d │ HP: %-5d │ Cooldown: %-4d │ Damage: %-4d",
                    index++,
                    truncate(name, 20),
                    plant.getSunCost(),
                    plant.getHealth().getMaxHealth(),
                    PlantCooldown.getCoolDown(plant.getDefinition().getName()),
                    plant.getAttackDamage()
            );
            sb.append("║").append(padRight(line1, TABLE_WIDTH - 2)).append("║\n");

            String category = def.getCategory() != null ? def.getCategory().name() : "N/A";
            String line2 = String.format("Category: %-16s", truncate(category, 16));
            sb.append("║").append(padRight(line2, TABLE_WIDTH - 2)).append("║\n");

            String tagsStr = "None";
            if (def.getTags() != null && !def.getTags().tags().isEmpty()) {
                tagsStr = def.getTags().toString();
            }
            String line3 = String.format("Tags: %-68s", truncate(tagsStr, 68));
            sb.append("║").append(padRight(line3, TABLE_WIDTH - 2)).append("║\n");

            String abilitiesStr = "None";
            if (def.getAbilities() != null && !def.getAbilities().isEmpty()) {
                List<String> abilityNames = new ArrayList<>();
                for (PlantAbility ability : def.getAbilities()) {
                    abilityNames.add(ability.getClass().getSimpleName());
                }
                abilitiesStr = String.join(", ", abilityNames);
            }
            String line4 = String.format("Abilities: %-60s", truncate(abilitiesStr, 60));
            sb.append("║").append(padRight(line4, TABLE_WIDTH - 2)).append("║\n");

            if (index - 1 < availablePlants.size()) {
                sb.append(separator);
            }
        }

        sb.append(borderBottom);
        return sb.toString();
    }

    public boolean isPlantInRegistry(PlantName plantName) {
        if (plantName == null) return false;
        return PlantRegistry.getDefinitions().containsKey(plantName);
    }

    public boolean isPlantAvailableInGame(PlantName plantName) {
        PendingGameSession session = App.getPendingGameSession();
        if (session.getSelectedChapter() == null) return false;

        List<PlantName> available = session.getSelectedChapter().getAvailablePlants();
        return available != null && available.contains(plantName);
    }

    public boolean isPlantAlreadySelected(PlantName plantName) {
        if (plantName == null) return false;
        return App.getPendingGameSession().getSelectedPlants().contains(plantName);
    }
}
