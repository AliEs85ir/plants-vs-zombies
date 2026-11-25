package game.service.plant;

import app.enums.effect.EffectType;
import game.engine.time.GameClock;
import game.entity.DamageContext;
import game.entity.DeathStyle;
import game.entity.Entity;
import game.entity.effect.FireEffect;
import game.entity.effect.FreezeEffect;
import game.entity.effect.PoisonEffect;
import game.entity.effect.SlowEffect;
import game.entity.plant.ability.PlantTargeting;
import game.entity.zombie.Zombie;
import game.world.GameWorld;
import game.world.playground.Cell;
import game.world.playground.Grid;

import java.util.ArrayList;
import java.util.List;

public class AttackService {
    public static final int STANDARD_FIRE_DURATION_TICKS = 4 * GameClock.TICKS_PER_SECOND;

    private final GameWorld gameWorld;
    private final Grid grid;

    public AttackService(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        this.grid = gameWorld.getGrid();
    }

    public boolean hasActiveZombieInCell(Cell cell) {
        return PlantTargeting.firstZombieInCell(gameWorld, cell) != null;
    }

    public Zombie getFrontZombie(List<Zombie> zombies) {
        if (zombies == null || zombies.isEmpty()) return null;
        Zombie frontZombie = null;
        for (Zombie z : zombies) {
            if (z != null && !z.isDead()) {
                if (frontZombie == null || z.getPosition().getX() < frontZombie.getPosition().getX()) {
                    frontZombie = z;
                }
            }
        }
        return frontZombie;
    }

    public boolean hasZombiesInArea(int centerRow, int centerCol, int radius) {
        return !PlantTargeting.zombiesInChebyshevArea(gameWorld, centerRow, centerCol, radius).isEmpty();
    }

    public List<Zombie> zombiesInCell(Cell cell) {
        return PlantTargeting.zombiesInCell(gameWorld, cell);
    }

    public List<Zombie> zombiesInArea(int centerRow, int centerCol, int radius) {
        return PlantTargeting.zombiesInChebyshevArea(gameWorld, centerRow, centerCol, radius);
    }

    public void damageZombie(Zombie zombie, long currentTick, int damage) {
        damageZombie(zombie, currentTick, damage, DeathStyle.NORMAL);
    }

    public void damageZombie(Zombie zombie, long currentTick, int damage, DeathStyle deathStyle) {
        damageZombie(zombie, currentTick, damage, deathStyle, null);
    }

    public void damageZombie(Zombie zombie, long currentTick, int damage, DeathStyle deathStyle, Entity attacker) {
        if (zombie == null || zombie.isDead()) return;
        DamageContext context = new DamageContext(attacker, damage, false, deathStyle);
        zombie.takeDamage(context, currentTick);
        gameWorld.addEvent(zombie.getName() + " took " + damage + " damage.");
    }

    public void killZombie(Zombie zombie, long currentTick, Entity attacker) {
        if (zombie == null || zombie.isDead()) {
            return;
        }
        zombie.takeDamage(new DamageContext(attacker, Integer.MAX_VALUE, true, DeathStyle.NORMAL), currentTick);
        if (!zombie.isDead()) {
            zombie.die();
        }
        gameWorld.addEvent(zombie.getName() + " was swallowed.");
    }

    public void damageFrontZombie(List<Zombie> zombies, long currentTick, int damage) {
        Zombie frontZombie = getFrontZombie(zombies);
        if (frontZombie != null) {
            damageZombie(frontZombie, currentTick, damage);
        }
    }

    public void damageZombiesInCell(int row, int col, int damage, long currentTick) {
        damageZombiesInCell(row, col, damage, currentTick, DeathStyle.NORMAL);
    }

    public void damageZombiesInCell(int row, int col, int damage, long currentTick, DeathStyle deathStyle) {
        Cell cell = grid.getCell(row, col);
        damageZombies(PlantTargeting.zombiesInCell(gameWorld, cell), currentTick, damage, deathStyle);
    }

    public void damageZombiesInRow(int row, int damage, long currentTick) {
        damageZombiesInRow(row, damage, currentTick, DeathStyle.NORMAL);
    }

    public void damageZombiesInRow(int row, int damage, long currentTick, DeathStyle deathStyle) {
        damageZombies(PlantTargeting.zombiesInRow(gameWorld, row), currentTick, damage, deathStyle);
    }

    public void damageArea(int centerRow, int centerCol, int radius, int damage, long currentTick) {
        damageArea(centerRow, centerCol, radius, damage, currentTick, DeathStyle.NORMAL);
    }

