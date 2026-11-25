package app.view.hud;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import game.component.AssetManager;
import game.world.chapter.rule.speciallevelRules.LovePlantsRules;

public class LovePlantsHud extends NormalHud{
    private final LovePlantsRules rule;

    private static final float CHALLENGE_BAR_WIDTH = 150;
    private static final float CHALLENGE_BAR_HEIGHT = 100;

    private Label countLabel;

    public LovePlantsHud(LovePlantsRules rule) {
        this.rule = rule;
    }

    @Override
    protected void buildTopBar() {
        Table topBar = new Table();

        addSunCounter(topBar);
        addLovePlantsObjective(topBar);
        addWaveLabel(topBar);

        rootTable.add(topBar)
            .top()
            .growX()
            .height(TOP_BAR_HEIGHT)
            .colspan(3)
            .row();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (countLabel != null && rule != null) {
            int number = Math.max(0, rule.getCount());
            countLabel.setText(number + " Left");
        }
    }

    private void addLovePlantsObjective(Table table) {
        TextureRegion background = AssetManager.textureBank.region("IMAGE_UI_HUD_INGAME_CHALLENGE_BACKGROUND");
        TextureRegion plantPot = AssetManager.textureBank.region("IMAGE_UI_HUD_INGAME_CHALLENGE_PLANT_LOST_ICON");

        Table challengeContainer = new Table();
        challengeContainer.setSize(CHALLENGE_BAR_WIDTH, CHALLENGE_BAR_HEIGHT);

        if (background != null) {
            challengeContainer.setBackground(new TextureRegionDrawable(background));
        }

        if (plantPot != null) {
            challengeContainer.add(new Image(plantPot)).padLeft(-5);
        }

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = AssetManager.skin.getFont("ASHLEYSCRIPTMTSTD");

        countLabel = new Label("", style);
        countLabel.setText(rule != null ? rule.getCount() + " Left" : "0 Left");

        challengeContainer.add(countLabel).padRight(5);

        table.add(challengeContainer).expandX().center();
    }
}
