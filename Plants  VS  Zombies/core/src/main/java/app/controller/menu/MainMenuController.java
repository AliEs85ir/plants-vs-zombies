package app.controller.menu;

import app.service.AppService;
import common.Result;

public class MainMenuController {

    public Result manageLogout() {
        AppService.logout();
        return new Result(true, "Logged out successfully.");
    }
}
