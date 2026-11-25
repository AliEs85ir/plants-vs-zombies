package app.view.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import game.component.AssetManager;
import game.world.chapter.rule.speciallevelRules.TimedWarRules;

import java.util.ArrayList;
import java.util.List;

public class TimedWarHud extends NormalHud{
    private TimedWarRules timedWarRule;
    private List<Image> tickImages;
    private static final float CHALLENGE_BAR_WIDTH = 150;
    private static final float CHALLENGE_BAR_HEIGHT = 100;

    public TimedWarHud(TimedWarRules timedWarRule) {
        this.timedWarRule = timedWarRule;
        this.tickImages = new ArrayList<>();
    }

    @Override
    protected void buildTopBar() {
        Table topBar = new Table();
        topBar.setTouchable(Touchable.childrenOnly);

        addSunCounter(topBar);
        addZombieObjectiveTimer(topBar);
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

        int currentKills = timedWarRule.getZombiesKilled();
        for (int i = 0; i < tickImages.size(); i++) {
            tickImages.get(i).setVisible(i < currentKills);
        }
    }

    private void addZombieObjectiveTimer(Table topBar) {
        TextureRegion zombieHead = AssetManager.textureBank.region("IMAGE_UI_HUD_INGAME_CHALLENGE_ZOMBIE_HEAD");
        TextureRegion backGround = AssetManager.textureBank.region("IMAGE_UI_HUD_INGAME_CHALLENGE_BACKGROUND");
        TextureRegion zombieMeter = AssetManager.textureBank.region("IMAGE_UI_HUD_INGAME_CHALLENGE_ZOMBIE_TICK");

        Table challengeContainer = new Table();
        challengeContainer.setSize(CHALLENGE_BAR_WIDTH, CHALLENGE_BAR_HEIGHT);
        if (backGround != null) {
            challengeContainer.setBackground(new TextureRegionDrawable(backGround));
        }

        if (zombieHead != null) {
            challengeContainer.add(new Image(zombieHead)).padLeft(-5);
        }

        Table meterTable = new Table();

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0, 0, 0, 0.7f));
        pixmap.fill();
        TextureRegionDrawable blackBg = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        meterTable.setBackground(blackBg);
        pixmap.dispose();

        int targetKills = timedWarRule.getTargetKillCount();
        for (int i = 0; i < targetKills; i++) {
            Image tick = new Image(zombieMeter);
            tick.setVisible(false);
            tickImages.add(tick);
            meterTable.add(tick).pad(2);
        }

        challengeContainer.add(meterTable).padRight(15).height(20f);

        topBar.add(challengeContainer).expandX().center();
    }
}
