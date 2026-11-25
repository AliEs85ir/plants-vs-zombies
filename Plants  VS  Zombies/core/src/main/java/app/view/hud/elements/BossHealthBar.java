package app.view.hud.elements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import game.audio.AudioCatalog;
import game.audio.AudioManager;
import game.component.AssetManager;
import game.entity.zombie.Zombie;

public class BossHealthBar extends ProgressZombieBar  {
    public static final float FALLBACK_WIDTH = 420f;
    public static final float FALLBACK_HEIGHT = 36f;
    private static final float DISPLAY_SCALE = 1.45f;
    private static final float INTRO_DURATION = 1.8f;
    private static final float[] STUN_MARKS = {1f / 3f, 2f / 3f};

    private enum Phase {
        WAITING,
        INTRO,
        TRACKING
    }

    private final TextureRegion notchTexture;
    private final TextureRegion skullTexture;
    private Zombie boss;
    private Phase phase = Phase.WAITING;
    private float introElapsed = 0f;
    private boolean introSfxPlayed;

    public BossHealthBar() {
        super(Direction.LEFT_TO_RIGHT);

        setBackgroundTexture(AssetManager.textureBank.region("IMAGE_UI_HUD_INGAME_ZOMBOSS_PROGRESS_METER"));
        setFillTexture(AssetManager.textureBank.region("IMAGE_UI_HUD_INGAME_PROGRESS_METER_ZOMBOSS_FILL"));
        setZombieHeadTexture("IMAGE_UI_HUD_INGAME_PROGRESS_METER_ZOMBOSS_HEAD");
        notchTexture = AssetManager.textureBank.region("IMAGE_UI_HUD_INGAME_PROGRESS_METER_ZOMBOSS_NOTCH");
        skullTexture = AssetManager.textureBank.region("IMAGE_UI_HUD_INGAME_ZOMBOSS_HEALTH_METER_SKULL_ICON");

        if (backgroundTexture != null) {
            float width = backgroundTexture.getRegionWidth() * DISPLAY_SCALE;
            float height = backgroundTexture.getRegionHeight() * DISPLAY_SCALE;
            setSize(width, height);
            barHeight = height;
            applyFillInsets(width, height);
            headSize = Math.max(40f, height * 1.35f);
        } else {
            setSize(FALLBACK_WIDTH, FALLBACK_HEIGHT);
            barHeight = FALLBACK_HEIGHT;
            applyFillInsets(FALLBACK_WIDTH, FALLBACK_HEIGHT);
        }

        setProgressImmediate(0f);
        setProgress(0f);
    }

    private void applyFillInsets(float width, float height) {
        setFillInsets(width * 0.038f, width * 0.038f, height * 0.24f, height * 0.24f);
    }

    @Override
    protected Color getFillColor() {
        return Color.WHITE;
    }

    public void setBoss(Zombie boss) {
        this.boss = boss;
        introSfxPlayed = false;
        if (boss != null) {
            phase = Phase.INTRO;
            introElapsed = 0f;
            setProgressImmediate(0f);
            setProgress(1f);
        } else {
            phase = Phase.WAITING;
            setProgressImmediate(0f);
            setProgress(0f);
        }
    }

    public Zombie getBoss() {
        return boss;
    }

    @Override
    public void act(float delta) {
        switch (phase) {
            case INTRO: {
                introElapsed += delta;
                float t = MathUtils.clamp(introElapsed / INTRO_DURATION, 0f, 1f);
                setProgressImmediate(t);
                if (!introSfxPlayed && t >= 0.5f) {
                    AudioManager.play(AudioCatalog.zombossIntroFor(
                        boss.getWorld() != null ? boss.getWorld().getCurrentChapter() : null
                    ));
                    introSfxPlayed = true;
                }
                if (t >= 1f) {
                    finishIntro();
                }
                break;
            }
            case TRACKING: {
                if (boss != null) {
                    setProgress(getHealthRatio());
                }
                super.act(delta);
                break;
            }
            case WAITING:
            default:
                super.act(delta);
                break;
        }
    }

    private void finishIntro() {
        phase = Phase.TRACKING;
        setProgressImmediate(1f);
        if (boss != null) {
            setProgress(getHealthRatio());
        }
    }

    @Override
    protected void drawEmptyTrack(Batch batch, float trackX, float trackY,
                                    float trackW, float trackH) {
        if (fillTexture == null || trackW <= 0f || trackH <= 0f) {
            return;
        }
        batch.setColor(0.10f, 0.05f, 0.05f, 0.88f);
        batch.draw(fillTexture, trackX, trackY, trackW, trackH);
    }

    @Override
    protected void drawTrackDecorations(Batch batch, float trackX, float trackY,
                                          float trackW, float trackH) {
        if (trackW <= 0f || trackH <= 0f) {
            return;
        }
        batch.setColor(Color.WHITE);
        float notchW = notchTexture != null ? Math.max(3f, notchTexture.getRegionWidth() * DISPLAY_SCALE) : 4f;
        float skullW = skullTexture != null ? skullTexture.getRegionWidth() * DISPLAY_SCALE : 16f;
        float skullH = skullTexture != null ? skullTexture.getRegionHeight() * DISPLAY_SCALE : 14f;
        for (float mark : STUN_MARKS) {
            float markX = trackX + trackW * mark;
            if (notchTexture != null) {
                batch.draw(notchTexture, markX - notchW / 2f, trackY, notchW, trackH);
            }
            if (skullTexture != null) {
                batch.draw(
                    skullTexture,
                    markX - skullW / 2f,
                    trackY + trackH - 2f,
                    skullW,
                    skullH
                );
            }
        }
    }

    protected float getHealthRatio() {
        if (boss == null) {
            return 0f;
        }
        float max = getBossMaxHealth();
        if (max <= 0f) {
            return 0f;
        }
        return MathUtils.clamp(getBossCurrentHealth() / max, 0f, 1f);
    }

    protected float getBossMaxHealth() {
        return boss.getPropertyData().getInitialHealth();
    }

    protected float getBossCurrentHealth() {
        return (float) boss.getCurrentHealth();
    }
}
