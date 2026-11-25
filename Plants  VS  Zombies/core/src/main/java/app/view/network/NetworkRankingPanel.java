package app.view.network;

import app.model.App;
import app.model.user.User;
import app.service.AppService;
import app.view.mainmenu.profile.ProfileUiFonts;
import app.view.ui.ScrollPanes;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import server.network.Packets;
import server.user.ScoringLeaderboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class NetworkRankingPanel extends WidgetGroup {
    private final NetworkMenuConfig.RankingConfig config;
    private final Image background;
    private final ScrollPane scrollPane;
    private final Table list;
    private final NetworkSortSelect sortSelect;
    private final List<RankedUser> serverUsers = new ArrayList<>();
    private float padScaleX = 1f;
    private float padScaleY = 1f;
    private float lastBuiltScaleX = -1f;
    private float lastBuiltScaleY = -1f;
    private NetworkRankSort sort;

    public NetworkRankingPanel(
        Texture backgroundTexture,
        NetworkMenuConfig.RankingConfig config
    ) {
        this.config = config;
        this.sort = config.sort.initialSort();
        setName("menu-network-ranking");
        setTransform(false);
        setTouchable(Touchable.childrenOnly);

        background = new Image();
        background.setTouchable(Touchable.disabled);
        background.setScaling(Scaling.stretch);
        if (backgroundTexture != null) {
            background.setDrawable(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));
        }

        list = new Table();
        list.top().left();
        list.defaults().growX();
        scrollPane = ScrollPanes.verticalHidden(list);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(true);
        scrollPane.setForceScroll(false, false);
        sortSelect = new NetworkSortSelect(config);
        sortSelect.setOnChanged(next -> {
            sort = next;
            lastBuiltScaleX = -1f;
            lastBuiltScaleY = -1f;
            rebuildRows();
            layoutChildren();
        });
        addActor(background);
        addActor(scrollPane);
        addActor(sortSelect);
        refreshFromServer();
    }

    public void place(float x, float y, float width, float height, float scaleX, float scaleY) {
        padScaleX = scaleX > 0f ? scaleX : 1f;
        padScaleY = scaleY > 0f ? scaleY : 1f;
        setBounds(x, y, Math.max(1f, width), Math.max(1f, height));
        if (Math.abs(lastBuiltScaleX - padScaleX) > 0.01f || Math.abs(lastBuiltScaleY - padScaleY) > 0.01f) {
            lastBuiltScaleX = padScaleX;
            lastBuiltScaleY = padScaleY;
            rebuildRows();
        }
        layoutChildren();
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        layoutChildren();
    }

    private void refreshFromServer() {
        AppService.loadLeaderboard(entries -> {
            serverUsers.clear();
            if (entries != null) {
                for (Packets.LeaderboardEntry entry : entries) {
                    RankedUser user = ranked(entry);
                    if (user != null) {
                        serverUsers.add(user);
                    }
                }
            }
            lastBuiltScaleX = -1f;
            lastBuiltScaleY = -1f;
            rebuildRows();
            layoutChildren();
        });
    }

    private void layoutChildren() {
        background.setBounds(0f, 0f, getWidth(), getHeight());
        float padLeft = config.contentPadLeft * padScaleX;
        float padRight = config.contentPadRight * padScaleX;
        float padTop = config.contentPadTop * padScaleY;
        float padBottom = config.contentPadBottom * padScaleY;
        float paneWidth = Math.max(1f, getWidth() - padLeft - padRight);
        float sortWidth = paneWidth;
        float sortHeight = config.sort.height * padScaleY;
        float sortGap = config.sort.gap * padScaleY;
        float paneHeight = Math.max(1f, getHeight() - padTop - padBottom - sortHeight - sortGap);
        scrollPane.setBounds(padLeft, padBottom + sortHeight + sortGap, paneWidth, paneHeight);
        list.setWidth(paneWidth);
        list.invalidateHierarchy();
        float sortX = padLeft + (paneWidth - sortWidth) * 0.5f;
        sortSelect.place(sortX, padBottom, sortWidth, sortHeight, padScaleX, padScaleY);
        sortSelect.toFront();
    }

    private void rebuildRows() {
        list.clearChildren();
        List<RankedUser> users = rankedUsers();
        if (users.isEmpty()) {
            BitmapFont font = ProfileUiFonts.resolve(config.nameFont, config.nameFontFallback);
            Color color = NetworkMenuConfig.color(config.rankColor, Color.valueOf("5A2E0A"));
            Label empty = new Label(config.emptyText, new Label.LabelStyle(font, color));
            empty.setAlignment(Align.center);
            empty.setWrap(true);
            empty.setFontScale(Math.max(0.1f, config.nameFontSize));
            list.add(empty).growX().pad(8f);
            list.row();
            return;
        }
        float rowHeight = config.rowHeight * padScaleY;
        float spacing = config.rowSpacing * padScaleY;
        int rank = 1;
        for (RankedUser user : users) {
            NetworkRankSlot slot = new NetworkRankSlot(
                config,
                rank,
                user.name,
                user.score(sort),
                user.pictureId,
                user.you,
                sort,
                padScaleX,
                padScaleY
            );
            list.add(slot).growX().height(rowHeight).padBottom(spacing);
            list.row();
            rank++;
        }
    }

    private List<RankedUser> rankedUsers() {
        List<RankedUser> users = new ArrayList<>(serverUsers);
        if (config.includeLoggedInUser) {
            mergeCurrentUser(users);
        }
        users.removeIf(user -> user.score(sort) <= 0);
        users.sort(Comparator
            .comparingInt((RankedUser user) -> user.score(sort))
            .reversed()
            .thenComparing(user -> user.name, String.CASE_INSENSITIVE_ORDER));
        return users;
    }

    private static RankedUser ranked(Packets.LeaderboardEntry entry) {
        if (entry == null) {
            return null;
        }
        String username = entry.username == null ? "" : entry.username.trim();
        if (username.isBlank() && (entry.nickname == null || entry.nickname.isBlank())) {
            return null;
        }
        return new RankedUser(
            username,
            ScoringLeaderboard.displayName(entry),
            Math.max(0, entry.mioPoint),
            Math.max(0, entry.gems),
            Math.max(0, entry.coins),
            Math.max(0, entry.gamesPlayed),
            false,
            entry.pictureId
        );
    }

    private static void mergeCurrentUser(List<RankedUser> users) {
        User current = App.getCurrentUser();
        if (current == null || current.getUsername() == null || current.getUsername().isBlank()) {
            return;
        }
        String username = current.getUsername().trim();
        String display = current.getNickname() != null && !current.getNickname().isBlank()
            ? current.getNickname()
            : username;
        RankedUser live = new RankedUser(
            username,
            display,
            Math.max(0, current.getMioPoint()),
            Math.max(0, current.getGems()),
            Math.max(0, current.getCoins()),
            Math.max(0, current.getGamesPlayed()),
            true,
            current.getProfilePictureId()
        );
        for (int i = 0; i < users.size(); i++) {
            RankedUser existing = users.get(i);
            if (matches(existing.username, username) || matches(existing.name, display, username)) {
                users.set(i, live);
                return;
            }
        }
        users.add(live);
    }

    private static boolean matches(String left, String... names) {
        if (left == null || left.isBlank()) {
            return false;
        }
        for (String name : names) {
            if (name != null && left.equalsIgnoreCase(name.trim())) {
                return true;
            }
        }
        return false;
    }

    private record RankedUser(
        String username,
        String name,
        int mioPoint,
        int gems,
        int coins,
        int gamesPlayed,
        boolean you,
        String pictureId
    ) {
        int score(NetworkRankSort sort) {
            NetworkRankSort resolved = sort == null ? NetworkRankSort.MIOPOINT : sort;
            return switch (resolved) {
                case MIOPOINT -> mioPoint;
                case DIAMOND -> gems;
                case COIN -> coins;
                case GAMES_PLAYED -> gamesPlayed;
            };
        }
    }
}
