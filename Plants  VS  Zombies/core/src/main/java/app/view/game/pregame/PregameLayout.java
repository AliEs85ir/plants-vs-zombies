package app.view.game.pregame;

import app.view.game.pregame.visual.providers.IdlePlantVisualProvider;
import app.view.game.pregame.visual.providers.PlantVisualProvider;
import app.view.ui.button.ButtonTheme;
import app.view.ui.PanelStyle;
import app.view.ui.text.TextAlign;
import app.view.ui.text.TextStyle;
import app.view.ui.UiTheme;
import com.badlogic.gdx.graphics.Color;

public class PregameLayout {

    public final Spacing spacing = new Spacing();
    public final Buttons buttons = new Buttons();
    public final Intro intro = new Intro();
    public final Selection selection = new Selection();
    public final Cards cards = new Cards();
    public final Backgrounds backgrounds = new Backgrounds();
    public final Text text = new Text();

    public static PregameLayout defaults() {
        return new PregameLayout();
    }

    public static PregameLayout couchPanel() {
        PregameLayout layout = defaults();
        layout.selection.width = 610f;
        layout.selection.height = 620f;
        layout.selection.topSectionHeight = 118f;
        layout.selection.footerHeight = 42f;
        layout.selection.topToMiddleGap = 10f;
        layout.selection.middleToFooterGap = 10f;
        layout.selection.counterWidth = 96f;
        layout.selection.selectedBarWidth = 96f;
        layout.cards.gridColumns = 4;
        layout.buttons.selectionFooter.width = 92f;
        layout.buttons.selectionFooter.height = 38f;
        layout.buttons.selectionFooter.minWidth = 72f;
        layout.buttons.selectionFooter.gap = 6f;
        layout.buttons.selectionFooter.labelScale = 0.68f;
        return layout;
    }

    public static class Spacing {
        public float contentPadding = 14f;
        public float innerPadding   = 10f;
        public float tightPadding   = 5f;
    }

    public static class Buttons {
        public String primaryStyle   = UiTheme.BTN_GREEN;
        public String secondaryStyle = UiTheme.BTN_BROWN;
        public String tertiaryStyle  = UiTheme.BTN_PURPLE;

        public final ButtonTheme introFooter     = new ButtonTheme();
        public final ButtonTheme selectionFooter = new ButtonTheme();
        public final ButtonTheme infoActions     = new ButtonTheme();

        public Buttons() {
            introFooter.minWidth   = 90f;
            introFooter.gap        = 12f;
            introFooter.height     = 36f;
            introFooter.padX       = 8f;
            introFooter.padY       = 2f;
            introFooter.labelScale = 1f;

            selectionFooter.width      = 125f;
            selectionFooter.height     = 45f;
            selectionFooter.minWidth   = 84f;
            selectionFooter.gap        = 10f;
            selectionFooter.padX       = 6f;
            selectionFooter.padY       = 1f;
            selectionFooter.labelScale = 0.75f;

            infoActions.width      = 120f;
            infoActions.height     = 40f;
            infoActions.minWidth   = 64f;
            infoActions.gap        = 8f;
            infoActions.padX       = 4f;
            infoActions.padY       = 0f;
            infoActions.labelScale = 0.75f;
        }
    }

    public static class Intro {
        public float width  = 600f;
        public float height = 430f;

        public float seasonBannerHeight = 48f;
        public float chapterBadgeHeight = 30f;
        public float titleGap           = 14f;

        public float factRowGap       = 14f;
        public float factCardHeight   = 36f;
        public float factCardMinWidth = 92f;
        public float factCardGap      = 10f;
        public int   maxFactCards     = 4;

        public float descriptionGap       = 14f;
        public float descriptionMaxHeight = 168f;

        public float tooltipGap       = 12f;
        public float tooltipHeight    = 34f;
        public float tooltipButtonGap = 10f;
        public float footerHeight     = 42f;
    }

    public static class Selection {
        public float width  = 920f;
        public float height = 560f;

        public float topSectionHeight  = 190f;
        public float footerHeight      = 38f;
        public float topToMiddleGap    = 16f;
        public float middleToFooterGap = 20f;

        public float counterWidth = 108f;
        public float counterGap   = 12f;

        public float selectedBarWidth = 108f;
        public float selectedBarGap   = 12f;

        public float infoPadding          = 10f;
        public float visualCardSize       = 112f;
        public float visualCardPadding    = 8f;
        public float visualCardGap        = 12f;
        public float descriptionMinHeight = 36f;
        public float upgradeProgressHeight = 12f;
        public float upgradeProgressGap    = 3f;
        public float upgradeIconHeight     = 16f;

        public String visualCardBackgroundKey = "IMAGE_UI_PACKETS_MODERNDAY";
        public PlantVisualProvider visualProvider = new IdlePlantVisualProvider();
    }

    public static class Cards {
        public int   gridColumns     = 5;
        public float gridMinScale    = 0.12f;
        public float gridMaxScale    = 1.0f;
        public float cellGapX        = 4f;
        public float cellGapY        = 6f;
        public float selectedSlotGap = 4f;
        public float scrollbarWidth  = 7f;
        public float costPaddingX    = 9f;
        public float costPaddingY    = 8f;
        public float costFontScale   = 0.55f;
        public float hoverLift       = 5f;
        public float hoverScale      = 1.06f;
        public float hoverSpeed      = 10f;
        public float selectAnimTime  = 0.22f;
    }

