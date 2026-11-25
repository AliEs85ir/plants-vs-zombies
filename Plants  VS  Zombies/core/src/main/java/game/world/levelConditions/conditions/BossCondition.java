package game.world.levelConditions.conditions;

import game.entity.zombie.Zombie;
import game.entity.zombie.boss.boss.Zomboss;
import game.world.GameWorld;
import game.world.chapter.rule.BossRule;
import game.world.chapter.rule.ChapterRule;

public class BossCondition implements LevelCondition {

    @Override
    public LevelOutcome evaluate(GameWorld world) {
        if (StageOutcomeRules.zombieReachedLoseLine(world)) {
            return LevelOutcome.PLAYER_LOST;
        }

        Zombie boss = findBoss(world);
        if (boss == null || !boss.isDead()) {
            return LevelOutcome.NONE;
        }
        if (boss instanceof Zomboss zomboss && !zomboss.isDeathSequenceComplete()) {
            return LevelOutcome.NONE;
        }
//        if (!remainingThreatsCleared(world, boss)) {
//            return LevelOutcome.NONE;
//        }
        return LevelOutcome.PLAYER_WON;
    }

    private Zombie findBoss(GameWorld world) {
        BossRule bossRule = findBossRule(world);
        if (bossRule != null && bossRule.getBoss() != null) {
            return bossRule.getBoss();
        }
        if (world == null || world.getZombies() == null) {
            return null;
        }
        for (Zombie zombie : world.getZombies()) {
            if (zombie instanceof Zomboss boss) {
                return boss;
            }
        }
        return null;
    }

    private BossRule findBossRule(GameWorld world) {
        if (world == null || world.getCurrentChapter() == null || world.getCurrentChapter().getRules() == null) {
            return null;
        }
        for (ChapterRule rule : world.getCurrentChapter().getRules()) {
            if (rule instanceof BossRule bossRule) {
                return bossRule;
            }
        }
        return null;
    }

    private boolean remainingThreatsCleared(GameWorld world, Zombie boss) {
        if (world.getZombies() != null) {
            for (Zombie zombie : world.getZombies()) {
                if (zombie == null || zombie == boss) {
                    continue;
                }
                if (zombie.persistAfterDeath() && zombie.isDead()) {
                    continue;
                }
                if (!zombie.isDead()) {
                    return false;
                }
            }
        }
        return world.getMap() == null
            || world.getMap().getSpawnedZombiesDuringGame() == null
            || world.getMap().getSpawnedZombiesDuringGame().isEmpty();
    }
}
