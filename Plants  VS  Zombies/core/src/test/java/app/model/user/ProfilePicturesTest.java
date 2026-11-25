package app.model.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProfilePicturesTest {
    @Test
    void normalizeUsesDefaultWhenBlank() {
        assertEquals(ProfilePictures.DEFAULT_ID, ProfilePictures.normalize(null));
        assertEquals(ProfilePictures.DEFAULT_ID, ProfilePictures.normalize("  "));
        assertEquals("IMAGE_UI_JOUST_AVATARS_AVATAR_4", ProfilePictures.normalize(" IMAGE_UI_JOUST_AVATARS_AVATAR_4 "));
    }

    @Test
    void catalogRecognizesJoustAndFallbackIds() {
        assertTrue(ProfilePictures.isKnown(ProfilePictures.DEFAULT_ID));
        assertTrue(ProfilePictures.isKnown("IMAGE_UI_JOUST_AVATARS_AVATAR_30"));
        assertTrue(ProfilePictures.isKnown("IMAGE_UI_PACKETS_SUNFLOWER"));
        assertFalse(ProfilePictures.isKnown("not-an-avatar"));
        assertFalse(ProfilePictures.isKnown(""));
    }

    @Test
    void catalogIncludesThirtyJoustPortraits() {
        assertEquals(30, ProfilePictures.joustIdsList().size());
        assertTrue(ProfilePictures.allIds().size() > 30);
    }
}
