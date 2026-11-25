package app.model.greenhouse;

import app.enums.greenhouse.PotStatus;
import app.service.greenhouse.GreenhouseGameplaySettings;
import common.model.Position;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Greenhouse
{
    private final GreenhousePot[][] pots;
    private final int row = 4;
    private final int col = 5;
    private LocalDateTime billLastUsedAt;
    private int shopUnlockedTableCount;
    private final ArrayList<LocalDateTime> shovelLastUsedAt = new ArrayList<>();

    public Greenhouse() {
        pots = new GreenhousePot[row][col];
        List<Integer> unlockCosts = resolveUnlockCosts();
        for (int rowCursor = 0; rowCursor < row; rowCursor++) {
            for (int colCursor = 0; colCursor < col; colCursor++) {
                Position position = new Position(colCursor + 1, rowCursor + 1);
                PotStatus potStatus;
                if (rowCursor == 0) {
                    potStatus = PotStatus.EMPTY;
                } else if (rowCursor == 1 || rowCursor == 2) {
                    potStatus = PotStatus.NO_POT;
                } else {
                    potStatus = PotStatus.LOCKED;
                }
                int cost = rowCursor < unlockCosts.size() ? Math.max(0, unlockCosts.get(rowCursor)) : 0;
                pots[rowCursor][colCursor] = new GreenhousePot(position, potStatus, cost);
            }
        }
        billLastUsedAt = null;
        shopUnlockedTableCount = 0;
        ensureShovelList();
    }

    private void ensureShovelList() {
        if (shovelLastUsedAt.isEmpty()) {
            shovelLastUsedAt.add(billLastUsedAt);
        }
    }

    public GreenhousePot[][] getPots()
    {
        return pots;
    }

    public GreenhousePot getPot(int x , int y)
    {
        return pots[y-1][x-1];
    }
    public GreenhousePot getPot(Position position)
    {
        return pots[(int)position.getY()-1][(int)position.getX()-1];
    }

    public int getPotCost(int x , int y)
    {
        return getPot(x , y).getCost();
    }
    public int getPotCost(Position position)
    {
        return getPot(position).getCost();
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public LocalDateTime getBillLastUsedAt() {
        ensureShovelList();
        return shovelLastUsedAt.get(0);
    }

    public void setBillLastUsedAt(LocalDateTime billLastUsedAt) {
        this.billLastUsedAt = billLastUsedAt;
        ensureShovelList();
        shovelLastUsedAt.set(0, billLastUsedAt);
    }

    public int getShopUnlockedTableCount() {
        return shopUnlockedTableCount;
    }

    public void setShopUnlockedTableCount(int shopUnlockedTableCount) {
        this.shopUnlockedTableCount = Math.max(0, shopUnlockedTableCount);
    }

    public List<LocalDateTime> getShovelLastUsedAt() {
        ensureShovelList();
        return new ArrayList<>(shovelLastUsedAt);
    }

    public void setShovelLastUsedAt(List<LocalDateTime> values) {
        shovelLastUsedAt.clear();
        if (values == null || values.isEmpty()) {
            shovelLastUsedAt.add(billLastUsedAt);
            return;
        }
        shovelLastUsedAt.addAll(values);
        billLastUsedAt = shovelLastUsedAt.get(0);
    }

    public int getShovelCount() {
        ensureShovelList();
        return shovelLastUsedAt.size();
    }

    public boolean canAddShovel(int maxShovels) {
        return getShovelCount() < maxShovels;
    }

    public boolean addShovel(int maxShovels) {
        if (!canAddShovel(maxShovels)) {
            return false;
        }
        shovelLastUsedAt.add(null);
        return true;
    }

    public boolean hasLockedTable() {
        for (int rowCursor = 0; rowCursor < row; rowCursor++) {
            for (int colCursor = 0; colCursor < col; colCursor++) {
                if (pots[rowCursor][colCursor].getPotStatus() == PotStatus.LOCKED) {
                    return true;
                }
            }
        }
        return false;
    }

    public int countAvailablePotSlots() {
        int found = 0;
        for (int rowCursor = 0; rowCursor < row; rowCursor++) {
            for (int colCursor = 0; colCursor < col; colCursor++) {
                if (pots[rowCursor][colCursor].getPotStatus() == PotStatus.NO_POT) {
                    found++;
                }
            }
        }
        return found;
    }

    public boolean canUnlockTableViaShop(int maxShopTables) {
        return shopUnlockedTableCount < maxShopTables && hasLockedTable();
    }

    public boolean unlockTableViaShop() {
        for (int rowCursor = 0; rowCursor < row; rowCursor++) {
            for (int colCursor = 0; colCursor < col; colCursor++) {
                GreenhousePot pot = pots[rowCursor][colCursor];
                if (pot.getPotStatus() == PotStatus.LOCKED) {
                    pot.unlock();
                    shopUnlockedTableCount++;
                    return true;
                }
            }
        }
        return false;
    }

    public void reduceAllShovelTimers(double percent, Duration minimumRemaining, float cooldownHours) {
        ensureShovelList();
        LocalDateTime now = LocalDateTime.now();
        Duration fullCooldown = Duration.ofSeconds(Math.max(1L, Math.round(cooldownHours * 3600d)));
        for (int index = 0; index < shovelLastUsedAt.size(); index++) {
            LocalDateTime lastUsed = shovelLastUsedAt.get(index);
            if (lastUsed == null) {
                continue;
            }
            LocalDateTime readyAt = lastUsed.plus(fullCooldown);
            if (!now.isBefore(readyAt)) {
                shovelLastUsedAt.set(index, null);
                continue;
            }
            Duration remaining = Duration.between(now, readyAt);
            long reducedSeconds = Math.round(remaining.getSeconds() * (percent / 100d));
            Duration newRemaining = remaining.minusSeconds(reducedSeconds);
            if (newRemaining.compareTo(minimumRemaining) < 0) {
                newRemaining = minimumRemaining;
            }
            if (newRemaining.isZero() || newRemaining.isNegative()) {
                shovelLastUsedAt.set(index, null);
            } else {
                LocalDateTime adjustedLastUsed = readyAt.minus(newRemaining);
                shovelLastUsedAt.set(index, adjustedLastUsed);
            }
        }
        billLastUsedAt = shovelLastUsedAt.get(0);
    }

    public boolean isPotAvailable(int count) {
        if (count <= 0) {
            return true;
        }
        return countAvailablePotSlots() >= count;
    }

    public void addPot() {
        for (int rowCursor = 0; rowCursor < row; rowCursor++) {
            for (int colCursor = 0; colCursor < col; colCursor++) {
                GreenhousePot pot = pots[rowCursor][colCursor];
                if (pot.getPotStatus() == PotStatus.NO_POT) {
                    pot.addPhysicalPot();
                    return;
                }
            }
        }
    }

    private static List<Integer> resolveUnlockCosts() {
        try {
            ArrayList<Integer> configured = GreenhouseGameplaySettings.current().sourceConfig().grid.unlockCosts;
            if (configured != null && !configured.isEmpty()) {
                return configured;
            }
        } catch (Throwable ignored) {
        }
        ArrayList<Integer> fallback = new ArrayList<>();
        fallback.add(0);
        fallback.add(300);
        fallback.add(500);
        fallback.add(750);
        return fallback;
    }
}
