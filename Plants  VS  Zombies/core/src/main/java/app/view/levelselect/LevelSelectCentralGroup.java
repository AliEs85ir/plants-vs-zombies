package app.view.levelselect;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class LevelSelectCentralGroup extends WidgetGroup {
    private final LevelSelectMenuConfig config;
    private final LevelSelectMenuConfig.ChapterConfig chapter;
    private final LevelSelectPamActor main;
    private final LevelSelectPamActor subordinate;
    private final LevelSelectPamActor levelReference;
    private final List<LevelSelectPamActor> layers = new ArrayList<>();
    private boolean hovered;
    private float hoverFactor;
    private float bobTime;
    private float time;

    public LevelSelectCentralGroup(LevelSelectMenuConfig config, LevelSelectMenuConfig.ChapterConfig chapter) {
        this.config = config;
        this.chapter = chapter;
        setName("menu-level-select-center");
        setTransform(false);
        setTouchable(Touchable.enabled);
        main = create(chapter.main);
        subordinate = create(chapter.subordinate);
        levelReference = create(chapter.levelReference);
        addLayer(main);
        addLayer(subordinate);
        addLayer(levelReference);
        layers.sort(Comparator.comparingInt(this::layerOf));
        for (LevelSelectPamActor actor : layers) {
            actor.setTouchable(Touchable.disabled);
            addActor(actor);
        }
        addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if (pointer != -1) {
                    return;
                }
                hovered = true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                hovered = false;
            }
        });
    }

    public void update(float delta, float centerX, float centerY) {
        time += Math.max(0f, delta);
        updateHover(delta);
        float groupScale = Math.max(0.01f, config.layout.centralGroupScale);
        float hoverScale = config.hover.centerEnabled
            ? 1f + (config.hover.centerScale - 1f) * hoverFactor
            : 1f;
        float composed = groupScale * hoverScale;
        float floatX = 0f;
        float floatY = 0f;
        if (config.centralFloat.enabled) {
            floatX = (float) Math.sin(time * config.centralFloat.speedX + config.centralFloat.phaseX)
                * config.centralFloat.amplitudeX;
            floatY = (float) Math.sin(time * config.centralFloat.speedY + config.centralFloat.phaseY)
                * config.centralFloat.amplitudeY;
        }
        float bob = 0f;
        if (config.hover.centerEnabled && hoverFactor > 0.001f) {
            bob = (float) Math.sin(bobTime * config.hover.centerBobSpeed)
                * config.hover.centerBobAmplitude
                * hoverFactor;
        }
        float hoverY = config.hover.centerEnabled ? config.hover.centerOffsetY * hoverFactor : 0f;
        float width = config.layout.centralHitWidth * composed;
        float height = config.layout.centralHitHeight * composed;
        setSize(Math.max(1f, width), Math.max(1f, height));
        setOrigin(getWidth() * 0.5f, getHeight() * 0.5f);
        setPosition(
            centerX - getOriginX() + floatX,
            centerY - getOriginY() + floatY + hoverY + bob
        );
        layoutChildren(composed);
        int z = 0;
        for (LevelSelectPamActor actor : layers) {
            actor.setZIndex(z++);
        }
    }

    private void layoutChildren(float groupScale) {
        float originX = getWidth() * 0.5f;
        float originY = getHeight() * 0.5f;
        LevelSelectMenuConfig.AnimationConfig mainConfig = chapter.main;
        float mainX = originX + mainConfig.offsetX * groupScale;
        float mainY = originY + mainConfig.offsetY * groupScale;
        place(main, mainConfig, mainX, mainY, groupScale);
        placeRelative(subordinate, chapter.subordinate, mainX, mainY, groupScale);
        placeRelative(levelReference, chapter.levelReference, mainX, mainY, groupScale);
    }

    private void placeRelative(
        LevelSelectPamActor actor,
        LevelSelectMenuConfig.AnimationConfig animation,
        float mainX,
        float mainY,
        float groupScale
    ) {
        if (actor == null || animation == null || !animation.present()) {
            return;
        }
        place(
            actor,
            animation,
            mainX + animation.offsetX * groupScale,
            mainY + animation.offsetY * groupScale,
            groupScale
        );
    }

    private void place(
        LevelSelectPamActor actor,
        LevelSelectMenuConfig.AnimationConfig animation,
        float centerX,
        float centerY,
        float groupScale
    ) {
        if (actor == null || animation == null || !animation.present()) {
            if (actor != null) {
                actor.setVisible(false);
            }
            return;
        }
        float scale = Math.max(0.01f, groupScale * animation.scale);
        actor.setVisible(actor.ready());
        actor.applyVisualSize(scale);
        actor.setCenter(centerX, centerY);
        actor.setFlip(animation.flipX, animation.flipY);
        actor.setVisualRotation(animation.rotation);
        float alpha = animation.alpha;
        if (config.hover.centerEnabled) {
            alpha *= (1f + (config.hover.centerAlpha - 1f) * hoverFactor);
        }
        float darkness = config.hover.centerEnabled ? config.hover.centerDarkness * hoverFactor : 0f;
        float tint = 1f - Math.max(0f, Math.min(1f, darkness));
        actor.setColor(tint, tint, tint, Math.max(0f, Math.min(1f, alpha)));
    }

    private void updateHover(float delta) {
        float duration = config.hover.centerDuration;
        float target = hovered && config.hover.centerEnabled ? 1f : 0f;
        if (duration <= 0f) {
            hoverFactor = target;
        } else {
            float step = Math.max(0f, delta) / duration;
            if (hoverFactor < target) {
                hoverFactor = Math.min(target, hoverFactor + step);
            } else if (hoverFactor > target) {
                hoverFactor = Math.max(target, hoverFactor - step);
            }
        }
        if (hoverFactor > 0.001f) {
            bobTime += Math.max(0f, delta);
        } else {
            bobTime = 0f;
        }
    }

    private LevelSelectPamActor create(LevelSelectMenuConfig.AnimationConfig animation) {
        if (animation == null || !animation.present()) {
            return null;
        }
        return new LevelSelectPamActor(animation.pamPath, animation.clip, animation.loop);
    }

    private void addLayer(LevelSelectPamActor actor) {
        if (actor != null) {
            layers.add(actor);
        }
    }

    private int layerOf(LevelSelectPamActor actor) {
        if (actor == subordinate) {
            return chapter.subordinate.layer;
        }
        if (actor == main) {
            return chapter.main.layer;
        }
        return chapter.levelReference.layer;
    }
}
