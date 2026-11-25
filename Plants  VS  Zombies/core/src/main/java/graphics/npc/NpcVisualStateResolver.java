package graphics.npc;

import graphics.general.resolver.VisualStateResolver;

public class NpcVisualStateResolver implements VisualStateResolver<NpcPresence, NpcVisualState, NpcRenderInstance> {
    @Override
    public NpcVisualState resolve(
        NpcPresence presence,
        NpcRenderInstance instance,
        NpcVisualState currentState,
        boolean animationFinished
    ) {
        if (presence == null || presence.getVisualState() == null) {
            return currentState;
        }
        return presence.getVisualState();
    }
}
