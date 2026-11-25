package app.view.adventure;

import java.util.Arrays;
import java.util.Comparator;

public final class AdventureCarouselMath {
    public static final class Slot {
        public float centerX;
        public float centerY;
        public float depth;
        public float scale;
        public float alpha;
        public float shade;
        public boolean render;
        public boolean front;
    }

    private AdventureCarouselMath() {
    }

    public static int wrap(int index, int count) {
        if (count <= 0) {
            return 0;
        }
        int wrapped = index % count;
        return wrapped < 0 ? wrapped + count : wrapped;
    }

    public static float stepRadians(int count, AdventureMenuConfig.RotationConfig rotation) {
        if (rotation == null) {
            return count <= 0 ? 0f : (float) (Math.PI * 2.0 / count);
        }
        return rotation.stepRadians(count);
    }

    public static void compute(
        Slot[] slots,
        int count,
        float rotationRadians,
        int frontIndex,
        AdventureMenuConfig config,
        float areaWidth,
        float areaHeight
    ) {
        if (slots == null) {
            return;
        }
        int n = Math.min(count, slots.length);
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == null) {
                slots[i] = new Slot();
            }
            slots[i].render = false;
            slots[i].front = false;
            slots[i].depth = 0f;
            slots[i].scale = 0f;
            slots[i].alpha = 0f;
            slots[i].shade = 0f;
        }
        if (n <= 0 || config == null) {
            return;
        }
        float globalScale = config.carousel.scale;
        float cx = config.carousel.resolvedCenterX(areaWidth);
        float cy = config.carousel.resolvedCenterY(areaHeight);
        float radiusX = (config.geometry.radiusX + config.chapter.spacing) * globalScale;
        float radiusY = config.geometry.radiusY * globalScale;
        float step = stepRadians(n, config.rotation);
        float frontAngle = (float) Math.toRadians(config.rotation.frontAngle);
        int logicalFront = wrap(frontIndex, n);
        for (int i = 0; i < n; i++) {
            Slot slot = slots[i];
            float angle = i * step - rotationRadians + frontAngle;
            float cos = (float) Math.cos(angle);
            float sin = (float) Math.sin(angle);
            float depthNorm = (cos + 1f) * 0.5f;
            float depthScale = lerp(config.depth.backScale, config.depth.frontScale, depthNorm);
            boolean front = i == logicalFront;
            slot.centerX = cx + config.carousel.frontOffsetX * depthNorm + radiusX * sin;
            slot.centerY = cy
                + config.carousel.frontOffsetY * depthNorm
                + radiusY * (1f - cos) * 0.5f
                - config.depth.verticalShift * (1f - depthNorm) * globalScale;
            slot.depth = depthNorm;
            slot.scale = config.chapter.scale * globalScale * depthScale;
            slot.alpha = config.depth.fadeBack
                ? lerp(config.depth.backAlpha, config.depth.frontAlpha, depthNorm)
                : config.depth.frontAlpha;
            slot.shade = config.depth.shadeEnabled
                ? lerp(config.depth.shadeBack, config.depth.shadeFront, depthNorm)
                : 0f;
            slot.render = true;
            slot.front = front;
        }
        int maxVisible = config.chapter.maxVisible;
        if (maxVisible > 0 && maxVisible < n) {
            Integer[] order = new Integer[n];
            for (int i = 0; i < n; i++) {
                order[i] = i;
            }
            Arrays.sort(order, Comparator.comparingDouble((Integer i) -> slots[i].depth).reversed());
            for (int i = maxVisible; i < n; i++) {
                slots[order[i]].render = false;
            }
        }
    }

    public static int[] drawOrder(Slot[] slots, int count) {
        int n = Math.max(0, Math.min(count, slots == null ? 0 : slots.length));
        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) {
            indices[i] = i;
        }
        Arrays.sort(indices, (a, b) -> {
            float depthA = slots[a] == null ? 0f : slots[a].depth;
            float depthB = slots[b] == null ? 0f : slots[b].depth;
            return Float.compare(depthA, depthB);
        });
        int[] order = new int[n];
        for (int i = 0; i < n; i++) {
            order[i] = indices[i];
        }
        return order;
    }

    private static float lerp(float from, float to, float alpha) {
        return from + (to - from) * clamp01(alpha);
    }

    private static float clamp01(float value) {
        if (value < 0f) {
            return 0f;
        }
        if (value > 1f) {
            return 1f;
        }
        return value;
    }
}
