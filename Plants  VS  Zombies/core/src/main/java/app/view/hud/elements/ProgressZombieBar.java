package app.view.hud.elements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import game.component.AssetManager;

public class ProgressZombieBar extends Actor {

    public static final float WIDTH = 200f;
    public static final float HEIGHT = 12f;

    public enum Direction {
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT
    }

    public static class WaveFlag {
        public float position;
        public TextureRegion region;
        public boolean reached;

        public WaveFlag(float position, TextureRegion region) {
            this.position = MathUtils.clamp(position, 0f, 1f);
            this.region = region;
            this.reached = false;
        }
    }

    private Direction direction;

    private float progress = 0f;
    private float displayedProgress = 0f;

    protected TextureRegion backgroundTexture;
    protected TextureRegion fillTexture;
    protected TextureRegion zombieHeadTexture;
    private TextureRegion flagTexture;

    protected float fillPadLeft;
    protected float fillPadRight;
    protected float fillPadTop;
    protected float fillPadBottom;

    private final Array<WaveFlag> waveFlags = new Array<>();

    protected float barHeight = 20f;
    protected float headSize = 48f;
    private float flagWidth = 16f;
    private float flagHeight = 24f;

    private float smoothingSpeed = 5f;

    private BitmapFont font;
    private String label = "";
    private boolean showLabel = false;

