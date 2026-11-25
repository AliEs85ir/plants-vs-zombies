package app.view.mainmenu.navigation;

import app.service.NewsService;
import app.view.cursor.GameCursor;
import app.view.mainmenu.MenuTextures;
import app.view.mainmenu.config.MainMenuConfig;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import java.util.List;
import java.util.function.Consumer;

public final class MainMenuNavBar extends WidgetGroup implements Disposable {
    private final MainMenuConfig.NavigationConfig config;
    private final Array<MainMenuNavIcon> actors = new Array<>();
    private final Array<Texture> textures = new Array<>();
    private MainMenuNavIcon newsIcon;
    private Image warningBadge;
    private Texture warningTexture;
    private float warningPulseTime;

    public MainMenuNavBar(
        MainMenuConfig.NavigationConfig config,
        GameCursor gameCursor,
        Consumer<MainMenuConfig.NavIconConfig> onActivate
    ) {
        this.config = config;
        setFillParent(true);
        setTransform(false);
        setTouchable(Touchable.childrenOnly);
        List<MainMenuConfig.NavIconConfig> icons = config.visibleIcons();
        for (MainMenuConfig.NavIconConfig icon : icons) {
            Texture texture = MenuTextures.load(icon.assetPath, "MainMenuNavBar");
            if (texture == null) {
                continue;
            }
            textures.add(texture);
            MainMenuNavIcon actor = new MainMenuNavIcon(texture, icon, config.appearance, gameCursor);
            actor.setAction(() -> {
                if (onActivate != null) {
                    onActivate.accept(icon);
                }
            });
            actors.add(actor);
            addActor(actor);
            if ("news".equalsIgnoreCase(icon.id)) {
                newsIcon = actor;
            }
        }
        createWarningBadge();
    }

    private void createWarningBadge() {
        MainMenuConfig.UnreadWarningConfig warning = config.unreadWarning;
        if (warning == null || !warning.enabled || newsIcon == null) {
            return;
        }
        warningTexture = MenuTextures.load(warning.assetPath, "MainMenuNavBarWarning");
        if (warningTexture == null) {
            return;
        }
        textures.add(warningTexture);
        warningBadge = new Image(new TextureRegionDrawable(new TextureRegion(warningTexture)));
        warningBadge.setOrigin(Align.center);
        warningBadge.setVisible(false);
        warningBadge.setTouchable(Touchable.disabled);
        addActor(warningBadge);
        refreshUnreadWarning();
    }

    public void refreshUnreadWarning() {
        if (warningBadge == null || newsIcon == null || config.unreadWarning == null || !config.unreadWarning.enabled) {
            if (warningBadge != null) {
                warningBadge.setVisible(false);
                warningBadge.clearActions();
            }
            return;
        }
        int unread = NewsService.unreadCount();
        boolean show = unread > 0;
        warningBadge.setVisible(show);
        if (!show) {
            warningBadge.clearActions();
            return;
        }
        MainMenuConfig.UnreadWarningConfig warning = config.unreadWarning;
        warningBadge.clearActions();
        warningBadge.getColor().a = warning.pulseMaxAlpha;
        warningBadge.setScale(warning.pulseMaxScale);
        warningBadge.addAction(Actions.forever(Actions.sequence(
            Actions.parallel(
                Actions.scaleTo(warning.pulseMinScale, warning.pulseMinScale, warning.pulseDurationSec, Interpolation.sine),
                Actions.alpha(warning.pulseMinAlpha, warning.pulseDurationSec, Interpolation.sine)
            ),
            Actions.parallel(
                Actions.scaleTo(warning.pulseMaxScale, warning.pulseMaxScale, warning.pulseDurationSec, Interpolation.sine),
                Actions.alpha(warning.pulseMaxAlpha, warning.pulseDurationSec, Interpolation.sine)
            )
        )));
    }

    public void setSelectedId(String id) {
        String selectedId = id == null ? "" : id;
        for (MainMenuNavIcon actor : actors) {
            actor.setSelected(selectedId.equals(actor.definition().id));
        }
        refreshUnreadWarning();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        warningPulseTime += delta;
        if (warningPulseTime >= 1.5f) {
            warningPulseTime = 0f;
            refreshUnreadWarning();
        }
    }

    @Override
    public void layout() {
        MainMenuNavLayout.Placement[] placements = MainMenuNavLayout.place(
            actors.size,
            getWidth(),
            getHeight(),
            config.layout
        );
        for (int i = 0; i < actors.size && i < placements.length; i++) {
            MainMenuNavLayout.Placement placement = placements[i];
            MainMenuNavIcon actor = actors.get(i);
            MainMenuConfig.NavIconConfig definition = actor.definition();
            float width = definition.width > 0f ? definition.width : placement.width;
            float height = definition.height > 0f ? definition.height : placement.height;
            actor.setBounds(
                placement.x + definition.offsetX,
                placement.y + definition.offsetY + actor.layoutLift(),
                Math.max(1f, width),
                Math.max(1f, height)
            );
        }
        layoutWarningBadge();
    }

    private void layoutWarningBadge() {
        if (warningBadge == null || newsIcon == null || !warningBadge.isVisible()) {
            return;
        }
        MainMenuConfig.UnreadWarningConfig warning = config.unreadWarning;
        float size = warning.size;
        warningBadge.setSize(size, size);
        warningBadge.setPosition(
            newsIcon.getX() + newsIcon.getWidth() * 0.5f + warning.anchorOffsetX - size * 0.5f,
            newsIcon.getY() + newsIcon.getHeight() * 0.5f + warning.anchorOffsetY - size * 0.5f
        );
        warningBadge.setOrigin(Align.center);
    }

    @Override
    public void dispose() {
        for (Texture texture : textures) {
            texture.dispose();
        }
        textures.clear();
        actors.clear();
        warningBadge = null;
        warningTexture = null;
        newsIcon = null;
    }
}
