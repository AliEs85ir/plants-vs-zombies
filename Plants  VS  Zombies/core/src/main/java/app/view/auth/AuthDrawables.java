package app.view.auth;

import app.view.ui.Solid;
import app.view.ui.UiTheme;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.ray3k.tenpatch.TenPatchDrawable;

import java.util.HashMap;
import java.util.Map;

final class AuthDrawables {
    private static final Map<String, Texture> ROUNDED_TEXTURES = new HashMap<>();

    private AuthDrawables() {
    }

    static Drawable panelBackground(AuthMenuConfig.PanelConfig panel) {
        if (panel == null) {
            return panelBackground(UiTheme.BG_GUARANTEE, UiTheme.BG_GUARANTEE, 1f, 8f);
        }
        return panelBackground(panel.background, UiTheme.BG_GUARANTEE, panel.alpha, panel.cornerRadius);
    }

    static Drawable panelBackground(String name, String fallback, float alpha, float cornerRadius) {
        Drawable source = UiTheme.drawableOrFallback(name, fallback);
        if (source == null) {
            return null;
        }
        float tintAlpha = Math.max(0f, Math.min(1f, alpha));
        float radius = cornerRadius > 0f ? cornerRadius : 8f;
        if (source instanceof TenPatchDrawable tenPatch) {
            return simpleTenPatch(tenPatch, tintAlpha, radius);
        }
        TextureRegion region = regionOf(source);
        if (region != null) {
            return simpleNinePatch(region, tintAlpha, radius);
        }
        return tint(source, tintAlpha);
    }

    static Drawable inputBackground(AuthMenuConfig.FieldStyleConfig style, boolean focused) {
        Drawable skinBackground = skinInputBackground(style, focused);
        if (skinBackground != null) {
            return skinBackground;
        }
        Color fill = focused
            ? AuthMenuConfig.color(style.focusedBackgroundColor, new Color(1f, 0.96f, 0.85f, 1f))
            : AuthMenuConfig.color(style.inputBackgroundColor, new Color(0.97f, 0.93f, 0.85f, 1f));
        float radius = style.inputCornerRadius > 0f ? style.inputCornerRadius : 6f;
        Drawable rounded = roundedRect(fill, radius);
        if (rounded != null) {
            return rounded;
        }
        return Solid.drawable(fill, 8f, style.height);
    }

    private static Drawable skinInputBackground(AuthMenuConfig.FieldStyleConfig style, boolean focused) {
        String name = style == null ? "" : style.inputBackgroundDrawable;
        if (name == null || name.isBlank()) {
            return null;
        }
        Drawable source = UiTheme.drawableOrFallback(name, UiTheme.BG_MULTIPLIER);
        if (source == null) {
            return null;
        }
        Color tint = focused
            ? AuthMenuConfig.color(style.focusedBackgroundColor, new Color(1f, 0.96f, 0.85f, 1f))
            : Color.WHITE;
        if (source instanceof TenPatchDrawable tenPatch) {
            TenPatchDrawable copy = new TenPatchDrawable(tenPatch);
            copy.setMinWidth(0f);
            copy.setMinHeight(0f);
            copy.setLeftWidth(0f);
            copy.setRightWidth(0f);
            copy.setTopHeight(0f);
            copy.setBottomHeight(0f);
            if (focused) {
                copy.getColor().mul(1.10f, 1.08f, 1.02f, 1f);
            }
            return copy;
        }
        if (source instanceof NinePatchDrawable nine) {
            NinePatchDrawable copy = new NinePatchDrawable(nine);
            copy.setMinWidth(0f);
            copy.setMinHeight(0f);
            copy.setLeftWidth(0f);
            copy.setRightWidth(0f);
            copy.setTopHeight(0f);
            copy.setBottomHeight(0f);
            if (focused) {
                copy = copy.tint(tint);
                copy.setMinWidth(0f);
                copy.setMinHeight(0f);
            }
            return copy;
        }
        if (source instanceof TextureRegionDrawable region) {
            TextureRegionDrawable copy = new TextureRegionDrawable(region);
            copy.setMinWidth(0f);
            copy.setMinHeight(0f);
            copy.setLeftWidth(0f);
            copy.setRightWidth(0f);
            copy.setTopHeight(0f);
            copy.setBottomHeight(0f);
            return focused ? copy.tint(tint) : copy;
        }
        return source;
    }

