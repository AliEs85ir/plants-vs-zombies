package graphics.general.resolver;

import graphics.general.instance.RenderInstance;

public interface VisualStateResolver <
    M,
    S extends Enum<S>,
    I extends RenderInstance<M, S, ?, ?>
    >{
    S resolve(
        M model,
        I instance,
        S currentState,
        boolean animationFinished
    );
}
