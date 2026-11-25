package graphics.drop;

import app.enums.drop.DropType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import graphics.general.animation.AnimationLibrary;
import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.pam.PamPlayer;

import java.util.HashMap;
import java.util.Map;

public class DropRenderer {
    private final PamPlayer pamPlayer;
    private final AnimationLibrary animationLibrary;
    private final Map<String, Texture> imageCache = new HashMap<>();

    public DropRenderer(PamPlayer pamPlayer, AnimationLibrary animationLibrary) {
        this.pamPlayer = pamPlayer;
        this.animationLibrary = animationLibrary;
    }

    public void render(SpriteBatch batch, DropRenderInstance instance) {
        DropType type = instance.getDrop().getType();
        float x = (float) instance.getDrop().getPosition().getX();
        float y = (float) instance.getDrop().getPosition().getY();
        if (type.usesPam()) {
            drawPam(batch, instance, type, x, y);
            return;
        }
        if (type.usesImage()) {
            drawImage(batch, type, x, y);
        }
    }

    private void drawPam(SpriteBatch batch, DropRenderInstance instance, DropType type, float x, float y) {
        ClipRef clip = animationLibrary.resolveClip(type.getPamPath(), type.getClipName());
        if (clip == null) {
            return;
        }
        float scale = type.getScale();
        Matrix4 original = null;
        if (scale != 1f) {
            original = batch.getTransformMatrix().cpy();
            batch.end();
            batch.getTransformMatrix()
                .translate(x, y, 0)
                .scale(scale, scale, 1)
                .translate(-x, -y, 0);
            batch.begin();
        }
        pamPlayer.draw(batch, clip, instance.getStateTime(), x, y, true);
        if (original != null) {
            batch.end();
            batch.setTransformMatrix(original);
            batch.begin();
        }
    }

    private void drawImage(SpriteBatch batch, DropType type, float x, float y) {
        Texture texture = textureOf(type.getImagePath());
        if (texture == null) {
            return;
        }
        TextureRegion region = new TextureRegion(texture);
        float width = region.getRegionWidth() * type.getScale();
        float height = region.getRegionHeight() * type.getScale();
        batch.draw(region, x - width / 2f, y - height / 2f, width, height);
    }

    private Texture textureOf(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        Texture cached = imageCache.get(path);
        if (cached != null) {
            return cached;
        }
        FileHandle file = Gdx.files.internal(path);
        if (!file.exists()) {
            file = Gdx.files.internal("assets/" + path);
        }
        if (!file.exists()) {
            return null;
        }
        Texture texture = new Texture(file);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        imageCache.put(path, texture);
        return texture;
    }
}
