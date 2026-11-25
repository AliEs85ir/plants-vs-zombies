package app.view.game.pregame.chapter_popup;
import app.view.game.pregame.PregameLayout;
import app.view.ui.ModalOverlay;
import app.view.ui.ScrollPanes;
import app.view.ui.SkinPanel;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import game.world.chapter.Chapter;
public abstract class ChapterIntroPopup extends ModalOverlay {

    protected final Chapter chapter;
    protected final PregameLayout layout;
    protected Label errorLabel;

    public ChapterIntroPopup(Chapter chapter, PregameLayout layout) {
        this.chapter = chapter;
        this.layout = layout;
    }

    protected void build() {
        SkinPanel root = new SkinPanel(layout.backgrounds.popupRoot);
        root.pad(layout.spacing.contentPadding);
        root.top();

        root.add(buildSeasonBanner()).growX()
            .height(layout.intro.seasonBannerHeight).row();

        root.add(buildChapterBadge()).growX()
            .height(layout.intro.chapterBadgeHeight)
            .padTop(layout.intro.titleGap).row();

        Table factRow = buildFactRow();
        if (factRow != null) {
            root.add(factRow).growX()
                .height(layout.intro.factCardHeight)
                .padTop(layout.intro.factRowGap).row();
        }

        root.add(buildDescription()).grow()
            .padTop(layout.intro.descriptionGap).row();

        Table hintBar = buildHintBar();
        if (hintBar != null) {
            root.add(hintBar).growX()
                .height(layout.intro.tooltipHeight)
                .padTop(layout.intro.tooltipGap).row();
        }

        Table footer = buildFooter();
        if (footer != null) {
            root.add(footer).growX()
                .height(layout.intro.footerHeight)
                .padTop(layout.intro.tooltipButtonGap).row();
        }

        add(root).size(layout.intro.width, layout.intro.height);
    }

    private Table buildSeasonBanner() {
        SkinPanel banner = new SkinPanel(layout.backgrounds.seasonBanner);
        banner.pad(layout.spacing.tightPadding);
        banner.add(TextSpec.of(layout.text.seasonTitle).build(seasonTitle())).grow();
        return banner;
    }

    private Table buildChapterBadge() {
        Table wrapper = new Table();
        SkinPanel badge = new SkinPanel(layout.backgrounds.chapterBadge);
        badge.pad(layout.spacing.tightPadding, layout.spacing.innerPadding,
            layout.spacing.tightPadding, layout.spacing.innerPadding);
        badge.add(TextSpec.of(layout.text.chapterBadge).build(chapterLabel()));
        wrapper.add(badge);
        return wrapper;
    }

    private Table buildDescription() {
        SkinPanel body = new SkinPanel(layout.backgrounds.descriptionBox);
        body.pad(layout.spacing.innerPadding);
        body.top();

        Label description = TextSpec.of(layout.text.description).build(descriptionText());

        ScrollPane scroll = ScrollPanes.vertical(description, layout.cards.scrollbarWidth);
        body.add(scroll).grow().maxHeight(layout.intro.descriptionMaxHeight).row();

        errorLabel = TextSpec.of(layout.text.error).build("");
        body.add(errorLabel).growX().padTop(layout.spacing.tightPadding).row();

        return body;
    }

    protected void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
        }
    }

    private String seasonTitle() {
        if (chapter.getSeason() != null && chapter.getSeason().getSeasonType() != null) {
            return chapter.getSeason().getSeasonType().name().replace('_', ' ');
        }
        return "UNKNOWN SEASON";
    }

    private String chapterLabel() {
        String type = (chapter.getChapterType() != null) ? chapter.getChapterType().name() : "";
        String base = "CHAPTER " + chapter.getChapterNumber();
        return type.isEmpty() ? base : base + "  ·  " + type;
    }

    private String descriptionText() {
        String description = chapter.getDescription();
        if (description == null || description.trim().isEmpty()) {
            return "A new challenge awaits. Prepare your defenses.";
        }
        return description;
    }

    // --- Hooks for child classes to override ---

    protected abstract Table buildFactRow();
    protected abstract Table buildHintBar();
    protected abstract Table buildFooter();
}
