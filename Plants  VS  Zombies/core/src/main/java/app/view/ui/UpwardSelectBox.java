package app.view.ui;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Generic SelectBox that forces its popup list to open upward.
 * List is stage-rooted (LibGDX default), avoiding ScrollPane clipping and z-order issues.
 * Not hard-coded to a single dropdown — reuse for any filter control.
 */
public final class UpwardSelectBox<T> extends SelectBox<T> {
    private final float popupOffset;
    private final Vector2 tmp = new Vector2();

    public UpwardSelectBox(Skin skin, float popupOffset) {
        super(skin);
        this.popupOffset = Math.max(0f, popupOffset);
        installRepositionListener();
    }

    public UpwardSelectBox(SelectBoxStyle style, float popupOffset) {
        super(style);
        this.popupOffset = Math.max(0f, popupOffset);
        installRepositionListener();
    }

    private void installRepositionListener() {
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // SelectBox shows the list on touch; reposition after layout.
                Stage stage = getStage();
                if (stage != null) {
                    stage.addAction(com.badlogic.gdx.scenes.scene2d.actions.Actions.run(
                        UpwardSelectBox.this::placeListAbove
                    ));
                }
                return false;
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        ScrollPane pane = getScrollPane();
        if (pane != null && pane.getStage() != null && pane.isVisible()) {
            placeListAbove();
        }
    }

    private void placeListAbove() {
        ScrollPane pane = getScrollPane();
        if (pane == null) {
            return;
        }
        Stage stage = pane.getStage();
        if (stage == null) {
            return;
        }
        List<T> list = getList();
        float listHeight = pane.getHeight();
        if (list != null && list.getHeight() > 0f) {
            listHeight = Math.max(listHeight, list.getHeight());
        }
        tmp.set(0f, getHeight() + popupOffset);
        localToStageCoordinates(tmp);
        float x = tmp.x;
        float y = tmp.y;
        // Keep on screen horizontally if possible.
        float stageWidth = stage.getWidth();
        if (x + pane.getWidth() > stageWidth) {
            x = Math.max(0f, stageWidth - pane.getWidth());
        }
        if (x < 0f) {
            x = 0f;
        }
        pane.setPosition(x, y);
        pane.toFront();
        Actor parent = pane.getParent();
        if (parent != null) {
            parent.toFront();
        }
    }
}
