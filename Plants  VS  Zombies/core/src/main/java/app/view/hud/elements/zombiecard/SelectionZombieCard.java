package app.view.hud.elements.zombiecard;

import app.view.game.pregame.PregameLayout;
import app.view.game.pregame.zombie.ZombieSelectionState;
import app.view.ui.UiTheme;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import game.component.AssetManager;
import game.entity.registry.ZombieRegistry;
import game.entity.zombie.data.properties.Property;

public class SelectionZombieCard extends AbstractZombieCard{
    private final ZombieSelectionState state;
    private final PregameLayout.Cards config;
    private final int sunCost;

    private HoverListener hoverListener;
    private final BitmapFont font;
    private final GlyphLayout glyphLayout = new GlyphLayout();
    private float hoverAmount = 0f;
    private boolean keyboardFocused;

    public SelectionZombieCard(String alias, float cardScale,
                              ZombieSelectionState state, PregameLayout.Cards config) {
        super(alias, cardScale);
        this.state = state;
        this.config = config;

        Property property = ZombieRegistry.getZombie(alias);
        this.sunCost = (property != null) ? property.getZombieWaveCost() : 0;
        this.font = AssetManager.skin.getFont("FBUSV8C5EI_1");
    }

    public void setHoverListener(HoverListener listener) {
        this.hoverListener = listener;
    }

    public void setKeyboardFocused(boolean focused) {
        if (this.keyboardFocused == focused) {
            return;
        }
        this.keyboardFocused = focused;
        if (focused) {
            onHoverEnter();
        } else {
            onHoverExit();
        }
    }

    public boolean isKeyboardFocused() {
        return keyboardFocused;
    }

    @Override
    protected void onCardClicked() {
        if (state != null) {
            state.toggle(alias);
        }
    }

    @Override
    protected void onHoverEnter() {
        if (hoverListener != null) {
            hoverListener.onZombieHovered(alias);
        }
    }

    @Override
    protected void onHoverExit() {
        if (hoverListener != null) {
            hoverListener.onZombieHoverExit(alias);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        float target = ((hovered || keyboardFocused) && !isDisabled()) ? 1f : 0f;
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
        drawZombieIcon(batch, x, y, w, h);
        drawCostText(batch, x, y, w, h);

        if (isDisabled()) {
            batch.setColor(UiTheme.CARD_DISABLED);
            drawBackground(batch, x, y, w, h);
        } else if (ease > 0.01f) {
            batch.setColor(UiTheme.CARD_HOVER.r, UiTheme.CARD_HOVER.g, UiTheme.CARD_HOVER.b,
                UiTheme.CARD_HOVER.a * ease);
            drawBackground(batch, x, y, w, h);
        }

        batch.setColor(Color.WHITE);
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

    public boolean isDisabled() {
        return state != null && state.isSelected(alias);
    }

    public interface HoverListener {
        void onZombieHovered(String alias);
        void onZombieHoverExit(String alias);
    }
}
