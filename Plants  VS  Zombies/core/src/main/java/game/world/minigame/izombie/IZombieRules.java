package game.world.minigame.izombie;

import app.controller.game.IZombieController;
import app.controller.game.izombie.IZombieMatchContext;
import app.enums.SeasonType;
import app.enums.plant.PlantName;
import app.view.hud.HudType;
import app.view.hud.IZombieHud;
import game.entity.zombie.data.properties.Property;
import game.world.chapter.Chapter;
import game.world.chapter.ChapterId;

import java.util.ArrayList;
import java.util.List;

public final class IZombieRules {
    public static final String PENDING_MATCHMAKING = "IZOMBIE_PENDING_MATCHMAKING";

    private IZombieRules() {
    }

    public static boolean matches(Chapter chapter) {
        if (chapter == null) {
            return false;
        }
        if (chapter.getMinigameLogic() instanceof IZombieMode) {
            return true;
        }
        if (chapter.getScreenController() instanceof IZombieController) {
            return true;
        }
        return chapter.getHud() instanceof IZombieHud
            || (chapter.getHud() != null && chapter.getHud().getType() == HudType.I_ZOMBIE);
    }

    public static boolean needsMatchmaking(Chapter chapter) {
        return matches(chapter) && !IZombieMatchContext.isActive();
    }

    public static String queueChapterName(Chapter chapter) {
        return queueChapterName(chapter, null);
    }

    public static String queueChapterName(Chapter chapter, String requestedName) {
        if (requestedName != null && !requestedName.isBlank()) {
            return requestedName.trim();
        }
        return networkChapterId(chapter);
    }

    public static boolean isPendingMatchmaking(common.Result result) {
        return result != null && result.success() && PENDING_MATCHMAKING.equals(result.message());
    }

    public static IZombieSide localSide() {
        IZombieMatchContext context = IZombieMatchContext.get();
        if (context != null && context.localSide() != null) {
            return context.localSide();
        }
        return IZombieSide.ZOMBIES;
    }

    public static String networkChapterId(Chapter chapter) {
        if (chapter == null) {
            return null;
        }
        if (chapter.getSeason() != null
            && chapter.getSeason().getSeasonType() != null
            && chapter.getSeason().getSeasonType() != SeasonType.MINIGAME) {
            return new ChapterId(chapter.getSeason().getSeasonType(), chapter.getChapterNumber()).toName();
        }
        return "I_ZOMBIE";
    }

    public static void applyLayoutSeed(Chapter chapter, long seed) {
        if (chapter != null && chapter.getMinigameLogic() instanceof IZombieMode mode) {
            mode.setLayoutSeed(seed);
        }
    }

    public static List<String> availableZombieAliases(Chapter chapter) {
        List<String> aliases = new ArrayList<>();
        if (chapter == null || chapter.getAvailableZombies() == null) {
            return aliases;
        }
        for (Property property : chapter.getAvailableZombies()) {
            if (property != null && property.getAlias() != null && !property.getAlias().isBlank()) {
                aliases.add(property.getAlias());
            }
        }
        return aliases;
    }

    public static PlantName plantNameFromPacket(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return PlantName.valueOf(raw.trim());
        } catch (IllegalArgumentException ignored) {
            return PlantName.parsePlantName(raw);
        }
    }
}