    public void damageArea(int centerRow, int centerCol, int radius, int damage, long currentTick, DeathStyle deathStyle) {
        damageArea(centerRow, centerCol, radius, damage, currentTick, deathStyle, 0f);
    }

    public void damageArea(
        int centerRow,
        int centerCol,
        int radius,
        int damage,
        long currentTick,
        DeathStyle deathStyle,
        float areaPercent
    ) {
        damageZombies(
            PlantTargeting.zombiesInChebyshevArea(gameWorld, centerRow, centerCol, radius, areaPercent),
            currentTick,
            damage,
            deathStyle
        );
    }

    public void damageAll(int damage, long currentTick, DeathStyle deathStyle) {
        damageZombies(PlantTargeting.zombiesInChebyshevArea(gameWorld, 0, 0, -1), currentTick, damage, deathStyle);
    }

    private void applyFireEffect(Zombie zombie, long currentTick, int fireDuration) {
        if (zombie == null || zombie.isDead()) {
            return;
        }
        zombie.addEffect(new FireEffect((float) fireDuration / GameClock.TICKS_PER_SECOND));
    }

    public void heatZombie(Zombie zombie, int fireDurationTicks) {
        applyFireEffect(zombie, GameClock.currentTick, fireDurationTicks);
    }

    public void heatCell(Cell cell, int fireDurationTicks) {
        if (cell == null) {
            return;
        }
        long tick = GameClock.currentTick;
        for (Zombie zombie : new ArrayList<>(PlantTargeting.zombiesInCell(gameWorld, cell))) {
            applyFireEffect(zombie, tick, fireDurationTicks);
        }
    }

    public void damageFrontZombieWithFire(List<Zombie> zombies, long currentTick, int damage, int fireDuration) {
        Zombie frontZombie = getFrontZombie(zombies);
        if (frontZombie != null) {
            applyFireEffect(frontZombie, currentTick, fireDuration);
            damageZombie(frontZombie, currentTick, damage, DeathStyle.ASH);
        }
    }

    public void damageZombiesInCellWithFire(int row, int col, int damage, long currentTick, int fireDuration) {
        Cell cell = grid.getCell(row, col);
        for (Zombie zombie : new ArrayList<>(PlantTargeting.zombiesInCell(gameWorld, cell))) {
            applyFireEffect(zombie, currentTick, fireDuration);
            damageZombie(zombie, currentTick, damage, DeathStyle.ASH);
        }
    }

    public void damageZombiesInRowWithFire(int row, int damage, long currentTick, int fireDuration) {
        for (Zombie zombie : new ArrayList<>(PlantTargeting.zombiesInRow(gameWorld, row))) {
            applyFireEffect(zombie, currentTick, fireDuration);
            damageZombie(zombie, currentTick, damage, DeathStyle.ASH);
        }
    }

    public void damageAreaWithFire(int centerRow, int centerCol, int radius, int damage, long currentTick, int fireDuration) {
        for (int r = centerRow - radius; r <= centerRow + radius; r++) {
            for (int c = centerCol - radius; c <= centerCol + radius; c++) {
                damageZombiesInCellWithFire(r, c, damage, currentTick, fireDuration);
            }
        }
    }

    public void damageFrontZombieWithIce(List<Zombie> zombies, long currentTick, int damage, double slowFactor, int iceDuration) {
        Zombie frontZombie = getFrontZombie(zombies);
        if (frontZombie != null) {
            frontZombie.addEffect(new SlowEffect(
                (float) iceDuration / GameClock.TICKS_PER_SECOND,
                (float) slowFactor, EffectType.ICE));
            damageZombie(frontZombie, currentTick, damage);
        }
    }

    public void damageZombiesInCellWithIce(int row, int col, int damage, long currentTick, double slowFactor, int iceDuration) {
        Cell cell = grid.getCell(row, col);
        for (Zombie zombie : new ArrayList<>(PlantTargeting.zombiesInCell(gameWorld, cell))) {
            zombie.addEffect(new SlowEffect(
                (float) iceDuration / GameClock.TICKS_PER_SECOND,
                (float) slowFactor, EffectType.ICE));
            damageZombie(zombie, currentTick, damage);
        }
    }

    public void damageZombiesInRowWithIce(int row, int damage, long currentTick, double slowFactor, int iceDuration) {
        for (Zombie zombie : new ArrayList<>(PlantTargeting.zombiesInRow(gameWorld, row))) {
            zombie.addEffect(new SlowEffect(
                (float) iceDuration / GameClock.TICKS_PER_SECOND,
                (float) slowFactor, EffectType.ICE));
            damageZombie(zombie, currentTick, damage);
        }
    }

