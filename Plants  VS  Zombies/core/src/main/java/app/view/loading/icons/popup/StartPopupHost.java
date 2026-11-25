package app.view.loading.icons.popup;

import app.enums.cursor.CursorType;
import app.view.cursor.GameCursor;
import app.view.loading.icons.StartIconActionRouter;
import app.view.loading.icons.StartScreenStore;
import app.view.loading.icons.config.StartIconsConfig;
import app.view.loading.icons.content.AboutContent;
import app.view.loading.icons.content.CalendarContent;
import app.view.loading.icons.content.DonateContent;
import app.view.loading.icons.content.FaqContent;
import app.view.loading.icons.content.SocialContent;
import app.view.loading.icons.content.StartContentLoader;
import app.view.loading.icons.content.TutorialContent;
import app.view.ui.ScrollPanes;
import app.view.ui.button.ActionButton;
import app.view.ui.text.TextAlign;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import game.component.AssetManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class StartPopupHost {
    private final Stage stage;
    private final StartIconsConfig config;
    private final StartScreenStore store;
    private final GameCursor gameCursor;
    private StartPopupOverlay overlay;
    private StartPopupFrame frame;

    public StartPopupHost(Stage stage, StartIconsConfig config, StartScreenStore store, GameCursor gameCursor) {
        this.stage = stage;
        this.config = config;
        this.store = store;
        this.gameCursor = gameCursor;
    }

    public void open(String popupId) {
        if (popupId == null || popupId.isBlank() || AssetManager.skin == null) {
            return;
        }
        switch (popupId.trim().toLowerCase(Locale.ROOT)) {
            case "calendar" -> openCalendar();
            case "donate" -> openDonate();
            case "social", "social_media" -> openSocial();
            case "about" -> openAbout();
            case "faq" -> openFaq();
            case "tutorial" -> openTutorial();
            default -> {
            }
        }
    }

    public void close() {
        if (overlay == null) {
            resetCursor();
            return;
        }
        StartPopupOverlay closing = overlay;
        StartIconsConfig.PopupConfig popup = config.popup;
        if (frame != null) {
            frame.addAction(Actions.scaleTo(popup.startScale, popup.startScale, popup.fadeOutDuration, Interpolation.sineIn));
        }
        closing.addAction(Actions.sequence(
            Actions.fadeOut(popup.fadeOutDuration),
            Actions.run(() -> {
                closing.remove();
                if (overlay == closing) {
                    overlay = null;
                    frame = null;
                }
                resetCursor();
            })
        ));
    }

    private void openCalendar() {
        CalendarContent content = StartContentLoader.load(
            config.content.calendarPath, CalendarContent.class, null, null, new CalendarContent()
        );
        store.beginSession(0);
        Table body = new Table();
        body.top().left();
        LocalDateTime now = LocalDateTime.now();
        String month = now.getMonth().name();
        month = month.charAt(0) + month.substring(1).toLowerCase(Locale.ROOT);
        addField(body, content.yearLabel, String.valueOf(now.getYear()));
        addField(body, content.monthLabel, month);
        addField(body, content.dayLabel, String.valueOf(now.getDayOfMonth()));
        addField(body, content.timeLabel, StartScreenStore.format(now, content.datePattern));
        if (!store.hasLoggedInUser()) {
            addWrapped(body, content.guestMessage);
        } else if (store.previousVisit() == null || store.previousVisit().isBlank()) {
            addWrapped(body, content.firstVisitMessage);
        } else {
            addField(body, content.lastVisitLabel, store.previousVisit());
        }
        present(content.title, content.closeLabel, scroll(body));
    }

    private void openDonate() {
        DonateContent content = StartContentLoader.load(
            config.content.donatePath, DonateContent.class, null, null, new DonateContent()
        );
        store.beginSession(content.startingCount);
        Table body = new Table();
        body.top().left();
        addWrapped(body, content.message);
        ActionButton link = button(content.linkLabel, config.popup.secondaryStyle);
        link.onClick(() -> StartIconActionRouter.openUrl(content.url));
        body.add(link).padTop(12f).left().row();
        Label counterTitle = label(content.counterTitle, true);
        Label counterValue = label(formatCount(store.donationLikeCount()), true);
        counterValue.setFontScale(config.popup.titleScale);
        body.add(counterTitle).padTop(18f).left().row();
        body.add(counterValue).padTop(4f).left().row();
        ActionButton like = button(content.supportButtonLabel, config.popup.primaryStyle);
        like.onClick(() -> counterValue.setText(formatCount(store.incrementDonationLikes())));
        body.add(like).padTop(14f).left().row();
        present(content.title, content.closeLabel, scroll(body));
    }

    private void openSocial() {
        SocialContent content = StartContentLoader.load(
            config.content.socialPath, SocialContent.class, SocialContent.Platform.class, "platforms", new SocialContent()
        );
        Table body = new Table();
        body.top().left();
        addWrapped(body, content.intro);
        List<SocialContent.Platform> platforms = new ArrayList<>();
        if (content.platforms != null) {
            platforms.addAll(content.platforms);
        }
        platforms.removeIf(platform -> platform == null || !platform.enabled);
        platforms.sort(Comparator.comparingInt(platform -> platform.order));
        for (SocialContent.Platform platform : platforms) {
            Table row = new Table();
            row.left();
            Table text = new Table();
            text.left();
            text.add(label(platform.displayName, true)).left().row();
            text.add(label(platform.username, false)).left().padTop(2f).row();
            ActionButton open = button(content.openLabel, config.popup.secondaryStyle);
            String url = platform.url;
            open.onClick(() -> StartIconActionRouter.openUrl(url));
            row.add(text).growX().left();
            row.add(open).width(config.popup.closeButtonWidth).height(config.popup.closeButtonHeight).right();
            body.add(row).growX().padTop(10f).row();
        }
        present(content.title, content.closeLabel, scroll(body));
    }

    private void openAbout() {
        AboutContent content = StartContentLoader.load(
            config.content.aboutPath, AboutContent.class, AboutContent.Section.class, "sections", new AboutContent()
        );
        Table body = new Table();
        body.top().left();
        if (content.sections != null) {
            for (AboutContent.Section section : content.sections) {
                if (section == null || !section.enabled) {
                    continue;
                }
                body.add(label(section.title, true)).growX().left().padTop(config.popup.bodyRowSpacing).row();
                addWrapped(body, section.body);
            }
        }
        present(content.title, content.closeLabel, scroll(body));
    }

    private void openFaq() {
        FaqContent content = StartContentLoader.load(
            config.content.faqPath, FaqContent.class, FaqContent.Item.class, "items", new FaqContent()
        );
        present(content.title, content.closeLabel, scroll(new FaqAccordion(content, config.popup)));
    }

    private void openTutorial() {
        TutorialContent content = StartContentLoader.load(
            config.content.tutorialPath, TutorialContent.class, TutorialContent.Section.class, "sections", new TutorialContent()
        );
        present(content.title, content.closeLabel, new TutorialHub(content, config.popup));
    }

    private void present(String title, String closeLabel, Actor body) {
        closeImmediate();
        resetCursor();
        StartIconsConfig.PopupConfig popup = config.popup;
        float margin = Math.max(0f, popup.margin);
        float width = popup.width;
        float height = popup.height;
        if (stage.getWidth() > 0f) {
            width = Math.min(width, Math.max(160f, stage.getWidth() - margin * 2f));
        }
        if (stage.getHeight() > 0f) {
            height = Math.min(height, Math.max(160f, stage.getHeight() - margin * 2f));
        }
        overlay = new StartPopupOverlay(popup);
        frame = new StartPopupFrame(title, closeLabel, body, this::close, popup, width, height);
        overlay.add(frame).expand().center();
        stage.addActor(overlay);
        overlay.toFront();
        overlay.validate();
        frame.setOrigin(frame.getWidth() * 0.5f, frame.getHeight() * 0.5f);
        overlay.getColor().a = 0f;
        frame.setScale(popup.startScale);
        overlay.addAction(Actions.fadeIn(popup.fadeInDuration));
        frame.addAction(Actions.scaleTo(1f, 1f, popup.scaleDuration, Interpolation.sineOut));
    }

    private void closeImmediate() {
        if (overlay != null) {
            overlay.clearActions();
            overlay.remove();
            overlay = null;
            frame = null;
        }
    }

    private void addField(Table body, String caption, String value) {
        body.add(label(caption, true)).left().padTop(8f).row();
        body.add(label(value, false)).left().padTop(2f).row();
    }

    private void addWrapped(Table body, String text) {
        if (text == null || text.isBlank()) {
            return;
        }
        Label label = TextSpec.of(config.popup.bodyStyle)
            .align(TextAlign.LEFT)
            .scale(config.popup.bodyScale)
            .wrap(true)
            .build(text);
        body.add(label).growX().left().padTop(8f).row();
    }

    private Label label(String text, boolean emphasis) {
        return TextSpec.of(emphasis ? config.popup.titleStyle : config.popup.bodyStyle)
            .align(TextAlign.LEFT)
            .scale(emphasis ? config.popup.titleScale : config.popup.bodyScale)
            .build(text == null ? "" : text);
    }

    private ActionButton button(String text, String style) {
        ActionButton button = new ActionButton(text == null ? "" : text, style);
        button.getLabel().setFontScale(config.popup.bodyScale);
        return button;
    }

    private ScrollPane scroll(Actor content) {
        return ScrollPanes.vertical(content, config.popup.scrollbarWidth);
    }

    private void resetCursor() {
        if (gameCursor != null) {
            gameCursor.setType(CursorType.DEFAULT);
        }
    }

    private static String formatCount(int value) {
        return String.format(Locale.US, "%,d", value);
    }
}
