package app.controller.collection;

import app.enums.plant.PlantCategory;
import app.enums.plant.PlantName;
import app.enums.plant.PlantTag;
import app.model.App;
import app.model.user.GameProgress;
import app.model.user.User;
import app.service.plant.PlantBoostService;
import app.service.plant.PlantUnlockPurchaseService;
import app.service.collection.CollectionFilterService;
import app.service.collection.CollectionFilterState;
import app.view.collection.config.CollectionConfig;
import app.view.collection.data.CollectionData;
import app.view.hud.elements.plantcard.PlantCardConfigLoader;
import app.view.hud.elements.zombiecard.ZombieCardConfigLoader;
import common.Result;
import game.entity.plant.PlantDefinition;
import game.entity.plant.PlantInfo;
import game.entity.plant.PlantTags;
import game.entity.plant.plantfood.PlantFoodDefinition;
import game.entity.registry.PlantFoodDefinitionRegistry;
import game.entity.registry.PlantRegistry;
import game.entity.registry.ZombieRegistry;
import game.entity.zombie.data.properties.Property;
import game.service.Zombie.ZombieInfo;
import game.service.Zombie.ZombieInfoRegistry;
import game.service.plant.PlantInfoRegistry;
import game.service.plant.PlantUpgradeService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class CollectionController {
    private final CollectionConfig config;
    private CollectionData.Tab tab = CollectionData.Tab.PLANTS;
    private CollectionData.View view = CollectionData.View.HUB;
    private PlantName selectedPlant;
    private String selectedZombie;
    private final CollectionFilterState plantFilterState = CollectionFilterState.forPlants();
    private final CollectionFilterState zombieFilterState = CollectionFilterState.forZombies();

    public CollectionController(CollectionConfig config) {
        this.config = config;
    }

    public CollectionData.Tab tab() {
        return tab;
    }

    public CollectionData.View view() {
        return view;
    }

    public void openHub() {
        view = CollectionData.View.HUB;
        clearSelection();
    }

    public void openGrid(CollectionData.Tab tab) {
        setTab(tab);
        view = CollectionData.View.GRID;
        clearSelection();
    }

    public void openDetails() {
        view = CollectionData.View.DETAILS;
    }

    public void setTab(CollectionData.Tab tab) {
        this.tab = tab == null ? CollectionData.Tab.PLANTS : tab;
        if (this.tab == CollectionData.Tab.PLANTS) {
            selectedZombie = null;
        } else {
            selectedPlant = null;
        }
    }

    public PlantName selectedPlant() {
        return selectedPlant;
    }

    public String selectedZombie() {
        return selectedZombie;
    }

    public CollectionFilterState plantFilterState() {
        return plantFilterState;
    }

    public CollectionFilterState zombieFilterState() {
        return zombieFilterState;
    }

    public void applyPlantFilters() {
        plantFilterState.applyDraft();
    }

    public void applyZombieFilters() {
        zombieFilterState.applyDraft();
    }

    public void selectPlant(PlantName plantName) {
        this.selectedPlant = plantName;
        this.selectedZombie = null;
        this.tab = CollectionData.Tab.PLANTS;
    }

    public void selectZombie(String alias) {
        this.selectedZombie = alias;
        this.selectedPlant = null;
        this.tab = CollectionData.Tab.ZOMBIES;
    }

    public void clearSelection() {
        selectedPlant = null;
        selectedZombie = null;
    }

    public List<CollectionData.PlantCardModel> plantCards() {
        return CollectionFilterService.filterPlants(allPlantCards(), plantFilterState);
    }

    public List<CollectionData.PlantCardModel> allPlantCards() {
        List<CollectionData.PlantCardModel> cards = new ArrayList<>();
        User user = App.getCurrentUser();
        for (PlantName plantName : plants()) {
            boolean unlocked = user != null && user.isUnlocked(plantName);
            PlantDefinition definition = PlantRegistry.get(plantName);
            PlantCategory category = definition == null ? null : definition.getCategory();
            cards.add(new CollectionData.PlantCardModel(
                plantName,
                unlocked,
                PlantUpgradeService.getProgress(plantName),
                config.categoryIcon(category),
                PlantUpgradeService.getCurrentLevel(plantName)
            ));
        }
        return cards;
    }

    public List<CollectionData.ZombieCardModel> zombieCards() {
        return CollectionFilterService.filterZombies(allZombieCards(), zombieFilterState);
    }

    public List<CollectionData.ZombieCardModel> allZombieCards() {
        List<CollectionData.ZombieCardModel> cards = new ArrayList<>();
        for (String alias : zombies()) {
            cards.add(new CollectionData.ZombieCardModel(alias, isZombieSeen(alias)));
        }
        return cards;
    }

    public CollectionData.PlantDetailsModel plantDetails(PlantName plantName) {
        if (plantName == null) {
            return null;
        }
        User user = App.getCurrentUser();
        boolean unlocked = user != null && user.isUnlocked(plantName);
        PlantInfo info = PlantInfoRegistry.get(plantName);
        PlantDefinition definition = PlantRegistry.get(plantName);
        PlantCategory category = definition == null ? null : definition.getCategory();
        PlantCardConfigLoader.PlantCardConfig card = PlantCardConfigLoader.getConfig(plantName);
        String packetBg = card == null ? null : card.bgKey;
        boolean boosted = PlantBoostService.isBoosted(plantName);
        int unlockPrice = PlantUnlockPurchaseService.priceOf(plantName);
        boolean canAffordUnlock = !unlocked && PlantUnlockPurchaseService.canAfford(user, plantName);
        return new CollectionData.PlantDetailsModel(
            plantName,
            unlocked,
            boosted,
            info.getDisplayName(),
            unlocked ? info.getDescription() : config.labels.lockedPlantBody,
            unlocked ? plantFoodDescription(plantName, info) : "",
            category == null ? "" : category.getLabel(),
            config.categoryIcon(category),
            unlocked ? tagsLabel(definition == null ? null : definition.getTags()) : "",
            config.resolveDetailsBackground(packetBg, boosted),
            unlocked ? PlantUpgradeService.getPreview(user, plantName) : null,
            PlantUpgradeService.getProgress(plantName),
            unlockPrice,
            canAffordUnlock
        );
    }

    public CollectionData.ZombieDetailsModel zombieDetails(String alias) {
        if (alias == null || alias.isBlank()) {
            return null;
        }
        boolean seen = isZombieSeen(alias);
        ZombieInfo info = ZombieInfoRegistry.get(alias);
        Property property = ZombieRegistry.getZombie(alias);
        String hp = "";
        String speed = "";
        if (seen && property != null) {
            hp = String.valueOf(property.getInitialHealth());
            speed = formatSpeed(property.getSpeed());
        }
        ZombieCardConfigLoader.ZombieCardConfig card = ZombieCardConfigLoader.getConfig(alias);
        String packetBg = card == null ? null : card.bgKey;
        return new CollectionData.ZombieDetailsModel(
            alias,
            seen,
            seen ? info.getDisplayName() : config.labels.locked,
            seen ? info.getDescription() : config.labels.lockedZombieBody,
            hp,
            speed,
            config.resolveDetailsBackground(packetBg, false)
        );
    }

    public Result upgradeSelectedPlant() {
        if (selectedPlant == null) {
            return Result.failure(PlantUpgradeService.PLANT_NOT_FOUND);
        }
        return PlantUpgradeService.upgrade(selectedPlant);
    }

    public Result purchaseSelectedPlant() {
        if (selectedPlant == null) {
            return Result.failure(PlantUnlockPurchaseService.PLANT_NOT_FOUND);
        }
        return PlantUnlockPurchaseService.purchase(selectedPlant);
    }

    public PlantName cyclePlant(int delta) {
        List<PlantName> plants = filteredPlantNames();
        if (plants.isEmpty()) {
            return null;
        }
        int index = selectedPlant == null ? 0 : plants.indexOf(selectedPlant);
        if (index < 0) {
            index = 0;
        }
        int next = Math.floorMod(index + delta, plants.size());
        selectedPlant = plants.get(next);
        return selectedPlant;
    }

    public List<PlantName> filteredPlantNames() {
        List<PlantName> names = new ArrayList<>();
        for (CollectionData.PlantCardModel card : plantCards()) {
            if (card != null && card.plantName != null) {
                names.add(card.plantName);
            }
        }
        return names;
    }

    public String cycleZombie(int delta) {
        List<String> zombies = filteredZombieAliases();
        if (zombies.isEmpty()) {
            return null;
        }
        int index = selectedZombie == null ? 0 : zombies.indexOf(selectedZombie);
        if (index < 0) {
            index = 0;
        }
        int next = Math.floorMod(index + delta, zombies.size());
        selectedZombie = zombies.get(next);
        return selectedZombie;
    }

    public List<String> filteredZombieAliases() {
        List<String> aliases = new ArrayList<>();
        for (CollectionData.ZombieCardModel card : zombieCards()) {
            if (card != null && card.alias != null && !card.alias.isBlank()) {
                aliases.add(card.alias);
            }
        }
        return aliases;
    }

    public int coins() {
        User user = App.getCurrentUser();
        return user == null ? 0 : user.getCoins();
    }

    public int gems() {
        User user = App.getCurrentUser();
        return user == null ? 0 : user.getGems();
    }

    public List<PlantName> plants() {
        List<PlantName> names = new ArrayList<>();
        boolean registryReady = !PlantRegistry.getDefinitions().isEmpty();
        for (PlantName plantName : PlantName.values()) {
            if (!registryReady || PlantRegistry.contains(plantName)) {
                names.add(plantName);
            }
        }
        return names;
    }

    public List<String> zombies() {
        List<String> aliases = new ArrayList<>();
        for (Property property : ZombieRegistry.getAllZombies()) {
            if (property != null && property.getAlias() != null && !property.getAlias().isBlank()) {
                aliases.add(property.getAlias());
            }
        }
        aliases.sort(Comparator.naturalOrder());
        return aliases;
    }

    private boolean isZombieSeen(String alias) {
        GameProgress progress = App.getCurrentProgress();
        if (progress != null && progress.hasSeenZombie(alias)) {
            return true;
        }
        if (progress != null && progress.getCollection() != null) {
            return progress.getCollection().getSeenZombieAliases().contains(alias);
        }
        return false;
    }

    private String plantFoodDescription(PlantName plantName, PlantInfo info) {
        if (info != null && info.hasExtra("plantFood")) {
            return info.getExtra("plantFood");
        }
        if (info != null && info.hasExtra("plantFoodDescription")) {
            return info.getExtra("plantFoodDescription");
        }
        if (plantName == null || !plantName.hasPlantFoodEffect()) {
            return config.labels.noPlantFood;
        }
        PlantFoodDefinition definition = PlantFoodDefinitionRegistry.get(plantName);
        String duration = stripTrailingZero(plantName.getPlantFoodDuration());
        if (definition != null && definition.getAbilityType() != null) {
            String type = definition.getAbilityType().name().replace('_', ' ').toLowerCase(Locale.ROOT);
            return config.labels.plantFoodPrefix + type + " (" + duration + "s)";
        }
        return config.labels.plantFoodPrefix + duration + "s";
    }

    private static String tagsLabel(PlantTags tags) {
        if (tags == null || tags.tags().isEmpty()) {
            return "None";
        }
        return tags.tags().stream().map(PlantTag::getLabel).collect(Collectors.joining(", "));
    }

    private static String formatSpeed(double speed) {
        if (Math.abs(speed - Math.round(speed)) < 0.001d) {
            return String.valueOf(Math.round(speed));
        }
        return String.format(Locale.US, "%.2f", speed);
    }

    private static String stripTrailingZero(float value) {
        if (Math.abs(value - Math.round(value)) < 0.001f) {
            return String.valueOf(Math.round(value));
        }
        return String.format(Locale.US, "%.1f", value);
    }
}
