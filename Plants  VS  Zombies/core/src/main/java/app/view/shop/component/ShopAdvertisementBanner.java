package app.view.shop.component;

import app.controller.shop.ShopController;
import app.view.mainmenu.config.MainMenuEasings;
import app.view.shop.ShopTextureCache;
import app.view.shop.config.ShopCatalogConfig;
import app.view.shop.config.ShopConfig;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

public final class ShopAdvertisementBanner extends WidgetGroup {
    private Image banner;
    private Texture adTexture;
    private ShopConfig config;
    private float fittedWidth;

    public ShopAdvertisementBanner(
        ShopConfig config,
        ShopController controller,
        ShopTextureCache textures
    ) {
        rebuild(config, controller, textures);
    }

    public void rebuild(ShopConfig config, ShopController controller, ShopTextureCache textures) {
        clearChildren();
        clearActions();
        setScale(1f);
        getColor().a = 1f;
        this.config = config;
        banner = null;
        adTexture = null;
        ShopCatalogConfig.AdvertisementConfig ad = controller.selectedAdvertisement();
        if (ad == null) {
            setVisible(false);
            setSize(0f, 0f);
            return;
        }
        adTexture = textures.get(ad.imagePath);
        if (adTexture == null) {
            setVisible(false);
            setSize(0f, 0f);
            return;
        }
        setVisible(true);
        setTransform(true);
        setName("shop-ad");
        ShopConfig.AdvertisementUiConfig ui = config.advertisement;
        banner = new Image(new TextureRegionDrawable(new TextureRegion(adTexture)));
        banner.setFillParent(true);
        banner.setTouchable(Touchable.enabled);
        banner.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (ad.url != null && !ad.url.isBlank()) {
                    Gdx.net.openURI(ad.url);
                }
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                if (pointer != -1) {
                    return;
                }
                animateHover(true);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                if (pointer != -1) {
                    return;
                }
                animateHover(false);
            }
        });
        addActor(banner);
        layoutBanner(fittedWidth > 0f ? fittedWidth : ui.width);
    }

    public void fitWidth(float width) {
        fittedWidth = Math.max(1f, width);
        if (isVisible()) {
            layoutBanner(fittedWidth);
        }
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        setOrigin(Align.center);
    }

    private void layoutBanner(float width) {
        ShopConfig.AdvertisementUiConfig ui = config.advertisement;
        float height = ui.height;
        if (ui.keepAspect && adTexture != null && adTexture.getWidth() > 0) {
            height = width * (adTexture.getHeight() / (float) adTexture.getWidth());
        }
        setSize(width, height);
        setOrigin(Align.center);
        if (banner != null) {
            banner.setSize(width, height);
        }
    }

    private void animateHover(boolean hovering) {
        ShopConfig.AdvertisementUiConfig ui = config.advertisement;
        Interpolation easing = MainMenuEasings.parse(config.interaction.easing);
        clearActions();
        addAction(Actions.parallel(
            Actions.scaleTo(
                hovering ? ui.hoverScale : 1f,
                hovering ? ui.hoverScale : 1f,
                ui.hoverDuration,
                easing
            ),
            Actions.alpha(hovering ? ui.hoverAlpha : 1f, ui.hoverDuration, easing)
        ));
    }
}
