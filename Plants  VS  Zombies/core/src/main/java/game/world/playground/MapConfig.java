package game.world.playground;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import common.model.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapConfig {
    private final int rows;
    private final int cols;
    private final float cellWidth;
    private final float cellHeight;

    private final float playgroundX;
    private final float playgroundY;
    private final float playgroundWidth;
    private final float playgroundHeight;

    private final float mapX;
    private final float mapY;
    private final float mapWidth;
    private final float mapHeight;

    private final float loseLineX;
    private final Vector2 cameraCenter;

    private final List<Vector2> lawnMowerSpawns;
    private final List<Vector2> zombieSpawns;
    private final List<Vector2> obstaclePossiblePositions;

    private MapConfig(Builder builder) {
        this.rows = builder.rows;
        this.cols = builder.cols;
        this.cellWidth = builder.cellWidth;
        this.cellHeight = builder.cellHeight;
        this.playgroundX = builder.playgroundX;
        this.playgroundY = builder.playgroundY;
        this.playgroundWidth = builder.playgroundWidth;
        this.playgroundHeight = builder.playgroundHeight;
        this.mapX = builder.mapX;
        this.mapY = builder.mapY;
        this.mapHeight = builder.mapHeight;
        this.mapWidth = builder.mapWidth;
        this.loseLineX = builder.loseLineX;
        this.cameraCenter = builder.cameraCenter;
        this.lawnMowerSpawns = Collections.unmodifiableList(builder.lawnMowerSpawns);
        this.zombieSpawns = Collections.unmodifiableList(builder.zombieSpawns);
        this.obstaclePossiblePositions = Collections.unmodifiableList(builder.obstacleCells);
    }


    public static MapConfig rectangular(int rows, int cols, float cellWidth, float cellHeight) {
        Builder builder = new Builder();
        builder.rows = rows;
        builder.cols = cols;
        builder.cellWidth = cellWidth;
        builder.cellHeight = cellHeight;
        builder.playgroundX = 0f;
        builder.playgroundY = 0f;
        builder.playgroundWidth = cols * cellWidth;
        builder.playgroundHeight = rows * cellHeight;
        builder.mapX = 0f;
        builder.mapY = 0f;
        builder.mapWidth = builder.playgroundWidth;
        builder.mapHeight = builder.playgroundHeight;
        builder.loseLineX = 0f;
        builder.cameraCenter = new Vector2(builder.playgroundWidth / 2f, builder.playgroundHeight / 2f);
        return new MapConfig(builder);
    }

    public static MapConfig fromTiledMap(TiledMap tiledMap, int rows, int cols) {
        Builder builder = new Builder();
        builder.rows = rows;
        builder.cols = cols;

        Rectangle playground = extractSingleRectangle(tiledMap, "playground");

        if (playground != null) {
            builder.playgroundX = playground.x;
            builder.playgroundY = playground.y;
            builder.playgroundWidth = playground.width;
            builder.playgroundHeight = playground.height;
            builder.cellWidth = playground.width / cols;
            builder.cellHeight = playground.height / rows;
        }

        Rectangle loseRect = extractSingleRectangle(tiledMap, "lose");
        if (loseRect != null) {
            builder.loseLineX = loseRect.x;
        }

        Rectangle mapRect = extractSingleRectangle(tiledMap, "map");
        if (mapRect != null) {
            builder.mapX = mapRect.x;
            builder.mapY = mapRect.y;
            builder.mapWidth = mapRect.width;
            builder.mapHeight = mapRect.height;
            builder.cameraCenter = new Vector2(
                mapRect.x + mapRect.width / 2f,
                mapRect.y + mapRect.height / 2f
            );
        }

        builder.lawnMowerSpawns = extractAllRectanglePositions(tiledMap, "lawn_mower");
        builder.zombieSpawns = extractAllRectanglePositions(tiledMap, "zombie");
        builder.obstacleCells = extractAllRectanglePositions(tiledMap, "cell");

        return new MapConfig(builder);
    }

    public Vector2 gridToWorld(int row, int col) {
        float x = playgroundX + col * cellWidth + cellWidth / 2f;
        float y = playgroundY + row * cellHeight + cellHeight / 2f;
        return new Vector2(x, y);
    }

    public GridCoordinate worldToGrid(float worldX, float worldY) {
        if (worldX < playgroundX || worldX >= playgroundX + playgroundWidth ||
            worldY < playgroundY || worldY >= playgroundY + playgroundHeight) {
            return null;
        }

        int col = (int) ((worldX - playgroundX) / cellWidth);
        int row = (int) ((worldY - playgroundY) / cellHeight);

        // Clamp to valid range
        col = Math.max(0, Math.min(col, cols - 1));
        row = Math.max(0, Math.min(row, rows - 1));

        return new GridCoordinate(row, col);
    }

    public boolean isPastLoseLine(float worldX) {
        return worldX < loseLineX;
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public float getCellWidth() { return cellWidth; }
    public float getCellHeight() { return cellHeight; }
    public float getPlaygroundX() { return playgroundX; }
    public float getPlaygroundY() { return playgroundY; }
    public float getPlaygroundWidth() { return playgroundWidth; }
    public float getPlaygroundHeight() { return playgroundHeight; }
    public float getLoseLineX() { return loseLineX; }
    public Vector2 getCameraCenter() { return cameraCenter; }
    public List<Vector2> getLawnMowerSpawns() { return lawnMowerSpawns; }
    public List<Vector2> getZombieSpawns() { return zombieSpawns; }
    public List<Vector2> getObstacleCells() {return obstaclePossiblePositions;}

    private static Rectangle extractSingleRectangle(TiledMap map, String layerName) {
        MapLayer layer = map.getLayers().get(layerName);
        if (layer == null || layer.getObjects().getCount() == 0) return null;

        MapObject obj = layer.getObjects().get(0);
        if (obj instanceof RectangleMapObject) {
            return ((RectangleMapObject) obj).getRectangle();
        }
        return null;
    }


    private static List<Vector2> extractAllRectanglePositions(TiledMap map, String layerName) {
        List<Vector2> positions = new ArrayList<>();
        MapLayer layer = map.getLayers().get(layerName);
        if (layer == null) return positions;

        for (MapObject obj : layer.getObjects()) {
            if (obj instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) obj).getRectangle();
                positions.add(new Vector2(
                    rect.x + rect.width / 2f,
                    rect.y + rect.height / 2f
                ));
            }
        }
        return positions;
    }

    public boolean isInsideMap(Position position) {
        if (position == null) {
            return false;
        }
        float x = (float) position.getX();
        float y = (float) position.getY();

        return x >= mapX && x <= (mapX + mapWidth) &&
            y >= mapY && y <= (mapY + mapHeight);
    }

    public boolean isInsidePlayGround(Position position) {
        if (position == null) {
            return false;
        }
        float x = (float) position.getX();
        float y = (float) position.getY();

        return x >= playgroundX && x <= (playgroundX + playgroundWidth) &&
            y >= playgroundY && y <= (playgroundY + playgroundHeight);
    }

    public float getMapX() {
        return mapX;
    }

    public float getMapY() {
        return mapY;
    }

    public float getMapWidth() {
        return mapWidth;
    }

    public float getMapHeight() {
        return mapHeight;
    }

    public record GridCoordinate(int row, int col) {}

    private static class Builder {
        int rows, cols;
        float cellWidth, cellHeight;
        float playgroundX, playgroundY, playgroundWidth, playgroundHeight;
        float mapX, mapY, mapHeight, mapWidth;
        float loseLineX;
        Vector2 cameraCenter;
        List<Vector2> lawnMowerSpawns = new ArrayList<>();
        List<Vector2> zombieSpawns = new ArrayList<>();
        List<Vector2> obstacleCells = new ArrayList<>();
    }
}
