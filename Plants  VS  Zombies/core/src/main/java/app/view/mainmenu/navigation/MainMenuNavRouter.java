package app.view.mainmenu.navigation;

import app.view.mainmenu.MainMenuTab;
import app.view.mainmenu.config.MainMenuConfig;
import app.view.screen.ScreenManager;
import com.badlogic.gdx.Gdx;
import plants_vs_zombies.PlantsVSZombies;

import java.util.function.Consumer;

public final class MainMenuNavRouter {
    private final PlantsVSZombies game;
    private final Consumer<MainMenuTab> tabSwitcher;

    public MainMenuNavRouter(PlantsVSZombies game, Consumer<MainMenuTab> tabSwitcher) {
        this.game = game;
        this.tabSwitcher = tabSwitcher;
    }

    public void activate(MainMenuConfig.NavIconConfig icon) {
        if (icon == null || !icon.clickable()) {
            return;
        }
        switch (icon.action) {
            case "go_start_menu", "back" -> ScreenManager.showLoading(game);
            case "show_tab" -> {
                MainMenuTab tab = MainMenuTab.fromId(icon.tab.isBlank() ? icon.id : icon.tab);
                if (tabSwitcher != null) {
                    tabSwitcher.accept(tab);
                }
            }
            default -> Gdx.app.log("MainMenuNavRouter", "Unknown action: " + icon.action);
        }
    }
}
