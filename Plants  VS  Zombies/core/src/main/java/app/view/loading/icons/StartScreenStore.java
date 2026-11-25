package app.view.loading.icons;

import app.model.App;
import app.model.user.User;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.ObjectMap;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class StartScreenStore {
    public static final String STATE_PATH = "start_screen_state.json";
    private static final DateTimeFormatter ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final State state;
    private String sessionPreviousVisit;
    private boolean sessionMarked;

    public StartScreenStore() {
        this(loadState());
    }

    StartScreenStore(State state) {
        this.state = state != null ? state : new State();
        if (this.state.lastVisitByUsername == null) {
            this.state.lastVisitByUsername = new ObjectMap<>();
        }
    }

    public void beginSession(int startingDonationCount) {
        if (state.donationLikeCount <= 0 && startingDonationCount > 0) {
            state.donationLikeCount = startingDonationCount;
            save();
        }
        if (sessionMarked) {
            return;
        }
        sessionMarked = true;
        User user = App.getCurrentUser();
        if (user == null || user.getUsername() == null || user.getUsername().isBlank()) {
            sessionPreviousVisit = null;
            return;
        }
        String username = user.getUsername();
        sessionPreviousVisit = state.lastVisitByUsername.get(username);
        state.lastVisitByUsername.put(username, now());
        save();
    }

    public String previousVisit() {
        return sessionPreviousVisit;
    }

    public boolean hasLoggedInUser() {
        return App.getCurrentUser() != null;
    }

    public int donationLikeCount() {
        return Math.max(0, state.donationLikeCount);
    }

    public int incrementDonationLikes() {
        state.donationLikeCount = Math.max(0, state.donationLikeCount) + 1;
        save();
        return state.donationLikeCount;
    }

    public static String now() {
        return LocalDateTime.now().format(ISO);
    }

    public static String format(LocalDateTime time, String pattern) {
        DateTimeFormatter formatter = (pattern == null || pattern.isBlank())
            ? ISO
            : DateTimeFormatter.ofPattern(pattern);
        return time.format(formatter);
    }

    private void save() {
        try {
            Json json = new Json();
            json.setOutputType(JsonWriter.OutputType.json);
            json.setIgnoreUnknownFields(true);
            FileHandle file = Gdx.files.local(STATE_PATH);
            file.writeString(json.prettyPrint(state), false);
        } catch (Exception exception) {
            Gdx.app.error("StartScreenStore", "Could not save " + STATE_PATH, exception);
        }
    }

    private static State loadState() {
        try {
            FileHandle file = Gdx.files.local(STATE_PATH);
            if (!file.exists()) {
                return new State();
            }
            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            State loaded = json.fromJson(State.class, file);
            return loaded != null ? loaded : new State();
        } catch (Exception exception) {
            Gdx.app.error("StartScreenStore", "Could not load " + STATE_PATH, exception);
            return new State();
        }
    }

    public static class State {
        public int donationLikeCount;
        public ObjectMap<String, String> lastVisitByUsername = new ObjectMap<>();
    }
}
