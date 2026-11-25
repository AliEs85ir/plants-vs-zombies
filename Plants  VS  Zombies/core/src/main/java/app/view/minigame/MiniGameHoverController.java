package app.view.minigame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MiniGameHoverController {
    public static final class Entry {
        private final MiniGameDefinition definition;
        private final List<MiniGameHoverAnimation> animations;

        public Entry(MiniGameDefinition definition, List<MiniGameHoverAnimation> animations) {
            this.definition = definition;
            this.animations = animations == null ? List.of() : List.copyOf(animations);
        }

        public MiniGameDefinition definition() {
            return definition;
        }

        public List<MiniGameHoverAnimation> animations() {
            return animations;
        }

        void enter() {
            for (MiniGameHoverAnimation animation : animations) {
                animation.enter();
            }
        }

        void exit() {
            for (MiniGameHoverAnimation animation : animations) {
                animation.exit();
            }
        }

        void advance() {
            for (MiniGameHoverAnimation animation : animations) {
                animation.advance();
            }
        }
    }

    private final List<Entry> entries;
    private Entry hovered;

    public MiniGameHoverController(List<Entry> entries) {
        this.entries = entries == null ? List.of() : Collections.unmodifiableList(new ArrayList<>(entries));
    }

    public Entry hovered() {
        return hovered;
    }

    public List<Entry> entries() {
        return entries;
    }

    public void setHovered(MiniGameDefinition definition) {
        Entry next = entry(definition);
        if (hovered == next) {
            return;
        }
        if (hovered != null) {
            hovered.exit();
        }
        hovered = next;
        if (hovered != null) {
            hovered.enter();
        }
    }

    public void advance() {
        for (Entry entry : entries) {
            entry.advance();
        }
    }

    public void layout(MiniGameMapSpace space, float viewWidth, float viewHeight) {
        for (Entry entry : entries) {
            for (MiniGameHoverAnimation animation : entry.animations()) {
                animation.layout(space, viewWidth, viewHeight);
            }
        }
    }

    private Entry entry(MiniGameDefinition definition) {
        if (definition == null) {
            return null;
        }
        for (Entry entry : entries) {
            if (entry.definition() == definition) {
                return entry;
            }
        }
        for (Entry entry : entries) {
            if (entry.definition() != null && entry.definition().id().equals(definition.id())) {
                return entry;
            }
        }
        return null;
    }
}
