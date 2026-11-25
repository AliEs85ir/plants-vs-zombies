package game.service.plant;

import app.enums.plant.*;
import game.entity.plant.PlantDefinition;
import game.entity.plant.PlantTags;
import game.entity.plant.PlantStackLayer;
import game.entity.plant.ability.PlantAbility;

import java.util.ArrayList;
import java.util.List;

public class PlantDefinitionBuilder {
    private PlantName name;
    private PlantCategory category;
    private int sunCost;
    private int maxHealth;
    private int cooldown; // in ticks
    private PlantTags tags;
    private final List<PlantAbility> abilities = new ArrayList<>();
    private int attackDamage;
    private float actionInterval;
    private PlantStackLayer stackLayer = PlantStackLayer.CORE;
    private int maxPerCell = 1;

    public PlantDefinitionBuilder name(PlantName name) {
        this.name = name;
        return this;
    }

    public PlantDefinitionBuilder category(PlantCategory category) {
        this.category = category;
        return this;
    }

    public PlantDefinitionBuilder sunCost(int sunCost) {
        this.sunCost = sunCost;
        return this;
    }

    public PlantDefinitionBuilder maxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        return this;
    }

    public PlantDefinitionBuilder cooldown(int cooldown) {
        this.cooldown = cooldown;
        return this;
    }

    public PlantDefinitionBuilder tags(PlantTags tags) {
        this.tags = tags;
        return this;
    }

    public PlantDefinitionBuilder addAbilities(PlantAbility ability) {
        this.abilities.add(ability);
        return this;
    }

    public PlantDefinitionBuilder attackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
        return this;
    }

    public PlantDefinitionBuilder actionInterval(float actionInterval) {
        this.actionInterval = actionInterval;
        return this;
    }

    public PlantDefinitionBuilder stackLayer(PlantStackLayer stackLayer) {
        this.stackLayer = stackLayer;
        return this;
    }

    public PlantDefinitionBuilder maxPerCell(int maxPerCell) {
        this.maxPerCell = maxPerCell;
        return this;
    }

    public PlantDefinition build() {
        return new PlantDefinition(name, category, sunCost, maxHealth,
                cooldown, tags, abilities, attackDamage, actionInterval,
                stackLayer, maxPerCell);
    }
}