    private static Drawable simpleTenPatch(TenPatchDrawable source, float alpha, float cornerRadius) {
        TextureRegion region = source.getRegion();
        TenPatchDrawable copy = new TenPatchDrawable(source);
        copy.setTiling(false);
        copy.setMinWidth(0f);
        copy.setMinHeight(0f);
        copy.setLeftWidth(0f);
        copy.setRightWidth(0f);
        copy.setTopHeight(0f);
        copy.setBottomHeight(0f);
        applySlightCorners(copy, region, cornerRadius);
        if (alpha < 0.999f) {
            copy = copy.tint(new Color(1f, 1f, 1f, alpha));
            copy.setMinWidth(0f);
            copy.setMinHeight(0f);
            copy.setLeftWidth(0f);
            copy.setRightWidth(0f);
            copy.setTopHeight(0f);
            copy.setBottomHeight(0f);
            applySlightCorners(copy, region, cornerRadius);
        }
        return copy;
    }

    private static void applySlightCorners(TenPatchDrawable drawable, TextureRegion region, float cornerRadius) {
        if (drawable == null || region == null) {
            return;
        }
        int width = Math.max(3, region.getRegionWidth());
        int height = Math.max(3, region.getRegionHeight());
        int split = cornerSplit(width, height, cornerRadius);
        drawable.setHorizontalStretchAreas(new int[] {split, width - split - 1});
        drawable.setVerticalStretchAreas(new int[] {split, height - split - 1});
    }

    private static Drawable simpleNinePatch(TextureRegion region, float alpha, float cornerRadius) {
        int split = cornerSplit(region.getRegionWidth(), region.getRegionHeight(), cornerRadius);
        NinePatch patch = new NinePatch(region, split, split, split, split);
        NinePatchDrawable drawable = new NinePatchDrawable(patch);
        drawable.setMinWidth(0f);
        drawable.setMinHeight(0f);
        drawable.setLeftWidth(0f);
        drawable.setRightWidth(0f);
        drawable.setTopHeight(0f);
        drawable.setBottomHeight(0f);
        if (alpha < 0.999f) {
            drawable = drawable.tint(new Color(1f, 1f, 1f, alpha));
            drawable.setMinWidth(0f);
            drawable.setMinHeight(0f);
            drawable.setLeftWidth(0f);
            drawable.setRightWidth(0f);
            drawable.setTopHeight(0f);
            drawable.setBottomHeight(0f);
        }
        return drawable;
    }

    private static int cornerSplit(int width, int height, float cornerRadius) {
        int max = Math.max(1, Math.min(width, height) / 2 - 1);
        int split = Math.round(cornerRadius);
        return Math.max(2, Math.min(max, split));
    }

    private static TextureRegion regionOf(Drawable source) {
        if (source instanceof TextureRegionDrawable regionDrawable) {
            return regionDrawable.getRegion();
        }
        if (source instanceof NinePatchDrawable ninePatchDrawable) {
            NinePatch patch = ninePatchDrawable.getPatch();
            if (patch != null && patch.getTexture() != null) {
                return new TextureRegion(patch.getTexture());
            }
        }
        return null;
    }

    private static Drawable tint(Drawable source, float alpha) {
        if (alpha >= 0.999f) {
            return source;
        }
        Color tint = new Color(1f, 1f, 1f, alpha);
        if (source instanceof NinePatchDrawable nine) {
            return new NinePatchDrawable(nine).tint(tint);
        }
        if (source instanceof TextureRegionDrawable region) {
            return new TextureRegionDrawable(region).tint(tint);
        }
        return source;
    }

    private static Drawable roundedRect(Color color, float radius) {
        int r = Math.max(2, Math.round(radius));
        String key = color.toString() + ":" + r;
        Texture texture = ROUNDED_TEXTURES.get(key);
        if (texture == null) {
            int size = r * 2 + 3;
            Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
            pixmap.setBlending(Pixmap.Blending.None);
            pixmap.setColor(0f, 0f, 0f, 0f);
            pixmap.fill();
            pixmap.setBlending(Pixmap.Blending.SourceOver);
            pixmap.setColor(color);
            fillRounded(pixmap, 0, 0, size, size, r);
            texture = new Texture(pixmap);
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            pixmap.dispose();
            ROUNDED_TEXTURES.put(key, texture);
        }
        NinePatch patch = new NinePatch(new TextureRegion(texture), r, r, r, r);
        return new NinePatchDrawable(patch);
    }

    private static void fillRounded(Pixmap pixmap, int x, int y, int width, int height, int radius) {
        int innerW = Math.max(1, width - radius * 2);
        int innerH = Math.max(1, height - radius * 2);
        pixmap.fillRectangle(x + radius, y, innerW, height);
        pixmap.fillRectangle(x, y + radius, width, innerH);
        pixmap.fillCircle(x + radius, y + radius, radius);
        pixmap.fillCircle(x + width - radius - 1, y + radius, radius);
        pixmap.fillCircle(x + radius, y + height - radius - 1, radius);
        pixmap.fillCircle(x + width - radius - 1, y + height - radius - 1, radius);
    }
}
