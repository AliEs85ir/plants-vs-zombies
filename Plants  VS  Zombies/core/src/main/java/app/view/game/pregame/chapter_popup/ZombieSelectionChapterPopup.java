package app.view.game.pregame.chapter_popup;

import app.view.game.pregame.ChapterFact;
import app.view.game.pregame.PregameLayout;
import app.view.game.pregame.zombie.ZombieSelectionState;
import app.view.ui.HintTooltip;
import app.view.ui.SkinPanel;
import app.view.ui.button.ActionButton;
import app.view.ui.button.ButtonRow;
import app.view.ui.button.ButtonSpec;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.Arrays;
import java.util.List;

public class ZombieSelectionChapterPopup extends ChapterIntroPopup implements HintTooltip.Sink{
    private static final String ID_RANDOM      = "random";
    private static final String ID_MANUAL      = "manual";

    private static final String HINT_RANDOM =
        "Selects random zombies from all zombies available in this chapter.";
    private static final String HINT_MANUAL =
        "Lets you manually choose the zombies you want to use.";
    private static final String HINT_IDLE =
        "Hover an option to see what it does.";

    public interface Callback {
        void onRandomChosen(List<String> zombies);
        void onManualChosen();
    }

    private final ZombieSelectionState state;
    private final ZombieSelectionChapterPopup.Callback callback;

    private Label hintLabel;
    private ButtonRow actions;

    public ZombieSelectionChapterPopup(ZombieSelectionState state, PregameLayout layout, ZombieSelectionChapterPopup.Callback callback) {
        super(state.getChapter(), layout);
        this.state = state;
        this.callback = callback;

        build();

        attachHint(ID_RANDOM, HINT_RANDOM);
        attachHint(ID_MANUAL, HINT_MANUAL);
        actions.setEnabled(ID_MANUAL, true);
    }

    @Override
    protected Table buildFactRow() {
        Table row = new Table();

        List<ChapterFact> facts = ChapterFact.defaultFacts(
            state.getChapter(), state.getCapacity(),
            state.getAllowedZombies().size(), layout.intro.maxFactCards);

        boolean first = true;
        for (ChapterFact fact : facts) {
            Cell<SkinPanel> cell = row.add(buildFactCard(fact))
                .growX()
                .uniformX()
                .minWidth(layout.intro.factCardMinWidth)
                .height(layout.intro.factCardHeight);
            if (!first) {
                cell.padLeft(layout.intro.factCardGap);
            }
            first = false;
        }
        return row;
    }

    private SkinPanel buildFactCard(ChapterFact fact) {
        SkinPanel card = new SkinPanel(layout.backgrounds.factCard);
        card.pad(layout.spacing.tightPadding);
        card.add(TextSpec.of(layout.text.fact).build(fact.getText())).grow();
        return card;
    }

    @Override
    protected Table buildHintBar() {
        SkinPanel bar = new SkinPanel(layout.backgrounds.tooltip);
        bar.pad(layout.spacing.tightPadding);

        hintLabel = TextSpec.of(layout.text.hint).build(HINT_IDLE);
        bar.add(hintLabel).grow();
        return bar;
    }

    @Override
    protected Table buildFooter() {
        List<ButtonSpec> specs = Arrays.asList(
            ButtonSpec.of(ID_RANDOM, "Random")
                .style(layout.buttons.secondaryStyle)
                .action(this::chooseRandom),
            ButtonSpec.of(ID_MANUAL, "Choose Zombies")
                .style(layout.buttons.primaryStyle)
                .action(this::chooseManual)
        );

        actions = new ButtonRow(specs, layout.buttons.introFooter);
        return actions;
    }

    private void attachHint(String id, String hint) {
        ActionButton button = actions.get(id);
        if (button != null) {
            HintTooltip.attach(button, hint, this);
        }
    }

    @Override
    public void showHint(String text) {
        if (hintLabel != null) {
            hintLabel.setText(text);
            hintLabel.setColor(layout.text.hintActiveColor);
        }
    }

    @Override
    public void clearHint() {
        if (hintLabel != null) {
            hintLabel.setText(HINT_IDLE);
            hintLabel.setColor(layout.text.hintIdleColor);
        }
    }

    private void chooseRandom() {
        state.applyRandom();
        String error = state.validateForStart();
        if (error != null) {
            showError(error);
            return;
        }
        callback.onRandomChosen(state.getSelected());
    }

    private void chooseManual() {
        callback.onManualChosen();
    }
}
