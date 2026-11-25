package app.view.collection.plant;

import app.enums.plant.PlantName;
import app.view.collection.config.CollectionConfig;
import app.view.hud.elements.plantcard.AbstractPlantCard;
import app.view.ui.UiTheme;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import game.component.AssetManager;

public final class PlantCollectionCard extends AbstractPlantCard {
    private final boolean unlocked;
    private final int level;
    private final String categoryIconId;
    private final CollectionConfig config;
    private final Runnable clickAction;
    private final BitmapFont font;
    private final GlyphLayout glyphLayout = new GlyphLayout();
    private float hoverAmount = 0f;

    public PlantCollectionCard(
        PlantName plantName,
        float cardScale,
        boolean unlocked,
        int level,
        String categoryIconId,
        CollectionConfig config,
        Runnable clickAction
    ) {
        super(plantName, cardScale);
        setName("menu-collection-card");
        this.unlocked = unlocked;
        this.level = level;
        this.categoryIconId = categoryIconId == null ? "" : categoryIconId;
        this.config = config;
        this.clickAction = clickAction;
        BitmapFont resolved = null;
        if (AssetManager.skin != null && AssetManager.skin.has(config.card.font, BitmapFont.class)) {
            resolved = AssetManager.skin.getFont(config.card.font);
        } else if (AssetManager.skin != null) {
            resolved = AssetManager.skin.getFont("FBUSV8C5EI_1");
        }
        this.font = resolved;
    }

    @Override
    protected void onCardClicked() {
        if (clickAction != null) {
            clickAction.run();
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        float target = hovered ? 1f : 0f;
        hoverAmount = MathUtils.clamp(
            hoverAmount + (target - hoverAmount) * Math.min(1f, delta * config.cardHover.hoverSpeed),
            0f, 1f);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float ease = Interpolation.smooth.apply(hoverAmount);
        float lift = config.cardHover.hoverLift * ease;
        float grow = (config.cardHover.hoverScale - 1f) * ease;

        float w = getWidth() * (1f + grow);
        float h = getHeight() * (1f + grow);
        float x = getX() - (w - getWidth()) / 2f;
        float y = getY() + lift - (h - getHeight()) / 2f;

        if (unlocked) {
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
            drawBackground(batch, x, y, w, h);
            drawPlantIcon(batch, x, y, w, h);
            drawCategoryIcon(batch, x, y, w, h, parentAlpha);
            drawLevelText(batch, x, y, w, h);
            if (ease > 0.01f) {
                batch.setColor(UiTheme.CARD_HOVER.r, UiTheme.CARD_HOVER.g, UiTheme.CARD_HOVER.b,
                    UiTheme.CARD_HOVER.a * ease);
                drawBackground(batch, x, y, w, h);
            }
        } else {
            float tint = config.card.lockedTint;
            batch.setColor(tint, tint, tint, parentAlpha);
            drawBackground(batch, x, y, w, h);
            drawPlantIcon(batch, x, y, w, h);
            drawCategoryIcon(batch, x, y, w, h, parentAlpha);
            Color overlay = config.colors.resolve(config.colors.lockedOverlay, UiTheme.CARD_DISABLED);
            batch.setColor(overlay.r, overlay.g, overlay.b, config.card.lockedOverlayAlpha * parentAlpha);
            drawBackground(batch, x, y, w, h);
            drawLockIcon(batch, x, y, w, h, parentAlpha);
        }

        batch.setColor(Color.WHITE);
    }

    private void drawLevelText(Batch batch, float x, float y, float w, float h) {
        if (font == null) {
            return;
        }
        String text = String.format(config.card.levelFormat, level);
        float savedSX = font.getScaleX();
        float savedSY = font.getScaleY();
        boolean integerPositions = font.usesIntegerPositions();
        Color savedColor = font.getColor().cpy();
        float scale = Math.max(0.05f, config.card.levelFontScale * cardScale);
        font.setUseIntegerPositions(false);
        font.getData().setScale(scale, scale);
        glyphLayout.setText(font, text);
        float textX = x + w - (config.card.levelPadX * cardScale) - glyphLayout.width;
        float textY = y + h - (config.card.levelPadY * cardScale);
        font.setColor(1f, 1f, 1f, 1f);
        font.draw(batch, glyphLayout, textX, textY);
        font.setColor(savedColor);
        font.setUseIntegerPositions(integerPositions);
        font.getData().setScale(savedSX, savedSY);
    }

    private void drawCategoryIcon(Batch batch, float x, float y, float w, float h, float parentAlpha) {
        if (AssetManager.textureBank == null || categoryIconId.isBlank()) {
            return;
        }
        TextureRegion icon = AssetManager.textureBank.region(categoryIconId);
        if (icon == null) {
            return;
        }
        float size = Math.min(w, h) * config.card.categorySizeRatio;
        float iconX = x + config.card.categoryPadX * cardScale + config.card.categoryOffsetX;
        float iconY = y + h - size - config.card.categoryPadY * cardScale - config.card.categoryOffsetY;
        batch.setColor(1f, 1f, 1f, parentAlpha);
        batch.draw(icon, iconX, iconY, size, size);
    }

    private void drawLockIcon(Batch batch, float x, float y, float w, float h, float parentAlpha) {
        if (AssetManager.textureBank == null) {
            return;
        }
        TextureRegion lock = AssetManager.textureBank.region(config.lockIconId);
        if (lock == null) {
            return;
        }
        float lockW = Math.min(w, h) * config.card.lockSizeRatio;
        float lockH = lockW * ((float) lock.getRegionHeight() / Math.max(1, lock.getRegionWidth()));
        batch.setColor(1f, 1f, 1f, parentAlpha);
        batch.draw(lock, x + (w - lockW) / 2f, y + (h - lockH) / 2f, lockW, lockH);
    }

    public boolean isUnlocked() {
        return unlocked;
    }
}
