package app.view.loading.icons.popup;

import app.view.loading.icons.config.StartIconsConfig;
import app.view.loading.icons.content.FaqContent;
import app.view.ui.button.ActionButton;
import app.view.ui.text.TextAlign;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.ArrayList;
import java.util.List;

public final class FaqAccordion extends Table {
    private final StartIconsConfig.PopupConfig popup;
    private final String collapsedPrefix;
    private final String expandedPrefix;
    private final List<Entry> entries = new ArrayList<>();
    private int openIndex = -1;

    public FaqAccordion(FaqContent content, StartIconsConfig.PopupConfig popup) {
        this.popup = popup;
        this.collapsedPrefix = content.collapsedPrefix == null ? "▸ " : content.collapsedPrefix;
        this.expandedPrefix = content.expandedPrefix == null ? "▾ " : content.expandedPrefix;
        top().left();
        defaults().growX();
        if (content.hint != null && !content.hint.isBlank()) {
            add(body(content.hint)).left().padBottom(popup.faqItemSpacing).row();
        }
        if (content.items == null) {
            return;
        }
        for (FaqContent.Item item : content.items) {
            if (item == null || !item.enabled || item.question == null || item.question.isBlank()) {
                continue;
            }
            int index = entries.size();
            ActionButton question = new ActionButton(collapsedPrefix + item.question, popup.tabStyle);
            question.getLabel().setWrap(true);
            question.getLabel().setFontScale(popup.bodyScale);
            question.onClick(() -> toggle(index));
            AnswerBox answer = new AnswerBox();
            answer.top().left();
            answer.add(body(item.answer == null ? "" : item.answer)).growX().left();
            answer.setExpanded(false);
            add(question).left().padTop(popup.faqItemSpacing).row();
            add(answer).left().padTop(4f).padLeft(popup.faqAnswerIndent).row();
            entries.add(new Entry(question, answer, item.question));
        }
    }

    private void toggle(int index) {
        setOpen(openIndex == index ? -1 : index);
    }

    private void setOpen(int index) {
        openIndex = index;
        for (int i = 0; i < entries.size(); i++) {
            Entry entry = entries.get(i);
            boolean open = i == openIndex;
            entry.answer.setExpanded(open);
            entry.question.setText((open ? expandedPrefix : collapsedPrefix) + entry.questionText);
        }
        invalidateHierarchy();
    }

    private Label body(String text) {
        return TextSpec.of(popup.bodyStyle)
            .align(TextAlign.LEFT)
            .scale(popup.bodyScale)
            .wrap(true)
            .build(text);
    }

    private static final class Entry {
        final ActionButton question;
        final AnswerBox answer;
        final String questionText;

        Entry(ActionButton question, AnswerBox answer, String questionText) {
            this.question = question;
            this.answer = answer;
            this.questionText = questionText;
        }
    }

    private static final class AnswerBox extends Table {
        private boolean expanded;

        void setExpanded(boolean expanded) {
            this.expanded = expanded;
            setVisible(expanded);
            invalidateHierarchy();
        }

        @Override
        public float getPrefHeight() {
            return expanded ? super.getPrefHeight() : 0f;
        }

        @Override
        public float getMinHeight() {
            return getPrefHeight();
        }

        @Override
        public float getMaxHeight() {
            return expanded ? super.getMaxHeight() : 0f;
        }
    }
}
