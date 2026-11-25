package app.view.hud;

import app.view.hud.elements.BossHealthBar;
import game.entity.zombie.Zombie;
import game.world.chapter.rule.BossRule;
import game.world.chapter.rule.speciallevelRules.ConveyorBeltRule;

public class BossHud extends ConveyorBeltHud{
    private static final float BOSS_BAR_TOP_MARGIN = 36f;

    private BossRule bossRule;
    private BossHealthBar bossHealthBar;        // <-- add

    public BossHud(ConveyorBeltRule rule, BossRule bossRule) {
        super(rule);
        this.bossRule = bossRule;
    }

    @Override
    protected void buildUI() {
        super.buildUI(); // conveyor belt builds first -> boss bar lands on top

        bossHealthBar = new BossHealthBar();
        bossHealthBar.setBoss(bossRule.getBoss()); // may be null if boss spawns later
        stage.addActor(bossHealthBar);
        layoutBossBar();
    }

    private void layoutBossBar() {
        if (bossHealthBar == null) return;

        float width = bossHealthBar.getWidth();
        float height = bossHealthBar.getHeight();
        if (width <= 0f) { // fallback if native texture size wasn't available
            width = BossHealthBar.FALLBACK_WIDTH;
            height = BossHealthBar.FALLBACK_HEIGHT;
            bossHealthBar.setSize(width, height);
        }

        float x = (stage.getWidth() - width) / 2f;                             // centered
        float y = stage.getHeight() - height - BOSS_BAR_TOP_MARGIN;            // top
        bossHealthBar.setPosition(x, y);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime); // drives stage.act() -> BossHealthBar.act()

        // BossRule spawns the boss in onChapterStart, which can run after buildUI,
        // so bind the reference as soon as it exists.
        if (bossRule != null && bossHealthBar != null) {
            Zombie boss = bossRule.getBoss();
            if (boss != null && bossHealthBar.getBoss() != boss) {
                bossHealthBar.setBoss(boss); // triggers the empty->full intro
            }
        }
    }
}
