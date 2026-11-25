package game.component.contacts;

import java.util.ArrayDeque;
import java.util.Queue;

public class ContactBuffer {
    private static final Queue<Runnable> pending = new ArrayDeque<>();

    public static void add(Runnable action) {
        pending.add(action);
    }

    public static void flush() {
        while (!pending.isEmpty()) pending.poll().run();
    }
}