    public static class Backgrounds {
        public final PanelStyle popupRoot      = PanelStyle.of(UiTheme.BG_DIALOG);
        public final PanelStyle seasonBanner   = PanelStyle.of(UiTheme.BG_RIBBON, UiTheme.BG_TAB);
        public final PanelStyle chapterBadge   = PanelStyle.of(UiTheme.BG_COST, UiTheme.BG_TAB);
        public final PanelStyle factCard       = PanelStyle.of(UiTheme.BG_REWARD, UiTheme.BG_TAB);
        public final PanelStyle descriptionBox = PanelStyle.of(UiTheme.BG_GUARANTEE, UiTheme.BG_TAB);
        public final PanelStyle tooltip        = PanelStyle.of(UiTheme.BG_COST, UiTheme.BG_TAB);
        public final PanelStyle infoPanel      = PanelStyle.of(UiTheme.BG_BUNDLE);
        public final PanelStyle visualCard     = PanelStyle.of(UiTheme.BG_GUARANTEE, UiTheme.BG_PANEL);
        public final PanelStyle counterBadge   = PanelStyle.of(UiTheme.BG_COST, UiTheme.BG_TAB);
        public final PanelStyle selectedPanel  = PanelStyle.of(UiTheme.BG_TAB, UiTheme.BG_PANEL);
        public final PanelStyle gridPanel      = PanelStyle.of(UiTheme.BG_GUARANTEE);
    }

    public static class Text {
        public final TextStyle seasonTitle = TextStyle.of(UiTheme.LBL_BIG_OUTLINE)
            .align(TextAlign.CENTER).color(UiTheme.ACCENT);

        public final TextStyle chapterBadge = TextStyle.of(UiTheme.LBL_MEDIUM_OUTLINE)
            .align(TextAlign.CENTER).color(UiTheme.TEXT_PRIMARY);

        public final TextStyle fact = TextStyle.of(UiTheme.LBL_MEDIUM_OUTLINE)
            .align(TextAlign.CENTER).color(UiTheme.ACCENT).scale(0.88f).wrap(true);

        public final TextStyle description = TextStyle.of(UiTheme.LBL_DEFAULT)
            .align(TextAlign.TOP_LEFT).color(UiTheme.TEXT_PRIMARY).scale(1.12f).wrap(true);

        public final TextStyle error = TextStyle.of(UiTheme.LBL_SECONDARY)
            .align(TextAlign.TOP_LEFT).color(UiTheme.TEXT_ERROR).wrap(true);

        public final TextStyle hint = TextStyle.of(UiTheme.LBL_SECONDARY)
            .align(TextAlign.CENTER).color(UiTheme.TEXT_MUTED).wrap(true);

        public Color hintIdleColor   = UiTheme.TEXT_MUTED;
        public Color hintActiveColor = UiTheme.TEXT_PRIMARY;

        public final TextStyle plantName = TextStyle.of(UiTheme.LBL_MEDIUM_OUTLINE)
            .align(TextAlign.LEFT).color(UiTheme.ACCENT);

        public final TextStyle plantMeta = TextStyle.of(UiTheme.LBL_SECONDARY)
            .align(TextAlign.RIGHT).color(UiTheme.TEXT_SECONDARY);

        public final TextStyle plantDescription = TextStyle.of(UiTheme.LBL_DEFAULT)
            .align(TextAlign.TOP_LEFT).color(UiTheme.TEXT_PRIMARY).wrap(true);

        public final TextStyle plantStats = TextStyle.of(UiTheme.LBL_SECONDARY)
            .align(TextAlign.TOP_LEFT).color(UiTheme.TEXT_STAT).wrap(true);

        public final TextStyle upgradeProgress = TextStyle.of(UiTheme.LBL_SECONDARY)
            .align(TextAlign.LEFT).color(UiTheme.TEXT_PRIMARY).scale(0.72f);

        public final TextStyle upgradeCoins = TextStyle.of(UiTheme.LBL_SECONDARY)
            .align(TextAlign.LEFT).color(UiTheme.TEXT_SECONDARY).scale(0.68f);

        public final TextStyle counter = TextStyle.of(UiTheme.LBL_MEDIUM_OUTLINE)
            .align(TextAlign.CENTER).color(UiTheme.ACCENT);

        public final TextStyle status = TextStyle.of(UiTheme.LBL_SECONDARY)
            .align(TextAlign.CENTER).color(UiTheme.TEXT_SECONDARY).wrap(true);

        public final TextStyle selectedHeader = TextStyle.of(UiTheme.LBL_SECONDARY)
            .align(TextAlign.CENTER).color(UiTheme.ACCENT);

        public Color statusIdleColor  = UiTheme.TEXT_SECONDARY;
        public Color statusReadyColor = UiTheme.TEXT_SUCCESS;
        public Color statusErrorColor = UiTheme.TEXT_ERROR;
    }

    public PregameLayout withColumns(int columns) {
        cards.gridColumns = Math.max(1, columns);
        return this;
    }

    public PregameLayout withSelectionSize(float width, float height) {
        selection.width  = width;
        selection.height = height;
        return this;
    }

    public PregameLayout withIntroSize(float width, float height) {
        intro.width  = width;
        intro.height = height;
        return this;
    }

    public float gridViewportWidth() {
        return selection.width
             - selection.selectedBarWidth
             - selection.selectedBarGap
             - (spacing.contentPadding * 2f)
             - (spacing.innerPadding * 2f)
             - cards.scrollbarWidth;
    }

    public float gridCellWidth() {
        int gaps = Math.max(0, cards.gridColumns - 1);
        float usable = gridViewportWidth() - (gaps * cards.cellGapX);
        return Math.max(1f, usable / cards.gridColumns);
    }
}
