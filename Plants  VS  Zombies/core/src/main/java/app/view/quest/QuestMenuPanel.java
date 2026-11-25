package app.view.quest;

import app.enums.quest.QuestCategory;
import app.model.App;
import app.model.user.User;
import app.view.mainmenu.MenuTextures;
import app.view.mainmenu.news.NewsUiFonts;
import app.view.ui.ScrollPanes;
import app.view.ui.SkinPanel;
import app.view.ui.Solid;
import app.view.ui.UiTheme;
import app.view.ui.button.ActionButton;
import app.view.ui.button.ButtonTheme;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import game.audio.AudioId;
import game.audio.AudioManager;
import game.component.AssetManager;
import game.entity.quest.Quest;
import game.service.quest.QuestManager;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class QuestMenuPanel extends WidgetGroup implements Disposable {
    private enum TabId {
        DAILY,
        WORLD,
        EVENTS
    }

    private final QuestMenuConfig config;
    private final ObjectMap<String, Texture> textures = new ObjectMap<>();
    private final Table dimmer;
    private final SkinPanel panel;
    private final Actor closeButton;
    private final Table tabRow;
    private final Label timerLabel;
    private final Table listTable;
    private final ScrollPane scrollPane;
    private final Label emptyLabel;
    private final List<ActionButton> tabButtons = new ArrayList<>();
    private TabId selectedTab = TabId.DAILY;
    private boolean opened;

    public QuestMenuPanel() {
        this.config = QuestMenuConfigLoader.load();
        setFillParent(true);
        setTransform(false);
        setVisible(false);
        setTouchable(Touchable.disabled);

        dimmer = new Table();
        dimmer.setFillParent(true);
        dimmer.setBackground(Solid.drawable(
            QuestMenuConfig.color(config.overlay.color, new Color(0f, 0f, 0f, 0.55f)),
            1f,
            1f
        ));
        dimmer.setTouchable(Touchable.enabled);
        dimmer.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (config.overlay.closeOnOutsideClick) {
                    close();
                }
            }
        });
        addActor(dimmer);

        panel = new SkinPanel(config.panel.backgroundDrawable, config.panel.backgroundFallback);
        panel.setTouchable(Touchable.enabled);
        panel.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                event.stop();
                return true;
            }
        });
        addActor(panel);

        closeButton = createCloseButton();
        if (closeButton != null) {
            addActor(closeButton);
        }

        tabRow = new Table();
        tabRow.left();
        buildTabs();
        addActor(tabRow);

        timerLabel = new Label("", new Label.LabelStyle(
            NewsUiFonts.resolve(config.timer.font, config.timer.fontFallback),
            QuestMenuConfig.color(config.timer.color, Color.valueOf("F8D84A"))
        ));
        timerLabel.setAlignment(QuestMenuConfig.textAlign(config.timer.alignment));
        timerLabel.setFontScale(config.timer.fontSize);
        addActor(timerLabel);

        listTable = new Table();
        listTable.top().left();
        scrollPane = ScrollPanes.verticalHidden(listTable);
        scrollPane.setScrollingDisabled(true, false);
        addActor(scrollPane);

        emptyLabel = new Label("", new Label.LabelStyle(
            NewsUiFonts.resolve(config.list.emptyFont, config.list.emptyFontFallback),
            QuestMenuConfig.color(config.list.emptyColor, Color.valueOf("E4DCC4"))
        ));
        emptyLabel.setWrap(true);
        emptyLabel.setAlignment(Align.center);
        emptyLabel.setFontScale(config.list.emptyFontSize);
        emptyLabel.setVisible(false);
        addActor(emptyLabel);
    }

    public void open() {
        opened = true;
        setVisible(true);
        setTouchable(Touchable.enabled);
        toFront();
        selectedTab = TabId.DAILY;
        QuestManager manager = questManager();
        if (manager != null) {
            manager.checkDailyReset();
            manager.ensureEventQuests();
        }
        refreshTabs();
        refreshTimer();
        rebuildList();
    }

    public void close() {
        opened = false;
        setVisible(false);
        setTouchable(Touchable.disabled);
    }

    public boolean isOpen() {
        return opened && isVisible();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!isOpen()) {
            return;
        }
        refreshTimer();
    }

    @Override
    public void layout() {
        float width = getWidth();
        float height = getHeight();
        dimmer.setBounds(0f, 0f, width, height);

        float panelWidth = Math.min(width, Math.max(config.panel.minWidth, width * config.panel.widthRatio));
        float panelHeight = Math.min(height, Math.max(config.panel.minHeight, height * config.panel.heightRatio));
        float panelX = (width - panelWidth) * 0.5f + config.panel.offsetX;
        float panelY = (height - panelHeight) * 0.5f + config.panel.offsetY;
        panel.setBounds(panelX, panelY, panelWidth, panelHeight);

        float innerLeft = panelX + config.panel.padLeft;
        float innerRight = panelX + panelWidth - config.panel.padRight;
        float innerTop = panelY + panelHeight - config.panel.padTop;
        float innerBottom = panelY + config.panel.padBottom;

        if (closeButton != null) {
            float closeW = config.closeButton.width;
            float closeH = config.closeButton.height;
            closeButton.setBounds(
                innerRight - closeW + config.closeButton.offsetX,
                innerTop - closeH + config.closeButton.offsetY,
                closeW,
                closeH
            );
        }

        float tabWidth = Math.max(80f, innerRight - innerLeft - config.tabs.padRightForClose);
        float tabHeight = config.tabs.height;
        tabRow.setBounds(
            innerLeft + config.tabs.offsetX,
            innerTop - tabHeight + config.tabs.offsetY,
            tabWidth,
            tabHeight
        );

        float timerHeight = selectedTab == TabId.DAILY ? config.timer.height : 0f;
        timerLabel.setVisible(selectedTab == TabId.DAILY);
        float timerY = tabRow.getY() - (selectedTab == TabId.DAILY ? config.timer.topPadding + timerHeight : config.list.padTop) + config.timer.offsetY;
        timerLabel.setBounds(
            innerLeft + config.timer.offsetX,
            selectedTab == TabId.DAILY ? timerY : tabRow.getY(),
            Math.max(40f, tabWidth),
            Math.max(1f, timerHeight)
        );

        float listTop = selectedTab == TabId.DAILY
            ? timerY - config.list.padTop
            : tabRow.getY() - config.list.padTop;
        float listHeight = Math.max(40f, listTop - innerBottom - config.list.padBottom);
        float listWidth = Math.max(40f, innerRight - innerLeft);
        scrollPane.setBounds(innerLeft, innerBottom, listWidth, listHeight);
        emptyLabel.setBounds(innerLeft, innerBottom, listWidth, listHeight);

        if (closeButton != null) {
            closeButton.toFront();
        }
    }

    private Actor createCloseButton() {
        ImageButton imageButton = SkinImageButtons.create(
            config.closeButton.skinStyle,
            config.closeButton.width,
            config.closeButton.height,
            this::close
        );
        if (imageButton != null) {
            return imageButton;
        }
        ButtonTheme theme = new ButtonTheme().size(config.closeButton.width, config.closeButton.height);
        ActionButton fallback = new ActionButton("X", UiTheme.buttonStyleOrDefault("brown"), theme);
        fallback.onClick(this::close);
        return fallback;
    }

    private void buildTabs() {
        ButtonTheme theme = new ButtonTheme();
        theme.height = config.tabs.height;
        theme.labelScale = 0.86f;
        for (QuestMenuConfig.TabConfig tab : config.tabs.ordered()) {
            TabId id = parseTab(tab.id);
            ActionButton button = new ActionButton(tab.text, UiTheme.buttonStyleOrDefault(tab.style), theme);
            button.onClick(() -> selectTab(id));
            tabButtons.add(button);
            tabRow.add(button).growX().height(config.tabs.height).padRight(config.tabs.spacing);
        }
    }

    private void selectTab(TabId tab) {
        if (tab == null) {
            return;
        }
        selectedTab = tab;
        refreshTabs();
        rebuildList();
    }

    private void refreshTabs() {
        List<QuestMenuConfig.TabConfig> tabs = config.tabs.ordered();
        for (int i = 0; i < tabButtons.size() && i < tabs.size(); i++) {
            QuestMenuConfig.TabConfig tab = tabs.get(i);
            boolean selected = parseTab(tab.id) == selectedTab;
            String style = selected ? tab.selectedStyle : tab.style;
            tabButtons.get(i).setStyle(textButtonStyle(style));
        }
    }

    private static TextButton.TextButtonStyle textButtonStyle(String style) {
        return AssetManager.skin.get(
            UiTheme.buttonStyleOrDefault(style),
            TextButton.TextButtonStyle.class
        );
    }

    private void rebuildList() {
        listTable.clearChildren();
        QuestCategory category = switch (selectedTab) {
            case DAILY -> QuestCategory.DAILY;
            case EVENTS -> QuestCategory.EVENT;
            case WORLD -> null;
        };
        if (category == null) {
            showEmpty(emptyMessageFor(selectedTab));
            return;
        }
        QuestManager manager = questManager();
        List<Quest> quests = manager == null ? List.of() : manager.getQuestsByCategory(category);
        List<Quest> visible = new ArrayList<>();
        for (Quest quest : quests) {
            if (config.showsQuest(quest) && !quest.isCompleted()) {
                visible.add(quest);
            }
        }
        if (visible.isEmpty()) {
            showEmpty(emptyMessageFor(selectedTab));
            return;
        }
        emptyLabel.setVisible(false);
        scrollPane.setVisible(true);
        for (int i = 0; i < visible.size(); i++) {
            Quest quest = visible.get(i);
            QuestCardRow row = new QuestCardRow(quest, config, this::texture, () -> claim(quest));
            listTable.add(row).growX().height(config.card.height).padBottom(config.list.spacing).row();
        }
        listTable.invalidateHierarchy();
    }

    private void claim(Quest quest) {
        QuestManager manager = questManager();
        if (manager == null || quest == null) {
            return;
        }
        if (!manager.claimQuest(quest)) {
            AudioManager.play(AudioId.UI_ERROR);
            return;
        }
        rebuildList();
    }

    private void showEmpty(String message) {
        boolean hasMessage = message != null && !message.isBlank();
        emptyLabel.setText(hasMessage ? message : "");
        emptyLabel.setVisible(hasMessage);
        scrollPane.setVisible(!hasMessage);
    }

    private String emptyMessageFor(TabId tab) {
        return switch (tab) {
            case WORLD -> config.list.emptyWorldMessage;
            case EVENTS -> config.list.emptyEventsMessage;
            default -> config.list.emptyDailyMessage;
        };
    }

    private void refreshTimer() {
        timerLabel.setText(config.timer.text.replace("{time}", remainingText()));
    }

    private String remainingText() {
        QuestManager manager = questManager();
        LocalDate resetDate = manager == null || manager.getLastResetDate() == null
            ? LocalDate.now()
            : manager.getLastResetDate();
        LocalDateTime nextReset = resetDate.plusDays(1).atStartOfDay();
        Duration remaining = Duration.between(LocalDateTime.now(), nextReset);
        if (remaining.isNegative() || remaining.isZero()) {
            return "0min";
        }
        long hours = remaining.toHours();
        long minutes = remaining.minusHours(hours).toMinutes();
        if (hours > 0) {
            return hours + "h " + minutes + "min";
        }
        return Math.max(1L, minutes) + "min";
    }

    private Texture texture(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        String key = path.replace('\\', '/');
        if (textures.containsKey(key)) {
            return textures.get(key);
        }
        Texture loaded = MenuTextures.load(key, "QuestMenuPanel");
        textures.put(key, loaded);
        return loaded;
    }

    private static QuestManager questManager() {
        User user = App.getCurrentUser();
        return user == null ? null : user.getQuestManager();
    }

    private static TabId parseTab(String id) {
        if (id == null || id.isBlank()) {
            return TabId.DAILY;
        }
        try {
            return TabId.valueOf(id.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return TabId.DAILY;
        }
    }

    @Override
    public void dispose() {
        close();
        for (Texture texture : textures.values()) {
            if (texture != null) {
                texture.dispose();
            }
        }
        textures.clear();
    }
}
