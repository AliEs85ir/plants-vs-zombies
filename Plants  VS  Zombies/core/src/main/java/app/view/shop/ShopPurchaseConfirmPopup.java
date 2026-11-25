package app.view.shop;

import app.view.shop.config.ShopConfig;
import app.view.ui.ModalOverlay;
import app.view.ui.SkinPanel;
import app.view.ui.UiTheme;
import app.view.ui.button.ActionButton;
import app.view.ui.button.ButtonTheme;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

/**
 * Modal purchase confirmation dialog. Confirm runs the provided purchase path;
 * Cancel only closes the popup. Purchase business logic stays outside this UI.
 */
public final class ShopPurchaseConfirmPopup extends ModalOverlay {
    private final ShopConfig.PurchaseConfirmConfig popup;
    private final Color overlay;

    public ShopPurchaseConfirmPopup(ShopConfig config, Runnable onConfirm, Runnable onCancel) {
        super(true);
        this.popup = config.purchaseConfirm == null
            ? new ShopConfig.PurchaseConfirmConfig().sanitize()
            : config.purchaseConfirm;
        Color configured = ShopUiFactory.parseColor(popup.overlayColor, new Color(0f, 0f, 0f, popup.dimAlpha));
        this.overlay = new Color(configured.r, configured.g, configured.b, popup.dimAlpha);

        SkinPanel panel = new SkinPanel(
            popup.panelBackgroundDrawable,
            UiTheme.BG_GUARANTEE
        );
        panel.pad(popup.panelPadY, popup.panelPadX, popup.panelPadY, popup.panelPadX);
        panel.top();

        BitmapFont titleFont = ShopUiFactory.resolveFont(popup.titleFont, popup.titleFontFallback);
        BitmapFont bodyFont = ShopUiFactory.resolveFont(popup.messageFont, popup.messageFontFallback);

        Label title = new Label(
            resolveLabel(config, popup.titleEn, popup.titleFa, "Confirm Purchase",
                "\u062A\u0627\u06CC\u06CC\u062F\u0020\u062E\u0631\u06CC\u062F"),
            new Label.LabelStyle(titleFont, Color.BLACK)
        );
        title.setFontScale(popup.titleFontSize);
        title.setAlignment(Align.center);
        title.setColor(Color.BLACK);
        panel.add(title).growX().center();
        panel.row();

        Label message = new Label(
            resolveLabel(config, popup.messageEn, popup.messageFa,
                "Do you want to purchase this item?",
                "\u0622\u06CC\u0627\u0020\u0627\u0632\u0020\u062E\u0631\u06CC\u062F\u0020\u0627\u06CC\u0646\u0020\u0622\u06CC\u062A\u0645\u0020\u0645\u0637\u0645\u0626\u0646\u0020\u0647\u0633\u062A\u06CC\u062F\u061F"),
            new Label.LabelStyle(bodyFont, Color.BLACK)
        );
        message.setFontScale(popup.messageFontSize);
        message.setAlignment(Align.center);
        message.setWrap(true);
        message.setColor(Color.BLACK);
        panel.add(message)
            .width(popup.panelWidth - popup.panelPadX * 2f)
            .padTop(12f)
            .center();
        panel.row();

        String cancelText = resolveLabel(config, popup.cancelLabelEn, popup.cancelLabelFa,
            "Cancel", "\u06A9\u0646\u0633\u0644");
        String confirmText = resolveLabel(config, popup.confirmLabelEn, popup.confirmLabelFa,
            "Confirm Purchase", "\u0627\u0646\u062C\u0627\u0645\u0020\u062E\u0631\u06CC\u062F");

        ActionButton cancelButton = button(cancelText, () -> {
            if (onCancel != null) {
                onCancel.run();
            }
            close();
        });
        ActionButton confirmButton = button(confirmText, () -> {
            close();
            if (onConfirm != null) {
                onConfirm.run();
            }
        });

        Table buttons = new Table();
        // Left: Cancel / \u06A9\u0646\u0633\u0644 ; Right: Confirm / \u0627\u0646\u062C\u0627\u0645 \u062E\u0631\u06CC\u062F
        buttons.add(cancelButton).width(popup.buttonWidth).height(popup.buttonHeight);
        buttons.add(confirmButton)
            .width(popup.buttonWidth)
            .height(popup.buttonHeight)
            .padLeft(popup.buttonGap);
        panel.add(buttons).padTop(popup.buttonTopPadding).center();

        add(panel)
            .size(popup.panelWidth, popup.panelHeight)
            .padLeft(popup.offsetX)
            .padBottom(popup.offsetY)
            .align(Align.center);
    }

    @Override
    protected Color overlayColor() {
        return overlay;
    }

    /**
     * Shows the confirm dialog on the given stage. On Confirm, runs {@code purchaseAction}
     * unchanged; on Cancel, only dismisses the dialog.
     */
    public static void show(Stage stage, ShopConfig config, Runnable purchaseAction) {
        if (stage == null) {
            if (purchaseAction != null) {
                purchaseAction.run();
            }
            return;
        }
        ShopPurchaseConfirmPopup popup = new ShopPurchaseConfirmPopup(config, purchaseAction, null);
        stage.addActor(popup);
    }

    private ActionButton button(String label, Runnable action) {
        ButtonTheme theme = new ButtonTheme().size(popup.buttonWidth, popup.buttonHeight);
        theme.labelScale = popup.buttonLabelScale;
        theme.hoverScale = popup.buttonHoverScale;
        theme.hoverDuration = popup.buttonHoverDuration;
        // Leave button label color as skin default.
        theme.textColor = null;
        ActionButton button = new ActionButton(label, UiTheme.buttonStyleOrDefault(popup.buttonStyle), theme);
        button.onClick(action);
        return button;
    }

    private static String resolveLabel(
        ShopConfig config,
        String english,
        String farsi,
        String englishFallback,
        String farsiFallback
    ) {
        boolean useFa = isFaLocale(config);
        String primary = useFa ? farsi : english;
        String fallback = useFa ? farsiFallback : englishFallback;
        if (primary == null || primary.isBlank()) {
            return fallback;
        }
        return primary;
    }

    private static boolean isFaLocale(ShopConfig config) {
        if (config == null || config.labels == null || config.labels.locale == null) {
            return false;
        }
        String locale = config.labels.locale.trim().toLowerCase();
        return locale.startsWith("fa") || "persian".equals(locale) || "farsi".equals(locale);
    }
}