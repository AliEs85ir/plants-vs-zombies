package app.model.greenhouse;

import app.enums.Color;
import app.service.greenhouse.GreenhouseGameplaySettings;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

public class MarigoldPlant extends GreenhousePlant
{
    private final Color color;
    private final int coinValue;

    public MarigoldPlant(LocalDateTime plantedAt) {
        this(
            plantedAt,
            GreenhouseGameplaySettings.current().marigoldGrowthDuration(),
            GreenhouseGameplaySettings.current().marigoldRewardCoins()
        );
    }

    public MarigoldPlant(LocalDateTime plantedAt, Duration growth, int coinValue) {
        super(app.enums.greenhouse.GreenhousePlantType.MARIGOLD, plantedAt, growth);
        this.color = getRandomColor();
        this.coinValue = Math.max(0, coinValue);
    }

    public MarigoldPlant(Color color, int coinValue, LocalDateTime plantedAt, LocalDateTime readyAt) {
        super(app.enums.greenhouse.GreenhousePlantType.MARIGOLD, plantedAt, readyAt);
        this.color = color;
        this.coinValue = coinValue;
    }

    private Color getRandomColor() {
        Color[] colors = Color.values();
        Random random = new Random();
        return colors[random.nextInt(colors.length)];
    }

    public Color getColor() {
        return color;
    }

    public int getCoinValue() {
        return coinValue;
    }

    @Override
    public String getPlantName() {
        return color.toString() + "marigold";
    }
}
