package game.entity.zombie.part;

import java.util.Locale;

public enum ZombieBodyPart {
    HAND,
    HEAD;

    boolean matches(String layerName, boolean gargantuarPam) {
        if (layerName == null || layerName.isBlank()) {
            return false;
        }
        String name = layerName.toLowerCase(Locale.ROOT);
        if (name.startsWith("play_") || name.contains("armor") || name.contains("flag")) {
            return false;
        }
        return switch (this) {
            case HAND -> matchesHand(name, gargantuarPam);
            case HEAD -> matchesHead(name, gargantuarPam);
        };
    }

    private static boolean matchesHand(String name, boolean gargantuarPam) {
        if (name.contains("inner")) {
            return false;
        }
        if (gargantuarPam) {
            return name.contains("outerarm")
                || name.contains("particle_arm")
                || (name.contains("gargantuar") && (name.contains("hand") || name.contains("arm")));
        }
        if (name.contains("particle_arm")) {
            return true;
        }
        if (name.contains("hand_outer") || name.contains("hand-outer")) {
            return true;
        }
        if (name.contains("arm_outer_lower")) {
            return true;
        }
        return name.contains("hand") && name.contains("outer");
    }

    private static boolean matchesHead(String name, boolean gargantuarPam) {
        if (name.contains("bucket") || name.contains("cone") || name.contains("brick")) {
            return false;
        }
        if (gargantuarPam) {
            return name.contains("particle_head")
                || (name.contains("gargantuar") && (name.contains("head") || name.contains("jaw") || name.contains("skull")));
        }
        if (name.contains("particle_head") || name.contains("skull") || name.contains("jaw")) {
            return true;
        }
        return name.contains("head");
    }
}
