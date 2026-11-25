package app.view.adventure;

import app.view.mainmenu.MenuTextures;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import java.util.List;
import java.util.function.Consumer;

public final class AdventureCarousel extends WidgetGroup implements Disposable {
    private final AdventureMenuConfig config;
    private final Array<AdventureChapterActor> chapters = new Array<>();
    private final Array<Texture> textures = new Array<>();
    private final AdventureCarouselMath.Slot[] slots;
    private final AdventureArrowActor leftArrow;
    private final AdventureArrowActor rightArrow;
    private final AdventureFrontOverlay overlay;
    private int frontIndex;
    private float rotation;
    private boolean rotating;
    private float fromRotation;
    private float toRotation;
    private float elapsed;
    private float duration;
    private int pendingSteps;
    private int animDirection;
    private boolean selecting;

    public AdventureCarousel(
        AdventureMenuConfig config,
        Consumer<AdventureMenuConfig.ChapterConfig> onSelect
    ) {
        this.config = config;
        setFillParent(true);
        setTransform(false);
        setTouchable(Touchable.childrenOnly);
        List<AdventureMenuConfig.ChapterConfig> items = config.visibleChapters();
        slots = new AdventureCarouselMath.Slot[items.size()];
        for (int i = 0; i < items.size(); i++) {
            slots[i] = new AdventureCarouselMath.Slot();
            AdventureMenuConfig.ChapterConfig definition = items.get(i);
            AdventureChapterActor actor = new AdventureChapterActor(definition);
            actor.setAction(() -> select(definition, onSelect));
            actor.setHoverChanged(this::syncOverlayHover);
            chapters.add(actor);
            addActor(actor);
        }
        Texture leftTexture = load(config.arrows.left.assetPath);
        Texture rightTexture = load(config.arrows.right.assetPath);
        leftArrow = new AdventureArrowActor(leftTexture, false);
        rightArrow = new AdventureArrowActor(rightTexture, true);
        leftArrow.setAction(() -> rotate(-1));
        rightArrow.setAction(() -> rotate(1));
        addActor(leftArrow);
        addActor(rightArrow);
        overlay = new AdventureFrontOverlay(config);
        overlay.setEnterAction(() -> {
            if (chapters.size == 0) {
                return;
            }
            select(chapters.get(frontIndex).definition(), onSelect);
        });
        addActor(overlay);
        frontIndex = AdventureCarouselMath.wrap(config.indexOf(config.initialFrontId), chapters.size);
        rotation = frontIndex * AdventureCarouselMath.stepRadians(chapters.size, config.rotation);
        boolean arrowsEnabled = chapters.size > 1;
        leftArrow.setEnabled(arrowsEnabled);
        rightArrow.setEnabled(arrowsEnabled);
        if (chapters.size > 0) {
            overlay.setChapter(chapters.get(frontIndex).definition());
        }
    }

    public AdventureMenuConfig config() {
        return config;
    }

    public int frontIndex() {
        return frontIndex;
    }

    public boolean rotating() {
        return rotating;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (rotating) {
            elapsed += Math.max(0f, delta);
            float t = duration <= 0f ? 1f : Math.min(1f, elapsed / duration);
            Interpolation easing = config.rotation.interpolation();
            rotation = fromRotation + (toRotation - fromRotation) * easing.apply(t);
            if (t >= 1f) {
                finishRotation();
            }
        }
        layoutCarousel(delta);
    }

    @Override
    public void layout() {
        layoutCarousel(0f);
    }

    private void layoutCarousel(float delta) {
        int count = chapters.size;
        AdventureCarouselMath.compute(slots, count, rotation, frontIndex, config, getWidth(), getHeight());
        float targetWidth = config.chapter.width;
        float targetHeight = config.chapter.height;
        for (int i = 0; i < count; i++) {
            AdventureChapterActor actor = chapters.get(i);
            actor.updateHover(delta, config.chapter.hoverDuration);
            actor.setRotating(rotating);
            actor.setFront(!rotating && i == frontIndex);
            actor.applySlot(slots[i], targetWidth, targetHeight, config.chapter, Color.WHITE);
        }
        int[] order = AdventureCarouselMath.drawOrder(slots, count);
        int z = 0;
        for (int index : order) {
            chapters.get(index).setZIndex(z++);
        }
        placeArrows();
        leftArrow.setZIndex(z++);
        rightArrow.setZIndex(z++);
        overlay.setZIndex(z);
        if (count > 0 && frontIndex >= 0 && frontIndex < slots.length && slots[frontIndex] != null) {
            overlay.setRotating(rotating);
            overlay.setChapter(chapters.get(frontIndex).definition());
            overlay.setFrontPose(slots[frontIndex].centerX, slots[frontIndex].centerY, config.carousel.scale);
            overlay.setHovered(chapters.get(frontIndex).hovered());
        }
    }

