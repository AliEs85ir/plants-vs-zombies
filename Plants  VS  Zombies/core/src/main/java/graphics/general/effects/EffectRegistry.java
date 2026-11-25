package graphics.general.effects;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class EffectRegistry {

    private final Map<String, EffectDefinition<?>> definitions = new HashMap<>();

    public void register(EffectDefinition<?> definition) {
        if (definition != null) {
            definitions.put(definition.getId(), definition);
        }
    }

    public EffectDefinition<?> get(String id) {
        return definitions.get(id);
    }

    public Collection<EffectDefinition<?>> getAll() {
        return Collections.unmodifiableCollection(definitions.values());
    }

    public void clear() {
        definitions.clear();
    }
}
