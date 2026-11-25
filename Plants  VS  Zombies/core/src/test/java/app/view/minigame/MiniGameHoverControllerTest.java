package app.view.minigame;

import com.badlogic.gdx.math.Rectangle;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class MiniGameHoverControllerTest {
    @Test
    void stayingOnTheSameMiniGameDoesNotReplaceTheHoveredEntry() {
        MiniGameDefinition first = definition("A", 0f);
        MiniGameDefinition second = definition("B", 40f);
        MiniGameHoverController.Entry firstEntry = new MiniGameHoverController.Entry(first, List.of());
        MiniGameHoverController.Entry secondEntry = new MiniGameHoverController.Entry(second, List.of());
        MiniGameHoverController controller = new MiniGameHoverController(List.of(firstEntry, secondEntry));

        controller.setHovered(first);
        assertSame(firstEntry, controller.hovered());
        MiniGameHoverController.Entry hovered = controller.hovered();
        controller.setHovered(first);
        assertSame(hovered, controller.hovered());

        controller.setHovered(second);
        assertSame(secondEntry, controller.hovered());

        controller.setHovered(null);
        assertNull(controller.hovered());
    }

    private static MiniGameDefinition definition(String id, float x) {
        return new MiniGameDefinition(id, new Rectangle(x, 0f, 30f, 30f), "none", "", true, List.of());
    }
}
