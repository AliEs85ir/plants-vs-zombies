package app.view.hud.elements;

import app.view.mainmenu.MenuTextures;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import game.component.AssetManager;

public class MioPointBoard extends Table {
    public static final float WIDTH = 300f;
    public static final float HEIGHT = 78f;

    private static final String BOARD_PATH = "ui/scoring/mio_board.png";
    private static final String ICON_PATH = "ui/scoring/mio_icon.png";
    private static final float ICON_SIZE = 52f;
    private static final float BUMP_SCALE = 1.34f;
    private static final float BUMP_UP = 0.12f;
    private static final float BUMP_DOWN = 0.18f;
    private static final Color SCORE_COLOR = Color.valueOf("FFE27A");

    private final Group iconGroup;
    private final Group scoreGroup;
    private final Label pointsLabel;
    private Texture boardTexture;
    private Texture iconTexture;
    private int points;

    public MioPointBoard() {
        setTransform(true);
        setTouchable(Touchable.disabled);
        setOrigin(Align.center);

        TextureRegion boardRegion = loadOwnedBoard();
        if (boardRegion != null) {
            TextureRegionDrawable background = new TextureRegionDrawable(boardRegion);
            background.setMinWidth(WIDTH);
            background.setMinHeight(HEIGHT);
            setBackground(background);
        } else {
            TextureRegion fallback = AssetManager.textureBank.region("IMAGE_UI_STORE_SPROUTSTAB_DOWN");
            if (fallback != null) {
                TextureRegionDrawable background = new TextureRegionDrawable(fallback);
                background.setMinWidth(WIDTH);
                background.setMinHeight(HEIGHT);
                setBackground(background);
            }
        }

        Image icon = new Image(loadOwnedIcon());
        icon.setTouchable(Touchable.disabled);
        icon.setSize(ICON_SIZE, ICON_SIZE);
        iconGroup = wrap(icon, ICON_SIZE, ICON_SIZE);

        Label.LabelStyle style = new Label.LabelStyle(AssetManager.font(AssetManager.DEFAULT_FONT), SCORE_COLOR);
        pointsLabel = new Label("0", style);
        pointsLabel.setAlignment(Align.center);
        pointsLabel.setTouchable(Touchable.disabled);
        pointsLabel.setFontScale(1.15f);
        scoreGroup = wrap(pointsLabel, 120f, ICON_SIZE);

        padLeft(18f).padRight(22f);
        add(iconGroup).size(ICON_SIZE, ICON_SIZE).padRight(10f);
        add(scoreGroup).expandX().center().height(ICON_SIZE);
    }

    public void setPoints(int points, boolean bump) {
        int next = Math.max(0, points);
        boolean changed = next != this.points;
        this.points = next;
        pointsLabel.setText(String.valueOf(this.points));
        if (bump && changed) {
            playBump();
        }
    }

    public int getPoints() {
        return points;
    }

    @Override
    public float getPrefWidth() {
        return WIDTH;
    }

    @Override
    public float getPrefHeight() {
        return HEIGHT;
    }

    @Override
    public float getMinWidth() {
        return WIDTH;
    }

    @Override
    public float getMinHeight() {
        return HEIGHT;
    }

    @Override
    public float getMaxWidth() {
        return WIDTH;
    }

    @Override
    public float getMaxHeight() {
        return HEIGHT;
    }

    public void dispose() {
        if (boardTexture != null) {
            boardTexture.dispose();
            boardTexture = null;
        }
        if (iconTexture != null) {
            iconTexture.dispose();
            iconTexture = null;
        }
    }

    @Override
    public void layout() {
        super.layout();
        originCenter(iconGroup);
        originCenter(scoreGroup);
        if (scoreGroup != null && pointsLabel != null) {
            pointsLabel.setSize(scoreGroup.getWidth(), scoreGroup.getHeight());
        }
        setOrigin(getWidth() / 2f, getHeight() / 2f);
    }

    private void playBump() {
        bump(iconGroup);
        bump(scoreGroup);
    }

    private void bump(Actor actor) {
        if (actor == null) {
            return;
        }
        actor.clearActions();
        actor.setScale(1f);
        actor.addAction(Actions.sequence(
            Actions.scaleTo(BUMP_SCALE, BUMP_SCALE, BUMP_UP, Interpolation.swingOut),
            Actions.scaleTo(1f, 1f, BUMP_DOWN, Interpolation.sine)
        ));
    }

    private static Group wrap(Actor actor, float width, float height) {
        Group group = new Group();
        group.setTransform(true);
        group.setTouchable(Touchable.disabled);
        group.setSize(width, height);
        actor.setSize(width, height);
        actor.setPosition(0f, 0f);
        group.addActor(actor);
        group.setOrigin(width / 2f, height / 2f);
        return group;
    }

    private static void originCenter(Actor actor) {
        if (actor == null) {
            return;
        }
        actor.setOrigin(actor.getWidth() / 2f, actor.getHeight() / 2f);
    }

    private TextureRegion loadOwnedBoard() {
        boardTexture = MenuTextures.load(BOARD_PATH, "MioPointBoard");
        return boardTexture == null ? null : new TextureRegion(boardTexture);
    }

    private TextureRegionDrawable loadOwnedIcon() {
        iconTexture = MenuTextures.load(ICON_PATH, "MioPointBoard");
        if (iconTexture != null) {
            return new TextureRegionDrawable(new TextureRegion(iconTexture));
        }
        TextureRegion fallback = AssetManager.textureBank.region(
            "IMAGE_UI_SEASONS_UNCOMPRESSED_PVZ2_SEASONS_UIASSET_ICON_SUN"
        );
        return fallback == null ? null : new TextureRegionDrawable(fallback);
    }
}
