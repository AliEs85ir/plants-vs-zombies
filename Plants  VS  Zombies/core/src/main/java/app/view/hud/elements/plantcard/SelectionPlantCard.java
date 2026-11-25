package app.view.hud.elements.plantcard;

import app.enums.plant.PlantName;
import app.view.game.pregame.PlantSelectionState;
import app.view.game.pregame.PregameLayout;
import app.view.ui.UiTheme;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import game.component.AssetManager;
import game.service.plant.PlantUpgradeService;

public class SelectionPlantCard extends AbstractPlantCard {

    private static final String LOCK_ICON_ID = "IMAGE_UI_PACKETS_LOCK_SMALL";

    private final PlantSelectionState state;
    private final PregameLayout.Cards config;
    private int sunCost;

    private HoverListener hoverListener;
    private final BitmapFont font;
    private final GlyphLayout glyphLayout = new GlyphLayout();
    private float hoverAmount = 0f;

    public SelectionPlantCard(PlantName plantName, float cardScale,
                              PlantSelectionState state, PregameLayout.Cards config) {
        super(plantName, cardScale);
        this.state = state;
        this.config = config;

        this.sunCost = resolveSunCost();
        this.font = AssetManager.skin.getFont("FBUSV8C5EI_1");
    }

    public void setHoverListener(HoverListener listener) {
        this.hoverListener = listener;
    }

    @Override
    protected void onCardClicked() {
        if (state != null) {
            state.toggle(plantName);
        }
    }

    @Override
    protected void onHoverEnter() {
        if (hoverListener != null) {
            hoverListener.onPlantHovered(plantName);
        }
    }

    @Override
    protected void onHoverExit() {
        if (hoverListener != null) {
            hoverListener.onPlantHoverExit(plantName);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        float target = (hovered && !isDisabled()) ? 1f : 0f;
        hoverAmount = MathUtils.clamp(
            hoverAmount + (target - hoverAmount) * Math.min(1f, delta * config.hoverSpeed),
            0f, 1f);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float ease  = Interpolation.smooth.apply(hoverAmount);
        float lift  = config.hoverLift * ease;
        float grow  = (config.hoverScale - 1f) * ease;

        float w = getWidth()  * (1f + grow);
        float h = getHeight() * (1f + grow);
        float x = getX() - (w - getWidth()) / 2f;
        float y = getY() + lift - (h - getHeight()) / 2f;

        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        drawBackground(batch, x, y, w, h);
        drawPlantIcon(batch, x, y, w, h);
        if (!isLocked()) {
            drawCostText(batch, x, y, w, h);
        }

        if (isLocked()) {
            batch.setColor(0.35f, 0.35f, 0.35f, 0.55f * parentAlpha);
            drawBackground(batch, x, y, w, h);
            drawLockIcon(batch, x, y, w, h, parentAlpha);
        } else if (isDisabled()) {
            batch.setColor(UiTheme.CARD_DISABLED);
            drawBackground(batch, x, y, w, h);
        } else if (ease > 0.01f) {
            batch.setColor(UiTheme.CARD_HOVER.r, UiTheme.CARD_HOVER.g, UiTheme.CARD_HOVER.b,
                           UiTheme.CARD_HOVER.a * ease);
            drawBackground(batch, x, y, w, h);
        }

        batch.setColor(Color.WHITE);
    }

    private void drawLockIcon(Batch batch, float x, float y, float w, float h, float parentAlpha) {
        if (AssetManager.textureBank == null) {
            return;
        }
        TextureRegion lock = AssetManager.textureBank.region(LOCK_ICON_ID);
        if (lock == null) {
            return;
        }
        float lockW = Math.min(w, h) * 0.42f;
        float lockH = lockW * ((float) lock.getRegionHeight() / Math.max(1, lock.getRegionWidth()));
        float lockX = x + (w - lockW) / 2f;
        float lockY = y + (h - lockH) / 2f;
        batch.setColor(1f, 1f, 1f, parentAlpha);
        batch.draw(lock, lockX, lockY, lockW, lockH);
    }

    private void drawCostText(Batch batch, float x, float y, float w, float h) {
        if (font == null) return;

        String costStr = String.valueOf(sunCost);
        float savedSX = font.getScaleX();
        float savedSY = font.getScaleY();

        font.getData().setScale(config.costFontScale * cardScale);
        glyphLayout.setText(font, costStr);

        float textX = x + w - (config.costPaddingX * cardScale) - glyphLayout.width;
        float textY = y + (config.costPaddingY * cardScale) + glyphLayout.height;

        font.setColor(Color.WHITE);
        font.draw(batch, costStr, textX, textY);
        font.getData().setScale(savedSX, savedSY);
    }

    public boolean isLocked() {
        return state != null && !state.isPlantUnlocked(plantName);
    }

    public boolean isDisabled() {
        return isLocked() || (state != null && state.isSelected(plantName));
    }

    public void refreshUpgradeAppearance() {
        this.sunCost = resolveSunCost();
    }

    private int resolveSunCost() {
        return PlantUpgradeService.getEffectiveStats(plantName).sunCost();
    }

    public interface HoverListener {
        void onPlantHovered(PlantName plantName);
        void onPlantHoverExit(PlantName plantName);
    }
}
