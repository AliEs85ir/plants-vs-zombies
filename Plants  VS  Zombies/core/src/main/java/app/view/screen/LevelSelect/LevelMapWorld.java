package app.view.screen.LevelSelect;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import game.component.AssetManager;

import java.util.ArrayList;
import java.util.List;

public class LevelMapWorld extends Group {

    private static final String NODE_PAM =
        "768/INITIAL/WORLDMAP/LEVEL_NODE_MINIGAME/LEVEL_NODE_MINIGAME.PAM";

    private static final String PATH_PAM =
        "768/INITIAL/WORLDMAP/MAP_PATH/MAP_PATH.PAM";

    private static final float MAP_WIDTH = 1280f;

    /*
     * فاصله‌ی عمودی Levelها.
     * کاربر دیگر لازم نیست مختصات هر Level را بدهد.
     */
    private static final float LEVEL_SPACING = 260f;

    /*
     * فاصله از کناره‌های Map
     */
    private static final float SIDE_MARGIN = 220f;

    private final List<LevelNodeActor> nodes = new ArrayList<>();
    private final List<MapPathActor> paths = new ArrayList<>();

    private float mapHeight = 720f;

    private float dragStartY;
    private float worldStartY;
    private boolean dragging;

    public LevelMapWorld() {

        setSize(
            MAP_WIDTH,
            mapHeight
        );

        setupInput();
    }

    private void setupInput() {

        addListener(new InputListener() {

            @Override
            public boolean touchDown(
                InputEvent event,
                float x,
                float y,
                int pointer,
                int button
            ) {

                if (button != Input.Buttons.LEFT) {
                    return false;
                }

                dragging = true;

                dragStartY = y;
                worldStartY = getY();

                return true;
            }

            @Override
            public void touchDragged(
                InputEvent event,
                float x,
                float y,
                int pointer
            ) {

                if (!dragging) {
                    return;
                }

                float deltaY = y - dragStartY;

                setY(
                    worldStartY + deltaY
                );

                clampPosition();
            }

            @Override
            public void touchUp(
                InputEvent event,
                float x,
                float y,
                int pointer,
                int button
            ) {

                dragging = false;
            }

            @Override
            public boolean scrolled(
                InputEvent event,
                float x,
                float y,
                float amountX,
                float amountY
            ) {

                setY(
                    getY() - amountY * 80f
                );

                clampPosition();

                return true;
            }
        });
    }

    public LevelNodeActor addLevel(
        LevelNodeActor.State state
    ) {

        int index = nodes.size();

        LevelNodeActor node = new LevelNodeActor(
            AssetManager.player,
            NODE_PAM,
            state
        );

        /*
         * اندازه‌ی Node
         */
        node.setScale(0.12f);

        /*
         * زیگزاگ خودکار:
         *
         * 1   سمت چپ
         *      |
         *      |
         *          2 سمت راست
         *          |
         *          |
         * 3 سمت چپ
         */
        float x;

        if (index % 2 == 0) {
            x = SIDE_MARGIN;
        } else {
            x = MAP_WIDTH - SIDE_MARGIN;
        }

        float y =
            140f + index * LEVEL_SPACING;

        node.setCenter(x, y);

        nodes.add(node);

        addActor(node);

        /*
         * Path را قبل از Node قرار می‌دهیم.
         */
        if (nodes.size() >= 2) {

            LevelNodeActor previous =
                nodes.get(nodes.size() - 2);

            MapPathActor path = new MapPathActor(
                AssetManager.player,
                PATH_PAM
            );

            path.connect(
                previous,
                node
            );

            paths.add(path);

            /*
             * Path باید پشت Node باشد.
             */
            addActorBefore(
                node,
                path
            );
        }

        /*
         * ارتفاع World را بر اساس آخرین Level
         * خودش حساب می‌کنیم.
         */
        mapHeight =
            y + 180f;

        setSize(
            MAP_WIDTH,
            mapHeight
        );

        return node;
    }

    private void clampPosition() {

        /*
         * اگر Map از صفحه کوتاه‌تر باشد،
         * اصلاً حرکت نکند.
         */
        float viewportHeight = 720f;

        if (mapHeight <= viewportHeight) {
            setY(0f);
            return;
        }

        /*
         * پایین Map نباید بالاتر از پایین صفحه بیاید.
         */
        float minY =
            viewportHeight - mapHeight;

        /*
         * بالای Map نباید از بالای صفحه پایین‌تر بیاید.
         */
        float maxY = 0f;

        float y = getY();

        if (y < minY) {
            y = minY;
        }

        if (y > maxY) {
            y = maxY;
        }

        setY(y);
    }

    public List<LevelNodeActor> getNodes() {
        return nodes;
    }

    public float getMapHeight() {
        return mapHeight;
    }
}
