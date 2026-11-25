package graphics.general.effects;

import app.model.App;

import java.util.Queue;

public class SequentialEffectResolver<M> {
    private final M ownerContext;

    private EffectInstance<M> currentEffect = null;

    public SequentialEffectResolver(M ownerContext) {
        this.ownerContext = ownerContext;
    }

    public void resolve(Queue<EffectEvent<M>> effectQueue) {
        EffectEvent<M> nextEvent = effectQueue.peek();

        // 1. Interrupt Logic
        if (currentEffect != null && !currentEffect.isFinished()) {
            if (nextEvent != null && nextEvent.isInterrupting()) {
                currentEffect.forceFinish();
                currentEffect = null;
            }
        }

        // 2. Blocking Logic (Wait for non-looping effects to finish)
        if (currentEffect != null) {
            boolean isLooping = currentEffect.getCurrentVariant() != null
                && currentEffect.getCurrentVariant().isLoop();

            if (!isLooping && !currentEffect.isFinished()) {
                return;
            }
        }

        if (currentEffect != null && currentEffect.isFinished()) {
            currentEffect = null;
        }

        // 4. Spawn Next Event
        if (!effectQueue.isEmpty()) {
            if (currentEffect != null && !currentEffect.isFinished()) {
                currentEffect.forceFinish();
            }

            EffectEvent<M> event = effectQueue.poll();
            EffectRequest<M> request = new EffectRequest<>(
                event.getEffectId(),
                ownerContext,
                event.getAnchor(),
                event.getLayerOverride()
            );
            EffectManager manager = App.getRealTimeEngine().getGraphicsEngine().getEffectManager();
            if (manager != null) {
                this.currentEffect = manager.spawn(request);
            }
        }
    }

    public EffectInstance<M> getCurrentEffect() {
        return currentEffect;
    }

    public void stop() {
        if (currentEffect != null && !currentEffect.isFinished()) {
            currentEffect.forceFinish();
        }
        currentEffect = null;
    }
}
