package app.view.dialogue;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import game.component.AssetManager;
import game.dialogue.DialogueConfig;
import game.dialogue.DialogueLine;
import game.dialogue.Speaker;

public class DialogueOverlay extends Table {
    private static final float HUD_WIDTH = 1280f;
    private static final float HUD_HEIGHT = 720f;
    private static final float BUBBLE_PAD = 28f;
    private static final float HUD_MARGIN = 16f;
    private static final String FONT_NAME = "FBUSV8C5EI_1_outline";
    private static final String FONT_FALLBACK = "FBUSV8C5EI_1";
    private static final String BUBBLE_ASSET = "IMAGE_STORE_SPEECHBUBBLE2";

    private final Image bubbleImage;
    private final Label textLabel;
    private final Vector2 hudScratch = new Vector2();
    private Runnable advanceListener;

    public DialogueOverlay() {
        setFillParent(true);
        setTouchable(Touchable.disabled);
        setVisible(false);

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (advanceListener != null) {
                    advanceListener.run();
                }
                return true;
            }
        });

        TextureRegion bubbleRegion = AssetManager.textureBank.region(BUBBLE_ASSET);
        bubbleImage = bubbleRegion != null ? new Image(new TextureRegionDrawable(bubbleRegion)) : new Image();
        if (bubbleRegion != null) {
            bubbleImage.setSize(bubbleRegion.getRegionWidth(), bubbleRegion.getRegionHeight());
        } else {
            bubbleImage.setSize(359f, 234f);
        }
        bubbleImage.setOrigin(Align.center);
        bubbleImage.setVisible(false);
        addActor(bubbleImage);

        textLabel = new Label("", createLabelStyle());
        textLabel.setAlignment(Align.center);
        textLabel.setWrap(true);
        textLabel.setVisible(false);
        addActor(textLabel);
    }

    public void setAdvanceListener(Runnable advanceListener) {
        this.advanceListener = advanceListener;
    }

    public void beginSession() {
        setVisible(true);
        setTouchable(Touchable.enabled);
        hideLine();
        toFront();
    }

    public void endSession() {
        hideLine();
        bubbleImage.setScaleX(1f);
        setTouchable(Touchable.disabled);
        setVisible(false);
    }

    public void showLine(DialogueLine line) {
        showLine(line, Float.NaN, Float.NaN, null);
    }

    public void showLine(DialogueLine line, float worldX, float worldY, Viewport worldViewport) {
        if (line == null) {
            hideLine();
            return;
        }
        if (worldViewport != null && getStage() != null && !Float.isNaN(worldX) && !Float.isNaN(worldY)) {
            layoutBubbleAtWorld(worldX, worldY, worldViewport);
        } else {
            bubbleImage.setScaleX(1f);
            layoutBubble(line.getSpeaker());
        }
        textLabel.setText(line.getText());
        bubbleImage.setVisible(true);
        textLabel.setVisible(true);
    }

    public void hideLine() {
        bubbleImage.setVisible(false);
        textLabel.setVisible(false);
        textLabel.setText("");
    }

    private void layoutBubble(Speaker speaker) {
        float bubbleWidth = bubbleImage.getWidth();
        float bubbleHeight = bubbleImage.getHeight();
        float y = (HUD_HEIGHT - bubbleHeight) * 0.5f + DialogueConfig.BUBBLE_Y;
        float x = speaker != null && speaker.getSide() == Speaker.Side.RIGHT
            ? HUD_WIDTH - DialogueConfig.PENNY_BUBBLE_RIGHT_MARGIN - bubbleWidth
            : DialogueConfig.DAVE_BUBBLE_X;
        applyBubbleBounds(x, y);
    }

    private void layoutBubbleAtWorld(float worldX, float worldY, Viewport worldViewport) {
        hudScratch.set(worldX, worldY + DialogueConfig.BOSS_BUBBLE_WORLD_OFFSET_Y);
        worldViewport.project(hudScratch);
        getStage().getViewport().unproject(hudScratch);

        float bubbleWidth = bubbleImage.getWidth();
        float bubbleHeight = bubbleImage.getHeight();
        boolean tailPointsRight = hudScratch.x > HUD_WIDTH * 0.5f;
        bubbleImage.setScaleX(tailPointsRight ? -1f : 1f);

        float tailX = (tailPointsRight
            ? 1f - DialogueConfig.BOSS_BUBBLE_TAIL_X_FRACTION
            : DialogueConfig.BOSS_BUBBLE_TAIL_X_FRACTION) * bubbleWidth;
        float tailY = DialogueConfig.BOSS_BUBBLE_TAIL_Y_FRACTION * bubbleHeight;
        float x = MathUtils.clamp(hudScratch.x - tailX, HUD_MARGIN, HUD_WIDTH - bubbleWidth - HUD_MARGIN);
        float y = MathUtils.clamp(hudScratch.y - tailY, HUD_MARGIN, HUD_HEIGHT - bubbleHeight - HUD_MARGIN);
        applyBubbleBounds(x, y);
    }

    private void applyBubbleBounds(float x, float y) {
        float bubbleWidth = bubbleImage.getWidth();
        float bubbleHeight = bubbleImage.getHeight();
        bubbleImage.setPosition(x, y);
        textLabel.setBounds(
            x + BUBBLE_PAD + DialogueConfig.DIALOGUE_TEXT_OFFSET_X,
            y + BUBBLE_PAD + DialogueConfig.DIALOGUE_TEXT_OFFSET_Y,
            Math.max(8f, bubbleWidth - BUBBLE_PAD * 2f),
            Math.max(8f, bubbleHeight - BUBBLE_PAD * 2f)
        );
    }

    private Label.LabelStyle createLabelStyle() {
        BitmapFont font = resolveFont();
        return new Label.LabelStyle(font, Color.WHITE);
    }

    private BitmapFont resolveFont() {
        if (AssetManager.skin.has(FONT_NAME, BitmapFont.class)) {
            return AssetManager.skin.getFont(FONT_NAME);
        }
        return AssetManager.skin.getFont(FONT_FALLBACK);
    }
}
