package app.view.greenhouse.config;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GreenhousePlantVisualsConfig {
    public PlantVisualConfig defaultPlant = PlantVisualConfig.marigoldDefault();
    public HashMap<String, PlantVisualConfig> plants = new HashMap<>();

    public GreenhousePlantVisualsConfig sanitize() {
        if (defaultPlant == null) {
            defaultPlant = PlantVisualConfig.marigoldDefault();
        }
        defaultPlant.sanitize(
            "768/INITIAL/PLANT/MARIGOLD/MARIGOLD.PAM",
            "idle",
            0.35f,
            0f,
            20f
        );
        if (plants == null) {
            plants = new HashMap<>();
        }
        HashMap<String, PlantVisualConfig> cleaned = new HashMap<>();
        for (Map.Entry<String, PlantVisualConfig> entry : plants.entrySet()) {
            if (entry.getKey() == null || entry.getKey().isBlank() || entry.getValue() == null) {
                continue;
            }
            String key = entry.getKey().trim().toUpperCase(Locale.ROOT);
            PlantVisualConfig visual = entry.getValue();
            visual.sanitize(
                defaultPlant.pamPath,
                defaultPlant.clip,
                defaultPlant.scale,
                defaultPlant.offsetX,
                defaultPlant.offsetY
            );
            cleaned.put(key, visual);
        }
        plants = cleaned;
        return this;
    }

    public PlantVisualConfig resolve(String plantKey) {
        if (plantKey == null || plantKey.isBlank()) {
            return defaultPlant;
        }
        PlantVisualConfig visual = plants.get(plantKey.trim().toUpperCase(Locale.ROOT));
        return visual == null ? defaultPlant : visual;
    }

    public static class PlantVisualConfig {
        public String pamPath = "768/INITIAL/PLANT/MARIGOLD/MARIGOLD.PAM";
        public String clip = "idle";
        public float scale = 0.35f;
        public float offsetX;
        public float offsetY = 20f;
        public float rotation;
        public float alpha = 1f;
        public boolean loop = true;

        public static PlantVisualConfig marigoldDefault() {
            return new PlantVisualConfig();
        }

        public void sanitize(String fallbackPam, String fallbackClip, float fallbackScale, float fallbackOffsetX, float fallbackOffsetY) {
            pamPath = GreenhouseConfig.text(pamPath, fallbackPam);
            clip = GreenhouseConfig.text(clip, fallbackClip);
            scale = GreenhouseConfig.positive(scale, fallbackScale);
            offsetX = GreenhouseConfig.finite(offsetX, fallbackOffsetX);
            offsetY = GreenhouseConfig.finite(offsetY, fallbackOffsetY);
            rotation = GreenhouseConfig.finite(rotation, 0f);
            alpha = GreenhouseConfig.clamp01(alpha, 1f);
        }
    }
}
