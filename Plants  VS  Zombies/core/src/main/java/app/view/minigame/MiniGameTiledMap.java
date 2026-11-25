package app.view.minigame;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MiniGameTiledMap implements Disposable {
    public static final class ObjectRef {
        public final String name;
        public final Rectangle bounds;

        public ObjectRef(String name, Rectangle bounds) {
            this.name = name == null ? "" : name.trim();
            this.bounds = bounds == null ? new Rectangle() : new Rectangle(bounds);
        }
    }

    private final TiledMap map;
    private final float mapPixelWidth;
    private final float mapPixelHeight;
    private final Rectangle mapBounds;
    private final List<ObjectRef> miniGames;

    public MiniGameTiledMap(MiniGameMenuConfig config) {
        this(config == null ? new MiniGameMenuConfig().sanitize() : config, new TmxMapLoader());
    }

    MiniGameTiledMap(MiniGameMenuConfig config, TmxMapLoader loader) {
        MiniGameMenuConfig safe = config == null ? new MiniGameMenuConfig().sanitize() : config;
        TiledMap loaded = null;
        try {
            loaded = loader.load(safe.mapPath);
        } catch (RuntimeException ignored) {
            loaded = null;
        }
        this.map = loaded;
        float[] pixels = resolveMapPixels(loaded, safe);
        this.mapPixelWidth = pixels[0];
        this.mapPixelHeight = pixels[1];
        this.mapBounds = resolveMapBounds(loaded, safe.mapLayer, mapPixelWidth, mapPixelHeight);
        this.miniGames = Collections.unmodifiableList(
            readMiniGames(loaded, safe.miniGamesLayer, mapPixelHeight)
        );
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

    public Rectangle mapBounds() {
        return new Rectangle(mapBounds);
    }

    public List<ObjectRef> miniGames() {
        return miniGames;
    }

    public MiniGameMapSpace space() {
        return new MiniGameMapSpace(mapPixelWidth, mapPixelHeight, mapBounds);
    }

    public List<MiniGameDefinition> definitions(MiniGameMenuConfig config) {
        return MiniGameDefinition.bind(miniGames, config);
    }

    @Override
    public void dispose() {
        if (map != null) {
            map.dispose();
        }
    }

    private static float[] resolveMapPixels(TiledMap map, MiniGameMenuConfig config) {
        float width = config.mapPixelWidth;
        float height = config.mapPixelHeight;
        if (width <= 0f || height <= 0f) {
            if (map != null) {
                MapProperties properties = map.getProperties();
                int tileWidth = intProperty(properties, "tilewidth", 8);
                int tileHeight = intProperty(properties, "tileheight", 8);
                int columns = intProperty(properties, "width", 240);
                int rows = intProperty(properties, "height", 135);
                MapLayer bg = map.getLayers().get(config.bgLayer);
                if (bg instanceof TiledMapTileLayer tileLayer) {
                    columns = Math.max(1, tileLayer.getWidth());
                    rows = Math.max(1, tileLayer.getHeight());
                    if (tileLayer.getTileWidth() > 0) {
                        tileWidth = tileLayer.getTileWidth();
                    }
                    if (tileLayer.getTileHeight() > 0) {
                        tileHeight = tileLayer.getTileHeight();
                    }
                }
                if (width <= 0f) {
                    width = tileWidth * columns;
                }
                if (height <= 0f) {
                    height = tileHeight * rows;
                }
            }
        }
        if (width <= 0f) {
            width = 1920f;
        }
        if (height <= 0f) {
            height = 1080f;
        }
        return new float[]{width, height};
    }

    private static Rectangle resolveMapBounds(
        TiledMap map,
        String layerName,
        float mapPixelWidth,
        float mapPixelHeight
    ) {
        Rectangle rectangle = extractNamedOrFirstRectangle(map, layerName, "map", mapPixelHeight);
        if (rectangle == null) {
            return new Rectangle(0f, 0f, mapPixelWidth, mapPixelHeight);
        }
        return rectangle;
    }

    private static List<ObjectRef> readMiniGames(TiledMap map, String layerName, float mapPixelHeight) {
        ArrayList<ObjectRef> objects = new ArrayList<>();
        if (map == null || layerName == null || layerName.isBlank()) {
            return objects;
        }
        MapLayer layer = map.getLayers().get(layerName);
        if (layer == null) {
            return objects;
        }
        for (MapObject object : layer.getObjects()) {
            Rectangle bounds = rectangleOf(object, mapPixelHeight);
            if (bounds == null || bounds.width <= 0f || bounds.height <= 0f) {
                continue;
            }
            String name = objectName(object);
            if (name.isBlank()) {
                continue;
            }
            objects.add(new ObjectRef(name, bounds));
        }
        return objects;
    }

    private static Rectangle extractNamedOrFirstRectangle(
        TiledMap map,
        String layerName,
        String objectName,
        float mapPixelHeight
    ) {
        if (map == null || layerName == null || layerName.isBlank()) {
            return null;
        }
        MapLayer layer = map.getLayers().get(layerName);
        if (layer == null) {
            return null;
        }
        Rectangle named = null;
        Rectangle first = null;
        for (MapObject object : layer.getObjects()) {
            Rectangle bounds = rectangleOf(object, mapPixelHeight);
            if (bounds == null) {
                continue;
            }
            if (first == null) {
                first = bounds;
            }
            String name = objectName(object);
            if (objectName.equalsIgnoreCase(name) || objectName.equalsIgnoreCase(object.getName())) {
                named = bounds;
                break;
            }
        }
        return named != null ? named : first;
    }

    private static Rectangle rectangleOf(MapObject object, float mapPixelHeight) {
        if (object instanceof RectangleMapObject rectangleMapObject) {
            return new Rectangle(rectangleMapObject.getRectangle());
        }
        if (object == null) {
            return null;
        }
        MapProperties properties = object.getProperties();
        float x = floatProperty(properties, "x", 0f);
        float y = floatProperty(properties, "y", 0f);
        float width = floatProperty(properties, "width", 0f);
        float height = floatProperty(properties, "height", 0f);
        if (width <= 0f || height <= 0f) {
            return null;
        }
        return new Rectangle(x, mapPixelHeight - y - height, width, height);
    }

    private static String objectName(MapObject object) {
        if (object == null) {
            return "";
        }
        String property = stringProperty(object.getProperties(), "name", "");
        if (!property.isBlank()) {
            return property.trim();
        }
        if (object.getName() != null && !object.getName().isBlank()) {
            return object.getName().trim();
        }
        Object type = object.getProperties().get("type");
        return type == null ? "" : type.toString().trim();
    }

    private static String stringProperty(MapProperties properties, String key, String fallback) {
        if (properties == null || key == null || !properties.containsKey(key)) {
            return fallback;
        }
        Object value = properties.get(key);
        return value == null ? fallback : value.toString();
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
