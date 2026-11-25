package app.view.hud.notification;

import app.view.hud.elements.HudPamActor;
import app.view.hud.notification.emoji_sticker.AnimatedIconImage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import game.component.AssetManager;
import game.component.ResolvedIcon;

public class NotificationView extends Group {
    private final NotificationSpec spec;
    private final Group content;
    private final Group iconHolder;
    private final Group textHolder;
    private final Actor iconActor;
    private final Label textLabel;

    public NotificationView(NotificationSpec spec) {
        this.spec = spec;
        setTouchable(Touchable.disabled);
        setTransform(false);

        content = new Group();
        content.setTransform(true);
        content.setTouchable(Touchable.disabled);
        addActor(content);

        ResolvedIcon resolvedIcon;
        if (spec.iconAnimation() != null) {
            resolvedIcon = animatedResolvedIcon(spec);
        } else if (spec.iconRegion() != null) {
            resolvedIcon = staticRegionResolvedIcon(spec);
        } else {
            resolvedIcon = AssetManager.icon(spec.icon(), spec.iconScale());
        }
        HudPamActor pamActor = createPamActor(spec);
        BitmapFont font = AssetManager.font(spec.font());
        float fontScale = spec.textSize().fontScale() * spec.size().contentScale();
        float pad = spec.innerPadding();
        float gap = spec.iconTextSpacing();
        float iconWidth = resolvedIcon.isPresent() ? resolvedIcon.width() : 0f;
        float iconHeight = resolvedIcon.isPresent() ? resolvedIcon.height() : 0f;
        float pamWidth = pamActor != null ? spec.pamWidth() : 0f;
        float pamHeight = pamActor != null ? spec.pamHeight() : 0f;
        boolean hasNote = resolvedIcon.isPresent();
        boolean hasPam = pamActor != null;
        boolean hasVisual = hasNote || hasPam;
        float clusterGap = hasNote && hasPam ? gap : 0f;
        float clusterWidth = pamWidth + clusterGap + iconWidth;
        float clusterHeight = Math.max(pamHeight, iconHeight);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, spec.resolvedColor());
        textLabel = new Label(spec.text(), labelStyle);
        textLabel.setAlignment(Align.left | Align.center);
        textLabel.setWrap(true);
        textLabel.setFontScale(fontScale);

        float lineHeight = Math.max(8f, font.getLineHeight() * fontScale);
        float textHeight = lineHeight * spec.lineCount();
        float width = spec.resolvedWidth();

        IconPlacement placement = spec.iconPlacement();
        boolean horizontal = placement == IconPlacement.LEFT || placement == IconPlacement.RIGHT;
        float textWidth;
        float height;

        if (hasVisual && horizontal) {
            textWidth = Math.max(48f, width - pad * 2f - clusterWidth - gap);
            height = Math.max(clusterHeight, textHeight) + pad * 2f;
        } else if (hasVisual) {
            textWidth = Math.max(48f, width - pad * 2f);
            height = clusterHeight + gap + textHeight + pad * 2f;
        } else {
            textWidth = Math.max(48f, width - pad * 2f);
            height = textHeight + pad * 2f;
        }

        if (spec.customHeight() != null) {
            height = spec.customHeight();
        }

        iconHolder = new Group();
        iconHolder.setTransform(true);
        iconHolder.setTouchable(Touchable.disabled);
        iconActor = buildIconCluster(
            resolvedIcon,
            pamActor,
            iconWidth,
            iconHeight,
            pamWidth,
            pamHeight,
            clusterWidth,
            clusterHeight,
            clusterGap,
            placement
        );
        iconHolder.addActor(iconActor);
        iconHolder.setSize(Math.max(1f, clusterWidth), Math.max(1f, clusterHeight));
        content.addActor(iconHolder);

        textHolder = new Group();
        textHolder.setTransform(true);
        textHolder.setTouchable(Touchable.disabled);
        textLabel.setSize(textWidth, textHeight);
        textHolder.setSize(textWidth, textHeight);
        textHolder.addActor(textLabel);
        content.addActor(textHolder);

        layoutParts(width, height, pad, gap, clusterWidth, clusterHeight, textWidth, textHeight, hasVisual);

