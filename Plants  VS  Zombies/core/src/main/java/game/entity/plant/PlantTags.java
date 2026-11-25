package game.entity.plant;

import app.enums.plant.PlantTag;

import java.util.EnumSet;
import java.util.Set;

public record PlantTags(Set<PlantTag> tags) {
    public PlantTags() {
        this(EnumSet.noneOf(PlantTag.class));
    }

    public PlantTags(Set<PlantTag> tags) {
        this.tags = EnumSet.copyOf(tags);
    }

    public boolean hasTag(PlantTag tag) {
        return tags.contains(tag);
    }

    public void addTag(PlantTag tag) {
        tags.add(tag);
    }

    public void removeTag(PlantTag tag) {
        tags.remove(tag);
    }

    @Override
    public Set<PlantTag> tags() {
        return EnumSet.copyOf(tags);
    }

    @Override
    public String toString() {
        return tags.toString();
    }
}
