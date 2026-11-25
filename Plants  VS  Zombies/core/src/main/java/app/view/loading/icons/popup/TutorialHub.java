package app.view.loading.icons.popup;

import app.view.loading.icons.config.StartIconsConfig;
import app.view.loading.icons.content.TutorialContent;
import app.view.ui.ScrollPanes;
import app.view.ui.button.ActionButton;
import app.view.ui.text.TextAlign;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.ArrayList;
import java.util.List;

public final class TutorialHub extends Table {
    private final StartIconsConfig.PopupConfig popup;
    private final List<TutorialContent.Section> sections = new ArrayList<>();
    private final List<ActionButton> navButtons = new ArrayList<>();
    private final Table article = new Table();
    private final ScrollPane articlePane;
    private final ActionButton previous;
    private final ActionButton next;
    private int index;

    public TutorialHub(TutorialContent content, StartIconsConfig.PopupConfig popup) {
        this.popup = popup;
        if (content.sections != null) {
            for (TutorialContent.Section section : content.sections) {
                if (section != null && section.enabled && section.title != null && !section.title.isBlank()) {
                    sections.add(section);
                }
            }
        }
        Table nav = new Table();
        nav.top().left();
        for (int i = 0; i < sections.size(); i++) {
            int sectionIndex = i;
            ActionButton button = new ActionButton(sections.get(i).title, popup.tabStyle);
            button.getLabel().setWrap(true);
            button.getLabel().setFontScale(popup.bodyScale * 0.92f);
            button.onClick(() -> show(sectionIndex));
            nav.add(button).growX().height(popup.tutorialNavButtonHeight).padBottom(popup.tutorialNavSpacing).row();
            navButtons.add(button);
        }
        ScrollPane navPane = ScrollPanes.vertical(nav, popup.scrollbarWidth);
        article.top().left();
        articlePane = ScrollPanes.vertical(article, popup.scrollbarWidth);
        previous = new ActionButton(content.previousLabel, popup.secondaryStyle);
        next = new ActionButton(content.nextLabel, popup.primaryStyle);
        previous.onClick(() -> show(index - 1));
        next.onClick(() -> show(index + 1));
        Table footer = new Table();
        footer.add(previous).width(popup.closeButtonWidth).height(popup.closeButtonHeight).left();
        footer.add(next).width(popup.closeButtonWidth).height(popup.closeButtonHeight).right().expandX();

        add(navPane).width(popup.tutorialNavigationWidth).growY().left().padRight(10f);
        Table right = new Table();
        right.add(articlePane).grow().row();
        right.add(footer).growX().padTop(popup.contentHeaderGap);
        add(right).grow();
        show(0);
    }

    private void show(int sectionIndex) {
        if (sections.isEmpty()) {
            return;
        }
        index = Math.max(0, Math.min(sections.size() - 1, sectionIndex));
        TutorialContent.Section section = sections.get(index);
        article.clearChildren();
        Label title = TextSpec.of(popup.titleStyle)
            .align(TextAlign.LEFT)
            .scale(popup.titleScale)
            .wrap(true)
            .build(section.title);
        Label body = TextSpec.of(popup.bodyStyle)
            .align(TextAlign.LEFT)
            .scale(popup.bodyScale)
            .wrap(true)
            .build(section.body == null ? "" : section.body);
        article.add(title).growX().left().row();
        article.add(body).growX().left().padTop(popup.contentHeaderGap).row();
        articlePane.setScrollY(0f);
        previous.setEnabledState(index > 0);
        next.setEnabledState(index < sections.size() - 1);
        for (int i = 0; i < navButtons.size(); i++) {
            if (i == index) {
                navButtons.get(i).setColor(1.15f, 1.15f, 1.05f, 1f);
            } else {
                navButtons.get(i).setColor(1f, 1f, 1f, 1f);
            }
        }
    }
}