    public ProgressZombieBar(Direction direction) {
        this.direction = direction;
        this.backgroundTexture = AssetManager.textureBank.region("IMAGE_UI_HUD_INGAME_PROGRESS_METER");
        this.fillTexture = AssetManager.textureBank.region("IMAGE_UI_HUD_INGAME_PROGRESS_METER_FILL");
        this.zombieHeadTexture = AssetManager.textureBank.region("IMAGE_UI_HUD_INGAME_PROGRESS_METER_ZOMBIEHEAD");
        this.flagTexture = AssetManager.textureBank.region("IMAGE_UI_HUD_INGAME_PROGRESS_METER_FLAG_DEFAULT");
        this.font = AssetManager.skin.getFont("FBUSV8C5EI_2");
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setZombieHeadTexture(TextureRegion texture) {
        this.zombieHeadTexture = texture;
    }

    public void setZombieHeadTexture(String assetId) {
        this.zombieHeadTexture = AssetManager.textureBank.region(assetId);
    }

    public void setBackgroundTexture(TextureRegion texture) {
        this.backgroundTexture = texture;
    }

    public void setFillTexture(TextureRegion texture) {
        this.fillTexture = texture;
    }

    public void setFillInsets(float left, float right, float top, float bottom) {
        this.fillPadLeft = Math.max(0f, left);
        this.fillPadRight = Math.max(0f, right);
        this.fillPadTop = Math.max(0f, top);
        this.fillPadBottom = Math.max(0f, bottom);
    }

    public void setFlagTexture(TextureRegion texture) {
        this.flagTexture = texture;
    }

    public void setLabel(String label) {
        this.label = label;
        this.showLabel = label != null && !label.isEmpty();
    }

    public void setSmoothingSpeed(float speed) {
        this.smoothingSpeed = speed;
    }

    public void addWaveFlag(float position) {
        waveFlags.add(new WaveFlag(position, flagTexture));
    }

    public void addWaveFlag(float position, TextureRegion texture) {
        waveFlags.add(new WaveFlag(position, texture));
    }

    public void clearWaveFlags() {
        waveFlags.clear();
    }

    public void setWaveFlags(float... positions) {
        waveFlags.clear();
        for (float pos : positions) {
            waveFlags.add(new WaveFlag(pos, flagTexture));
        }
    }

    public void setProgress(float progress) {
        this.progress = MathUtils.clamp(progress, 0f, 1f);
    }

    public float getProgress() {
        return progress;
    }

    public float getDisplayedProgress() {
        return displayedProgress;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (Math.abs(displayedProgress - progress) > 0.001f) {
            displayedProgress += (progress - displayedProgress)
                * Math.min(smoothingSpeed * delta, 1f);
        } else {
            displayedProgress = progress;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float x = getX();
        float y = getY();
        float width = getWidth();
        float height = getHeight();

        float trackX = x + fillPadLeft;
        float trackY = y + fillPadBottom;
        float trackW = Math.max(0f, width - fillPadLeft - fillPadRight);
        float trackH = Math.max(0f, height - fillPadTop - fillPadBottom);

        if (backgroundTexture != null) {
            batch.setColor(getColor());
            batch.draw(backgroundTexture, x, y, width, height);
        }

        drawEmptyTrack(batch, trackX, trackY, trackW, trackH);

        float fillWidth = trackW * displayedProgress;
        float fillX = trackX;
        if (direction == Direction.RIGHT_TO_LEFT) {
            fillX = trackX + trackW - fillWidth;
        }

        if (fillTexture != null && fillWidth > 0f && trackH > 0f) {
            batch.setColor(getFillColor());
            batch.draw(fillTexture, fillX, trackY, fillWidth, trackH);
        }

        drawTrackDecorations(batch, trackX, trackY, trackW, trackH);
        drawWaveFlags(batch, x, y, width, height);
        drawZombieHead(batch, x, y, width, height);

        if (showLabel && font != null) {
            drawLabel(batch, x, y, width, height);
        }

        batch.setColor(Color.WHITE);
    }

    private void drawLabel(Batch batch, float barX, float barY,
                           float barWidth, float barHeight) {
        float textWidth = font.getScaleX() * label.length() * 8f;
        float textX = barX + (barWidth - textWidth) / 2f;
        float textY = barY + barHeight / 2f + font.getLineHeight() / 4f;
        font.setColor(Color.WHITE);
        font.draw(batch, label, textX, textY);
    }

    protected Color getFillColor() {
        return Color.GREEN;
    }

    protected void drawEmptyTrack(Batch batch, float trackX, float trackY,
                                   float trackW, float trackH) {
    }

    protected void drawTrackDecorations(Batch batch, float trackX, float trackY,
                                         float trackW, float trackH) {
    }

    protected void drawZombieHead(Batch batch, float barX, float barY,
                                  float barWidth, float barHeight) {
        if (zombieHeadTexture == null) {
            return;
        }

        float trackX = barX + fillPadLeft;
        float trackW = Math.max(0f, barWidth - fillPadLeft - fillPadRight);
        float headX;
        float headY = barY + (barHeight - headSize) / 2f;

        if (direction == Direction.LEFT_TO_RIGHT) {
            headX = trackX + (trackW * displayedProgress) - headSize / 2f;
        } else {
            headX = trackX + trackW - (trackW * displayedProgress) - headSize / 2f;
        }

        headX = MathUtils.clamp(headX, barX - headSize / 4f,
            barX + barWidth - headSize * 0.75f);

        batch.setColor(Color.WHITE);
        batch.draw(zombieHeadTexture, headX, headY, headSize, headSize);
    }

    private void drawWaveFlags(Batch batch, float barX, float barY,
                               float barWidth, float barHeight) {
        for (WaveFlag flag : waveFlags) {
            TextureRegion tex = flag.region != null ? flag.region : flagTexture;
            if (tex == null) {
                continue;
            }

            if (displayedProgress >= flag.position) {
                flag.reached = true;
            }

            float flagX;
            if (direction == Direction.LEFT_TO_RIGHT) {
                flagX = barX + (barWidth * flag.position) - (flagWidth / 2f);
            } else {
                flagX = barX + barWidth - (barWidth * flag.position) - (flagWidth / 2f);
            }

            float flagY = barY;
            if (flag.reached) {
                flagY += 20f;
            }

            batch.setColor(Color.WHITE);
            batch.draw(tex, flagX, flagY, flagWidth, flagHeight);
        }
    }

    public void setProgressImmediate(float quantity) {
        displayedProgress = quantity;
    }

    public static ProgressZombieBar createBossHealthBar() {
        ProgressZombieBar bar = new ProgressZombieBar(Direction.RIGHT_TO_LEFT);
        bar.setProgressImmediate(1f);
        return bar;
    }

    public static ProgressZombieBar createRaidProgressBar() {
        ProgressZombieBar bar = new ProgressZombieBar(Direction.LEFT_TO_RIGHT);
        bar.setProgressImmediate(0f);
        return bar;
    }
}
