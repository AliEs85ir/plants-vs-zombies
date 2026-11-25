package app.view.mainmenu.content;

import app.view.mainmenu.config.MainMenuConfig;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public final class MainMenuContentHost extends WidgetGroup {
    private final MainMenuConfig.ContentAreaConfig config;
    private final MainMenuConfig.TabTitleConfig tabTitle;
    private Actor content;
    private boolean inset = true;

    public MainMenuContentHost(MainMenuConfig.ContentAreaConfig config) {
        this(config, null);
    }

    public MainMenuContentHost(MainMenuConfig.ContentAreaConfig config, MainMenuConfig.TabTitleConfig tabTitle) {
        this.config = config;
        this.tabTitle = tabTitle;
        setFillParent(true);
        setTransform(false);
        setTouchable(Touchable.childrenOnly);
    }

    public void setContent(Actor next) {
        setContent(next, true);
    }

    public void setContent(Actor next, boolean inset) {
        if (content != null) {
            content.remove();
        }
        content = next;
        this.inset = inset;
        if (next != null) {
            addActor(next);
        }
        invalidate();
    }

    @Override
    public void layout() {
        if (content == null) {
            return;
        }
        if (!inset) {
            content.setBounds(0f, 0f, getWidth(), getHeight());
            return;
        }
        float padLeft = config.paddingLeft;
        float padRight = config.paddingRight;
        float padTop = config.paddingTop;
        if (tabTitle != null && tabTitle.layout != null) {
            padTop = Math.max(padTop, tabTitle.layout.occupiedHeight());
        }
        float padBottom = config.paddingBottom;
        float width = Math.max(1f, getWidth() - padLeft - padRight);
        float height = Math.max(1f, getHeight() - padTop - padBottom);
        content.setBounds(
            padLeft + config.offsetX,
            padBottom + config.offsetY,
            width,
            height
        );
    }
}
