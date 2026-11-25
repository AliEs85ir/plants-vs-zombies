package game.world.minigame.beghouled;

import app.enums.plant.PlantName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BeghouledMatcherTest {
    private static final PlantName P = PlantName.PEASHOOTER;
    private static final PlantName S = PlantName.SUNFLOWER;
    private static final PlantName W = PlantName.WALL_NUT;
    private static final PlantName N = PlantName.SNOW_PEA;
    private static final PlantName R = PlantName.REPEATER;

    @Test
    void validSwapCreatesHorizontalMatch() {
        BeghouledBoard board = board(new PlantName[][]{
            {P, P, S, W},
            {S, W, P, N},
            {W, N, R, S}
        });
        assertTrue(BeghouledMatcher.wouldSwapCreateMatch(board, 0, 2, 1, 2));
        assertFalse(BeghouledMatcher.wouldSwapCreateMatch(board, 0, 0, 0, 1));
    }

    @Test
    void invalidSwapDoesNotCreateAMatch() {
        BeghouledBoard board = board(new PlantName[][]{
            {P, S, W, N},
            {S, W, N, R},
            {W, N, R, P}
        });
        PlantName before = board.get(0, 0);
        assertFalse(BeghouledMatcher.wouldSwapCreateMatch(board, 0, 0, 0, 1));
        assertFalse(BeghouledMatcher.wouldSwapCreateMatch(board, 0, 0, 2, 0));
        assertEquals(before, board.get(0, 0));
        assertEquals(S, board.get(0, 1));
    }

    @Test
    void findsHorizontalVerticalAndLTOverlapAsOneGroup() {
        BeghouledBoard board = board(new PlantName[][]{
            {P, P, P, S},
            {W, P, N, R},
            {S, P, W, N}
        });
        List<BeghouledMatch> matches = BeghouledMatcher.findAllMatches(board);
        assertEquals(1, matches.size());
        assertEquals(5, matches.get(0).length());
    }

    @Test
    void cascadeScoringAddsAnExtraPackPerGroup() {
        BeghouledMatch three = new BeghouledMatch(P, List.of(
            new BeghouledCell(0, 0), new BeghouledCell(0, 1), new BeghouledCell(0, 2)
        ));
        BeghouledMatch five = new BeghouledMatch(S, List.of(
            new BeghouledCell(1, 0), new BeghouledCell(1, 1), new BeghouledCell(1, 2),
            new BeghouledCell(1, 3), new BeghouledCell(2, 1)
        ));
        BeghouledScore swapScore = BeghouledScorer.score(List.of(three), false);
        assertEquals(1, swapScore.packs());
        assertEquals(1, swapScore.combos());
        assertEquals(50, swapScore.sun());

        BeghouledScore cascadeScore = BeghouledScorer.score(List.of(five), true);
        assertEquals(4, cascadeScore.packs());
        assertEquals(1, cascadeScore.combos());
        assertEquals(200, cascadeScore.sun());
    }

    @Test
    void compactFallsTowardRowZeroAndSkipsCraters() {
        BeghouledBoard board = board(new PlantName[][]{
            {null, P, null},
            {S, null, W},
            {N, R, null}
        });
        board.setCrater(0, 0, true);
        BeghouledMatcher.compactColumnTowardBottom(board);
        assertTrue(board.isCrater(0, 0));
        assertEquals(S, board.get(1, 0));
        assertEquals(N, board.get(2, 0));
        assertEquals(P, board.get(0, 1));
        assertEquals(R, board.get(1, 1));
        assertEquals(W, board.get(0, 2));
    }

    @Test
    void refillNeverWritesOntoCraters() {
        BeghouledBoard board = new BeghouledBoard(2, 2);
        board.setCrater(0, 1, true);
        BeghouledMatcher.refillEmpties(board, BeghouledPool.BOARD_TYPES, new Random(1));
        assertTrue(board.isCrater(0, 1));
        assertEquals(null, board.get(0, 1));
        assertTrue(board.get(0, 0) != null);
        assertTrue(board.get(1, 0) != null);
        assertTrue(board.get(1, 1) != null);
    }

    @Test
    void shuffleKeepsCratersAndCreatesAMoveWhenPossible() {
        BeghouledBoard board = board(new PlantName[][]{
            {P, S, W, N, R},
            {S, W, N, R, P},
            {W, N, R, P, S},
            {N, R, P, S, W}
        });
        board.setCrater(1, 2, true);
        boolean shuffled = BeghouledMatcher.shuffleNonCraterPlants(board, new Random(3));
        assertTrue(shuffled);
        assertTrue(board.isCrater(1, 2));
        assertEquals(null, board.get(1, 2));
        assertTrue(BeghouledMatcher.hasAnyValidMove(board) || BeghouledMatcher.findAllMatches(board).isEmpty());
    }

    @Test
    void reportsNoValidMovesWhenNoMatchIsPossible() {
        BeghouledBoard board = board(new PlantName[][]{
            {P, S},
            {W, N}
        });
        assertTrue(BeghouledMatcher.findAllMatches(board).isEmpty());
        assertFalse(BeghouledMatcher.hasAnyValidMove(board));
    }

    @Test
    void fillWithoutMatchesHasAValidMoveAndNoInstantMatches() {
        BeghouledBoard board = BeghouledMatcher.fillWithoutMatches(5, 9, BeghouledPool.BOARD_TYPES, new Random(42));
        assertTrue(BeghouledMatcher.findAllMatches(board).isEmpty());
        assertTrue(BeghouledMatcher.hasAnyValidMove(board));
    }

    private static BeghouledBoard board(PlantName[][] tiles) {
        BeghouledBoard board = new BeghouledBoard(tiles.length, tiles[0].length);
        for (int row = 0; row < tiles.length; row++) {
            for (int col = 0; col < tiles[row].length; col++) {
                board.set(row, col, tiles[row][col]);
            }
        }
        return board;
    }
}
