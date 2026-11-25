package app.view.game.result;

import app.view.game.pause.PauseLayout;
import app.view.ui.SkinPanel;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Align;

import java.util.function.Consumer;

public final class ResultDropdown extends SkinPanel {
    private final ResultLayout layout;
    private final String headerBase;
    private final Actor content;
    private final ScrollPane scroll;
    private final Table body;
    private final Cell<Table> bodyCell;
    private final Label toggleLabel;
    private final Consumer<ResultDropdown> onOpen;
    private final Runnable onLayoutChanged;
    private boolean expanded;

    public ResultDropdown(
        ResultLayout layout,
        String title,
        int itemCount,
        Actor content,
        Consumer<ResultDropdown> onOpen,
        Runnable onLayoutChanged
    ) {
        super(layout.pause.backgrounds.section);
        this.layout = layout;
        this.headerBase = layout.dropdownCountFormat
            .replace("{title}", title != null ? title : "")
            .replace("{count}", String.valueOf(Math.max(0, itemCount)));
        this.content = content != null ? content : new Table();
        this.onOpen = onOpen;
        this.onLayoutChanged = onLayoutChanged;
        PauseLayout pause = layout.pause;
        pad(pause.spacing.innerPadding);
        top();

        toggleLabel = TextSpec.of(pause.text.sectionTitle).build(headerBase + layout.dropdownCollapsedMark);
        toggleLabel.setAlignment(Align.left);

        Table header = new Table();
        header.setTouchable(Touchable.enabled);
        header.add(toggleLabel).growX().left().height(layout.dropdownHeaderHeight);
        header.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean next = !expanded;
                if (next && ResultDropdown.this.onOpen != null) {
                    ResultDropdown.this.onOpen.accept(ResultDropdown.this);
                }
                setExpanded(next);
            }
        });

        add(header).growX().fillX().row();
        SkinPanel separator = new SkinPanel(pause.backgrounds.separator);
        add(separator).growX()
            .height(pause.spacing.separatorHeight)
            .padTop(pause.spacing.separatorGap)
            .padBottom(pause.header.paddingBottom)
            .row();

        scroll = new ScrollPane(this.content);
        scroll.setFadeScrollBars(false);
        scroll.setScrollingDisabled(true, false);
        scroll.setOverscroll(false, false);

        body = new Table();
        body.top().left();
        body.add(scroll).grow().pad(layout.dropdownBodyPad);
        body.setVisible(false);

        bodyCell = add(body).growX().top().height(0f);
        bodyCell.pad(0f);
    }

    public void setExpanded(boolean value) {
        setExpanded(value, true);
    }

    public void collapseQuietly() {
        setExpanded(false, false);
    }

    private void setExpanded(boolean value, boolean notify) {
        if (expanded == value) {
            return;
        }
        expanded = value;
        toggleLabel.setText(headerBase + (value ? layout.dropdownExpandedMark : layout.dropdownCollapsedMark));
        if (value) {
            showBody();
        } else {
            hideBody();
        }
        invalidateHierarchy();
        if (notify && onLayoutChanged != null) {
            onLayoutChanged.run();
        }
    }

    public boolean isExpanded() {
        return expanded;
    }

    private void showBody() {
        float prefHeight = content.getHeight();
        if (content instanceof Table table) {
            table.invalidateHierarchy();
            table.pack();
            prefHeight = table.getPrefHeight();
        } else if (content instanceof Layout layoutContent) {
            layoutContent.invalidateHierarchy();
            layoutContent.validate();
            prefHeight = layoutContent.getPrefHeight();
        }
        float pref = Math.max(1f, prefHeight + layout.dropdownBodyPad * 2f);
        float height = Math.min(pref, layout.dropdownContentMaxHeight);
        boolean needsScroll = pref > layout.dropdownContentMaxHeight + 0.5f;
        scroll.setScrollingDisabled(true, !needsScroll);
        body.setVisible(true);
        bodyCell.height(height);
        bodyCell.padTop(0f);
    }

    private void hideBody() {
        body.setVisible(false);
        bodyCell.height(0f);
        bodyCell.pad(0f);
        scroll.setScrollY(0f);
    }
}
