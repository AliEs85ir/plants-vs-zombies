package app.view.hud;

import app.enums.asset.GameIcon;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import game.component.AssetManager;
import game.world.minigame.beghouled.BeghouledWorld;

public class BeghouledHud extends NormalHud{
    private Label combinationLabel;
    private static final float COMBINATION_BAR_WIDTH = 150;
    private static final float COMBINATION_BAR_HEIGHT = 100;

    @Override
    protected void buildTopBar() {
        Table topBar = new Table();
        topBar.setTouchable(Touchable.childrenOnly);

        addSunCounter(topBar);
        addCombinationCounter(topBar);
        addWaveLabel(topBar);

        rootTable.add(topBar)
            .top()
            .growX()
            .height(TOP_BAR_HEIGHT)
            .colspan(3)
            .row();
    }


    private void addCombinationCounter(Table topBar) {
        TextureRegion fruits = AssetManager.region(GameIcon.FRUITS);
        TextureRegion backGround = AssetManager.textureBank.region("IMAGE_UI_HUD_INGAME_CHALLENGE_BACKGROUND");
        Table comboTable = new Table();
        comboTable.setSize(COMBINATION_BAR_WIDTH, COMBINATION_BAR_HEIGHT);
        if (backGround != null) {
            comboTable.setBackground(new TextureRegionDrawable(backGround));
        }
        if(fruits != null) {
            comboTable.add(new Image(fruits)).padLeft(-5);
        }
        combinationLabel = new Label("", AssetManager.skin);
        comboTable.add(combinationLabel).padLeft(10);
        topBar.add(comboTable).expandX().center();
    }


    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        BeghouledWorld beghouledWorld = (BeghouledWorld) world;
        int count = beghouledWorld.getComboCount();
        int target = beghouledWorld.getComboTarget();
        combinationLabel.setText("Combinations left : " + count + " / " + target);
    }
}
