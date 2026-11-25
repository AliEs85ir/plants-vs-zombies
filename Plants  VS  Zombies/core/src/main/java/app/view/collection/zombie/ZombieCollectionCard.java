package app.view.collection.zombie;

import app.view.collection.config.CollectionConfig;
import app.view.hud.elements.zombiecard.AbstractZombieCard;
import app.view.ui.UiTheme;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import game.component.AssetManager;

public final class ZombieCollectionCard extends AbstractZombieCard {
    private final boolean seen;
    private final String lockIconId;
    private final CollectionConfig.CardHoverConfig hoverConfig;
    private final CollectionConfig.CardChromeConfig chrome;
    private final CollectionConfig.ColorsConfig colors;
    private final Runnable clickAction;
    private float hoverAmount = 0f;

    public ZombieCollectionCard(
        String alias,
        float cardScale,
        boolean seen,
        CollectionConfig config,
        Runnable clickAction
    ) {
        super(alias, cardScale);
        setName("menu-collection-card");
        this.seen = seen;
        this.lockIconId = config.lockIconId;
        this.hoverConfig = config.cardHover;
        this.chrome = config.card;
        this.colors = config.colors;
        this.clickAction = clickAction;
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
            hoverAmount + (target - hoverAmount) * Math.min(1f, delta * hoverConfig.hoverSpeed),
            0f, 1f);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float ease = Interpolation.smooth.apply(hoverAmount);
        float lift = hoverConfig.hoverLift * ease;
        float grow = (hoverConfig.hoverScale - 1f) * ease;

        float w = getWidth() * (1f + grow);
        float h = getHeight() * (1f + grow);
        float x = getX() - (w - getWidth()) / 2f;
        float y = getY() + lift - (h - getHeight()) / 2f;

        if (seen) {
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
            drawBackground(batch, x, y, w, h);
            drawZombieIcon(batch, x, y, w, h);
            if (ease > 0.01f) {
                batch.setColor(UiTheme.CARD_HOVER.r, UiTheme.CARD_HOVER.g, UiTheme.CARD_HOVER.b,
                    UiTheme.CARD_HOVER.a * ease);
                drawBackground(batch, x, y, w, h);
            }
        } else {
            float tint = chrome.lockedTint;
            batch.setColor(tint, tint, tint, parentAlpha);
            drawBackground(batch, x, y, w, h);
            drawZombieIcon(batch, x, y, w, h);
            Color overlay = colors.resolve(colors.lockedOverlay, UiTheme.CARD_DISABLED);
            batch.setColor(overlay.r, overlay.g, overlay.b, chrome.lockedOverlayAlpha * parentAlpha);
            drawBackground(batch, x, y, w, h);
            drawLockIcon(batch, x, y, w, h, parentAlpha);
        }

        batch.setColor(Color.WHITE);
    }

    private void drawLockIcon(Batch batch, float x, float y, float w, float h, float parentAlpha) {
        if (AssetManager.textureBank == null || lockIconId == null || lockIconId.isBlank()) {
            return;
        }
        TextureRegion lock = AssetManager.textureBank.region(lockIconId);
        if (lock == null) {
            return;
        }
        float lockW = Math.min(w, h) * chrome.lockSizeRatio;
        float lockH = lockW * ((float) lock.getRegionHeight() / Math.max(1, lock.getRegionWidth()));
        batch.setColor(1f, 1f, 1f, parentAlpha);
        batch.draw(lock, x + (w - lockW) / 2f, y + (h - lockH) / 2f, lockW, lockH);
    }

    public boolean isSeen() {
        return seen;
    }
}
