package game.service.drop;

import app.enums.drop.DropType;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public final class DropTable {
    private DropTable() {
    }

    public static Optional<DropType> roll() {
        float roll = ThreadLocalRandom.current().nextFloat();
        float cursor = DropSettings.NOTHING_CHANCE;
        if (roll < cursor) {
            return Optional.empty();
        }
        cursor += DropSettings.SILVER_CHANCE;
        if (roll < cursor) {
            return Optional.of(DropType.SILVER_COIN);
        }
        cursor += DropSettings.GOLD_CHANCE;
        if (roll < cursor) {
            return Optional.of(DropType.GOLD_COIN);
        }
        cursor += DropSettings.PLANT_FOOD_CHANCE;
        if (roll < cursor) {
            return Optional.empty();
        }
        cursor += DropSettings.POT_CHANCE;
        if (roll < cursor) {
            return Optional.of(DropType.POT);
        }
        return Optional.of(DropType.DIAMOND);
    }
}
