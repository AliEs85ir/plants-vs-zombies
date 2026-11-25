package app.service.plantselection;

import app.enums.plant.PlantName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

public final class SelectionHelper {

    private SelectionHelper() {}

    public static List<PlantName> randomPlantSelect(List<PlantName> pool, int count) {
        if (pool == null || pool.isEmpty()) {
            throw new IllegalArgumentException("Plant pool must not be empty.");
        }
        if (count <= 0) {
            throw new IllegalArgumentException("Selection count must be positive, got: " + count);
        }
        List<PlantName> distinct = new ArrayList<>(new LinkedHashSet<>(pool));
        if (distinct.size() < count) {
            throw new IllegalArgumentException(
                "Pool size (" + distinct.size() + ") is smaller than required count ("
                + count + ").");
        }
        Collections.shuffle(distinct);
        return Collections.unmodifiableList(new ArrayList<>(distinct.subList(0, count)));
    }
    public static List<String> randomZombieSelect(List<String> pool, int count) {
        return randomZombieSelect(pool, count, new Random());
    }

    public static List<String> randomZombieSelect(List<String> pool, int count, Random random) {
        if (pool == null || pool.isEmpty()) {
            throw new IllegalArgumentException("Plant pool must not be empty.");
        }
        if (count <= 0) {
            throw new IllegalArgumentException("Selection count must be positive, got: " + count);
        }
        List<String> distinct = new ArrayList<>(new LinkedHashSet<>(pool));
        if (distinct.size() < count) {
            throw new IllegalArgumentException(
                "Pool size (" + distinct.size() + ") is smaller than required count ("
                + count + ").");
        }
        Collections.shuffle(distinct, random == null ? new Random() : random);
        return Collections.unmodifiableList(new ArrayList<>(distinct.subList(0, count)));
    }
}
