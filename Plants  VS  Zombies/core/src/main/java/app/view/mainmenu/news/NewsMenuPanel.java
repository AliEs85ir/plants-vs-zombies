package app.view.mainmenu.news;

import app.enums.NewsOrder;
import app.model.News;
import app.service.NewsService;
import app.service.news.NewsPublisher;
import app.view.ui.ScrollPanes;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NewsMenuPanel extends WidgetGroup {
    private final NewsMenuConfig config;
    private final Table listTable;
    private final Table detailTable;
    private final ScrollPane listScroll;
    private final ScrollPane detailScroll;
    private final CheckBox showAllBox;
    private final Label emptyLabel;
    private final Label headerLabel;
    private final TextButton backButton;
    private final List<News> currentList = new ArrayList<>();
    private boolean showAll;
    private float lastListWidth = -1f;
    private News selected;

    public NewsMenuPanel(Skin skin) {
        this.config = NewsMenuConfigLoader.load();
        this.showAll = config.sorting.showAllByDefault();
        NewsPublisher.onCurrentUserMenuOpened();
        setFillParent(false);
        setTransform(false);
        setTouchable(Touchable.childrenOnly);

        listTable = new Table();
        listTable.top().left();
        detailTable = new Table();
        detailTable.top().left();

        listScroll = ScrollPanes.verticalHidden(listTable);
        listScroll.setScrollingDisabled(true, false);
        detailScroll = ScrollPanes.verticalHidden(detailTable);
        detailScroll.setScrollingDisabled(true, false);
        bindScroll(listScroll);
        bindScroll(detailScroll);

        showAllBox = new CheckBox(config.checkbox.checkboxLabel, checkboxStyle(skin));
        showAllBox.setChecked(showAll);
        showAllBox.getLabel().setStyle(new Label.LabelStyle(
            NewsUiFonts.resolve(config.checkbox.checkboxLabelFont, "BRIANNETOD"),
            NewsMenuConfig.color(config.checkbox.checkboxLabelColor, Color.valueOf("F2E7C4"))
        ));
        showAllBox.getLabel().setFontScale(config.checkbox.checkboxLabelFontSize);
        if (showAllBox.getImageCell() != null) {
            showAllBox.getImageCell().size(config.checkbox.checkboxSize);
        }
        bindHover(showAllBox, NewsMenuConfig.color(config.checkbox.checkboxHoverColor, Color.valueOf("FFF4C0")));
        showAllBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showAll = showAllBox.isChecked();
                selected = null;
                rebuildList();
            }
        });

        headerLabel = new Label("", new Label.LabelStyle(
            NewsUiFonts.resolve(config.newsTitle.titleFont, config.newsTitle.titleFontFallback),
            NewsMenuConfig.color(config.newsTitle.titleColor, Color.valueOf("F4E7B0"))
        ));
        headerLabel.setAlignment(Align.center);
        headerLabel.setFontScale(config.newsTitle.titleFontSize);
        refreshHeader();

        String backLabel = config.detailPane == null ? "Back to list" : config.detailPane.backLabel;
        backButton = new TextButton(backLabel, textButtonStyle(skin));
        backButton.setVisible(false);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selected = null;
                backButton.setVisible(false);
                rebuildList();
            }
        });

        emptyLabel = new Label("", new Label.LabelStyle(
            NewsUiFonts.resolve(config.emptyState.emptyMessageFont, config.emptyState.emptyMessageFontFallback),
            NewsMenuConfig.color(config.emptyState.emptyMessageColor, Color.valueOf("E4DCC4"))
        ));
        emptyLabel.setWrap(true);
        emptyLabel.setAlignment(NewsMenuConfig.textAlign(config.emptyState.emptyMessageAlignment));
        emptyLabel.setFontScale(config.emptyState.emptyMessageFontSize);
        emptyLabel.setVisible(false);

        addActor(headerLabel);
        addActor(showAllBox);
        addActor(backButton);
        addActor(listScroll);
        addActor(detailScroll);
        addActor(emptyLabel);
        detailScroll.setVisible(false);
    }

    private void refreshHeader() {
        String paper = config.detailPane == null ? "Neighborhood Newspaper" : config.detailPane.headerTitle;
        int unread = NewsService.unreadCount();
        headerLabel.setText(paper + "  ·  " + LocalDate.now() + "  ·  "
            + (unread > 0 ? unread + " unread" : "all caught up"));
    }

    private void bindScroll(ScrollPane scrollPane) {
        scrollPane.addListener(new InputListener() {
            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                float delta = amountY * config.scrollArea.scrollSensitivity * (config.scrollArea.scrollSpeed / 60f);
                scrollPane.setScrollY(scrollPane.getScrollY() - delta);
                event.stop();
                return true;
            }
        });
    }

    @Override
    public void layout() {
        float width = getWidth();
        float height = getHeight();

        float headerHeight = Math.max(28f, headerLabel.getPrefHeight());
        headerLabel.setBounds(0f, height - headerHeight - 4f, width, headerHeight);

        float checkboxHeight = Math.max(config.checkbox.checkboxSize, showAllBox.getPrefHeight());
        float checkboxWidth = Math.min(width, Math.max(config.checkbox.checkboxSize, showAllBox.getPrefWidth()));
        float topY = height - headerHeight - 10f;
        float checkboxY = topY - config.checkbox.checkboxTopPadding - checkboxHeight
            + config.checkbox.checkboxOffsetY + config.checkbox.checkboxPositionY;
        float checkboxX = ((width - checkboxWidth) * 0.5f)
            + config.checkbox.checkboxOffsetX + config.checkbox.checkboxPositionX;
        showAllBox.setBounds(checkboxX, checkboxY, Math.max(1f, checkboxWidth), checkboxHeight);
        backButton.setBounds(12f, checkboxY, Math.min(180f, width * 0.3f), checkboxHeight);

        float inset = width * config.scrollArea.scrollAreaHorizontalInset;
        float scrollWidth = config.scrollArea.scrollAreaWidth > 0f
            ? Math.min(config.scrollArea.scrollAreaWidth, width)
            : Math.max(80f, width - inset * 2f);
        float top = checkboxY - config.checkbox.checkboxBottomPadding - config.scrollArea.scrollAreaTopPadding;
        float scrollHeight = config.scrollArea.scrollAreaHeight > 0f
            ? config.scrollArea.scrollAreaHeight
            : Math.max(80f, top - config.scrollArea.scrollAreaBottomPadding);
        float scrollX = ((width - scrollWidth) * 0.5f) + config.scrollArea.scrollAreaOffsetX;
        float scrollY = config.scrollArea.scrollAreaBottomPadding + config.scrollArea.scrollAreaOffsetY;

        listScroll.setBounds(scrollX, scrollY, Math.max(1f, scrollWidth), Math.max(1f, scrollHeight));
        detailScroll.setBounds(scrollX, scrollY, Math.max(1f, scrollWidth), Math.max(1f, scrollHeight));
        emptyLabel.setBounds(scrollX, scrollY, Math.max(1f, scrollWidth), Math.max(1f, scrollHeight));
        emptyLabel.setFontScale(config.emptyState.emptyMessageFontSize);
        rebuildListIfWidthChanged(scrollWidth);
    }

    private void rebuildListIfWidthChanged(float scrollWidth) {
        if (Math.abs(scrollWidth - lastListWidth) < 0.5f) {
            return;
        }
        lastListWidth = scrollWidth;
        rebuildList();
    }

    private void rebuildList() {
        refreshHeader();
        listTable.clearChildren();
        detailTable.clearChildren();
        currentList.clear();
        NewsOrder order = config.sorting.order();
        List<News> visible = NewsService.visibleNews(showAll, order);
        currentList.addAll(visible);
        boolean empty = visible.isEmpty();
        emptyLabel.setText(config.emptyState.message(showAll));
        emptyLabel.setVisible(empty && selected == null);
        boolean detailMode = selected != null;
        listScroll.setVisible(!empty && !detailMode);
        detailScroll.setVisible(detailMode);
        backButton.setVisible(detailMode);
        showAllBox.setVisible(!detailMode);
        if (empty && !detailMode) {
            listTable.invalidateHierarchy();
            return;
        }
        float wrapWidth = Math.max(80f, (lastListWidth > 0f ? lastListWidth : getWidth())
            - config.newsItem.newsItemPaddingLeft - config.newsItem.newsItemPaddingRight);
        if (config.newsItem.newsItemWidth > 0f) {
            wrapWidth = Math.min(wrapWidth, config.newsItem.newsItemWidth);
        }
        if (detailMode) {
            buildDetail(selected, wrapWidth + config.newsItem.newsItemPaddingLeft + config.newsItem.newsItemPaddingRight);
            return;
        }
        listTable.padTop(config.newsItem.firstNewsTopPadding);
        listTable.padBottom(config.newsItem.lastNewsBottomPadding);
        for (int i = 0; i < visible.size(); i++) {
            boolean last = i == visible.size() - 1;
            News news = visible.get(i);
            NewsItemWidget item = new NewsItemWidget(
                news,
                config,
                wrapWidth + config.newsItem.newsItemPaddingLeft + config.newsItem.newsItemPaddingRight,
                last,
                true,
                () -> openArticle(news)
            );
            listTable.add(item).growX().padBottom(last ? 0f : config.newsItem.newsItemSpacing);
            listTable.row();
        }
        listTable.invalidateHierarchy();
    }

    private void openArticle(News news) {
        selected = news;
        NewsService.markRead(news);
        rebuildList();
    }

    private void buildDetail(News news, float wrapWidth) {
        News item = news.sanitize();
        detailTable.padTop(config.newsItem.firstNewsTopPadding);
        detailTable.padBottom(config.newsItem.lastNewsBottomPadding);
        NewsItemWidget detail = new NewsItemWidget(item, config, wrapWidth, true, false, null);
        detailTable.add(detail).growX();
        detailTable.row();
        detailTable.invalidateHierarchy();
        detailScroll.setScrollY(0f);
    }

    private static void bindHover(Actor actor, Color hoverColor) {
        Color rest = actor.getColor().cpy();
        Color hover = hoverColor == null ? rest : hoverColor.cpy();
        actor.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer != -1) {
                    return;
                }
                actor.setColor(hover);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer != -1) {
                    return;
                }
                actor.setColor(rest);
            }
        });
    }

    private CheckBox.CheckBoxStyle checkboxStyle(Skin skin) {
        String styleName = config.checkbox.checkboxStyle;
        if (skin.has(styleName, CheckBox.CheckBoxStyle.class)) {
            return skin.get(styleName, CheckBox.CheckBoxStyle.class);
        }
        if (skin.has("default", CheckBox.CheckBoxStyle.class)) {
            return skin.get("default", CheckBox.CheckBoxStyle.class);
        }
        return skin.get(CheckBox.CheckBoxStyle.class);
    }

    private TextButton.TextButtonStyle textButtonStyle(Skin skin) {
        if (skin.has("default", TextButton.TextButtonStyle.class)) {
            return skin.get("default", TextButton.TextButtonStyle.class);
        }
        return skin.get(TextButton.TextButtonStyle.class);
    }
}
