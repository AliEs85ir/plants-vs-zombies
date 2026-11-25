package app.view.loading.icons.content;

import java.util.ArrayList;

public class AboutContent {
    public String title = "About";
    public String closeLabel = "Close";
    public ArrayList<Section> sections = new ArrayList<>();

    public static class Section {
        public String id = "";
        public String title = "";
        public String body = "";
        public boolean enabled = true;
    }
}
