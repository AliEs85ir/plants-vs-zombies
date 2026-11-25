package app.view.shop;

import app.view.mainmenu.config.MainMenuEasings;
import app.view.shop.config.ShopConfig;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public final class ShopHoverAnimator {
    private ShopHoverAnimator() {
    }

    public static void animateHoverEnter(
        com.badlogic.gdx.scenes.scene2d.Actor titleButton,
        com.badlogic.gdx.scenes.scene2d.Actor priceButton,
        float baseTitleY,
        ShopConfig.HoverConfig config
    ) {
        Interpolation easing = MainMenuEasings.parse(config.easing);
        titleButton.clearActions();
        priceButton.clearActions();
        priceButton.setVisible(true);
        priceButton.getColor().a = 0f;
        titleButton.addAction(Actions.moveTo(titleButton.getX(), baseTitleY + config.titleOffsetY, config.duration, easing));
        priceButton.addAction(Actions.parallel(
            Actions.moveTo(priceButton.getX(), baseTitleY + config.priceRevealOffsetY, config.duration, easing),
            Actions.fadeIn(config.duration)
        ));
    }

    public static void animateHoverExit(
        com.badlogic.gdx.scenes.scene2d.Actor titleButton,
        com.badlogic.gdx.scenes.scene2d.Actor priceButton,
        float baseTitleY,
        float basePriceY,
        ShopConfig.HoverConfig config
    ) {
        Interpolation easing = MainMenuEasings.parse(config.easing);
        titleButton.clearActions();
        priceButton.clearActions();
        titleButton.addAction(Actions.moveTo(titleButton.getX(), baseTitleY, config.duration, easing));
        priceButton.addAction(Actions.sequence(
            Actions.parallel(
                Actions.moveTo(priceButton.getX(), basePriceY, config.duration, easing),
                Actions.fadeOut(config.duration)
            ),
            Actions.run(() -> priceButton.setVisible(false))
        ));
    }
}
