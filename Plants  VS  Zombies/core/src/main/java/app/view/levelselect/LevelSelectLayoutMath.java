package app.view.levelselect;

import java.util.Arrays;
import java.util.Comparator;

public final class LevelSelectLayoutMath {
    public static final class Slot {
        public int levelNumber;
        public int side;
        public int outwardIndex;
        public float centerX;
        public float centerY;
        public float depth;
        public float alpha;
        public float darkness;
        public float scale;
        public boolean front;
        public boolean flipX;
        public float floatAmplitude;
        public float floatSpeed;
        public float floatPhase;
        public boolean render;
    }

    private LevelSelectLayoutMath() {
    }

    public static int leftCount(int levelCount) {
        if (levelCount <= 0) {
            return 0;
        }
        return (levelCount + 1) / 2;
    }

    public static int rightCount(int levelCount) {
        if (levelCount <= 0) {
            return 0;
        }
        return levelCount / 2;
    }

    public static void compute(
        Slot[] slots,
        int levelCount,
        LevelSelectMenuConfig config,
        float areaWidth,
        float areaHeight
    ) {
        if (slots == null) {
            return;
        }
        for (Slot slot : slots) {
            if (slot != null) {
                reset(slot);
            }
        }
        if (config == null || levelCount <= 0 || areaWidth <= 0f || areaHeight <= 0f) {
            return;
        }
        int count = Math.min(levelCount, slots.length);
        if (count <= 0) {
            return;
        }
        LevelSelectMenuConfig.LayoutConfig layout = config.layout;
        LevelSelectMenuConfig.LevelFloatConfig floating = config.levelFloat;
        float axisX = layout.resolvedAxisX(areaWidth);
        float axisY = layout.resolvedAxisY(areaHeight);
        int left = leftCount(count);
        int right = rightCount(count);
        for (int outward = 0; outward < left; outward++) {
            int index = left - 1 - outward;
            Slot slot = ensure(slots, index);
            fill(
                slot,
                index + 1,
                -1,
                outward,
                axisX - layout.levelLineDistance - outward * layout.levelSpacingX + layout.leftOffsetX,
                axisY + outward * layout.levelSpacingY + layout.leftOffsetY,
                layout,
                floating
            );
        }
        for (int outward = 0; outward < right; outward++) {
            int index = left + outward;
            Slot slot = ensure(slots, index);
            fill(
                slot,
                index + 1,
                1,
                outward,
                axisX + layout.levelLineDistance + outward * layout.levelSpacingX + layout.rightOffsetX,
                axisY + outward * layout.levelSpacingY + layout.rightOffsetY,
                layout,
                floating
            );
        }
    }

    public static int[] drawOrder(Slot[] slots, int count) {
        int n = Math.max(0, Math.min(count, slots == null ? 0 : slots.length));
        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) {
            indices[i] = i;
        }
        Arrays.sort(indices, Comparator
            .comparingDouble((Integer i) -> slots[i] == null ? 0f : slots[i].depth)
            .thenComparingInt(i -> i));
        int[] order = new int[n];
        for (int i = 0; i < n; i++) {
            order[i] = indices[i];
        }
        return order;
    }

    private static void fill(
        Slot slot,
        int levelNumber,
        int side,
        int outwardIndex,
        float x,
        float y,
        LevelSelectMenuConfig.LayoutConfig layout,
        LevelSelectMenuConfig.LevelFloatConfig floating
    ) {
        boolean front = outwardIndex % 2 == 0;
        slot.levelNumber = levelNumber;
        slot.side = side;
        slot.outwardIndex = outwardIndex;
        slot.centerX = x;
        slot.centerY = y;
        slot.depth = front ? layout.frontDepth : layout.backDepth;
        slot.front = front;
        slot.alpha = layout.applyDepthFade
            ? (front ? layout.frontAlpha : layout.backAlpha)
            : layout.frontAlpha;
        slot.darkness = layout.applyDepthDarkness
            ? (front ? layout.frontDarkness : layout.backDarkness)
            : 0f;
        slot.scale = layout.nodeScale(front);
        slot.flipX = side < 0;
        slot.render = true;
        applyFloat(slot, levelNumber, floating);
    }

    private static void applyFloat(Slot slot, int levelNumber, LevelSelectMenuConfig.LevelFloatConfig floating) {
        if (floating == null || !floating.enabled) {
            slot.floatAmplitude = 0f;
            slot.floatSpeed = 0f;
            slot.floatPhase = 0f;
            return;
        }
        float mixA = hash01(levelNumber, 17);
        float mixB = hash01(levelNumber, 31);
        float mixC = hash01(levelNumber, 47);
        slot.floatAmplitude = floating.baseAmplitude + floating.amplitudeVariation * mixA;
        slot.floatSpeed = floating.baseSpeed + floating.speedVariation * mixB;
        slot.floatPhase = floating.phaseVariation * mixC * (float) (Math.PI * 2.0) + levelNumber * 0.73f;
    }

    private static float hash01(int value, int salt) {
        int hashed = value * 73856093 ^ salt * 19349663;
        hashed = hashed ^ (hashed >>> 16);
        hashed = hashed * 0x7feb352d;
        hashed = hashed ^ (hashed >>> 15);
        return (hashed & 0x7fffffff) / 2147483647f;
    }

    private static Slot ensure(Slot[] slots, int index) {
        if (slots[index] == null) {
            slots[index] = new Slot();
        }
        return slots[index];
    }

    private static void reset(Slot slot) {
        slot.levelNumber = 0;
        slot.side = 0;
        slot.outwardIndex = 0;
        slot.centerX = 0f;
        slot.centerY = 0f;
        slot.depth = 0f;
        slot.alpha = 0f;
        slot.darkness = 0f;
        slot.scale = 0f;
        slot.front = false;
        slot.flipX = false;
        slot.floatAmplitude = 0f;
        slot.floatSpeed = 0f;
        slot.floatPhase = 0f;
        slot.render = false;
    }
}
