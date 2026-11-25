package graphics.general.effects;

import graphics.general.effects.anchers.EffectAnchor;

public class EffectEvent<M> {
    private final String effectId;
    private final EffectAnchor anchor;
    private final EffectLayer layerOverride;
    private final boolean isInterrupting;

    public EffectEvent(String effectId, EffectAnchor anchor, boolean isInterrupting) {
        this(effectId, anchor, null, isInterrupting);
    }

    public EffectEvent(String effectId, EffectAnchor anchor, EffectLayer layerOverride, boolean isInterrupting) {
        this.effectId = effectId;
        this.anchor = anchor;
        this.layerOverride = layerOverride;
        this.isInterrupting = isInterrupting;
    }

    public String getEffectId() { return effectId; }
    public EffectAnchor getAnchor() { return anchor; }
    public EffectLayer getLayerOverride() { return layerOverride; }
    public boolean isInterrupting() { return isInterrupting; }
}
