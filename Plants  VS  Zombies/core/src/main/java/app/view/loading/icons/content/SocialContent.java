package app.view.loading.icons.content;

import java.util.ArrayList;

public class SocialContent {
    public String title = "Find Us Online";
    public String closeLabel = "Close";
    public String intro = "";
    public String openLabel = "Open";
    public ArrayList<Platform> platforms = new ArrayList<>();

    public static class Platform {
        public String id = "";
        public String displayName = "";
        public String username = "";
        public String url = "";
        public boolean enabled = true;
        public int order;
    }
}
