package app.controller.menu;

import app.model.App;
import app.model.user.User;
import app.service.AppService;
import common.Result;

public class SettingsMenuController {
    public Result changeDifficulty(int level) {
        User user = App.getCurrentUser();
        if (user == null) {
            return new Result(false, "no user is logged in");
        }
        int clamped = User.clampDifficultyLevel(level);
        user.setDifficultyLevel(clamped);
        AppService.syncGameData();
        return new Result(true, "difficulty successfully changed to " + clamped);
    }
}
