package app.view.hud.notification;

import app.enums.asset.GameIcon;
import app.service.ConfigFiles;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameplayNotificationsTest {
    @Test
    void scoringBonusPlaysConsultantEffectBesideNotesForClipDuration() {
        NotificationSpec spec = GameplayNotifications.scoringBonus("Double Kill!");

        assertEquals(GameplayNotifications.SCORING_BONUS_DURATION_SECONDS, spec.resolvedDuration(), 0.001f);
        assertEquals(2.1f, spec.resolvedDuration(), 0.001f);
        assertEquals(GameplayNotifications.SCORING_BONUS_PAM_PATH, spec.pamPath());
        assertEquals(GameplayNotifications.SCORING_BONUS_PAM_CLIP, spec.pamClip());
        assertEquals("animation", spec.pamClip());
        assertFalse(spec.pamLoop());
        assertTrue(spec.hasPam());
        assertEquals(GameIcon.IMAGE_UI_HUD_INGAME_NOTIFICATION_ICON, spec.icon());
        assertEquals(IconPlacement.LEFT, spec.iconPlacement());
    }

    @Test
    void effectsJsonRegistersScoringBonusConsultantPam() throws Exception {
        String json = Files.readString(ConfigFiles.resolve("effect/effects.json").toPath());

        assertTrue(json.contains("\"id\": \"" + GameplayNotifications.SCORING_BONUS_EFFECT_ID + "\""));
        assertTrue(json.contains(GameplayNotifications.SCORING_BONUS_PAM_PATH));
        assertTrue(json.contains("\"clip\": \"animation\""));
    }
}
