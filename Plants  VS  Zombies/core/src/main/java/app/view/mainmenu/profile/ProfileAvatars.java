package app.view.mainmenu.profile;

import app.model.user.ProfilePictures;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.component.AssetManager;

import java.util.ArrayList;
import java.util.List;

public final class ProfileAvatars {
    public static final String DEFAULT_ID = ProfilePictures.DEFAULT_ID;
    public static final String BORDER_ID = "IMAGE_UI_JOUST_AVATARS_AVATAR_BORDER";
    public static final String SELECTED_OVERLAY_ID = "IMAGE_UI_JOUST_AVATARS_AVATAR_SELECTED";

    private ProfileAvatars() {
    }

    public static String normalize(String pictureId) {
        return ProfilePictures.normalize(pictureId);
    }

    public static boolean isKnown(String pictureId) {
        return ProfilePictures.isKnown(pictureId);
    }

    public static List<String> suggestions() {
        List<String> joust = loaded(ProfilePictures.joustIdsList());
        if (joust.size() >= 4) {
            return joust;
        }
        List<String> fallback = loaded(ProfilePictures.fallbackIdsList());
        if (!fallback.isEmpty()) {
            List<String> mixed = new ArrayList<>(joust);
            for (String id : fallback) {
                if (!mixed.contains(id)) {
                    mixed.add(id);
                }
            }
            return mixed;
        }
        return ProfilePictures.allIds();
    }

    public static TextureRegion region(String pictureId) {
        String id = normalize(pictureId);
        TextureRegion region = lookup(id);
        if (region != null) {
            return region;
        }
        if (!DEFAULT_ID.equals(id)) {
            return lookup(DEFAULT_ID);
        }
        return null;
    }

    public static TextureRegion overlay(String assetId) {
        return lookup(assetId);
    }

    private static List<String> loaded(List<String> ids) {
        List<String> result = new ArrayList<>();
        for (String id : ids) {
            if (lookup(id) != null) {
                result.add(id);
            }
        }
        return result;
    }

    private static TextureRegion lookup(String id) {
        if (id == null || id.isBlank() || AssetManager.textureBank == null) {
            return null;
        }
        try {
            return AssetManager.textureBank.region(id);
        } catch (RuntimeException exception) {
            return null;
        }
    }
}
