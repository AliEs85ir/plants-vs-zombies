package graphics.general.effects.anchers;

import com.badlogic.gdx.math.Vector2;

import java.util.function.Function;

public final class EntityAnchor<M> implements EffectAnchor {
    private final M entity;
    private final Function<M, Vector2> positionProvider;
    private final Vector2 offset;

    public EntityAnchor(M entity, Function<M, Vector2> positionProvider, Vector2 offset) {
        this.entity = entity;
        this.positionProvider = positionProvider;
        this.offset = offset;
    }

    public EntityAnchor(M entity, Function<M, Vector2> positionProvider) {
        this(entity, positionProvider, new Vector2());
    }

    @Override
    public Vector2 resolvePosition() {
        Vector2 base = positionProvider.apply(entity);
        return base == null ? offset.cpy() : base.cpy().add(offset);
    }

    public M getEntity() {
        return entity;
    }

    @Override
    public String toString() {
        return "EntityAnchor(" + entity + ")" + offset;
    }
}
