package app.controller.menu;

import app.enums.NewsOrder;
import app.model.App;
import app.model.News;
import app.model.user.User;
import app.service.NewsQuery;
import app.service.NewsService;
import common.Result;

import java.util.List;

public class NewsMenuController {

    public Result showUnreadNews() {
        User user = App.getCurrentUser();
        if (user == null) {
            return new Result(false, "There are no unread news\n");
        }
        NewsService.ensureReady(user);
        List<News> visible = NewsQuery.visible(user.getNewsList(), false, NewsOrder.NEWEST_FIRST);
        if (visible.isEmpty()) {
            return new Result(false, "There are no unread news\n");
        }
        String text = NewsService.formatForConsole(visible, "yyyy-MM-dd HH:mm");
        NewsService.markDisplayed(visible);
        return new Result(true, text);
    }

    public Result showAllNews() {
        User user = App.getCurrentUser();
        if (user == null) {
            return new Result(false, "There are no news\n");
        }
        NewsService.ensureReady(user);
        List<News> visible = NewsQuery.visible(user.getNewsList(), true, NewsOrder.NEWEST_FIRST);
        if (visible.isEmpty()) {
            return new Result(false, "There are no news\n");
        }
        String text = NewsService.formatForConsole(visible, "yyyy-MM-dd HH:mm");
        NewsService.markDisplayed(visible);
        return new Result(true, text);
    }
}
