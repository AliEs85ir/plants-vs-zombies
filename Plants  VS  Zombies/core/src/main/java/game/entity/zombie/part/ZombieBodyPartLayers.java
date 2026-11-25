package game.entity.zombie.part;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Picks the PAM layers that belong to a droppable body part.
 * Gargantuar PAMs also embed imp / basic-zombie sprites; those nested
 * characters are ignored so only the gargantuar's own parts drop.
 */
public final class ZombieBodyPartLayers {
    private ZombieBodyPartLayers() {
    }

    public static List<String> matching(ZombieBodyPart part, Collection<String> layerNames) {
        if (part == null || layerNames == null || layerNames.isEmpty()) {
            return List.of();
        }
        boolean gargantuarPam = containsToken(layerNames, "gargantuar");
        List<String> matched = new ArrayList<>();
        for (String layerName : layerNames) {
            if (part.matches(layerName, gargantuarPam)) {
                matched.add(layerName);
            }
        }
        return matched;
    }

    /**
     * Visibility map for a dropped {@code particles} clip.
     * Only names the droppable parts (and armor): kept part on, everything
     * else in that set off. Unrelated parent layers are left at default
     * visible so PamPlayer can still walk the clip tree.
     */
    public static Map<String, Boolean> effectVisibility(ZombieBodyPart part, Collection<String> layerNames) {
        Map<String, Boolean> visibility = new HashMap<>();
        if (part == null || layerNames == null) {
            return visibility;
        }
        for (String layerName : layerNames) {
            if (layerName != null && layerName.toLowerCase(Locale.ROOT).contains("armor")) {
                visibility.put(layerName, false);
            }
        }
        for (ZombieBodyPart candidate : ZombieBodyPart.values()) {
            boolean keep = candidate == part;
            for (String layer : matching(candidate, layerNames)) {
                visibility.put(layer, keep);
            }
        }
        return visibility;
    }

    private static boolean containsToken(Collection<String> layerNames, String token) {
        for (String layerName : layerNames) {
            if (layerName != null && layerName.toLowerCase(Locale.ROOT).contains(token)) {
                return true;
            }
        }
        return false;
    }

    public static final class Node {
        public final String name;
        public final List<Node> children;

        public Node(String name, Node... children) {
            this(name, List.of(children));
        }

        public Node(String name, List<Node> children) {
            this.name = name;
            this.children = children == null ? List.of() : List.copyOf(children);
        }
    }
}
