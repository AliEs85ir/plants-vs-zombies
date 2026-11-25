package graphics.general.animation;

import java.util.Objects;

public record ClipKey<K>(K entityKey, String clipName, String pamPath) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClipKey<?> clipKey = (ClipKey<?>) o;
        return Objects.equals(entityKey, clipKey.entityKey) &&
            Objects.equals(clipName, clipKey.clipName) &&
            Objects.equals(pamPath, clipKey.pamPath);
    }
}
