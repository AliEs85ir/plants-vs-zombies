package graphics.npc;

import common.model.Position;
import graphics.general.instance.RenderInstance;

public class NpcRenderInstance
    extends RenderInstance<NpcPresence, NpcVisualState, NpcVisualEventType, NpcAnimationVariant> {

    public NpcRenderInstance(NpcPresence presence) {
        super(presence, presence.getVisualState());
    }

    public NpcPresence getNpc() {
        return getModel();
    }

    public Position getRenderPosition() {
        return getModel().getPosition();
    }

    @Override
    protected float generateRandomVariationInterval() {
        return 1000f;
    }
}
