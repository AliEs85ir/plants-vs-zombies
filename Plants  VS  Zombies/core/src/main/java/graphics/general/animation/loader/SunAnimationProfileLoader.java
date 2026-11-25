package graphics.general.animation.loader;

import app.enums.sun.SunType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import graphics.sun.SunAnimationProfile;
import graphics.sun.SunAnimationTag;
import graphics.sun.SunAnimationVariant;
import graphics.sun.SunVisualState;

public class SunAnimationProfileLoader {

    public static SunAnimationProfile load(String resourcePath) {
        JsonValue root = new JsonReader().parse(Gdx.files.internal(resourcePath));
        String sunTypeStr = root.getString("sunType");
        SunType type = SunType.valueOf(sunTypeStr);
        String pamPath = root.getString("pamPath");
        SunAnimationProfile profile = new SunAnimationProfile(type, pamPath);

        JsonValue states = root.get("states");
        for (JsonValue stateEntry : states) {
            SunVisualState state = SunVisualState.valueOf(stateEntry.name());
            for (JsonValue variantJson : stateEntry) {
                String clip = variantJson.getString("clip");
                boolean loop = variantJson.getBoolean("loop");
                int priority = variantJson.getInt("priority", 0);
                SunAnimationTag tag = SunAnimationTag.valueOf(
                    variantJson.getString("tag", "DEFAULT").toUpperCase());
                profile.addVariant(state, new SunAnimationVariant(clip, loop, priority, tag));
            }
        }
        return profile;
    }
}
