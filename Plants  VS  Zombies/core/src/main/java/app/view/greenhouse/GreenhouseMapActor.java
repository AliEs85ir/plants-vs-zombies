package app.view.greenhouse;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Disposable;

public final class GreenhouseMapActor extends Widget implements Disposable {
    private final TiledMap map;
    private final float mapPixelWidth;
    private final float mapPixelHeight;
    private final OrthogonalTiledMapRenderer renderer;
    private final Matrix4 projection = new Matrix4();
    private final Vector2 tmp = new Vector2();
    private final int[] tileLayerIndices;

    public GreenhouseMapActor(TiledMap map, float mapPixelWidth, float mapPixelHeight) {
        this.map = map;
        this.mapPixelWidth = mapPixelWidth <= 0f ? 1920f : mapPixelWidth;
        this.mapPixelHeight = mapPixelHeight <= 0f ? 1080f : mapPixelHeight;
        this.renderer = map == null ? null : new OrthogonalTiledMapRenderer(map, 1f);
        this.tileLayerIndices = resolveTileLayers(map);
        setFillParent(true);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (map == null || renderer == null || getStage() == null) {
            return;
        }
        batch.end();
        Vector2 origin = localToStageCoordinates(tmp.set(0f, 0f));
        float scaleX = getWidth() / mapPixelWidth;
        float scaleY = getHeight() / mapPixelHeight;
        projection.set(getStage().getCamera().combined);
        projection.translate(origin.x, origin.y, 0f);
        projection.scale(scaleX, scaleY, 1f);
        renderer.setView(projection, 0f, 0f, mapPixelWidth, mapPixelHeight);
        if (tileLayerIndices.length > 0) {
            renderer.render(tileLayerIndices);
        } else {
            renderer.render();
        }
        batch.begin();
    }

    @Override
    public void dispose() {
        if (renderer != null) {
            renderer.dispose();
        }
    }

    private static int[] resolveTileLayers(TiledMap map) {
        if (map == null) {
            return new int[0];
        }
        MapLayer bg = map.getLayers().get("bg");
        if (bg instanceof TiledMapTileLayer) {
            return new int[]{map.getLayers().getIndex("bg")};
        }
        int count = 0;
        for (MapLayer layer : map.getLayers()) {
            if (layer instanceof TiledMapTileLayer) {
                count++;
            }
        }
        int[] indices = new int[count];
        int cursor = 0;
        for (int i = 0; i < map.getLayers().getCount(); i++) {
            if (map.getLayers().get(i) instanceof TiledMapTileLayer) {
                indices[cursor++] = i;
            }
        }
        return indices;
    }
}
