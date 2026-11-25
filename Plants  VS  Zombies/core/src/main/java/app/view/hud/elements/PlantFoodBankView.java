package app.view.hud.elements;

import app.view.hud.config.HudLayoutConfig;
import app.view.hud.config.PlantFoodBankStyle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import game.component.AssetManager;

import java.util.ArrayList;
import java.util.List;

public class PlantFoodBankView extends Table {
    private final PlantFoodBankStyle style;
    private final Image iconImage;
    private final HudPamActor hoverAnimation;
    private final Table slotsTable;
    private final List<Image> slotImages = new ArrayList<>();
    private Runnable clickListener;
    private int currentAmount;
    private int maxAmount;
    private boolean selecting;
    private boolean hovered;

    public PlantFoodBankView() {
        this.style = HudLayoutConfig.PLANT_FOOD_BANK;
        TextureRegion background = AssetManager.textureBank.region(style.backgroundImage);
        TextureRegion iconRegion = AssetManager.textureBank.region(style.iconImage);
        if (background != null) {
            setBackground(new TextureRegionDrawable(background));
        }
        setColor(style.tint);
        setTouchable(Touchable.enabled);

        iconImage = iconRegion == null ? new Image() : new Image(iconRegion);
        hoverAnimation = new HudPamActor(
            style.hoverPamPath,
            style.hoverClip,
            style.hoverAnimationLoop,
            style.hoverAnimationScale,
            style.hoverAnimationOffsetX,
            style.hoverAnimationOffsetY
        );
        hoverAnimation.setVisible(false);
        hoverAnimation.setTouchable(Touchable.disabled);

        slotsTable = new Table();
        add(iconImage)
            .size(style.iconSize * style.iconScale, style.iconSize * style.iconScale)
            .padTop(style.backgroundPad)
            .padBottom(style.backgroundPad)
            .padRight(style.backgroundPad)
            .padLeft(style.iconPadLeft);
        addActor(hoverAnimation);
        add(slotsTable).expandX().left().padLeft(style.slotPadLeft).padRight(style.padRight);

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (clickListener != null) {
                    clickListener.run();
                }
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                hovered = true;
                refreshHoverVisual();
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                hovered = false;
                refreshHoverVisual();
            }
        });
    }

    public void setClickListener(Runnable clickListener) {
        this.clickListener = clickListener;
    }

    public void setAmount(int amount, int max) {
        int nextAmount = Math.max(0, amount);
        int nextMax = Math.max(1, max);
        if (nextAmount == currentAmount && nextMax == maxAmount && !slotImages.isEmpty()) {
            return;
        }
        this.currentAmount = nextAmount;
        this.maxAmount = nextMax;
        rebuildSlots();
    }

    public void setSelecting(boolean selecting) {
        this.selecting = selecting;
        applyBlink();
    }

    public boolean isSelecting() {
        return selecting;
    }

    private void rebuildSlots() {
        slotsTable.clearChildren();
        slotImages.clear();
        TextureRegion filledSlot = AssetManager.textureBank.region(style.filledSlotImage);
        if (filledSlot == null) {
            return;
        }
        for (int i = 0; i < maxAmount; i++) {
            Image slotImage = new Image(filledSlot);
            if (i >= currentAmount) {
                slotImage.getColor().a = 0f;
            }
            slotsTable.add(slotImage).size(style.slotSize, style.slotSize).padRight(style.slotSpacing);
            slotImages.add(slotImage);
        }
        applyBlink();
    }

    private void applyBlink() {
        for (Image slotImage : slotImages) {
            slotImage.clearActions();
        }
        if (!selecting || currentAmount <= 0 || slotImages.isEmpty()) {
            return;
        }
        Image selected = slotImages.get(currentAmount - 1);
        selected.addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.alpha(0.25f, style.blinkPeriodSeconds),
                    Actions.alpha(1f, style.blinkPeriodSeconds)
                )
            )
        );
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (hovered && hoverAnimation.isVisible()) {
            layoutHoverAnimation();
        }
    }

    private void refreshHoverVisual() {
        boolean showHover = hovered;
        iconImage.setVisible(!showHover);
        hoverAnimation.setVisible(showHover);
        if (showHover) {
            layoutHoverAnimation();
            hoverAnimation.restart();
        }
    }

    private void layoutHoverAnimation() {
        hoverAnimation.setSize(style.hoverAnimationWidth, style.hoverAnimationHeight);
        hoverAnimation.setPosition(
            iconImage.getX() + (iconImage.getWidth() - hoverAnimation.getWidth()) / 2f + style.hoverAnimationOffsetX,
            iconImage.getY() + (iconImage.getHeight() - hoverAnimation.getHeight()) / 2f + style.hoverAnimationOffsetY
        );
    }
}
