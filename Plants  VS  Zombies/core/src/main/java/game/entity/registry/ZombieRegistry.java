package game.entity.registry;

import game.entity.zombie.data.properties.Property;
import game.entity.zombie.data.properties.armor.ArmorProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZombieRegistry {
    public static final Map<String, Property> DEFINITIONS = new HashMap<>();
    public static final Map<String, ArmorProperty> ARMORS = new HashMap<>();

    private ZombieRegistry() {
    }

    public static void register(
            Property definition)
    {
        if (definition != null && definition.getAlias() != null) {
            DEFINITIONS.put(definition.getAlias(), definition);
        }
    }


    public static void register (ArmorProperty armorProperty) {
        if(armorProperty != null && armorProperty.getAlias() != null) {
            ARMORS.put(armorProperty.getAlias(), armorProperty);

        }
    }

    public static Property getZombie(String type)
    {
        return DEFINITIONS.get(type);
    }
    public static ArmorProperty getArmor(String type) {return ARMORS.get(type);}

    public static List<Property> getAllZombies() {
        return new ArrayList<>(DEFINITIONS.values());
    }
}
