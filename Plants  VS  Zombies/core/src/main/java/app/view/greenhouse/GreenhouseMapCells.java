package app.view.greenhouse;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GreenhouseMapCells implements Disposable {
    public static final class CellInfo {
        public final int row;
        public final int col;
        public final float mapCenterX;
        public final float mapCenterY;
        public final float width;
        public final float height;

        public CellInfo(int row, int col, float mapCenterX, float mapCenterY, float width, float height) {
            this.row = row;
            this.col = col;
            this.mapCenterX = mapCenterX;
            this.mapCenterY = mapCenterY;
            this.width = width;
            this.height = height;
        }
    }

    private final TiledMap map;
    private final float mapPixelWidth;
    private final float mapPixelHeight;
    private final List<CellInfo> cells;

    public GreenhouseMapCells(String mapPath, float mapPixelWidth, float mapPixelHeight) {
        this.mapPixelWidth = mapPixelWidth <= 0f ? 1920f : mapPixelWidth;
        this.mapPixelHeight = mapPixelHeight <= 0f ? 1080f : mapPixelHeight;
        TmxMapLoader loader = new TmxMapLoader();
        this.map = loader.load(mapPath == null || mapPath.isBlank() ? "Maps/greenhouse.tmx" : mapPath);
        ArrayList<CellInfo> parsed = new ArrayList<>();
        MapLayer layer = map.getLayers().get("cells");
        if (layer != null) {
            MapObjects objects = layer.getObjects();
            for (MapObject object : objects) {
                MapProperties properties = object.getProperties();
                int row = intProperty(properties, "row", 0);
                int col = intProperty(properties, "col", 0);
                if (row <= 0 || col <= 0) {
                    continue;
                }
                float objectX;
                float objectY;
                float width;
                float height;
                if (object instanceof RectangleMapObject rectangleMapObject) {
                    Rectangle rectangle = rectangleMapObject.getRectangle();
                    objectX = rectangle.x;
                    objectY = rectangle.y;
                    width = rectangle.width;
                    height = rectangle.height;
                } else {
                    objectX = floatProperty(properties, "x", 0f);
                    objectY = floatProperty(properties, "y", 0f);
                    width = floatProperty(properties, "width", 1f);
                    height = floatProperty(properties, "height", 1f);
                    objectY = mapPixelHeight - objectY - height;
                }
                if (width <= 0f) {
                    width = 1f;
                }
                if (height <= 0f) {
                    height = 1f;
                }
                float centerX = objectX + width * 0.5f;
                float centerY = objectY + height * 0.5f;
                parsed.add(new CellInfo(row, col, centerX, centerY, width, height));
            }
        }
        this.cells = Collections.unmodifiableList(parsed);
    }

    public TiledMap map() {
        return map;
    }

    public float mapPixelWidth() {
        return mapPixelWidth;
    }

    public float mapPixelHeight() {
        return mapPixelHeight;
    }

    public List<CellInfo> cells() {
        return cells;
    }

    @Override
    public void dispose() {
        if (map != null) {
            map.dispose();
        }
    }

    private static int intProperty(MapProperties properties, String key, int fallback) {
        if (properties == null || key == null || !properties.containsKey(key)) {
            return fallback;
        }
        Object value = properties.get(key);
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value instanceof String text) {
            try {
                return Integer.parseInt(text.trim());
            } catch (NumberFormatException ignored) {
                return fallback;
            }
        }
        return fallback;
    }

    private static float floatProperty(MapProperties properties, String key, float fallback) {
        if (properties == null || key == null || !properties.containsKey(key)) {
            return fallback;
        }
        Object value = properties.get(key);
        if (value instanceof Number number) {
            return number.floatValue();
        }
        if (value instanceof String text) {
            try {
                return Float.parseFloat(text.trim());
            } catch (NumberFormatException ignored) {
                return fallback;
            }
        }
        return fallback;
    }
}
