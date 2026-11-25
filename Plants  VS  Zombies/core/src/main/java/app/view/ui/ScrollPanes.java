package app.view.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;

public final class ScrollPanes {

    private ScrollPanes() {}

    public static ScrollPane vertical(Actor content, float scrollbarWidth) {
        ScrollPane.ScrollPaneStyle style = new ScrollPane.ScrollPaneStyle();
        style.vScrollKnob = Solid.drawable(
            UiTheme.SCROLL_KNOB, scrollbarWidth, scrollbarWidth * 2f);
        style.vScroll = Solid.drawable(
            UiTheme.SCROLL_TRACK, scrollbarWidth, scrollbarWidth);

        ScrollPane pane = new ScrollPane(content, style);
        pane.setFadeScrollBars(false);
        pane.setScrollingDisabled(true, false);
        pane.setOverscroll(false, false);
        pane.setSmoothScrolling(true);
        pane.setScrollbarsOnTop(false);
        return pane;
    }

    public static ScrollPane verticalHidden(Actor content) {
        ScrollPane pane = new ScrollPane(content, new ScrollPane.ScrollPaneStyle());
        pane.setFadeScrollBars(true);
        pane.setScrollingDisabled(true, false);
        pane.setOverscroll(false, false);
        pane.setSmoothScrolling(true);
        pane.setScrollbarsOnTop(true);
        pane.setScrollbarsVisible(false);
        return pane;
    }
}
