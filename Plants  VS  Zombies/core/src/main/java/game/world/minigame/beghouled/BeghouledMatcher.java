package game.world.minigame.beghouled;

import app.enums.plant.PlantName;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public final class BeghouledMatcher {
    private static final int[][] ORTHO = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    private BeghouledMatcher() {}

    public static List<BeghouledMatch> findAllMatches(BeghouledBoard board) {
        if (board == null) {
            return List.of();
        }
        Set<BeghouledCell> matched = new LinkedHashSet<>();
        collectLineMatches(board, true, matched);
        collectLineMatches(board, false, matched);
        return groupConnected(board, matched);
    }

    public static boolean wouldSwapCreateMatch(
        BeghouledBoard board,
        int r1,
        int col1,
        int r2,
        int col2
    ) {
        if (board == null || !board.adjacent(r1, col1, r2, col2)) {
            return false;
        }
        if (board.isCrater(r1, col1) || board.isCrater(r2, col2)) {
            return false;
        }
        if (board.get(r1, col1) == null || board.get(r2, col2) == null) {
            return false;
        }
        if (board.get(r1, col1) == board.get(r2, col2)) {
            return false;
        }
        board.swap(r1, col1, r2, col2);
        boolean creates = !findAllMatches(board).isEmpty();
        board.swap(r1, col1, r2, col2);
        return creates;
    }

    public static boolean hasAnyValidMove(BeghouledBoard board) {
        if (board == null) {
            return false;
        }
        for (int row = 0; row < board.rows(); row++) {
            for (int col = 0; col < board.cols(); col++) {
                if (wouldSwapCreateMatch(board, row, col, row, col + 1)
                    || wouldSwapCreateMatch(board, row, col, row + 1, col)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Compacts plants toward row 0, the visually lower lawn row.
     * Crater cells never receive a plant.
     */
    public static void compactColumnTowardBottom(BeghouledBoard board) {
        if (board == null) {
            return;
        }
        for (int col = 0; col < board.cols(); col++) {
            List<PlantName> falling = new ArrayList<>();
            for (int row = 0; row < board.rows(); row++) {
                if (board.isCrater(row, col)) {
                    continue;
                }
                PlantName plant = board.get(row, col);
                if (plant != null) {
                    falling.add(plant);
                    board.set(row, col, null);
                }
            }
            int index = 0;
            for (int row = 0; row < board.rows() && index < falling.size(); row++) {
                if (board.isCrater(row, col)) {
                    continue;
                }
                board.set(row, col, falling.get(index++));
            }
        }
    }

    public static void refillEmpties(BeghouledBoard board, List<PlantName> pool, Random random) {
        if (board == null || pool == null || pool.isEmpty()) {
            return;
        }
        Random rng = random == null ? new Random() : random;
        for (int row = 0; row < board.rows(); row++) {
            for (int col = 0; col < board.cols(); col++) {
                if (board.isEmpty(row, col)) {
                    board.set(row, col, pool.get(rng.nextInt(pool.size())));
                }
            }
        }
    }

    public static void clearMatches(BeghouledBoard board, List<BeghouledMatch> matches) {
        if (board == null || matches == null) {
            return;
        }
        for (BeghouledMatch match : matches) {
            for (BeghouledCell cell : match.cells()) {
                board.set(cell.row(), cell.col(), null);
            }
        }
    }

    public static boolean shuffleNonCraterPlants(BeghouledBoard board, Random random) {
        if (board == null) {
            return false;
        }
        List<PlantName> types = new ArrayList<>();
        List<BeghouledCell> cells = new ArrayList<>();
        for (int row = 0; row < board.rows(); row++) {
            for (int col = 0; col < board.cols(); col++) {
                if (board.isCrater(row, col)) {
                    continue;
                }
                cells.add(new BeghouledCell(row, col));
                types.add(board.get(row, col));
            }
        }
        if (cells.isEmpty()) {
            return false;
        }
        Random rng = random == null ? new Random() : random;
        boolean[] craterSnapshot = craterMask(board);
        for (int attempt = 0; attempt < 80; attempt++) {
            java.util.Collections.shuffle(types, rng);
            applyTypes(board, cells, types);
            if (findAllMatches(board).isEmpty() && hasAnyValidMove(board) && cratersUnchanged(board, craterSnapshot)) {
                return true;
            }
        }
        applyTypes(board, cells, types);
        return cratersUnchanged(board, craterSnapshot);
    }

    public static BeghouledBoard fillWithoutMatches(
        int rows,
        int cols,
        List<PlantName> pool,
        Random random
    ) {
        Random rng = random == null ? new Random() : random;
        List<PlantName> types = pool == null || pool.isEmpty() ? BeghouledPool.BOARD_TYPES : pool;
        BeghouledBoard board = new BeghouledBoard(rows, cols);
        for (int attempt = 0; attempt < 80; attempt++) {
            fillAvoidingMatches(board, types, rng);
            if (findAllMatches(board).isEmpty() && hasAnyValidMove(board)) {
                return board;
            }
            shuffleNonCraterPlants(board, rng);
            if (findAllMatches(board).isEmpty() && hasAnyValidMove(board)) {
                return board;
            }
        }
        return board;
    }

    private static void fillAvoidingMatches(BeghouledBoard board, List<PlantName> types, Random rng) {
        for (int row = 0; row < board.rows(); row++) {
            for (int col = 0; col < board.cols(); col++) {
                PlantName chosen = types.get(rng.nextInt(types.size()));
                for (int tryCount = 0; tryCount < types.size() * 3; tryCount++) {
                    PlantName candidate = types.get(rng.nextInt(types.size()));
                    board.set(row, col, candidate);
                    if (!formsLineMatchAt(board, row, col)) {
                        chosen = candidate;
                        break;
                    }
                    chosen = candidate;
                }
                board.set(row, col, chosen);
            }
        }
    }

    private static boolean formsLineMatchAt(BeghouledBoard board, int row, int col) {
        PlantName type = board.get(row, col);
        if (type == null) {
            return false;
        }
        int horizontal = 1;
        for (int c = col - 1; c >= 0 && board.get(row, c) == type; c--) {
            horizontal++;
        }
        for (int c = col + 1; c < board.cols() && board.get(row, c) == type; c++) {
            horizontal++;
        }
        int vertical = 1;
        for (int r = row - 1; r >= 0 && board.get(r, col) == type; r--) {
            vertical++;
        }
        for (int r = row + 1; r < board.rows() && board.get(r, col) == type; r++) {
            vertical++;
        }
        return horizontal >= 3 || vertical >= 3;
    }

    private static void collectLineMatches(BeghouledBoard board, boolean horizontal, Set<BeghouledCell> matched) {
        int major = horizontal ? board.rows() : board.cols();
        int minor = horizontal ? board.cols() : board.rows();
        for (int a = 0; a < major; a++) {
            int runStart = 0;
            while (runStart < minor) {
                PlantName type = tile(board, a, runStart, horizontal);
                int runEnd = runStart + 1;
                while (runEnd < minor && type != null && tile(board, a, runEnd, horizontal) == type) {
                    runEnd++;
                }
                if (type != null && runEnd - runStart >= 3) {
                    for (int b = runStart; b < runEnd; b++) {
                        matched.add(cell(a, b, horizontal));
                    }
                }
                runStart = runEnd;
            }
        }
    }

    private static PlantName tile(BeghouledBoard board, int a, int b, boolean horizontal) {
        return horizontal ? board.get(a, b) : board.get(b, a);
    }

    private static BeghouledCell cell(int a, int b, boolean horizontal) {
        return horizontal ? new BeghouledCell(a, b) : new BeghouledCell(b, a);
    }

    private static List<BeghouledMatch> groupConnected(BeghouledBoard board, Set<BeghouledCell> matched) {
        Set<BeghouledCell> remaining = new LinkedHashSet<>(matched);
        List<BeghouledMatch> groups = new ArrayList<>();
        while (!remaining.isEmpty()) {
            BeghouledCell start = remaining.iterator().next();
            PlantName type = board.get(start.row(), start.col());
            List<BeghouledCell> group = new ArrayList<>();
            flood(board, start, type, remaining, group);
            if (group.size() >= 3) {
                groups.add(new BeghouledMatch(type, group));
            }
        }
        return groups;
    }

    private static void flood(
        BeghouledBoard board,
        BeghouledCell start,
        PlantName type,
        Set<BeghouledCell> remaining,
        List<BeghouledCell> group
    ) {
        ArrayList<BeghouledCell> stack = new ArrayList<>();
        stack.add(start);
        while (!stack.isEmpty()) {
            BeghouledCell cell = stack.remove(stack.size() - 1);
            if (!remaining.remove(cell)) {
                continue;
            }
            group.add(cell);
            for (int[] delta : ORTHO) {
                BeghouledCell next = new BeghouledCell(cell.row() + delta[0], cell.col() + delta[1]);
                if (remaining.contains(next) && board.get(next.row(), next.col()) == type) {
                    stack.add(next);
                }
            }
        }
    }

    private static boolean[] craterMask(BeghouledBoard board) {
        boolean[] mask = new boolean[board.rows() * board.cols()];
        for (int row = 0; row < board.rows(); row++) {
            for (int col = 0; col < board.cols(); col++) {
                mask[row * board.cols() + col] = board.isCrater(row, col);
            }
        }
        return mask;
    }

    private static boolean cratersUnchanged(BeghouledBoard board, boolean[] mask) {
        for (int row = 0; row < board.rows(); row++) {
            for (int col = 0; col < board.cols(); col++) {
                if (board.isCrater(row, col) != mask[row * board.cols() + col]) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void applyTypes(BeghouledBoard board, List<BeghouledCell> cells, List<PlantName> types) {
        for (int i = 0; i < cells.size(); i++) {
            BeghouledCell cell = cells.get(i);
            board.set(cell.row(), cell.col(), types.get(i));
        }
    }
}
