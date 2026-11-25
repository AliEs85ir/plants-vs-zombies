package graphics.map.hover;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import game.world.playground.Grid;
import game.world.playground.MapConfig;

/**
 * Permanent red cell-border overlay for the main playfield.
 * Reuses {@link Grid}/{@link MapConfig} geometry (same SoT as hover),
 * but draws fixed outlines rather than hover fills.
 */
public class GridOverlayRenderer {
    private static final Color DEFAULT_COLOR = new Color(1f, 0.12f, 0.12f, 0.90f);
    private static final float DEFAULT_LINE_WIDTH = 1.5f;

    private final ShapeRenderer shapeRenderer;
    private final Color color = DEFAULT_COLOR.cpy();
    private float lineWidth = DEFAULT_LINE_WIDTH;

    public GridOverlayRenderer() {
        shapeRenderer = new ShapeRenderer();
    }

    public void render(OrthographicCamera camera, Grid grid) {
        if (camera == null || grid == null) {
            return;
        }

        MapConfig config = grid.getConfig();
        if (config == null || config.getCols() <= 0 || config.getRows() <= 0) {
            return;
        }

        float originX = config.getPlaygroundX();
        float originY = config.getPlaygroundY();
        float width = config.getPlaygroundWidth();
        float height = config.getPlaygroundHeight();
        float cellWidth = config.getCellWidth();
        float cellHeight = config.getCellHeight();
        int cols = config.getCols();
        int rows = config.getRows();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glLineWidth(Math.max(1f, lineWidth));

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(color);

        for (int col = 0; col <= cols; col++) {
            float x = originX + col * cellWidth;
            shapeRenderer.line(x, originY, x, originY + height);
        }
        for (int row = 0; row <= rows; row++) {
            float y = originY + row * cellHeight;
            shapeRenderer.line(originX, y, originX + width, y);
        }

        shapeRenderer.end();
        Gdx.gl.glLineWidth(1f);
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void setColor(Color value) {
        if (value != null) {
            color.set(value);
        }
    }

    public void setAlpha(float alpha) {
        color.a = Math.max(0f, Math.min(1f, alpha));
    }

    public void setLineWidth(float width) {
        lineWidth = Math.max(1f, width);
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
