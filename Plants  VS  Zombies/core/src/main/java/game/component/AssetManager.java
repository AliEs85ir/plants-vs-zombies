package game.component;

import app.enums.asset.GameIcon;
import app.enums.cursor.CursorType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.textures.TextureBank;
import pvz.skin.PvzSkin;

import java.util.EnumMap;

public class AssetManager {
    public static TextureBank textureBank;
    public static PamPlayer player;
    public static Skin skin;
    public static final String DEFAULT_FONT = "BRIANNETOD";
    public static final String HOUSE_OF_TERROR_FONT = "HOUSE_OF_TERROR";
    private static final EnumMap<CursorType, Texture> CURSOR_TEXTURES = new EnumMap<>(CursorType.class);
    private static boolean cursorsLoaded;
    private static volatile TextureBank pendingTextureBank;
    private static volatile boolean coreReady;

    public static void createTextureBankOffThread() {
        if (textureBank != null || pendingTextureBank != null) {
            return;
        }
        pendingTextureBank = new TextureBank("768", Gdx.files.internal("assets"));
    }

    public static void attachTextureBankOnGlThread() {
        if (textureBank != null) {
            return;
        }
        textureBank = pendingTextureBank;
        pendingTextureBank = null;
    }

    public static void initializePlayerAndSkin() {
        if (player != null && skin != null) {
            coreReady = true;
            return;
        }
        if (textureBank == null) {
            throw new IllegalStateException("Texture bank is not ready");
        }
        player = new PamPlayer(textureBank, Gdx.files.internal("assets"));
        skin = PvzSkin.get();
        coreReady = true;
    }

    public static boolean isCoreReady() {
        return coreReady && textureBank != null && player != null && skin != null;
    }

    public static void pump() {
        if (textureBank != null) {
            textureBank.update();
        }
    }

    public static void ensureCursorsLoaded() {
        loadCursors();
    }

    public static boolean hasCursorTextures() {
        return CURSOR_TEXTURES.get(CursorType.DEFAULT) != null;
    }

    public static TextureRegion region(GameIcon icon) {
        if (icon == null || textureBank == null) {
            return null;
        }
        return textureBank.region(icon.assetId());
    }

    public static ResolvedIcon icon(GameIcon icon) {
        return icon(icon, 1f);
    }

    public static ResolvedIcon icon(GameIcon icon, float scale) {
        float safeScale = scale <= 0f ? 1f : scale;
        TextureRegion region = region(icon);
        if (region == null) {
            return new ResolvedIcon(null, 0f, 0f, safeScale);
        }
        return new ResolvedIcon(
            region,
            region.getRegionWidth() * safeScale,
            region.getRegionHeight() * safeScale,
            safeScale
        );
    }

    public static Texture cursor(CursorType type) {
        loadCursors();
        CursorType resolved = type != null ? type : CursorType.DEFAULT;
        Texture texture = CURSOR_TEXTURES.get(resolved);
        if (texture != null) {
            return texture;
        }
        return CURSOR_TEXTURES.get(CursorType.DEFAULT);
    }

    private static void loadCursors() {
        if (cursorsLoaded) {
            return;
        }
        cursorsLoaded = true;
        for (CursorType type : CursorType.values()) {
            FileHandle file = resolveCursorFile(type);
            if (file == null || !file.exists()) {
                continue;
            }
            Texture texture = new Texture(file);
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            CURSOR_TEXTURES.put(type, texture);
        }
    }

    private static FileHandle resolveCursorFile(CursorType type) {
        FileHandle direct = Gdx.files.internal(type.assetPath());
        if (direct.exists()) {
            return direct;
        }
        FileHandle nested = Gdx.files.internal("assets/" + type.assetPath());
        if (nested.exists()) {
            return nested;
        }
        return direct;
    }

    public static BitmapFont font(String name) {
        if (skin == null) {
            throw new IllegalStateException("Skin is not ready");
        }
        if (name != null && !name.isBlank() && skin.has(name, BitmapFont.class)) {
            return skin.getFont(name);
        }
        if (skin.has(DEFAULT_FONT, BitmapFont.class)) {
            return skin.getFont(DEFAULT_FONT);
        }
        return skin.get(Label.LabelStyle.class).font;
    }
}
