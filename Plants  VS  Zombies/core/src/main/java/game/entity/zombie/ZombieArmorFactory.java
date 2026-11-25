package game.entity.zombie;

import game.entity.registry.ZombieRegistry;
import game.entity.zombie.data.properties.armor.ArmorProperty;

public class ZombieArmorFactory {
    private ZombieArmorFactory() {}

    public static ZombieArmor create(String armorAlias) {
        ArmorProperty property = ZombieRegistry.getArmor(armorAlias);
        if (property == null) {
            throw new IllegalArgumentException("No armor found with name : " + armorAlias);
        }
        return new ZombieArmor(property);
    }

    public static ZombieArmor create(ArmorProperty property) {
        if (property == null) {
            throw new IllegalArgumentException("No armor found.");
        }
        return new ZombieArmor(property);
    }
}
