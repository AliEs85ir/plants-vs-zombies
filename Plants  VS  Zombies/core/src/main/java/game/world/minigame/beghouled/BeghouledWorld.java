package game.world.minigame.beghouled;

import app.enums.obstacle.ObstacleType;
import game.entity.obstacle.Obstacle;
import game.entity.obstacle.ObstacleFactory;
import game.entity.plant.Plant;
import game.world.minigame.Minigame;
import game.world.minigame.MinigameWorld;
import game.world.playground.Cell;
import game.world.seasons.Season;

import java.util.List;

public class BeghouledWorld extends MinigameWorld {
    public enum BoardState {
        IDLE,
        RESOLVING,
        ANIMATING
    }

    private final int comboTarget;
    private int comboCount;
    private int cascadeDepth;
    private BoardState boardState = BoardState.IDLE;
    private Cell selectedCell;
    private boolean winArmed;
    private boolean pendingResolve;
    private final java.util.ArrayList<BeghouledSlide> slides = new java.util.ArrayList<>();

    public BeghouledWorld(Minigame logic, Season season, int comboTarget) {
        super(logic, season);
        this.comboTarget = Math.max(1, comboTarget);
    }

    public int getComboCount() {
        return comboCount;
    }

    public int getComboTarget() {
        return comboTarget;
    }

    public void addCombos(int amount) {
        if (amount > 0) {
            comboCount += amount;
        }
    }

    public boolean hasReachedTarget() {
        return comboCount >= comboTarget;
    }

    public int getCascadeDepth() {
        return cascadeDepth;
    }

    public void setCascadeDepth(int cascadeDepth) {
        this.cascadeDepth = Math.max(0, cascadeDepth);
    }

    public boolean isBoardLocked() {
        return boardState == BoardState.RESOLVING || boardState == BoardState.ANIMATING;
    }

    public BoardState getBoardState() {
        return boardState;
    }

    public void setBoardState(BoardState boardState) {
        this.boardState = boardState == null ? BoardState.IDLE : boardState;
    }

    public Cell getSelectedCell() {
        return selectedCell;
    }

    public void setSelectedCell(Cell selectedCell) {
        this.selectedCell = selectedCell;
    }

    public boolean isWinArmed() {
        return winArmed;
    }

    public void armWin() {
        this.winArmed = true;
    }

    public void addSlide(BeghouledSlide slide) {
        if (slide != null) {
            slides.add(slide);
            boardState = BoardState.ANIMATING;
        }
    }

    public boolean hasActiveSlides() {
        return !slides.isEmpty();
    }

    public void setPendingResolve(boolean pendingResolve) {
        this.pendingResolve = pendingResolve;
    }

    public boolean isPendingResolve() {
        return pendingResolve;
    }

    public void advanceSlides(float delta) {
        if (slides.isEmpty()) {
            return;
        }
        slides.removeIf(slide -> slide.advance(delta));
        if (slides.isEmpty() && (boardState == BoardState.ANIMATING || pendingResolve)) {
            boardState = BoardState.RESOLVING;
            pendingResolve = false;
        }
    }

    public boolean isPermanentCrater(Cell cell) {
        return cell != null
            && cell.getObstacle() != null
            && cell.getObstacle().getType() == ObstacleType.PERMANENT_SCORCHED;
    }

    @Override
    public void notifyPlantEaten(Plant plant, Cell cell) {
        placePermanentCrater(cell);
    }

    public void placePermanentCrater(Cell cell) {
        if (cell == null || isPermanentCrater(cell)) {
            return;
        }
        for (Plant plant : new java.util.ArrayList<>(cell.getPlants())) {
            if (plant != null && !plant.isDead()) {
                removePlant(plant);
            }
        }
        Obstacle obstacle = ObstacleFactory.getInstance().createRuntimeObstacle(
            ObstacleType.PERMANENT_SCORCHED,
            cell.getWidth() / 2f,
            cell.getHeight() / 2f,
            cell.getWorldX(),
            cell.getWorldY(),
            List.of()
        );
        cell.setObstacle(obstacle);
    }

    private void removePlant(Plant plant) {
        if (plant == null || getGrid() == null) {
            return;
        }
        Cell cell = plant.getCell();
        if (cell != null) {
            cell.removePlant(plant);
        }
        getPlants().remove(plant);
    }
}
