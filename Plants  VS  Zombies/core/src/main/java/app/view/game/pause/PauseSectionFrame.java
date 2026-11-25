package app.view.game.pause;

import app.view.ui.PanelStyle;
import app.view.ui.SkinPanel;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import game.component.AssetManager;

public class PauseSectionFrame extends SkinPanel {
    private final Table body;

    public PauseSectionFrame(PauseLayout layout, String iconId, String title) {
        this(layout, layout.backgrounds.section, iconId, title);
    }

    public PauseSectionFrame(PauseLayout layout, PanelStyle background, String iconId, String title) {
        super(background);
        pad(layout.spacing.innerPadding);
        top();

        Table header = new Table();
        Image icon = icon(iconId, layout.header.sectionIconSize);
        if (icon != null) {
            header.add(icon)
                .size(layout.header.sectionIconSize)
                .padRight(layout.header.sectionIconGap);
        }
        header.add(TextSpec.of(layout.text.sectionTitle).build(title)).growX().left();
        add(header).growX().height(layout.header.sectionHeaderHeight).row();

        SkinPanel separator = new SkinPanel(layout.backgrounds.separator);
        add(separator).growX()
            .height(layout.spacing.separatorHeight)
            .padTop(layout.header.separatorGap)
            .padBottom(layout.header.paddingBottom)
            .row();

        body = new Table();
        body.top();
        add(body).growX().top();
    }

    public Table body() {
        return body;
    }

    private static Image icon(String id, float size) {
        if (id == null || id.isBlank()) {
            return null;
        }
        TextureRegion region = AssetManager.textureBank.region(id);
        if (region == null) {
            return null;
        }
        Image image = new Image(new TextureRegionDrawable(region));
        image.setSize(size, size);
        return image;
    }
}
