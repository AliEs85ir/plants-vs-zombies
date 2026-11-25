package app.view.mainmenu.news;

import app.model.News;
import app.view.ui.Solid;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public final class NewsItemWidget extends Table {
    private final News news;

    public NewsItemWidget(
        News news,
        NewsMenuConfig config,
        float wrapWidth,
        boolean last,
        boolean previewOnly,
        Runnable onOpen
    ) {
        this.news = news.sanitize();
        NewsMenuConfig.NewsItemConfig item = config.newsItem;
        float innerWidth = Math.max(40f, wrapWidth - item.newsItemPaddingLeft - item.newsItemPaddingRight);
        pad(item.newsItemPaddingTop, item.newsItemPaddingLeft, item.newsItemPaddingBottom, item.newsItemPaddingRight);
        defaults().left().top();

        float titleWidth = capped(innerWidth, config.newsTitle.titleMaxWidth);
        float messageWidth = capped(innerWidth, config.newsMessage.messageMaxWidth);

        String titleText = news.isRead()
            ? news.getTitle()
            : (config.listPane == null ? "• " : config.listPane.unreadMarker + " ") + news.getTitle();
        Label title = text(
            titleText,
            NewsUiFonts.resolve(config.newsTitle.titleFont, config.newsTitle.titleFontFallback),
            NewsMenuConfig.color(config.newsTitle.titleColor, Color.valueOf("F4E7B0")),
            config.newsTitle.titleFontSize,
            config.newsTitle.titleLineSpacing,
            NewsMenuConfig.textAlign(config.newsTitle.titleAlignment),
            titleWidth
        );

        String bodyText = previewOnly
            ? news.preview(config.listPane == null ? 90 : config.listPane.previewMaxChars)
            : news.getMessage();
        Label message = text(
            bodyText,
            NewsUiFonts.resolve(config.newsMessage.messageFont, config.newsMessage.messageFontFallback),
            NewsMenuConfig.color(config.newsMessage.messageColor, Color.valueOf("E4DCC4")),
            config.newsMessage.messageFontSize,
            config.newsMessage.messageLineSpacing,
            NewsMenuConfig.textAlign(config.newsMessage.messageAlignment),
            messageWidth
        );
        Label time = text(
            news.formattedTime(config.newsTime.timePattern) + "  ·  " + news.statusLabel()
                + (news.getCategory() == null ? "" : "  ·  " + news.getCategory().name()),
            NewsUiFonts.resolve(config.newsTime.timeFont, config.newsTime.timeFontFallback),
            NewsMenuConfig.color(config.newsTime.timeColor, Color.valueOf("E8C84A")),
            config.newsTime.timeFontSize,
            1f,
            NewsMenuConfig.textAlign(config.newsTime.timeAlignment),
            innerWidth
        );

        add(title).width(titleWidth).padBottom(config.newsTitle.titleMessageSpacing);
        row();
        add(message).width(messageWidth).padBottom(config.newsMessage.messageTimeSpacing);
        row();
        add(time)
            .width(innerWidth)
            .padLeft(config.newsTime.timeOffsetX)
            .padTop(config.newsTime.timeTopPadding + config.newsTime.timeOffsetY)
            .padBottom(config.newsTime.timeBottomPadding);
        if (config.separator.enabled && !last) {
            row();
            Image line = new Image(separator(config));
            add(line)
                .width(innerWidth)
                .height(Math.max(1f, config.separator.height))
                .padTop(config.separator.topPadding)
                .padBottom(config.separator.bottomPadding);
        }

        if (onOpen != null) {
            addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    onOpen.run();
                }
            });
            Color rest = getColor().cpy();
            Color hover = NewsMenuConfig.color(
                config.listPane == null ? null : config.listPane.selectedTint,
                new Color(1f, 0.95f, 0.75f, 0.25f)
            );
            addListener(new InputListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    if (pointer == -1) {
                        setColor(hover);
                    }
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    if (pointer == -1) {
                        setColor(rest);
                    }
                }
            });
        }
    }

    public News news() {
        return news;
    }

    private static float capped(float innerWidth, float maxWidth) {
        if (maxWidth <= 0f) {
            return innerWidth;
        }
        return Math.min(innerWidth, maxWidth);
    }

    private static Label text(
        String value,
        BitmapFont font,
        Color color,
        float scale,
        float lineSpacing,
        int align,
        float width
    ) {
        Label.LabelStyle style = new Label.LabelStyle(font, color);
        Label label = new Label(value == null ? "" : value, style);
        label.setWrap(true);
        label.setAlignment(align);
        label.setFontScale(scale, scale * Math.max(0.5f, lineSpacing));
        label.setWidth(width);
        return label;
    }

    private static Drawable separator(NewsMenuConfig config) {
        return Solid.drawable(
            NewsMenuConfig.color(config.separator.color, new Color(1f, 1f, 1f, 0.2f)),
            1f,
            Math.max(1f, config.separator.height)
        );
    }
}
