package app.view.hud.config;

import com.badlogic.gdx.graphics.Color;

public final class PlantFoodBankStyle {
    public float x = 160f;
    public float y = 0f;
    public float width = 0f;
    public float height = 0f;
    public float padLeft = 18f;
    public float padBottom = 10f;
    public float padRight = 8.0f;
    public float backgroundPad = 4f;
    public Color tint = Color.WHITE.cpy();
    public float slotSize = 16f;
    public float slotSpacing = 8.5f;
    public float iconSize = 42f;
    public float iconScale = 1f;
    public float iconPadLeft = 24.5f;
    public float slotPadLeft = 8.5f;
    public float hoverAnimationScale = 1.0f;
    public float hoverAnimationOffsetX = -1.5f;
    public float hoverAnimationOffsetY = 1f;
    public float hoverAnimationWidth = 160f;
    public float hoverAnimationHeight = 160f;
    public boolean hoverAnimationLoop = true;
    public float blinkPeriodSeconds = 0.28f;
    public String backgroundImage = "IMAGE_UI_HUD_INGAME_PLANTFOOD_BANK";
    public String filledSlotImage = "IMAGE_EFFECTS_MAGNIFYING_GRASS_PLANTFOOD_HIT_MAGNIFYING_GRASS_PLANTFOOD_HIT_95X95";
    public String iconImage = "IMAGE_BACKGROUNDS_TILE_PLANTFOOD_TILE_PLANTFOOD_45X46";
    public String hoverPamPath = "768/INITIAL/EFFECTS/PLANTFOOD_PICKUP/PLANTFOOD_PICKUP.PAM";
    public String hoverClip = "idle";
    public String applyEffectId = "plantfood pickup";
}
