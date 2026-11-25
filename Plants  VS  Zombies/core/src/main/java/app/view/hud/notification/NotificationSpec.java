package app.view.hud.notification;

import app.enums.asset.GameIcon;
import app.view.ui.UiColor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class NotificationSpec {
    private String text = "";
    private GameIcon icon = NotificationDefaults.ICON;
    private float iconScale = NotificationDefaults.ICON_SCALE;
    private Animation<TextureRegion> iconAnimation;
    private float iconAnimationSize;
    private TextureRegion iconRegion;
    private float iconRegionSize;
    private IconPlacement iconPlacement = NotificationDefaults.ICON_PLACEMENT;
    private String font = NotificationDefaults.FONT;
    private TextSize textSize = NotificationDefaults.TEXT_SIZE;
    private int lineCount = NotificationDefaults.LINE_COUNT;
    private NotificationSize size = NotificationDefaults.SIZE;
    private Float customWidth;
    private Float customHeight;
    private NotificationAnchor anchor = NotificationDefaults.ANCHOR;
    private float padLeft = NotificationDefaults.PAD_LEFT;
    private float padBottom = NotificationDefaults.PAD_BOTTOM;
    private float padRight = NotificationDefaults.PAD_RIGHT;
    private float padTop = NotificationDefaults.PAD_TOP;
    private float innerPadding = NotificationDefaults.INNER_PADDING;
    private float iconTextSpacing = NotificationDefaults.ICON_TEXT_SPACING;
    private String pamPath;
    private String pamClip = "animation";
    private boolean pamLoop;
    private float pamWidth;
    private float pamHeight;
    private float pamDrawScale = 1f;
    private Float durationSeconds;
    private DurationPolicy durationPolicy = NotificationDefaults.DURATION_POLICY;
    private NotificationPriority priority = NotificationDefaults.PRIORITY;
    private UiColor color = NotificationDefaults.COLOR;
    private Color customColor;
    private NotificationAnimationSpec animation = NotificationAnimationSpec.defaults();

    private NotificationSpec(String text) {
        this.text = text == null ? "" : text;
    }

    public static NotificationSpec of(String text) {
        return new NotificationSpec(text);
    }

    public NotificationSpec icon(GameIcon icon) {
        if (icon != null) {
            this.icon = icon;
            this.iconAnimation = null;
            this.iconRegion = null;
        }
        return this;
    }

    /**
     * Use a looping frame animation instead of a static {@link GameIcon}.
     * {@code size} is the animation's square footprint before {@link #iconScale}
     * is applied.
     */
    public NotificationSpec iconAnimation(Animation<TextureRegion> iconAnimation, float size) {
        if (iconAnimation != null) {
            this.iconAnimation = iconAnimation;
            this.iconAnimationSize = Math.max(1f, size);
            this.iconRegion = null;
        }
        return this;
    }

    /**
     * Use a single custom image region instead of a static {@link GameIcon}
     * (e.g. real emoji art looked up by name). {@code size} is the region's
     * square footprint before {@link #iconScale} is applied.
     */
    public NotificationSpec iconRegion(TextureRegion iconRegion, float size) {
        if (iconRegion != null) {
            this.iconRegion = iconRegion;
            this.iconRegionSize = Math.max(1f, size);
            this.iconAnimation = null;
        }
        return this;
    }

    public NotificationSpec iconScale(float iconScale) {
        this.iconScale = iconScale <= 0f ? 1f : iconScale;
        return this;
    }

    public NotificationSpec iconPlacement(IconPlacement iconPlacement) {
        if (iconPlacement != null) {
            this.iconPlacement = iconPlacement;
        }
        return this;
    }

    public NotificationSpec font(String font) {
        if (font != null && !font.isBlank()) {
            this.font = font;
        }
        return this;
    }

    public NotificationSpec textSize(TextSize textSize) {
        if (textSize != null) {
            this.textSize = textSize;
        }
        return this;
    }

    public NotificationSpec lineCount(int lineCount) {
        this.lineCount = Math.max(1, Math.min(2, lineCount));
        return this;
    }

    public NotificationSpec size(NotificationSize size) {
        if (size != null) {
            this.size = size;
        }
        return this;
    }

    public NotificationSpec customSize(float width, float height) {
        this.customWidth = Math.max(80f, width);
        this.customHeight = Math.max(32f, height);
        return this;
    }

    public NotificationSpec customWidth(float width) {
        this.customWidth = Math.max(80f, width);
        return this;
    }

    public NotificationSpec anchor(NotificationAnchor anchor) {
        if (anchor != null) {
            this.anchor = anchor;
        }
        return this;
    }

    public NotificationSpec padding(float padLeft, float padBottom) {
        this.padLeft = Math.max(0f, padLeft);
        this.padBottom = Math.max(0f, padBottom);
        return this;
    }

    public NotificationSpec screenPadding(float padLeft, float padRight, float padBottom, float padTop) {
        this.padLeft = Math.max(0f, padLeft);
        this.padRight = Math.max(0f, padRight);
        this.padBottom = Math.max(0f, padBottom);
        this.padTop = Math.max(0f, padTop);
        return this;
    }

    public NotificationSpec innerPadding(float innerPadding) {
        this.innerPadding = Math.max(0f, innerPadding);
        return this;
    }

    public NotificationSpec iconTextSpacing(float iconTextSpacing) {
        this.iconTextSpacing = Math.max(0f, iconTextSpacing);
        return this;
    }

    public NotificationSpec pam(String pamPath, String clip, float width, float height) {
        if (pamPath != null && !pamPath.isBlank()) {
            this.pamPath = pamPath;
            this.pamClip = clip == null || clip.isBlank() ? "animation" : clip;
            this.pamWidth = Math.max(1f, width);
            this.pamHeight = Math.max(1f, height);
            this.pamLoop = false;
        }
        return this;
    }

    public NotificationSpec pamLoop(boolean pamLoop) {
        this.pamLoop = pamLoop;
        return this;
    }

    public NotificationSpec pamDrawScale(float pamDrawScale) {
        this.pamDrawScale = pamDrawScale <= 0f ? 1f : pamDrawScale;
        return this;
    }

    public NotificationSpec duration(float durationSeconds) {
        this.durationSeconds = Math.max(0.2f, durationSeconds);
        return this;
    }

    public NotificationSpec durationPolicy(DurationPolicy durationPolicy) {
        if (durationPolicy != null) {
            this.durationPolicy = durationPolicy;
        }
        return this;
    }

    public NotificationSpec priority(NotificationPriority priority) {
        if (priority != null) {
            this.priority = priority;
        }
        return this;
    }

    public NotificationSpec color(UiColor color) {
        if (color != null) {
            this.color = color;
            this.customColor = null;
        }
        return this;
    }

    public NotificationSpec color(Color color) {
        if (color != null) {
            this.customColor = color.cpy();
        }
        return this;
    }

    public NotificationSpec animation(NotificationAnimationSpec animation) {
        if (animation != null) {
            this.animation = animation;
        }
        return this;
    }

    public String text() {
        return text;
    }

    public GameIcon icon() {
        return icon;
    }

    public Animation<TextureRegion> iconAnimation() {
        return iconAnimation;
    }

    public float iconAnimationSize() {
        return iconAnimationSize;
    }

    public TextureRegion iconRegion() {
        return iconRegion;
    }

    public float iconRegionSize() {
        return iconRegionSize;
    }

    public float iconScale() {
        return iconScale;
    }

    public IconPlacement iconPlacement() {
        return iconPlacement;
    }

    public String font() {
        return font;
    }

    public TextSize textSize() {
        return textSize;
    }

    public int lineCount() {
        return lineCount;
    }

    public NotificationSize size() {
        return size;
    }

    public float resolvedWidth() {
        if (customWidth != null) {
            return customWidth;
        }
        return size.width();
    }

    public Float customHeight() {
        return customHeight;
    }

    public NotificationAnchor anchor() {
        return anchor;
    }

    public float padLeft() {
        return padLeft;
    }

    public float padBottom() {
        return padBottom;
    }

    public float padRight() {
        return padRight;
    }

    public float padTop() {
        return padTop;
    }

    public float innerPadding() {
        return innerPadding;
    }

    public float iconTextSpacing() {
        return iconTextSpacing;
    }

    public String pamPath() {
        return pamPath;
    }

    public String pamClip() {
        return pamClip;
    }

    public boolean pamLoop() {
        return pamLoop;
    }

    public float pamWidth() {
        return pamWidth;
    }

    public float pamHeight() {
        return pamHeight;
    }

    public float pamDrawScale() {
        return pamDrawScale;
    }

    public boolean hasPam() {
        return pamPath != null && !pamPath.isBlank() && pamWidth > 0f && pamHeight > 0f;
    }

    public float resolvedDuration() {
        if (durationSeconds != null) {
            return durationSeconds;
        }
        return durationPolicy.durationSeconds(text);
    }

    public NotificationPriority priority() {
        return priority;
    }

    public Color resolvedColor() {
        if (customColor != null) {
            return customColor.cpy();
        }
        return color.gdx();
    }

    public UiColor color() {
        return color;
    }

    public NotificationAnimationSpec animation() {
        return animation;
    }
}