    public void damageAreaWithIce(int centerRow, int centerCol, int radius, int damage, long currentTick, double slowFactor, int iceDuration) {
        for (int r = centerRow - radius; r <= centerRow + radius; r++) {
            for (int c = centerCol - radius; c <= centerCol + radius; c++) {
                damageZombiesInCellWithIce(r, c, damage, currentTick, slowFactor, iceDuration);
            }
        }
    }

    public void damageFrontZombieWithPoison(List<Zombie> zombies, long currentTick, int damage, int poisonDuration, int damagePerTick) {
        Zombie frontZombie = getFrontZombie(zombies);
        if (frontZombie != null) {
            frontZombie.addEffect(new PoisonEffect((float) poisonDuration / GameClock.TICKS_PER_SECOND, damagePerTick));
            damageZombie(frontZombie, currentTick, damage);
        }
    }

    public void damageZombiesInCellWithPoison(int row, int col, int damage, long currentTick, int poisonDuration, int damagePerTick) {
        Cell cell = grid.getCell(row, col);
        for (Zombie zombie : new ArrayList<>(PlantTargeting.zombiesInCell(gameWorld, cell))) {
            zombie.addEffect(new PoisonEffect((float) poisonDuration / GameClock.TICKS_PER_SECOND, damagePerTick));
            damageZombie(zombie, currentTick, damage);
        }
    }

    public void damageZombiesInRowWithPoison(int row, int damage, long currentTick, int poisonDuration, int damagePerTick) {
        for (Zombie zombie : new ArrayList<>(PlantTargeting.zombiesInRow(gameWorld, row))) {
            zombie.addEffect(new PoisonEffect((float) poisonDuration / GameClock.TICKS_PER_SECOND, damagePerTick));
            damageZombie(zombie, currentTick, damage);
        }
    }

    public void damageAreaWithPoison(int centerRow, int centerCol, int radius, int damage, long currentTick, int poisonDuration, int damagePerTick) {
        for (int r = centerRow - radius; r <= centerRow + radius; r++) {
            for (int c = centerCol - radius; c <= centerCol + radius; c++) {
                damageZombiesInCellWithPoison(r, c, damage, currentTick, poisonDuration, damagePerTick);
            }
        }
    }

    public void freezeZombie(Zombie zombie, long currentTick, int freezeDurationTicks) {
        if (zombie == null || zombie.isDead()) return;
        zombie.addEffect(new FreezeEffect(
            (float) freezeDurationTicks / GameClock.TICKS_PER_SECOND)
        );
        gameWorld.addEvent(zombie.getName() + " is completely frozen.");
    }

    public void freezeZombieInCell(Cell cell, long currentTick, int freezeDurationTicks) {
        Zombie zombie = PlantTargeting.firstZombieInCell(gameWorld, cell);
        if (zombie != null) {
            freezeZombie(zombie, currentTick, freezeDurationTicks);
        }
    }

    public void freezeZombiesInMap(long currentTick, int freezeDurationTicks) {
        for (Zombie zombie : new ArrayList<>(PlantTargeting.zombiesInChebyshevArea(gameWorld, 0, 0, -1))) {
            freezeZombie(zombie, currentTick, freezeDurationTicks);
        }
    }

    public void freezeZombiesInRow(long currentTick, int freezeDurationTicks, int row) {
        for (Zombie zombie : new ArrayList<>(PlantTargeting.zombiesInRow(gameWorld, row))) {
            freezeZombie(zombie, currentTick, freezeDurationTicks);
        }
    }

    public void slowZombiesInRow(long currentTick, int durationTicks, int row, double slowFactor) {
        float durationSeconds = (float) durationTicks / GameClock.TICKS_PER_SECOND;
        for (Zombie zombie : new ArrayList<>(PlantTargeting.zombiesInRow(gameWorld, row))) {
            if (zombie == null || zombie.isDead()) {
                continue;
            }
            zombie.removeEffect(EffectType.FIRE);
            zombie.removeEffect(EffectType.BURN);
            zombie.addEffect(new SlowEffect(durationSeconds, slowFactor, EffectType.ICE));
        }
    }

    public Zombie getClosestZombieInFront(int row, double plantX, double maxDistanceX) {
        List<Zombie> zombies = gameWorld.getZombiesInRow(row);
        if (zombies == null) return null;

        Zombie closest = null;
        for (Zombie z : zombies) {
            if (z != null && !z.isDead()) {
                double zx = z.getPosition().getX();
                if (zx >= plantX && zx <= plantX + maxDistanceX) {
                    if (closest == null || zx < closest.getPosition().getX()) {
                        closest = z;
                    }
                }
            }
        }
        return closest;
    }

