package game.dialogue;

import app.view.game.GameViewport;
import graphics.GraphicsEngine;
import graphics.general.animation.AnimationController;
import graphics.npc.NpcPresence;
import graphics.npc.NpcRenderInstance;
import graphics.npc.NpcVisualState;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class NpcController {
    public interface Listener {
        void onEnterFinished(Speaker speaker);
        void onLeaveFinished(Speaker speaker);
        void onTalkDurationFinished(Speaker speaker);
    }

    private enum Phase {
        NONE,
        ENTERING,
        IDLE,
        TALKING,
        LEAVING
    }

    private static final float MAX_TRANSITION_SECONDS = 5f;

    private static final class Slot {
        final Speaker speaker;
        final NpcPresence presence;
        Phase phase = Phase.ENTERING;
        float talkElapsed;
        float talkDuration;
        float transitionElapsed;
        boolean awaitingFirstAnimationFrame = true;

        Slot(Speaker speaker, NpcPresence presence) {
            this.speaker = speaker;
            this.presence = presence;
        }
    }

    private final GraphicsEngine graphicsEngine;
    private final GameViewport viewport;
    private final Map<Speaker, Slot> slots = new EnumMap<>(Speaker.class);
    private Listener listener;

    public NpcController(GraphicsEngine graphicsEngine, GameViewport viewport) {
        this.graphicsEngine = graphicsEngine;
        this.viewport = viewport;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public boolean hasNpc(Speaker speaker) {
        Slot slot = slots.get(speaker);
        return slot != null && slot.phase != Phase.LEAVING;
    }

    public void enter(Speaker speaker) {
        if (speaker == null) {
            return;
        }
        Slot existing = slots.get(speaker);
        if (existing != null && existing.phase != Phase.LEAVING) {
            idle(speaker);
            if (listener != null) {
                listener.onEnterFinished(speaker);
            }
            return;
        }
        NpcPresence presence = new NpcPresence(speaker);
        presence.setVisualState(NpcVisualState.ENTERING);
        applyPlacement(presence);
        graphicsEngine.registerNpc(presence);
        slots.put(speaker, new Slot(speaker, presence));
    }

    public void idle(Speaker speaker) {
        Slot slot = slots.get(speaker);
        if (slot == null) {
            return;
        }
        slot.presence.setVisualState(NpcVisualState.IDLE);
        slot.phase = Phase.IDLE;
    }

    public void talk(Speaker speaker, String clipName, float durationSeconds) {
        Slot slot = slots.get(speaker);
        if (slot == null) {
            return;
        }
        slot.presence.setTalkClipName(clipName);
        slot.presence.setVisualState(NpcVisualState.TALKING);
        slot.phase = Phase.TALKING;
        slot.talkElapsed = 0f;
        slot.talkDuration = Math.max(0f, durationSeconds);
    }

    public void leave(Speaker speaker) {
        Slot slot = slots.get(speaker);
        if (slot == null) {
            if (listener != null) {
                listener.onLeaveFinished(speaker);
            }
            return;
        }
        slot.presence.setVisualState(NpcVisualState.LEAVING);
        slot.phase = Phase.LEAVING;
        slot.transitionElapsed = 0f;
        slot.awaitingFirstAnimationFrame = true;
    }

    public void update(float delta) {
        List<Speaker> entered = new ArrayList<>();
        List<Speaker> left = new ArrayList<>();
        List<Speaker> finishedTalk = new ArrayList<>();

        for (Slot slot : slots.values()) {
            applyPlacement(slot.presence);
            if (slot.awaitingFirstAnimationFrame) {
                slot.awaitingFirstAnimationFrame = false;
                continue;
            }
            if (slot.phase == Phase.ENTERING || slot.phase == Phase.LEAVING) {
                slot.transitionElapsed += delta;
            }
            if (slot.phase == Phase.TALKING) {
                slot.talkElapsed += delta;
                if (slot.talkElapsed >= slot.talkDuration) {
                    slot.presence.setVisualState(NpcVisualState.IDLE);
                    slot.phase = Phase.IDLE;
                    finishedTalk.add(slot.speaker);
                }
                continue;
            }
            if (slot.phase == Phase.ENTERING && isTransitionFinished(slot)) {
                slot.presence.setVisualState(NpcVisualState.IDLE);
                slot.phase = Phase.IDLE;
                entered.add(slot.speaker);
                continue;
            }
            if (slot.phase == Phase.LEAVING && isTransitionFinished(slot)) {
                left.add(slot.speaker);
            }
        }

        for (Speaker speaker : left) {
            Slot slot = slots.remove(speaker);
            if (slot != null) {
                graphicsEngine.unregisterNpc(slot.presence);
            }
        }

        if (listener != null) {
            for (Speaker speaker : finishedTalk) {
                listener.onTalkDurationFinished(speaker);
            }
            for (Speaker speaker : entered) {
                listener.onEnterFinished(speaker);
            }
            for (Speaker speaker : left) {
                listener.onLeaveFinished(speaker);
            }
        }
    }

    public void reset() {
        for (Slot slot : slots.values()) {
            graphicsEngine.unregisterNpc(slot.presence);
        }
        slots.clear();
    }

    private boolean isTransitionFinished(Slot slot) {
        return slot.transitionElapsed >= MAX_TRANSITION_SECONDS || isCurrentAnimationFinished(slot);
    }

    private boolean isCurrentAnimationFinished(Slot slot) {
        NpcRenderInstance instance = graphicsEngine.getNpcInstance(slot.presence);
        if (instance == null) {
            return true;
        }
        AnimationController<?> controller = instance.getAnimationController();
        return controller.getCurrentClip() == null || controller.isFinished();
    }

    private void applyPlacement(NpcPresence presence) {
        if (presence == null || viewport == null) {
            return;
        }
        Speaker speaker = presence.getSpeaker();
        float width = viewport.getWorldWidth();
        float height = viewport.getWorldHeight();
        float y = viewport.getWorldBottom() + height * DialogueConfig.NPC_VERTICAL_FRACTION;
        float inset = width * DialogueConfig.NPC_HORIZONTAL_INSET_FRACTION;
        float x = speaker.getSide() == Speaker.Side.LEFT
            ? viewport.getWorldLeft() + inset
            : viewport.getWorldRight() - inset;
        if (speaker == Speaker.CRAZY_DAVE) {
            x += DialogueConfig.DAVE_POSITION_OFFSET_X;
            y += DialogueConfig.DAVE_POSITION_OFFSET_Y;
        } else if (speaker == Speaker.PENNY) {
            x += DialogueConfig.PENNY_POSITION_OFFSET_X;
            y += DialogueConfig.PENNY_POSITION_OFFSET_Y;
        } else if (speaker == Speaker.ZOMBOSS) {
            x += DialogueConfig.ZOMBOSS_POSITION_OFFSET_X;
            y += DialogueConfig.ZOMBOSS_POSITION_OFFSET_Y;
        }
        presence.setPosition(x, y);
    }
}
