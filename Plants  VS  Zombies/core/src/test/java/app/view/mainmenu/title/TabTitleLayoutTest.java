package app.view.mainmenu.title;

import app.view.mainmenu.config.MainMenuConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TabTitleLayoutTest {
    @Test
    void defaultTabsShareTheSameCenteredTopBox() {
        MainMenuConfig.TabTitleConfig titles = new MainMenuConfig.TabTitleConfig();
        titles.sanitize();
        TabTitleLayout.Placement news = TabTitleLayout.place(1280f, 720f, titles.layout, titles.news);
        TabTitleLayout.Placement game = TabTitleLayout.place(1280f, 720f, titles.layout, titles.game);
        TabTitleLayout.Placement profile = TabTitleLayout.place(1280f, 720f, titles.layout, titles.profile);
        TabTitleLayout.Placement settings = TabTitleLayout.place(1280f, 720f, titles.layout, titles.settings);
        assertEquals(news.x, game.x, 0.001f);
        assertEquals(news.y, game.y, 0.001f);
        assertEquals(news.width, game.width, 0.001f);
        assertEquals(news.height, game.height, 0.001f);
        assertEquals(news.x, profile.x, 0.001f);
        assertEquals(news.y, profile.y, 0.001f);
        assertEquals(news.x, settings.x, 0.001f);
        assertEquals(news.y, settings.y, 0.001f);
        assertEquals((1280f - news.width) * 0.5f, news.x, 0.001f);
        assertEquals(720f - titles.layout.titleTopPadding - news.height, news.y, 0.001f);
    }

    @Test
    void sharedLayoutMovesEveryTabTogether() {
        MainMenuConfig.TabTitleLayoutConfig layout = new MainMenuConfig.TabTitleLayoutConfig();
        layout.titleWidth = 400f;
        layout.titleHeight = 80f;
        layout.titleScale = 1f;
        layout.titleTopPadding = 20f;
        layout.titleOffsetX = 12f;
        layout.titleOffsetY = -6f;
        layout.sanitize();
        MainMenuConfig.TabTitleTabConfig tab = new MainMenuConfig.TabTitleTabConfig();
        TabTitleLayout.Placement placement = TabTitleLayout.place(1000f, 600f, layout, tab);
        assertEquals((1000f - 400f) * 0.5f + 12f, placement.x, 0.001f);
        assertEquals(600f - 20f - 80f - 6f, placement.y, 0.001f);
        assertEquals(400f, placement.width, 0.001f);
        assertEquals(80f, placement.height, 0.001f);
    }
}
