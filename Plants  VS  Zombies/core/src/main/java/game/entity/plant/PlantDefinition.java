package game.entity.plant;

import app.enums.plant.*;
import game.entity.plant.ability.PlantAbility;

import java.util.List;
import java.util.stream.Collectors;

public class PlantDefinition {
    private final PlantName name;
    private final PlantCategory category;
    private final int sunCost;
    private final int maxHealth;
    private final int cooldown; // in ticks
    private final PlantTags tags;
    private final List<PlantAbility> abilities;
    private final int attackDamage;
    private float actionInterval; // seconds between actions
    private final PlantStackLayer stackLayer;
    private final int maxPerCell;

    // Constructor
    public PlantDefinition(PlantName name, PlantCategory category, int sunCost,
                           int maxHealth, int cooldown, PlantTags tags,
                           List<PlantAbility> abilities, int attackDamage,
                           float actionInterval) {
        this(name, category, sunCost, maxHealth, cooldown, tags, abilities,
            attackDamage, actionInterval, PlantStackLayer.CORE, 1);
    }

    public PlantDefinition(PlantName name, PlantCategory category, int sunCost,
                           int maxHealth, int cooldown, PlantTags tags,
                           List<PlantAbility> abilities, int attackDamage,
                           float actionInterval, PlantStackLayer stackLayer, int maxPerCell) {
        this.name = name;
        this.category = category;
        this.sunCost = sunCost;
        this.maxHealth = maxHealth;
        this.cooldown = cooldown;
        this.tags = tags;
        this.abilities = abilities;
        this.attackDamage = attackDamage;
        this.actionInterval = actionInterval;
        this.stackLayer = stackLayer != null ? stackLayer : PlantStackLayer.CORE;
        this.maxPerCell = Math.max(1, maxPerCell);
    }

    // Getters
    public PlantName getName() {
        return name;
    }

    public PlantCategory getCategory() {
        return category;
    }

    public int getSunCost() {
        return sunCost;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getCooldown() {
        return cooldown;
    }

    public PlantTags getTags() {
        return tags;
    }

    public List<PlantAbility> getAbilities() {
        return abilities;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public float getActionInterval() {
        return actionInterval;
    }

    public PlantStackLayer getStackLayer() {
        return stackLayer;
    }

    public int getMaxPerCell() {
        return maxPerCell;
    }

    // Setter for action interval (if needed)
    public void setActionInterval(float actionInterval) {
        this.actionInterval = actionInterval;
    }

    // Check if plant has a specific tag
    public boolean hasTag(PlantTag tag) {
        return tags != null && tags.hasTag(tag);
    }

    // Dummy update method (as in original)
    public boolean update() {
        return true;
    }

    // Print info with all fields including action interval
    public String printInfo() {
        game.entity.plant.upgrade.EffectivePlantStats stats =
            game.service.plant.PlantUpgradeService.getEffectiveStats(getName());
        String nameVal = String.valueOf(getName());
        String catVal = String.valueOf(getCategory());
        String costVal = String.valueOf(stats.sunCost());
        String healthVal = String.valueOf(stats.maxHealth());
        String cooldownVal = stats.cooldownTicks() + " ticks";
        String tagsVal = (getTags() != null) ? getTags().toString() : "None";
        String abilityVal = (getAbilities() != null) ?
                getAbilities().stream()
                        .map(PlantAbility::toString)
                        .collect(Collectors.joining(", ")) : "None";
        String attackVal = String.valueOf(stats.attackDamage());
        String intervalVal = String.format("%.1f ticks", stats.actionIntervalTicks());
        String levelVal = String.valueOf(game.service.plant.PlantUpgradeService.getCurrentLevel(getName()));

        String[] keys = {"Name", "Category", "Sun Cost", "Max Health",
                "Cooldown", "Tags", "Abilities", "Attack Damage", "Action Interval", "Level"};
        String[] vals = {nameVal, catVal, costVal, healthVal,
                cooldownVal, tagsVal, abilityVal, attackVal, intervalVal, levelVal};

        int maxKeyLen = 0, maxValLen = 0;
        for (int i = 0; i < keys.length; i++) {
            maxKeyLen = Math.max(maxKeyLen, keys[i].length());
            maxValLen = Math.max(maxValLen, vals[i].length());
        }
        int padding = 2;
        int keyColWidth = maxKeyLen + padding * 2;
        int valColWidth = maxValLen + padding * 2;
        int totalWidth = keyColWidth + valColWidth + 3;

        StringBuilder sb = new StringBuilder();
        String topLine = "+" + "-".repeat(keyColWidth) + "+" + "-".repeat(valColWidth) + "+";
        sb.append(topLine).append("\n");
        String title = " Plant Information ";
        int titleWidth = totalWidth - 2;
        sb.append("|").append(centerString(title, titleWidth)).append("|\n");
        sb.append(topLine).append("\n");
        for (int i = 0; i < keys.length; i++) {
            sb.append(formatRow(keys[i], vals[i], maxKeyLen, maxValLen, padding)).append("\n");
        }
        sb.append(topLine);
        return sb.toString();
    }

    private String formatRow(String key, String value, int keyWidth, int valWidth, int padding) {
        return String.format("| %-" + keyWidth + "s | %-" + valWidth + "s |", key, value);
    }

    private String centerString(String str, int width) {
        if (str.length() >= width) return str;
        int left = (width - str.length()) / 2;
        int right = width - str.length() - left;
        return " ".repeat(left) + str + " ".repeat(right);
    }

    public void addAbility(PlantAbility ability)
    {
        abilities.add(ability);
    }
}
