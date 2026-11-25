package app.view.network;

import app.view.adventure.AdventureHudIconButton;
import app.view.adventure.AdventureHudConfig;
import app.view.loading.CoverImage;
import app.view.mainmenu.MenuTextures;
import app.view.screen.ScreenManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import plants_vs_zombies.PlantsVSZombies;

import java.util.ArrayList;
import java.util.List;

public final class NetworkMenuPanel extends WidgetGroup implements Disposable {
    private final NetworkMenuConfig config;
    private final CoverImage background = new CoverImage();
    private final Array<Texture> textures = new Array<>();
    private final List<NetworkMinigameCard> cards = new ArrayList<>();
    private final NetworkRankingPanel ranking;
    private final AdventureHudIconButton backButton;

    public NetworkMenuPanel(PlantsVSZombies game) {
        this.config = NetworkMenuConfigLoader.load();
        setFillParent(true);
        setTransform(false);
        setTouchable(Touchable.childrenOnly);

        Texture backgroundTexture = load(config.background.path);
        if (backgroundTexture != null) {
            background.setTexture(
                backgroundTexture,
                config.background.sourceWidth > 0f ? config.background.sourceWidth : backgroundTexture.getWidth(),
                config.background.sourceHeight > 0f ? config.background.sourceHeight : backgroundTexture.getHeight()
            );
            background.setOffsets(config.background.offsetX, config.background.offsetY);
        }
        background.setFillParent(true);
        addActor(background);

        NetworkMinigameRouter router = new NetworkMinigameRouter(game);
        Texture frameTexture = load(config.minigames.frameAssetPath);
        for (NetworkMenuConfig.GameConfig minigame : config.minigames.visibleGames()) {
            Texture cardTexture = load(minigame.assetPath);
            Texture playTexture = load(minigame.playAssetPath);
            NetworkMinigameCard card = new NetworkMinigameCard(
                frameTexture,
                cardTexture,
                playTexture,
                config.minigames,
                minigame,
                () -> router.activate(minigame)
            );
            cards.add(card);
            addActor(card);
        }

        ranking = new NetworkRankingPanel(load(config.ranking.backgroundPath), config.ranking);
        addActor(ranking);

        AdventureHudConfig.HudIconConfig backStyle = backStyle(config.backButton);
        backButton = new AdventureHudIconButton(load(config.backButton.assetPath), backStyle, "menu-network-back");
        backButton.setAction(() -> ScreenManager.showMainMenu(game));
        backButton.setVisible(config.backButton.visible);
        addActor(backButton);
    }

    @Override
    public void layout() {
        background.setBounds(0f, 0f, getWidth(), getHeight());
        float scaleX = getWidth() / config.layout.designWidth;
        float scaleY = getHeight() / config.layout.designHeight;

        NetworkMenuConfig.MinigamesConfig games = config.minigames;
        float cardWidth = games.width * scaleX;
        float slotHeight = games.slotHeight * scaleY;
        float x = games.x * scaleX;
        float y = games.top * scaleY - slotHeight;
        float spacing = games.spacing * scaleY;
        for (NetworkMinigameCard card : cards) {
            card.place(x, y, cardWidth, slotHeight);
            y -= slotHeight + spacing;
        }

        ranking.place(
            config.ranking.x * scaleX,
            config.ranking.y * scaleY,
            config.ranking.width * scaleX,
            config.ranking.height * scaleY,
            scaleX,
            scaleY
        );

        float backWidth = config.backButton.drawWidth();
        float backHeight = config.backButton.drawHeight();
        backButton.place(
            config.backButton.resolvedX(getWidth()),
            config.backButton.resolvedY(getHeight(), backHeight),
            backWidth,
            backHeight
        );
        backButton.setZIndex(getChildren().size);
    }

    private Texture load(String path) {
        Texture texture = MenuTextures.load(path, "NetworkMenuPanel");
        if (texture != null) {
            textures.add(texture);
        }
        return texture;
    }

    private static AdventureHudConfig.HudIconConfig backStyle(NetworkMenuConfig.BackButtonConfig source) {
        AdventureHudConfig.HudIconConfig style = new AdventureHudConfig.HudIconConfig();
        style.assetPath = source.assetPath;
        style.width = source.width;
        style.height = source.height;
        style.scale = source.scale;
        style.hoverScale = source.hoverScale;
        style.pressedScale = source.pressedScale;
        style.hoverDuration = source.hoverDuration;
        style.easing = source.easing;
        style.visible = source.visible;
        return style;
    }

    @Override
    public void dispose() {
        for (Texture texture : textures) {
            texture.dispose();
        }
        textures.clear();
        cards.clear();
    }
}
