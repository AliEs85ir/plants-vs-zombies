package app.view.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import game.component.AssetManager;

public final class UiTheme {

    public static final String BTN_DEFAULT     = "default";
    public static final String BTN_BROWN       = "brown";
    public static final String BTN_PURPLE      = "purple";
    public static final String BTN_GREEN       = "green";
    public static final String BTN_GREEN_SMALL = "green_small";
    public static final String IMG_BTN_SHOVEL  = "ingame_shovel";
    public static final String IMG_BTN_PAUSE   = "ingame_pause";

    public static final String LBL_DEFAULT        = "default";
    public static final String LBL_BIG            = "big";
    public static final String LBL_BIG_OUTLINE    = "big_outline";
    public static final String LBL_MEDIUM         = "medium";
    public static final String LBL_MEDIUM_OUTLINE = "medium_outline";
    public static final String LBL_SECONDARY      = "secondary";
    public static final String LBL_RIBBON         = "promo_ribbon";
    public static final String LBL_MULTIPLIER     = "bundle_reward_multiplier";

    public static final String BG_DIALOG   = "image_ui_dialog_asset_inner_bkgd_10";
    public static final String BG_PANEL    = "image_ui_quests_panel_edge_to_edge_ten";
    public static final String BG_TAB      = "image_ui_mainmenu_mm_settings_tab_10";
    public static final String BG_REWARD   = "image_ui_if_bundle_reward1_bg_10";
    public static final String BG_COST     = "image_ui_powerups_powerup_cost_10";
    public static final String BG_GUARANTEE = "image_ui_generic_guaranteed_bg_10";
    public static final String BG_MULTIPLIER = "image_ui_if_bundle_reward_multiplier_bg_10";
    public static final String BG_RIBBON   = "image_ui_cards_store_promo_ribbon_10";
    public static final String BG_BUNDLE     = "image_ui_if_bundle_reward1_bg_10";

    public static final Color ACCENT        = new Color(0.98f, 0.84f, 0.32f, 1f);
    public static final Color TEXT_PRIMARY  = new Color(1f, 1f, 1f, 1f);
    public static final Color TEXT_MUTED    = new Color(0.78f, 0.80f, 0.76f, 1f);
    public static final Color TEXT_SECONDARY = new Color(0.93f, 0.95f, 0.90f, 1f);
    public static final Color TEXT_STAT      = new Color(0.99f, 0.89f, 0.56f, 1f);
    public static final Color TEXT_ERROR    = new Color(1f, 0.42f, 0.36f, 1f);
    public static final Color TEXT_SUCCESS  = new Color(0.55f, 0.95f, 0.45f, 1f);
    public static final Color SCRIM         = new Color(0f, 0f, 0f, 0.66f);
    public static final Color SLOT_EMPTY    = new Color(1f, 1f, 1f, 0.16f);
    public static final Color CARD_DISABLED = new Color(0.08f, 0.08f, 0.08f, 0.68f);
    public static final Color CARD_HOVER    = new Color(1f, 1f, 1f, 0.22f);
    public static final Color PROGRESS_TRACK = new Color(0f, 0f, 0f, 0.45f);
    public static final Color PROGRESS_FILL  = new Color(0.90f, 0.72f, 0.24f, 1f);
    public static final Color PROGRESS_READY = new Color(0.42f, 0.86f, 0.36f, 1f);
    public static final Color SCROLL_KNOB    = new Color(1f, 1f, 1f, 0.55f);
    public static final Color SCROLL_TRACK   = new Color(0f, 0f, 0f, 0.30f);

    private UiTheme() {}

    public static Skin skin() {
        return AssetManager.skin;
    }

    public static Drawable drawable(String name) {
        try {
            return AssetManager.skin.getDrawable(name);
        } catch (Exception e) {
            Gdx.app.error("UiTheme", "Missing drawable in skin: " + name);
            return null;
        }
    }

    public static Drawable drawableOrFallback(String name, String fallback) {
        Drawable d = drawable(name);
        return (d != null) ? d : drawable(fallback);
    }

    public static Drawable tintedDrawable(String name, String fallback, float alpha) {
        Drawable source = drawableOrFallback(name, fallback);
        if (source == null) {
            return null;
        }
        float tintAlpha = Math.max(0f, Math.min(1f, alpha));
        if (tintAlpha >= 0.999f) {
            return source;
        }
        Color tint = new Color(1f, 1f, 1f, tintAlpha);
        if (source instanceof com.ray3k.tenpatch.TenPatchDrawable tenPatch) {
            com.ray3k.tenpatch.TenPatchDrawable copy = new com.ray3k.tenpatch.TenPatchDrawable(tenPatch);
            copy.setMinWidth(0f);
            copy.setMinHeight(0f);
            copy = copy.tint(tint);
            copy.setMinWidth(0f);
            copy.setMinHeight(0f);
            return copy;
        }
        if (source instanceof com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable region) {
            com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable copy =
                new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(region);
            copy.setMinWidth(0f);
            copy.setMinHeight(0f);
            return copy.tint(tint);
        }
        if (source instanceof com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable nine) {
            com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable copy =
                new com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable(nine);
            copy.setMinWidth(0f);
            copy.setMinHeight(0f);
            return copy.tint(tint);
        }
        return source;
    }

    public static boolean hasLabelStyle(String name) {
        return AssetManager.skin.has(name, com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle.class);
    }

    public static String labelStyleOrDefault(String name) {
        return hasLabelStyle(name) ? name : LBL_DEFAULT;
    }

    public static boolean hasButtonStyle(String name) {
        return AssetManager.skin.has(name,
            com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle.class);
    }

    public static String buttonStyleOrDefault(String name) {
        return hasButtonStyle(name) ? name : BTN_DEFAULT;
    }

    public static boolean hasImageButtonStyle(String name) {
        return AssetManager.skin.has(name,
            com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle.class);
    }

    public static Color color(UiColor color) {
        return color == null ? TEXT_PRIMARY.cpy() : color.gdx();
    }

    public static Color color(UiColor color, float alpha) {
        return color == null ? TEXT_PRIMARY.cpy() : color.gdx(alpha);
    }
}
