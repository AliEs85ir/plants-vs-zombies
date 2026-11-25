package server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmbeddedServerPropertyTest {
    @Test
    void embedOffByDefault() {
        System.clearProperty(EmbeddedServer.PROPERTY);
        assertFalse(EmbeddedServer.shouldEmbed());
        System.setProperty(EmbeddedServer.PROPERTY, "true");
        try {
            assertTrue(EmbeddedServer.shouldEmbed());
        } finally {
            System.clearProperty(EmbeddedServer.PROPERTY);
        }
    }
}