    private void placeArrows() {
        float cx = config.carousel.resolvedCenterX(getWidth());
        float cy = config.carousel.resolvedCenterY(getHeight());
        float global = config.carousel.scale;
        float distance = config.arrows.distance * global;
        AdventureMenuConfig.ArrowSideConfig left = config.arrows.left;
        AdventureMenuConfig.ArrowSideConfig right = config.arrows.right;
        float leftHitW = config.arrows.hitWidth(left, global);
        float leftHitH = config.arrows.hitHeight(left, global);
        float rightHitW = config.arrows.hitWidth(right, global);
        float rightHitH = config.arrows.hitHeight(right, global);
        float leftX = cx - distance - config.arrows.horizontalOffset * global + left.offsetX * global - leftHitW * 0.5f;
        float rightX = cx + distance + config.arrows.horizontalOffset * global + right.offsetX * global - rightHitW * 0.5f;
        float leftY = cy + config.arrows.verticalOffset * global + left.offsetY * global - leftHitH * 0.5f;
        float rightY = cy + config.arrows.verticalOffset * global + right.offsetY * global - rightHitH * 0.5f;
        leftArrow.place(leftX, leftY, leftHitW, leftHitH, config.arrows.resolvedAlpha(left), config.arrows.resolvedRotation(left));
        rightArrow.place(rightX, rightY, rightHitW, rightHitH, config.arrows.resolvedAlpha(right), config.arrows.resolvedRotation(right));
    }

    private void rotate(int requestedDirection) {
        int count = chapters.size;
        if (count <= 1) {
            return;
        }
        int direction = requestedDirection;
        if (!config.rotation.nextOnRight()) {
            direction = -requestedDirection;
        }
        if (direction == 0) {
            return;
        }
        if (rotating) {
            if (config.rotation.queueInput) {
                pendingSteps += direction;
            }
            return;
        }
        startRotation(direction);
    }

    private void startRotation(int direction) {
        int count = chapters.size;
        if (count <= 1 || direction == 0) {
            return;
        }
        for (AdventureChapterActor actor : chapters) {
            actor.clearHover();
        }
        overlay.setHovered(false);
        overlay.setRotating(true);
        rotating = true;
        animDirection = direction > 0 ? 1 : -1;
        float step = AdventureCarouselMath.stepRadians(count, config.rotation);
        fromRotation = rotation;
        toRotation = rotation + animDirection * step;
        elapsed = 0f;
        duration = config.rotation.durationSeconds(Math.abs(step));
        if (duration <= 0f) {
            rotation = toRotation;
            finishRotation();
        }
    }

    private void finishRotation() {
        int count = chapters.size;
        rotating = false;
        frontIndex = AdventureCarouselMath.wrap(frontIndex + animDirection, count);
        rotation = frontIndex * AdventureCarouselMath.stepRadians(count, config.rotation);
        animDirection = 0;
        if (count > 0) {
            overlay.setRotating(false);
            overlay.setChapter(chapters.get(frontIndex).definition());
        }
        if (pendingSteps != 0) {
            int next = pendingSteps > 0 ? 1 : -1;
            pendingSteps -= next;
            startRotation(next);
        }
    }

    private void select(
        AdventureMenuConfig.ChapterConfig definition,
        Consumer<AdventureMenuConfig.ChapterConfig> onSelect
    ) {
        if (selecting || rotating || definition == null || !definition.interactive()) {
            return;
        }
        selecting = true;
        if (onSelect != null) {
            onSelect.accept(definition);
        }
        selecting = false;
    }

    private void syncOverlayHover() {
        if (rotating || chapters.size == 0) {
            overlay.setHovered(false);
            return;
        }
        overlay.setHovered(chapters.get(frontIndex).hovered());
    }

    private Texture load(String path) {
        Texture texture = MenuTextures.load(path, "AdventureCarousel");
        if (texture != null) {
            textures.add(texture);
        }
        return texture;
    }

    @Override
    public void dispose() {
        overlay.dispose();
        for (Texture texture : textures) {
            texture.dispose();
        }
        textures.clear();
        chapters.clear();
    }
}
