package app.controller.greenhouse;

import app.enums.greenhouse.BillAvailability;
import app.enums.greenhouse.GreenhouseInteractionState;
import app.enums.greenhouse.PotStatus;
import app.model.App;
import app.model.greenhouse.Greenhouse;
import app.model.greenhouse.GreenhousePot;
import app.model.user.User;
import app.service.AppService;
import app.service.greenhouse.GreenhouseBillService;
import app.service.greenhouse.GreenhouseGameplaySettings;
import app.service.greenhouse.GreenhouseGrowthCostService;
import app.service.greenhouse.GreenhouseService;
import app.view.greenhouse.config.GreenhouseConfig;
import app.view.greenhouse.config.GreenhouseMessages;
import common.Result;
import game.settings.GameSettings;

import java.time.Duration;

public class GreenhouseUiController {
    private final Greenhouse greenhouse;
    private final GreenhouseService greenhouseService;
    private final GreenhouseBillService billService;
    private final GreenhouseGrowthCostService growthCostService;
    private final GreenhouseConfig config;
    private GreenhouseInteractionState interactionState = GreenhouseInteractionState.NORMAL;

    public GreenhouseUiController() {
        User user = App.getCurrentUser();
        this.greenhouse = user == null ? new Greenhouse() : user.getGreenhouse();
        GreenhouseGameplaySettings settings = GreenhouseGameplaySettings.current();
        this.config = settings.sourceConfig();
        this.growthCostService = new GreenhouseGrowthCostService(settings.growthCost());
        this.greenhouseService = new GreenhouseService(greenhouse, growthCostService);
        this.billService = new GreenhouseBillService(greenhouse, settings.billCooldownHours());
    }

    public GreenhouseUiController(
        Greenhouse greenhouse,
        GreenhouseService greenhouseService,
        GreenhouseBillService billService,
        GreenhouseGrowthCostService growthCostService,
        GreenhouseConfig config
    ) {
        this.greenhouse = greenhouse;
        this.greenhouseService = greenhouseService;
        this.billService = billService;
        this.growthCostService = growthCostService;
        this.config = config == null ? GreenhouseGameplaySettings.current().sourceConfig() : config.sanitize();
    }

    public GreenhouseInteractionState getInteractionState() {
        return interactionState;
    }

    public BillAvailability getBillAvailability() {
        return billService.availability(interactionState == GreenhouseInteractionState.SHOVEL_SELECTED);
    }

    public Result selectBill() {
        if (!billService.isAvailable()) {
            return Result.failure(GreenhouseMessages.get("billUnavailable"));
        }
        interactionState = GreenhouseInteractionState.SHOVEL_SELECTED;
        return Result.success(GreenhouseMessages.get("billSelected"));
    }

    public Result cancelBill() {
        interactionState = GreenhouseInteractionState.NORMAL;
        return Result.success(GreenhouseMessages.get("cancelShovel"));
    }

    public Result plantWithBill(int col, int row) {
        if (interactionState != GreenhouseInteractionState.SHOVEL_SELECTED) {
            return Result.failure(GreenhouseMessages.get("shovelNotSelected"));
        }
        if (!billService.isAvailable()) {
            interactionState = GreenhouseInteractionState.NORMAL;
            return Result.failure(GreenhouseMessages.get("shovelUnavailable"));
        }
        if (!inBounds(col, row)) {
            return Result.failure(GreenhouseMessages.get("outsideSelectableArea"));
        }
        GreenhousePot pot = greenhouse.getPot(col, row);
        if (pot.getPotStatus() == PotStatus.LOCKED) {
            return Result.failure(GreenhouseMessages.get("potLocked"));
        }
        if (pot.getPotStatus() == PotStatus.NO_POT) {
            return Result.failure(GreenhouseMessages.get("noPot"));
        }
        if (!pot.isEmpty()) {
            return Result.failure(GreenhouseMessages.get("potOccupied"));
        }

        Result planted = greenhouseService.plantResult(col, row);
        if (!planted.success()) {
            return planted;
        }

        billService.consume();
        if (config.interaction.deselectBillAfterPlant) {
            interactionState = GreenhouseInteractionState.NORMAL;
        }
        sync();
        return planted;
    }

