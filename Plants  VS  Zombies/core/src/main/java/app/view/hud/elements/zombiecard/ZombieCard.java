package app.view.hud.elements.zombiecard;

import app.controller.game.WorldListener;
import app.enums.plant.PlantName;
import app.model.App;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import game.component.AssetManager;
import game.entity.registry.ZombieRegistry;
import game.entity.zombie.data.properties.Property;
import game.world.GameWorld;
import game.world.minigame.izombie.IZombieWorld;
import game.world.minigame.izombie.ZombieCooldown;
import game.world.minigame.izombie_couchplay.IZombieCouchPlayWorld;

public class ZombieCard extends AbstractZombieCard{
    private static final float COST_PADDING_X      = 6f;
    private static final float COST_PADDING_Y      = 5f;
    private static final float BASE_COST_FONT_SCALE = 0.80f;

    private static final float COOLDOWN_ALPHA = 0.65f;
    private static final Color COOLDOWN_TINT  = new Color(0.15f, 0.15f, 0.15f, COOLDOWN_ALPHA);

    private static final Color SELECTION_GLOW = new Color(1f, 0.92f, 0.2f, 1f);

    private final int            sunCost;
    private TextureRegion selectionBorder;
    private boolean              isSelected = false;
    private float                stateTime  = 0f;

    private WorldListener listener;
    private BitmapFont font;
    private final GlyphLayout glyphLayout = new GlyphLayout();


    public ZombieCard(String alias) {
        this(alias, 1.0f);
    }

    public ZombieCard(String alias, float cardScale) {
        super(alias, cardScale);

        Property zombieProperty = ZombieRegistry.getZombie(alias);
        this.sunCost = (zombieProperty != null) ? zombieProperty.getZombieWaveCost() : 0;

        loadUIAssets();
    }

    private void loadUIAssets() {
        this.selectionBorder = AssetManager.textureBank.region("IMAGE_UI_PACKETS_SELECT");
        this.font            = AssetManager.skin.getFont("FBUSV8C5EI_1");
    }


    @Override
    protected void onCardClicked() {
        if (listener == null) return;
        if (isSelected) {
            listener.onZombieCardDeselected(alias);
        } else {
            listener.onZombieCardSelected(alias);
        }
    }


    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
        ZombieCooldown cd = cooldown();
        if (cd != null && App.getRealTimeEngine() != null) {
            cd.update(App.getRealTimeEngine().getClock().getCurrentTick());
        }
    }

    private ZombieCooldown cooldown() {
        if (App.getCurrentGameEngine() == null) return null;
        if (!(App.getCurrentGameEngine().getGameWorld() instanceof IZombieWorld)) return null;
        IZombieWorld world = (IZombieWorld) App.getCurrentGameEngine().getGameWorld();
        return world.getCoolDownZombie(alias);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float x = getX(), y = getY(), w = getWidth(), h = getHeight();

        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        drawBackground(batch, x, y, w, h);
        drawZombieIcon(batch, x, y, w, h);
        drawCooldownOverlay(batch, x, y, w, h);
        drawCostText(batch, x, y, w, h);

        if (isSelected) {
            drawSelectionHighlight(batch, x, y, w, h);
        }

        batch.setColor(Color.WHITE);
    }


    private void drawCooldownOverlay(Batch batch, float x, float y, float w, float h) {
        ZombieCooldown cooldown = cooldown();
        if (cooldown == null || cooldown.isAvailable()) return;

        float progress      = (float) cooldown.getProgress();
        float remainingRatio = 1.0f - progress;
        if (remainingRatio <= 0f) return;

        float overlayH = h * remainingRatio;
        float overlayY = y + (h * progress);

        batch.setColor(COOLDOWN_TINT);

        if (cardConfig.cachedBgRegion != null) {
            float u  = cardConfig.cachedBgRegion.getU();
            float v  = cardConfig.cachedBgRegion.getV();
            float u2 = cardConfig.cachedBgRegion.getU2();
            float v2 = cardConfig.cachedBgRegion.getV2();
            float slicedV = v + (v2 - v) * progress;
            batch.draw(cardConfig.cachedBgRegion.getTexture(),
                x, overlayY, w, overlayH,
                u, slicedV, u2, v2);
        }
    }

    private void drawCostText(Batch batch, float x, float y, float w, float h) {
        if (font == null) return;

        String costStr = String.valueOf(sunCost);
        float  savedSX = font.getScaleX();
        float  savedSY = font.getScaleY();

        font.getData().setScale(BASE_COST_FONT_SCALE * cardScale);
        glyphLayout.setText(font, costStr);

        float textX = x + w - (COST_PADDING_X * cardScale) - glyphLayout.width;
        float textY = y + (COST_PADDING_Y * cardScale) + glyphLayout.height;

        font.setColor(getCurrentSun() >= sunCost ? Color.WHITE : Color.RED);
        font.draw(batch, costStr, textX, textY);
        font.getData().setScale(savedSX, savedSY);
    }


    private void drawSelectionHighlight(Batch batch, float x, float y, float w, float h) {
        if (selectionBorder == null) return;

        float alpha       = 0.75f + 0.25f * MathUtils.sin(stateTime * 6f);
        float borderThick = 3f * cardScale;

        batch.setColor(SELECTION_GLOW.r, SELECTION_GLOW.g, SELECTION_GLOW.b, alpha);
        batch.draw(selectionBorder,
            x - borderThick, y - borderThick,
            w + borderThick * 2f, h + borderThick * 2f);
    }


    private int getCurrentSun() {
        if (App.getCurrentGameEngine() == null
            || App.getCurrentGameEngine().getGameWorld() == null) {
            return 0;
        }
        GameWorld world = App.getCurrentGameEngine().getGameWorld();
        if (world instanceof IZombieCouchPlayWorld couchWorld) {
            return couchWorld.getZombieSunAmount();
        }
        return world.getSunAmount();
    }


    public void setListener(WorldListener listener) {
        this.listener = listener;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }
}
