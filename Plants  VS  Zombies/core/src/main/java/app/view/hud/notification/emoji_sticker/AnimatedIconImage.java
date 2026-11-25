package app.view.hud.notification.emoji_sticker;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;


public class AnimatedIconImage extends Image {
    private final Animation<TextureRegion> animation;
    private float stateTime;

    public AnimatedIconImage(Animation<TextureRegion> animation) {
        super(animation.getKeyFrame(0f));
        this.animation = animation;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
        setDrawable(new TextureRegionDrawable(animation.getKeyFrame(stateTime, true)));
    }
}
