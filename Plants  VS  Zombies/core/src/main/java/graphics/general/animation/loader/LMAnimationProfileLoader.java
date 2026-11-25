package graphics.general.animation.loader;

import app.enums.lawnmower.LawnMowerType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import graphics.lawnmower.LMAnimationProfile;
import graphics.lawnmower.LMAnimationTag;
import graphics.lawnmower.LMAnimationVariant;
import graphics.lawnmower.LMVisualState;

public final class LMAnimationProfileLoader {

    private LMAnimationProfileLoader() {
    }

    public static LMAnimationProfile load(String path) {
        FileHandle file = Gdx.files.internal(path);

        if (!file.exists()) {
            throw new IllegalArgumentException("Resource not found: " + path);
        }

        JsonObject root = JsonParser
            .parseReader(file.reader())
            .getAsJsonObject();

        LawnMowerType type = LawnMowerType.valueOf(root.get("lawnmower").getAsString());
        String pamPath = root.get("pamPath").getAsString();

        LMAnimationProfile profile = new LMAnimationProfile(type, pamPath);

        JsonObject states = root.getAsJsonObject("states");

        for (String stateName : states.keySet()) {
            LMVisualState state = LMVisualState.valueOf(stateName);
            states.getAsJsonArray(stateName).forEach(element -> {
                JsonObject obj = element.getAsJsonObject();
                profile.addVariant(
                    state,
                    new LMAnimationVariant(
                        obj.get("clip").getAsString(),
                        obj.get("loop").getAsBoolean(),
                        obj.get("priority").getAsInt(),
                        LMAnimationTag.valueOf(obj.get("tag").getAsString())
                    )
                );
            });
        }

        return profile;
    }
}
