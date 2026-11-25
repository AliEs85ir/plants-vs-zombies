package graphics.map.hover;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import game.world.playground.Cell;
import game.world.playground.Grid;
import game.world.playground.MapConfig;

public class CellHoverRenderer {
    private static final float DEFAULT_ROW_ALPHA = 0.4f;
    private static final float DEFAULT_COLUMN_ALPHA = 0.4f;
    private static final float DEFAULT_CELL_ALPHA = 0.6f;
    private static final float DEFAULT_ERROR_ROW_ALPHA = 0.35f;
    private static final float DEFAULT_ERROR_COLUMN_ALPHA = 0.35f;
    private static final float DEFAULT_ERROR_CELL_ALPHA = 0.65f;
    private static final float DEFAULT_INSET_RATIO = 0.90f;
    private static final float DEFAULT_PULSE_SPEED = 5.5f;
    private static final float DEFAULT_PULSE_AMOUNT = 0.08f;

    private final ShapeRenderer shapeRenderer;

    private Color highlightColor = Color.WHITE.cpy();
    private Color invalidColor = new Color(1f, 0.18f, 0.18f, 1f);

    private float rowAlpha = DEFAULT_ROW_ALPHA;
    private float columnAlpha = DEFAULT_COLUMN_ALPHA;
    private float cellAlpha = DEFAULT_CELL_ALPHA;

    private float invalidRowAlpha = DEFAULT_ERROR_ROW_ALPHA;
    private float invalidColumnAlpha = DEFAULT_ERROR_COLUMN_ALPHA;
    private float invalidCellAlpha = DEFAULT_ERROR_CELL_ALPHA;

    private float insetRatio = DEFAULT_INSET_RATIO;
    private float pulseSpeed = DEFAULT_PULSE_SPEED;
    private float pulseAmount = DEFAULT_PULSE_AMOUNT;

    private boolean highlightRow = true;
    private boolean highlightColumn = true;
    private boolean emphasizeHoveredCell = true;
    private boolean pulsing = true;

    private float time;

    public CellHoverRenderer() {
        shapeRenderer = new ShapeRenderer();
    }

    public void render(
        OrthographicCamera camera,
        Grid grid,
        Cell hoveredCell,
        boolean canPlant,
        float delta
    ) {
        if (camera == null || grid == null || hoveredCell == null) {
            return;
        }

        time += delta;

        MapConfig config = grid.getConfig();
        float cellWidth = config.getCellWidth();
        float cellHeight = config.getCellHeight();

        float pulse = getPulse();
        Color color = canPlant ? highlightColor : invalidColor;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(
            GL20.GL_SRC_ALPHA,
            GL20.GL_ONE_MINUS_SRC_ALPHA
        );

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        renderRow(
            grid,
            hoveredCell,
            cellWidth,
            cellHeight,
            color,
            canPlant,
            pulse
        );

        renderColumn(
            grid,
            hoveredCell,
            cellWidth,
            cellHeight,
            color,
            canPlant,
            pulse
        );

        renderHoveredCell(
            hoveredCell,
            cellWidth,
            cellHeight,
            color,
            canPlant,
            pulse
        );

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void renderRow(
        Grid grid,
        Cell hoveredCell,
        float width,
        float height,
        Color color,
        boolean valid,
        float pulse
    ) {
        if (!highlightRow) {
            return;
        }

        float alpha = valid ? rowAlpha : invalidRowAlpha;
        alpha *= pulse;

        shapeRenderer.setColor(
            color.r,
            color.g,
            color.b,
            alpha
        );

        int row = hoveredCell.getRow();
        int targetColumn = hoveredCell.getCol();

        for (int col = 0; col < grid.getColCount(); col++) {
            if (col == targetColumn) {
                continue;
            }

            Cell cell = grid.getCell(row, col);

            if (cell != null) {
                drawCell(cell, width, height);
            }
        }
    }

    private void renderColumn(
        Grid grid,
        Cell hoveredCell,
        float width,
        float height,
        Color color,
        boolean valid,
        float pulse
    ) {
        if (!highlightColumn) {
            return;
        }

        float alpha = valid ? columnAlpha : invalidColumnAlpha;
        alpha *= pulse;

        shapeRenderer.setColor(
            color.r,
            color.g,
            color.b,
            alpha
        );

        int column = hoveredCell.getCol();
        int targetRow = hoveredCell.getRow();

        for (int row = 0; row < grid.getRowCount(); row++) {
            if (row == targetRow) {
                continue;
            }

            Cell cell = grid.getCell(row, column);

            if (cell != null) {
                drawCell(cell, width, height);
            }
        }
    }

    private void renderHoveredCell(
        Cell cell,
        float width,
        float height,
        Color color,
        boolean valid,
        float pulse
    ) {
        if (!emphasizeHoveredCell) {
            return;
        }

        float alpha = valid ? cellAlpha : invalidCellAlpha;
        alpha *= pulse;

        shapeRenderer.setColor(
            color.r,
            color.g,
            color.b,
            alpha
        );

        drawCell(cell, width, height);
    }

    private void drawCell(Cell cell, float width, float height) {
        float drawWidth = width * insetRatio;
        float drawHeight = height * insetRatio;
        float x = cell.getWorldX() - drawWidth / 2f;
        float y = cell.getWorldY() - drawHeight / 2f;

        shapeRenderer.rect(
            x,
            y,
            drawWidth,
            drawHeight
        );
    }

    private float getPulse() {
        if (!pulsing) {
            return 1f;
        }

        return 1f + pulseAmount *
            (float) Math.sin(time * pulseSpeed);
    }

    public void setHighlightColor(Color color) {
        if (color != null) {
            highlightColor.set(color);
        }
    }

    public void setInvalidColor(Color color) {
        if (color != null) {
            invalidColor.set(color);
        }
    }

    public void setRowAlpha(float alpha) {
        rowAlpha = clampAlpha(alpha);
    }

    public void setColumnAlpha(float alpha) {
        columnAlpha = clampAlpha(alpha);
    }

    public void setCellAlpha(float alpha) {
        cellAlpha = clampAlpha(alpha);
    }

    public void setInvalidRowAlpha(float alpha) {
        invalidRowAlpha = clampAlpha(alpha);
    }

    public void setInvalidColumnAlpha(float alpha) {
        invalidColumnAlpha = clampAlpha(alpha);
    }

    public void setInvalidCellAlpha(float alpha) {
        invalidCellAlpha = clampAlpha(alpha);
    }

    public void setInsetRatio(float ratio) {
        insetRatio = Math.max(0f, Math.min(1f, ratio));
    }

    public void setPulseSpeed(float speed) {
        pulseSpeed = Math.max(0f, speed);
    }

    public void setPulseAmount(float amount) {
        pulseAmount = Math.max(0f, amount);
    }

    public void setHighlightRow(boolean enabled) {
        highlightRow = enabled;
    }

    public void setHighlightColumn(boolean enabled) {
        highlightColumn = enabled;
    }

    public void setEmphasizeHoveredCell(boolean enabled) {
        emphasizeHoveredCell = enabled;
    }

    public void setPulsing(boolean enabled) {
        pulsing = enabled;
    }

    private float clampAlpha(float alpha) {
        return Math.max(0f, Math.min(1f, alpha));
    }

    public void resetPulse() {
        time = 0f;
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
