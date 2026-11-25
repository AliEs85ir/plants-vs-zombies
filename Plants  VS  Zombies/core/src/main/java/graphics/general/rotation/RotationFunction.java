package graphics.general.rotation;

@FunctionalInterface
public interface RotationFunction<M> {
    float getRotation(M model);
}
