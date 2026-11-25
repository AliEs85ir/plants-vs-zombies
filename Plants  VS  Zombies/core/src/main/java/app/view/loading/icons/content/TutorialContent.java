package app.view.loading.icons.content;

import java.util.ArrayList;

public class TutorialContent {
    public String title = "Lawn Tutorial";
    public String closeLabel = "Close";
    public String previousLabel = "Back";
    public String nextLabel = "Next";
    public ArrayList<Section> sections = new ArrayList<>();

    public static class Section {
        public String id = "";
        public String title = "";
        public String body = "";
        public boolean enabled = true;
    }
}