    public Result instantGrow(int col, int row) {
        if (!inBounds(col, row)) {
            return Result.failure(GreenhouseMessages.get("outsideSelectableArea"));
        }
        Result result = greenhouseService.growResult(col, row);
        if (result.success()) {
            sync();
        }
        return result;
    }

    public Result growHours(int col, int row, int hours) {
        if (!inBounds(col, row)) {
            return Result.failure(GreenhouseMessages.get("outsideSelectableArea"));
        }
        Result result = greenhouseService.growResult(col, row, hours);
        if (result.success()) {
            sync();
        }
        return result;
    }

    public Result collect(int col, int row) {
        if (!inBounds(col, row)) {
            return Result.failure(GreenhouseMessages.get("outsideSelectableArea"));
        }
        Result result = greenhouseService.collectResult(col, row);
        if (result.success()) {
            sync();
        }
        return result;
    }

    public Result unlock(int col, int row) {
        if (!inBounds(col, row)) {
            return Result.failure(GreenhouseMessages.get("outsideSelectableArea"));
        }
        Result result = greenhouseService.unlockPotResult(col, row);
        if (result.success()) {
            sync();
        }
        return result;
    }

    public Result addPot() {
        Result result = greenhouseService.addPhysicalPotResult();
        if (result.success()) {
            sync();
        }
        return result;
    }

    public Result onQuestionClick(int col, int row) {
        if (!inBounds(col, row)) {
            return Result.failure(GreenhouseMessages.get("outsideSelectableArea"));
        }
        GreenhousePot pot = greenhouse.getPot(col, row);
        if (pot.getPotStatus() != PotStatus.NO_POT && pot.getPotStatus() != PotStatus.LOCKED) {
            return Result.failure(GreenhouseMessages.get("invalidGreenhouseState"));
        }
        boolean locked = pot.getPotStatus() == PotStatus.LOCKED;
        String text = config.questionIcon.textForStatus(locked, row, col);
        if (text == null || text.isBlank()) {
            text = GreenhouseMessages.get(locked ? "lockedQuestionDefault" : "questionDefault");
        }
        return Result.success(text);
    }

    public Result debugSkipBillCooldown() {
        if (!GameSettings.gameplay().debugMode()) {
            return Result.failure(GreenhouseMessages.get("debugRequired"));
        }
        if (billService.isAvailable()) {
            return Result.failure(GreenhouseMessages.get("billAlreadyAvailable"));
        }
        long minutes = Math.max(1L, Math.round(config.bill.debugSkipMinutes));
        billService.skip(Duration.ofMinutes(minutes));
        sync();
        return Result.success(GreenhouseMessages.get("billDebugSkipSuccess"));
    }

    public Result handleRightClickCancel() {
        if (!config.interaction.cancelOnRightClick) {
            return Result.failure(GreenhouseMessages.get("actionFailed"));
        }
        if (interactionState != GreenhouseInteractionState.SHOVEL_SELECTED) {
            return Result.failure(GreenhouseMessages.get("shovelNotSelected"));
        }
        return cancelBill();
    }

    public int growthCostFor(int col, int row) {
        if (!inBounds(col, row)) {
            return 0;
        }
        GreenhousePot pot = greenhouse.getPot(col, row);
        if (pot.getPlant() == null) {
            return 0;
        }
        return growthCostService.compute(pot.getPlant().getRemainingTime());
    }

    public GreenhouseService getGreenhouseService() {
        return greenhouseService;
    }

    public GreenhouseBillService getBillService() {
        return billService;
    }

    public Greenhouse getGreenhouse() {
        return greenhouse;
    }

    public GreenhouseConfig getConfig() {
        return config;
    }

    private boolean inBounds(int col, int row) {
        return col >= 1 && row >= 1 && col <= greenhouse.getCol() && row <= greenhouse.getRow();
    }

    private void sync() {
        AppService.syncGameData();
    }
}
