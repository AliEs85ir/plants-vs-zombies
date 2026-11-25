package app.view.greenhouse.config;

import java.util.HashMap;
import java.util.Map;

public class GreenhouseMessagesConfig {
    public HashMap<String, String> messages = new HashMap<>();

    public GreenhouseMessagesConfig sanitize() {
        if (messages == null) {
            messages = new HashMap<>();
        }
        HashMap<String, String> cleaned = new HashMap<>();
        for (Map.Entry<String, String> entry : defaults().messages.entrySet()) {
            cleaned.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String> entry : messages.entrySet()) {
            if (entry.getKey() == null || entry.getKey().isBlank()) {
                continue;
            }
            String key = entry.getKey().trim();
            String value = entry.getValue();
            if (value == null || value.isBlank()) {
                continue;
            }
            cleaned.put(key, value);
        }
        messages = cleaned;
        return this;
    }

    public String get(String key) {
        if (key == null || key.isBlank()) {
            return "";
        }
        String value = messages.get(key.trim());
        return value == null ? "" : value;
    }

    public static GreenhouseMessagesConfig defaults() {
        GreenhouseMessagesConfig config = new GreenhouseMessagesConfig();
        config.messages.put("potLocked", "This pot is locked.");
        config.messages.put("potEmpty", "This pot is empty.");
        config.messages.put("noPot", "There is no pot on this plot.");
        config.messages.put("potOccupied", "This pot already has a plant.");
        config.messages.put("shovelUnavailable", "Bill is still on cooldown.");
        config.messages.put("shovelNotSelected", "Select Bill before planting.");
        config.messages.put("invalidPlantingTarget", "You can only plant in an empty pot.");
        config.messages.put("plantNotUnlocked", "That plant is locked. Unlock it in the Collection first.");
        config.messages.put("insufficientGems", "Not enough diamonds to accelerate.");
        config.messages.put("insufficientCoins", "Not enough coins.");
        config.messages.put("plantNotReady", "This plant is not ready yet.");
        config.messages.put("invalidPlantState", "Invalid plant state for this action.");
        config.messages.put("invalidGreenhouseState", "Invalid greenhouse state.");
        config.messages.put("invalidConfiguration", "Greenhouse configuration is invalid.");
        config.messages.put("invalidProbability", "Planting probabilities are invalid.");
        config.messages.put("invalidSavedState", "Saved greenhouse state is invalid.");
        config.messages.put("missingPam", "Plant animation is missing.");
        config.messages.put("missingClip", "Plant animation clip is missing.");
        config.messages.put("missingAsset", "Required asset is missing.");
        config.messages.put("missingJson", "Greenhouse configuration file is missing.");
        config.messages.put("invalidJson", "Greenhouse configuration file is invalid.");
        config.messages.put("invalidTimerData", "Timer data is invalid.");
        config.messages.put("persistenceFailure", "Failed to save greenhouse progress.");
        config.messages.put("outsideSelectableArea", "Outside the selectable area.");
        config.messages.put("billUnavailable", "Bill is unavailable right now.");
        config.messages.put("billSelected", "Bill selected. Click an empty pot to plant.");
        config.messages.put("cancelShovel", "Bill selection cancelled.");
        config.messages.put("plantSuccess", "Planted successfully.");
        config.messages.put("collectSuccess", "Collected successfully.");
        config.messages.put("collectMarigoldSuccess", "%d coins received");
        config.messages.put("collectBoostSuccess", "Plant %s boosted");
        config.messages.put("growSuccess", "Growth accelerated.");
        config.messages.put("instantGrowSuccess", "Plant is ready.");
        config.messages.put("unlockSuccess", "Plot unlocked.");
        config.messages.put("addPotSuccess", "Pot placed.");
        config.messages.put("noPotAvailable", "No empty plot slots for a new pot.");
        config.messages.put("questionDefault", "Buy a pot from the shop to place it on this table.");
        config.messages.put("lockedQuestionDefault", "Unlock this table with coins to make it usable.");
        config.messages.put("debugRequired", "Debug Mode must be enabled.");
        config.messages.put("billDebugSkipSuccess", "Bill cooldown advanced.");
        config.messages.put("billAlreadyAvailable", "Bill is already available.");
        config.messages.put("actionFailed", "Action failed. Please try again.");
        config.messages.put("invalidCoordinates", "Invalid coordinates.");
        return config;
    }
}
