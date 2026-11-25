package app.view.loading.icons.content;

import java.util.ArrayList;

public class FaqContent {
    public String title = "Lawn FAQ";
    public String closeLabel = "Close";
    public String hint = "";
    public String collapsedPrefix = "▸ ";
    public String expandedPrefix = "▾ ";
    public ArrayList<Item> items = new ArrayList<>();

    public static class Item {
        public String id = "";
        public String question = "";
        public String answer = "";
        public boolean enabled = true;
    }
}