    public Zombie getClosestZombieBehind(int row, double plantX, double maxDistanceX) {
        List<Zombie> zombies = gameWorld.getZombiesInRow(row);
        if (zombies == null) return null;

        Zombie closest = null;
        for (Zombie z : zombies) {
            if (z != null && !z.isDead()) {
                double zx = z.getPosition().getX();
                if (zx < plantX && zx >= plantX - maxDistanceX) {
                    if (closest == null || zx > closest.getPosition().getX()) {
                        closest = z;
                    }
                }
            }
        }
        return closest;
    }

    public Zombie getClosestZombieInFront(int centerRow, int centerCol, int radius) {
        return closestInBand(centerRow, centerCol, radius, 0, radius, -radius, radius);
    }

    public Zombie getClosestZombieBehind(int centerRow, int centerCol, int radius) {
        return closestInBand(centerRow, centerCol, radius, -radius, -1, -radius, radius);
    }

    public Zombie getClosestZombieAbove(int centerRow, int centerCol, int radius) {
        return closestInBand(centerRow, centerCol, radius, -radius, radius, -radius, -1);
    }

    public Zombie getClosestZombieBelow(int centerRow, int centerCol, int radius) {
        return closestInBand(centerRow, centerCol, radius, -radius, radius, 1, radius);
    }

    private Zombie closestInBand(int centerRow, int centerCol, int radius, int minColOffset, int maxColOffset, int minRowOffset, int maxRowOffset) {
        Zombie closest = null;
        double minDistance = Double.MAX_VALUE;
        for (Zombie zombie : PlantTargeting.zombiesInChebyshevArea(gameWorld, centerRow, centerCol, radius)) {
            Cell cell = PlantTargeting.cellOf(gameWorld, zombie);
            if (cell == null) {
                continue;
            }
            int rowOffset = cell.getRow() - centerRow;
            int colOffset = cell.getCol() - centerCol;
            if (rowOffset < minRowOffset || rowOffset > maxRowOffset || colOffset < minColOffset || colOffset > maxColOffset) {
                continue;
            }
            double dist = Math.hypot(rowOffset, colOffset);
            if (dist < minDistance) {
                minDistance = dist;
                closest = zombie;
            }
        }
        return closest;
    }

    private double getZombieDistance(Zombie zombie, int centerRow, int centerCol, int radius) {
        if (zombie == null) return Double.MAX_VALUE;
        Cell cell = PlantTargeting.cellOf(gameWorld, zombie);
        if (cell == null) return Double.MAX_VALUE;
        if (Math.abs(cell.getRow() - centerRow) > radius || Math.abs(cell.getCol() - centerCol) > radius) {
            return Double.MAX_VALUE;
        }
        return Math.hypot(cell.getRow() - centerRow, cell.getCol() - centerCol);
    }

    public Zombie getClosestZombieFromSides(int centerRow, int centerCol, int radius) {
        Zombie above = getClosestZombieAbove(centerRow, centerCol, radius);
        Zombie below = getClosestZombieBelow(centerRow, centerCol, radius);
        double distAbove = getZombieDistance(above, centerRow, centerCol, radius);
        double distBelow = getZombieDistance(below, centerRow, centerCol, radius);
        return distAbove <= distBelow ? above : below;
    }

    public Zombie getClosestZombieFromFrontAndBehind(int centerRow, int centerCol, int radius) {
        Zombie front = getClosestZombieInFront(centerRow, centerCol, radius);
        Zombie behind = getClosestZombieBehind(centerRow, centerCol, radius);
        double distFront = getZombieDistance(front, centerRow, centerCol, radius);
        double distBehind = getZombieDistance(behind, centerRow, centerCol, radius);
        return distFront <= distBehind ? front : behind;
    }

    public void damageClosestZombieInRadius(int centerRow, int centerCol, int radius, int damage, long currentTick) {
        Zombie sides = getClosestZombieFromSides(centerRow, centerCol, radius);
        Zombie frontBehind = getClosestZombieFromFrontAndBehind(centerRow, centerCol, radius);
        double distSides = getZombieDistance(sides, centerRow, centerCol, radius);
        double distFrontBehind = getZombieDistance(frontBehind, centerRow, centerCol, radius);

        Zombie closest = distSides <= distFrontBehind ? sides : frontBehind;
        if (closest != null) {
            damageZombie(closest, currentTick, damage);
        }
    }

    private void damageZombies(List<Zombie> zombies, long currentTick, int damage, DeathStyle deathStyle) {
        if (zombies == null) {
            return;
        }
        for (Zombie zombie : new ArrayList<>(zombies)) {
            damageZombie(zombie, currentTick, damage, deathStyle);
        }
    }
}
