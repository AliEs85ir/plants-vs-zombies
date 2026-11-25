package app.view.game.result;

import app.view.ui.SkinPanel;
import app.view.ui.button.ButtonRow;
import app.view.ui.button.ButtonSpec;
import game.world.levelConditions.result.StageResult;

public final class LoseResultPopup extends StageResultPopup {
    public interface Callbacks {
        void onRetry();
        void onBuyWin();
        void onBackToStageSelect();
    }

    private final Callbacks callbacks;

    public LoseResultPopup(StageResult result, Callbacks callbacks) {
        super(result, ResultLayout.lose());
        this.callbacks = callbacks != null ? callbacks : new Callbacks() {
            @Override public void onRetry() {}
            @Override public void onBuyWin() {}
            @Override public void onBackToStageSelect() {}
        };
        assemble();
    }

    @Override
    protected String titleText() {
        return layout.texts.title;
    }

    @Override
    protected String messageText() {
        return layout.texts.message;
    }

    @Override
    protected void addBody(SkinPanel root) {
    }

    @Override
    protected ButtonRow createButtons() {
        return buildButtons(
            ButtonSpec.of("back", layout.texts.stageSelect)
                .style(layout.texts.leftButtonStyle)
                .fixedWidth()
                .action(() -> callbacks.onBackToStageSelect()),
            ButtonSpec.of("buy", buyWinLabel())
                .style(layout.texts.middleButtonStyle)
                .fixedWidth()
                .action(() -> callbacks.onBuyWin()),
            ButtonSpec.of("retry", layout.texts.retry)
                .style(layout.texts.rightButtonStyle)
                .fixedWidth()
                .action(() -> callbacks.onRetry())
        );
    }
}
