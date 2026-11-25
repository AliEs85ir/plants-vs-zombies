package app.view.izombie;

import app.view.game.pause.PauseLayout;
import app.view.ui.ModalOverlay;
import app.view.ui.SkinPanel;
import app.view.ui.button.ButtonRow;
import app.view.ui.button.ButtonSpec;
import app.view.ui.text.TextSpec;

import java.util.Arrays;

public final class IZombieInvitePopup extends ModalOverlay {
    public interface Callback {
        void onAccept();

        void onDecline();
    }

    private static final String ID_ACCEPT = "accept";
    private static final String ID_DECLINE = "decline";

    public IZombieInvitePopup(String fromUsername, Callback callback) {
        PauseLayout layout = PauseLayout.defaults();
        SkinPanel root = new SkinPanel(layout.backgrounds.popupRoot);
        root.pad(layout.spacing.contentPadding);
        root.top();

        SkinPanel header = new SkinPanel(layout.backgrounds.header);
        header.pad(layout.spacing.tightPadding);
        header.add(TextSpec.of(layout.text.header).build("I, ZOMBIE INVITE")).grow();
        root.add(header).growX().height(layout.size.headerHeight).row();

        SkinPanel body = new SkinPanel(layout.backgrounds.section);
        body.pad(layout.spacing.innerPadding);
        String name = fromUsername == null || fromUsername.isBlank() ? "A player" : fromUsername;
        body.add(TextSpec.of(layout.text.toggle).build(
            name + " wants to play I, Zombie with you."
        )).growX().row();
        body.add(TextSpec.of(layout.text.statLabel).build(
            "You will both share the same lawn and the same updates."
        )).growX().padTop(layout.spacing.tightPadding).row();
        root.add(body).grow().padTop(layout.spacing.sectionSpacing).row();

        ButtonRow actions = new ButtonRow(Arrays.asList(
            ButtonSpec.of(ID_DECLINE, "Decline")
                .style(layout.buttons.restartStyle)
                .action(() -> {
                    callback.onDecline();
                    close();
                })
                .fixedWidth(),
            ButtonSpec.of(ID_ACCEPT, "Accept")
                .style(layout.buttons.continueStyle)
                .action(() -> {
                    callback.onAccept();
                    close();
                })
                .fixedWidth()
        ), layout.buttons.actions);
        root.add(actions).growX().height(layout.size.footerHeight).padTop(layout.spacing.sectionSpacing);
        add(root).size(layout.size.width * 0.62f, 240f);
    }
}
