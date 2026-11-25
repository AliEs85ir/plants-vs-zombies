package app.view.loading.icons.popup;

import app.view.loading.icons.config.StartIconsConfig;
import app.view.ui.SkinPanel;
import app.view.ui.UiTheme;
import app.view.ui.button.ActionButton;
import app.view.ui.text.TextAlign;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public final class StartPopupFrame extends Table {
    private final Table bodyHost;

    public StartPopupFrame(
        String title,
        String closeLabel,
        Actor body,
        Runnable onClose,
        StartIconsConfig.PopupConfig popup,
        float width,
        float height
    ) {
        setTransform(true);
        setOrigin(Align.center);
        SkinPanel panel = new SkinPanel(popup.panelDrawable, UiTheme.BG_DIALOG);
        panel.pad(popup.contentPadding);
        panel.top();

        SkinPanel header = new SkinPanel(popup.headerDrawable, UiTheme.BG_TAB);
        header.pad(popup.headerPadding);
        Label titleLabel = TextSpec.of(popup.titleStyle)
            .align(TextAlign.LEFT)
            .scale(popup.titleScale)
            .build(title == null ? "" : title);
        String resolvedClose = closeLabel == null || closeLabel.isBlank() ? popup.defaultCloseLabel : closeLabel;
        ActionButton close = new ActionButton(resolvedClose == null ? "" : resolvedClose, popup.closeStyle);
        close.onClick(onClose);
        header.add(titleLabel).growX().left();
        header.add(close).width(popup.closeButtonWidth).height(popup.closeButtonHeight).right();

        bodyHost = new Table();
        bodyHost.top();
        if (body != null) {
            bodyHost.add(body).grow();
        }

        panel.add(header).growX().height(popup.headerHeight).row();
        panel.add(bodyHost).grow().padTop(popup.contentHeaderGap);
        add(panel).size(width, height);
    }

    public Table bodyHost() {
        return bodyHost;
    }
}
