package app.service.greenhouse;

import app.enums.greenhouse.GreenhousePlantType;
import app.enums.greenhouse.PotStatus;
import app.enums.plant.PlantName;
import app.model.App;
import app.model.greenhouse.*;
import app.model.user.User;
import app.view.greenhouse.config.GreenhouseMessages;
import common.Result;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

public class GreenhouseService
{
    private final Greenhouse greenhouse;
    private final GreenhouseGrowthCostService growthCostService;
    private static final Random RANDOM = new Random();

    public GreenhouseService(Greenhouse greenhouse) {
        this.greenhouse = greenhouse;
        this.growthCostService = new GreenhouseGrowthCostService();
    }

    public GreenhouseService(Greenhouse greenhouse, GreenhouseGrowthCostService growthCostService) {
        this.greenhouse = greenhouse;
        this.growthCostService = growthCostService == null
            ? new GreenhouseGrowthCostService()
            : growthCostService;
    }

    public String showGreenhouse() {
        StringBuilder sb = new StringBuilder();

        sb.append("----------------------------------------------------\n");
        sb.append("|||||||||||||||||||||||||GREENHOUSE||||||||||||||||||||||||||||\n");
        sb.append("----------------------------------------------------\n");

        GreenhousePot[][] pots = greenhouse.getPots();
        int rows = pots.length;
        int cols = pots[0].length;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                GreenhousePot pot = pots[row][col];
                int x = col + 1;
                int y = row + 1;

                sb.append("(").append(x).append(",").append(y).append("): ");

                switch (pot.getPotStatus()) {
                    case LOCKED:
                        sb.append("locked\n");
                        break;
                    case NO_POT:
                        sb.append("no pot\n");
                        break;
                    case EMPTY:
                        sb.append("empty\n");
                        break;
                    case READY:
                        GreenhousePlant plantReady = pot.getPlant();
                        if (plantReady instanceof MarigoldPlant marigold) {
                            sb.append("marigold (")
                                    .append(marigold.getColor())
                                    .append(") - ready\n");
                        } else if (plantReady instanceof BoostPlant boost) {
                            sb.append(boost.getPlant().name().toLowerCase())
                                    .append(" - ready\n");
                        } else {
                            sb.append("ready\n");
                        }
                        break;
                    case GROWING:
                        GreenhousePlant plant = pot.getPlant();
                        if (plant instanceof MarigoldPlant marigold) {
                            sb.append("marigold (")
                                    .append(marigold.getColor())
                                    .append(") - ")
                                    .append(plant.getRemainingTimeText())
                                    .append("\n");
                        } else if (plant instanceof BoostPlant boost) {
                            sb.append(boost.getPlant().name().toLowerCase())
                                    .append(" - ")
                                    .append(plant.getRemainingTimeText())
                                    .append("\n");
                        } else {
                            sb.append("growing - ")
                                    .append(plant.getRemainingTimeText())
                                    .append("\n");
                        }
                        break;
                    default:
                        sb.append("unknown\n");
                }
            }
        }

        sb.append("----------------------------------------------------");
        return sb.toString();
    }

    public boolean unlockPot(int x, int y)
    {
        return unlockPotResult(x, y).success();
    }

    public Result unlockPotResult(int x, int y)
    {
        if (!inBounds(x, y)) {
            return Result.failure(GreenhouseMessages.get("invalidCoordinates"));
        }
        GreenhousePot pot = greenhouse.getPot(x , y);
        if(pot.isUnlocked()) {
            return Result.failure(GreenhouseMessages.get("invalidGreenhouseState"));
        }
        int cost = pot.getCost();
        User user = App.getCurrentUser();
        if (user == null) {
            return Result.failure(GreenhouseMessages.get("invalidGreenhouseState"));
        }
        if (user.getCoins() < cost) {
            return Result.failure(GreenhouseMessages.get("insufficientCoins"));
        }

        pot.unlock();
        user.deductCoins(cost);
        return Result.success(GreenhouseMessages.get("unlockSuccess"));
    }

    public boolean plant(int x, int y)
    {
        return plantResult(x, y).success();
    }

    public Result plantResult(int x, int y)
    {
        if (!GreenhouseGameplaySettings.current().probabilitiesValid()) {
            return Result.failure(GreenhouseMessages.get("invalidProbability"));
        }
        if (!canPlant(x , y)) {
            if (!inBounds(x, y)) {
                return Result.failure(GreenhouseMessages.get("invalidCoordinates"));
            }
            GreenhousePot pot = greenhouse.getPot(x, y);
            if (pot.getPotStatus() == PotStatus.LOCKED) {
                return Result.failure(GreenhouseMessages.get("potLocked"));
            }
            if (pot.getPotStatus() == PotStatus.NO_POT) {
                return Result.failure(GreenhouseMessages.get("noPot"));
            }
            if (pot.getPotStatus() == PotStatus.GROWING || pot.getPotStatus() == PotStatus.READY) {
                return Result.failure(GreenhouseMessages.get("potOccupied"));
            }
            return Result.failure(GreenhouseMessages.get("invalidPlantingTarget"));
        }
        User user = App.getCurrentUser();
        if (user == null) {
            return Result.failure(GreenhouseMessages.get("invalidGreenhouseState"));
        }
        GreenhousePlant generated = generateRandomPlant(user);
        if (generated == null) {
            return Result.failure(GreenhouseMessages.get("invalidConfiguration"));
        }
        if (generated instanceof BoostPlant boost) {
            PlantName boostPlant = boost.getPlant();
            if (boostPlant == null || !user.isUnlocked(boostPlant)) {
                return Result.failure(GreenhouseMessages.get("plantNotUnlocked"));
            }
        }
        GreenhousePot pot = greenhouse.getPot(x , y);
        pot.setPlant(generated);
        app.service.news.NewsPublisher.onGreenhousePlanted(user, x, y, generated);
        return Result.success(GreenhouseMessages.get("plantSuccess"));
    }

    public boolean collect(int x, int y)
    {
        return collectResult(x, y).success();
    }

    public Result collectResult(int x, int y)
    {
        if (!inBounds(x, y)) {
            return Result.failure(GreenhouseMessages.get("invalidCoordinates"));
        }

        GreenhousePot pot = greenhouse.getPot(x, y);
        if (pot.getPotStatus() == PotStatus.LOCKED) {
            return Result.failure(GreenhouseMessages.get("potLocked"));
        }
        if (pot.getPotStatus() == PotStatus.NO_POT) {
            return Result.failure(GreenhouseMessages.get("noPot"));
        }
        if (pot.isEmpty()) {
            return Result.failure(GreenhouseMessages.get("potEmpty"));
        }
        if (!pot.isReady()) {
            return Result.failure(GreenhouseMessages.get("plantNotReady"));
        }

        GreenhousePlant plant = pot.getPlant();
        User user = App.getCurrentUser();
        if (user == null || plant == null) {
            return Result.failure(GreenhouseMessages.get("invalidGreenhouseState"));
        }

        String message = GreenhouseMessages.get("collectSuccess");
        GreenhouseGameplaySettings settings = GreenhouseGameplaySettings.current();

        if (plant instanceof MarigoldPlant marigold) {
            user.addCoins(marigold.getCoinValue());
            message = GreenhouseMessages.get("collectMarigoldSuccess", marigold.getCoinValue());
        } else if (plant.getGreenhousePlantType() == GreenhousePlantType.BOOST) {
            BoostPlant boost = (BoostPlant) plant;
            if (!user.getPlantBoostStatus().containsKey(boost.getPlant()) ||
                    !Boolean.TRUE.equals(user.getPlantBoostStatus().get(boost.getPlant()))) {
                user.getPlantBoostStatus().put(boost.getPlant(), true);
            }
            if (settings.boostGrantsCoins() && settings.boostCoinReward() > 0) {
                user.addCoins(settings.boostCoinReward());
            }
            message = GreenhouseMessages.get("collectBoostSuccess", boost.getPlant().name());
        }

        pot.collect();
        return Result.success(message);
    }

    public boolean grow(int x, int y)
    {
        return growResult(x, y).success();
    }

    public Result growResult(int x, int y)
    {
        if (!inBounds(x, y)) {
            return Result.failure(GreenhouseMessages.get("invalidCoordinates"));
        }

        GreenhousePot pot = greenhouse.getPot(x, y);
        if (pot.getPotStatus()!= PotStatus.GROWING) {
            return Result.failure(GreenhouseMessages.get("invalidPlantState"));
        }

        GreenhousePlant plant = pot.getPlant();
        if (plant == null) {
            return Result.failure(GreenhouseMessages.get("invalidPlantState"));
        }
        int diamondCost = calculateGrowCost(plant);

        User user = App.getCurrentUser();
        if (user == null) {
            return Result.failure(GreenhouseMessages.get("invalidGreenhouseState"));
        }
        if (user.getGems() < diamondCost ) {
            return Result.failure(GreenhouseMessages.get("insufficientGems"));
        }

        plant.completeGrowth();
        user.deductGems(diamondCost);
        pot.isPlantReady();
        if (pot.isReady()) {
            app.service.news.NewsPublisher.onGreenhouseReady(user, x, y, plant);
        }
        return Result.success(GreenhouseMessages.get("instantGrowSuccess"));
    }

    public boolean grow(int x, int y , int hours)
    {
        return growResult(x, y, hours).success();
    }

    public Result growResult(int x, int y , int hours)
    {
        if (hours <= 0) {
            return Result.failure(GreenhouseMessages.get("invalidTimerData"));
        }
        if (!inBounds(x, y)) {
            return Result.failure(GreenhouseMessages.get("invalidCoordinates"));
        }

        GreenhousePot pot = greenhouse.getPot(x, y);
        if (pot.getPotStatus()!= PotStatus.GROWING) {
            return Result.failure(GreenhouseMessages.get("invalidPlantState"));
        }

        GreenhousePlant plant = pot.getPlant();
        if (plant == null) {
            return Result.failure(GreenhouseMessages.get("invalidPlantState"));
        }
        int diamondCost = calculateGrowCost(plant , hours);

        User user = App.getCurrentUser();
        if (user == null) {
            return Result.failure(GreenhouseMessages.get("invalidGreenhouseState"));
        }
        if (user.getGems() < diamondCost ) {
            return Result.failure(GreenhouseMessages.get("insufficientGems"));
        }

        plant.reduceRemainingTime(hours);
        user.deductGems(diamondCost);
        pot.isPlantReady();
        if (pot.isReady()) {
            app.service.news.NewsPublisher.onGreenhouseReady(user, x, y, plant);
        }
        return Result.success(GreenhouseMessages.get("growSuccess"));
    }

    public int calculateGrowCost(GreenhousePlant plant)
    {
        Duration remaining = plant.getRemainingTime();
        return growthCostService.compute(remaining);
    }

    public int calculateGrowCost(GreenhousePlant plant , int hours)
    {
        Duration remaining = plant.getRemainingTime();
        return growthCostService.compute(remaining, hours);
    }

    public boolean canPlant(int x, int y)
    {
        if (!inBounds(x, y)) return false;
        GreenhousePot pot = greenhouse.getPot(x , y);
        return pot.isEmpty();
    }

    public GreenhousePlant generateRandomPlant(User user) {
        GreenhouseGameplaySettings settings = GreenhouseGameplaySettings.current();
        if (!settings.probabilitiesValid()) {
            return null;
        }

        double roll = RANDOM.nextDouble();
        boolean chooseMarigold = roll < settings.marigoldProbability();

        if (chooseMarigold) {
            return new MarigoldPlant(
                LocalDateTime.now(),
                settings.marigoldGrowthDuration(),
                settings.marigoldRewardCoins()
            );
        }

        List<PlantName> eligiblePlants = user.getUnlockedPlants().stream()
                .filter(user::isUnlocked)
                .filter(PlantName::hasPlantFoodEffect)
                .toList();

        if (eligiblePlants.isEmpty()) {
            return new MarigoldPlant(
                LocalDateTime.now(),
                settings.marigoldGrowthDuration(),
                settings.marigoldRewardCoins()
            );
        }

        PlantName randomType = eligiblePlants.get(RANDOM.nextInt(eligiblePlants.size()));
        return new BoostPlant(randomType, LocalDateTime.now(), settings.plantFoodGrowthDuration());
    }

    public boolean tryAddDroppedPlantedPot() {
        User user = App.getCurrentUser();
        if (user == null) {
            return false;
        }
        GreenhousePlant plant = generateRandomPlant(user);
        if (plant == null) {
            return false;
        }
        if (plantInFirstEmptyPot(plant)) {
            return true;
        }
        if (!greenhouse.isPotAvailable(1)) {
            return false;
        }
        greenhouse.addPot();
        return plantInFirstEmptyPot(plant);
    }

    private boolean plantInFirstEmptyPot(GreenhousePlant plant) {
        GreenhousePot[][] pots = greenhouse.getPots();
        for (int row = 0; row < pots.length; row++) {
            for (int col = 0; col < pots[row].length; col++) {
                GreenhousePot pot = pots[row][col];
                if (pot.isEmpty()) {
                    pot.setPlant(plant);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addPhysicalPot() {
        if (!greenhouse.isPotAvailable(1)) {
            return false;
        }
        greenhouse.addPot();
        return true;
    }

    public Result addPhysicalPotResult() {
        if (!greenhouse.isPotAvailable(1)) {
            return Result.failure(GreenhouseMessages.get("noPotAvailable"));
        }
        greenhouse.addPot();
        return Result.success(GreenhouseMessages.get("addPotSuccess"));
    }

    public Greenhouse getGreenhouse() {
        return greenhouse;
    }

    private boolean inBounds(int x, int y) {
        return x >= 1 && y >= 1 && x <= greenhouse.getCol() && y <= greenhouse.getRow();
    }
}
