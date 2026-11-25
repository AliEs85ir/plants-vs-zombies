package game.entity.zombie.data.properties.armor;

import app.enums.zombie.Armor.ArmorFlag;
import app.enums.zombie.Armor.ArmorLayers;
import app.enums.zombie.Armor.ArmorType;

import java.util.ArrayList;

public class ArmorProperty{
    private String alias;
    private ArmorType ArmorType;
    private int BaseHealth;
    private ArrayList<ArmorFlag> ArmorFlags;
    private ArrayList<ArmorLayers> armorLayers;
    private ArrayList<Double> armorLevelHealth;
    private String impactSoundEvent;
    private String dropSoundEvent;
    private ArrayList<String> particleLayerOverride;


    public String getAlias() {
        return alias;
    }

    public int getBaseHealth() {
        return BaseHealth;
    }

    public ArmorType getArmorType() {
        return ArmorType;
    }

    public ArrayList<ArmorFlag> getArmorFlags() {
        return ArmorFlags;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setArmorType(ArmorType armorType) {
        ArmorType = armorType;
    }

    public void setBaseHealth(int baseHealth) {
        BaseHealth = baseHealth;
    }

    public void setArmorFlags(ArrayList<ArmorFlag> armorFlags) {
        ArmorFlags = armorFlags;
    }
}
