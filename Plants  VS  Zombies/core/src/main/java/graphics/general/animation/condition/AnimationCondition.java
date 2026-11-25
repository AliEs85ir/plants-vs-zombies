package graphics.general.animation.condition;

@FunctionalInterface
public interface AnimationCondition<M> {
    boolean matches(M model);
    default void update(float delta) {}
}