        content.setSize(width, height);
        content.setOrigin(width * 0.5f, height * 0.5f);
        setSize(width, height);
    }

    private void layoutParts(
        float width,
        float height,
        float pad,
        float gap,
        float iconWidth,
        float iconHeight,
        float textWidth,
        float textHeight,
        boolean hasIcon
    ) {
        float iconX;
        float iconY;
        float textX;
        float textY;
        IconPlacement placement = spec.iconPlacement();
        if (!hasIcon) {
            iconX = 0f;
            iconY = 0f;
            textX = pad;
            textY = (height - textHeight) * 0.5f;
            iconHolder.setVisible(false);
            textLabel.setAlignment(Align.center);
        } else if (placement == IconPlacement.RIGHT) {
            textX = pad;
            textY = (height - textHeight) * 0.5f;
            iconX = width - pad - iconWidth;
            iconY = (height - iconHeight) * 0.5f;
        } else if (placement == IconPlacement.TOP) {
            iconX = (width - iconWidth) * 0.5f;
            iconY = height - pad - iconHeight;
            textX = pad;
            textY = pad;
            textLabel.setAlignment(Align.center);
        } else if (placement == IconPlacement.BOTTOM) {
            iconX = (width - iconWidth) * 0.5f;
            iconY = pad;
            textX = pad;
            textY = height - pad - textHeight;
            textLabel.setAlignment(Align.center);
        } else {
            iconX = pad;
            iconY = (height - iconHeight) * 0.5f;
            textX = pad + iconWidth + gap;
            textY = (height - textHeight) * 0.5f;
        }

        iconHolder.setPosition(iconX, iconY);
        textHolder.setPosition(textX, textY);
        textLabel.setPosition(0f, 0f);
        if (iconActor != null) {
            iconActor.setPosition(0f, 0f);
            iconActor.setOrigin(iconHolder.getWidth() * 0.5f, iconHolder.getHeight() * 0.5f);
        }
        textLabel.setOrigin(textHolder.getWidth() * 0.5f, textHolder.getHeight() * 0.5f);
    }

    public NotificationSpec spec() {
        return spec;
    }

    public Group content() {
        return content;
    }

    public Actor iconActor() {
        return iconActor;
    }

    public Actor textActor() {
        return textLabel;
    }

    public boolean hasIcon() {
        return iconHolder.isVisible();
    }

    public void resetContentTransform() {
        content.clearActions();
        content.setScale(1f);
        content.setPosition(0f, 0f);
        content.getColor().a = 1f;
    }

    public void prepareEnter(NotificationMotion iconMotion, NotificationMotion textMotion) {
        Color iconColor = iconActor.getColor();
        iconColor.a = iconMotion.startAlpha;
        iconActor.setColor(iconColor);
        iconActor.setScale(iconMotion.startScale);
        iconActor.setPosition(iconMotion.offsetX, iconMotion.offsetY);

        Color textColor = textLabel.getColor();
        textColor.a = textMotion.startAlpha;
        textLabel.setColor(textColor);
        textLabel.setScale(textMotion.startScale);
        textLabel.setPosition(textMotion.offsetX, textMotion.offsetY);
    }

    public void restoreRestingChildren() {
        iconActor.clearActions();
        textLabel.clearActions();
        iconActor.setScale(1f);
        textLabel.setScale(1f);
        iconActor.setPosition(0f, 0f);
        textLabel.setPosition(0f, 0f);
        Color iconColor = iconActor.getColor();
        iconColor.a = 1f;
        iconActor.setColor(iconColor);
        Color labelColor = textLabel.getColor();
        labelColor.a = 1f;
        textLabel.setColor(labelColor);
    }

    private static ResolvedIcon animatedResolvedIcon(NotificationSpec spec) {
        Animation<TextureRegion> animation = spec.iconAnimation();
        float size = spec.iconAnimationSize() * spec.iconScale();
        TextureRegion firstFrame = animation.getKeyFrame(0f);
        return new ResolvedIcon(firstFrame, size, size, spec.iconScale());
    }

    private static ResolvedIcon staticRegionResolvedIcon(NotificationSpec spec) {
        float size = spec.iconRegionSize() * spec.iconScale();
        return new ResolvedIcon(spec.iconRegion(), size, size, spec.iconScale());
    }

    private Actor buildIconCluster(
        ResolvedIcon resolvedIcon,
        HudPamActor pamActor,
        float iconWidth,
        float iconHeight,
        float pamWidth,
        float pamHeight,
        float clusterWidth,
        float clusterHeight,
        float clusterGap,
        IconPlacement placement
    ) {
        boolean hasNote = resolvedIcon.isPresent();
        boolean hasPam = pamActor != null;
        if (hasNote && !hasPam) {
            Image iconImage = spec.iconAnimation() != null
                ? new AnimatedIconImage(spec.iconAnimation())
                : new Image(resolvedIcon.region());
            iconImage.setSize(iconWidth, iconHeight);
            return iconImage;
        }

        Group cluster = new Group();
        cluster.setTransform(true);
        cluster.setTouchable(Touchable.disabled);
        cluster.setSize(Math.max(1f, clusterWidth), Math.max(1f, clusterHeight));

        boolean pamOnRight = placement == IconPlacement.RIGHT;
        float noteX = 0f;
        float pamX = 0f;
        if (hasNote && hasPam) {
            if (pamOnRight) {
                noteX = 0f;
                pamX = iconWidth + clusterGap;
            } else {
                pamX = 0f;
                noteX = pamWidth + clusterGap;
            }
        }

        if (hasPam) {
            pamActor.setSize(pamWidth, pamHeight);
            pamActor.setPosition(pamX, Math.max(0f, (clusterHeight - pamHeight) * 0.5f));
            cluster.addActor(pamActor);
        }
        if (hasNote) {
            Image iconImage = spec.iconAnimation() != null
                ? new AnimatedIconImage(spec.iconAnimation())
                : new Image(resolvedIcon.region());
            iconImage.setSize(iconWidth, iconHeight);
            iconImage.setPosition(noteX, Math.max(0f, (clusterHeight - iconHeight) * 0.5f));
            cluster.addActor(iconImage);
        }
        return cluster;
    }

    private static HudPamActor createPamActor(NotificationSpec spec) {
        if (spec == null || !spec.hasPam() || AssetManager.player == null) {
            return null;
        }
        try {
            AssetManager.player.loadSync(spec.pamPath());
            return new HudPamActor(
                spec.pamPath(),
                spec.pamClip(),
                spec.pamLoop(),
                spec.pamDrawScale(),
                0f,
                0f
            );
        } catch (RuntimeException exception) {
            Gdx.app.error("NotificationView", "Failed to create scoring PAM " + spec.pamPath(), exception);
            return null;
        }
    }
}
