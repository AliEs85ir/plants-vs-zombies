package plants_vs_zombies;

import app.model.App;
import app.model.user.User;
import app.service.AppService;
import app.view.hud.notification.emoji_sticker.EmojiAssetLoader;
import app.view.screen.LoadingScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import game.audio.AudioManager;
import game.component.AssetManager;

public class PlantsVSZombies extends Game {
    public SpriteBatch batch;
    private static PlantsVSZombies pvz;
    private float gameDataSyncTimer = 0f;
    private static final float GAME_DATA_SYNC_INTERVAL_SECONDS = 20f;

    @Override
    public void create() {
        batch = new SpriteBatch();
        pvz = this;
        setScreen(new LoadingScreen(this));
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        AssetManager.pump();
        AudioManager.update(delta);

        gameDataSyncTimer += delta;

        if (gameDataSyncTimer >= GAME_DATA_SYNC_INTERVAL_SECONDS) {
            gameDataSyncTimer = 0f;
            AppService.syncGameDataIfDirty();
        }

        super.render();
    }

    @Override
    public void pause() {
        saveQuestProgress();
        super.pause();
    }

    @Override
    public void dispose() {
        saveQuestProgress();
        AudioManager.dispose();
        EmojiAssetLoader.dispose();
        if (batch != null) {
            batch.dispose();
        }
        super.dispose();
    }

    public static PlantsVSZombies getPvz() {return pvz;}

    private static void saveQuestProgress() {
        User user = App.getCurrentUser();
        if (user != null && user.getQuestManager() != null) {
            user.getQuestManager().saveQuests();
            return;
        }
        AppService.syncGameDataIfDirty();
    }
}
