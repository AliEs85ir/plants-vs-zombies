package app.model.user;

import java.util.ArrayList;
import java.util.List;

public final class ProfilePictures {
    public static final String DEFAULT_ID = "IMAGE_UI_JOUST_AVATARS_AVATAR_1";

    private static final String[] JOUST_IDS = joustIds();
    private static final String[] FALLBACK_IDS = {
        "IMAGE_UI_PACKETS_PEASHOOTER",
        "IMAGE_UI_PACKETS_SUNFLOWER",
        "IMAGE_UI_PACKETS_WALL_NUT",
        "IMAGE_UI_PACKETS_CHERRY_BOMB",
        "IMAGE_UI_PACKETS_SNOW_PEA",
        "IMAGE_UI_PACKETS_REPEATER",
        "IMAGE_UI_PACKETS_CHOMPER",
        "IMAGE_UI_PACKETS_BONK_CHOY",
        "IMAGE_UI_PACKETS_KERNEL_PULT",
        "IMAGE_UI_PACKETS_MELON_PULT",
        "IMAGE_UI_PACKETS_TALL_NUT",
        "IMAGE_UI_PACKETS_SQUASH",
        "IMAGE_UI_PACKETS_POTATO_MINE",
        "IMAGE_UI_PACKETS_JALAPENO",
        "IMAGE_UI_PACKETS_CABBAGE_PULT",
        "IMAGE_UI_HUD_INGAME_PROGRESS_METER_ZOMBIEHEAD"
    };

    private ProfilePictures() {
    }

    public static String normalize(String pictureId) {
        if (pictureId == null || pictureId.isBlank()) {
            return DEFAULT_ID;
        }
        return pictureId.trim();
    }

    public static boolean isKnown(String pictureId) {
        if (pictureId == null || pictureId.isBlank()) {
            return false;
        }
        String id = pictureId.trim();
        return contains(JOUST_IDS, id) || contains(FALLBACK_IDS, id);
    }

    public static List<String> joustIdsList() {
        return List.of(JOUST_IDS);
    }

    public static List<String> fallbackIdsList() {
        return List.of(FALLBACK_IDS);
    }

    public static List<String> allIds() {
        List<String> ids = new ArrayList<>(List.of(JOUST_IDS));
        for (String id : FALLBACK_IDS) {
            if (!ids.contains(id)) {
                ids.add(id);
            }
        }
        return ids;
    }

    private static boolean contains(String[] ids, String id) {
        for (String known : ids) {
            if (known.equals(id)) {
                return true;
            }
        }
        return false;
    }

    private static String[] joustIds() {
        String[] ids = new String[30];
        for (int index = 0; index < ids.length; index++) {
            ids[index] = "IMAGE_UI_JOUST_AVATARS_AVATAR_" + (index + 1);
        }
        return ids;
    }
}
