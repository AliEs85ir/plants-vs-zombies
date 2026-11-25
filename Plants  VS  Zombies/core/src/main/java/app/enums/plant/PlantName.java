package app.enums.plant;

public enum PlantName {
    SUNFLOWER("Sunflower", 2.0f),
    TWIN_SUNFLOWER("Twin Sunflower", 2.0f),
    SUN_SHROOM("Sun-shroom", 2.5f),
    PRIMAL_SUNFLOWER("Primal Sunflower", 2.0f),
    GOLD_BLOOM("Gold Bloom", 0.0f),
    PEASHOOTER("Peashooter", 3.0f),
    REPEATER("Repeater", 3.0f),
    THREEPEATER("Threepeater", 3.5f),
    SNOW_PEA("Snow Pea", 3.5f),
    ROTOBAGA("Rotobaga", 3.0f),
    PEA_POD("Pea Pod", 3.5f),
    SPLIT_PEA("Split Pea", 3.0f),
    CITRON("Citron", 2.0f),
    CAULIPOWER("Caulipower", 4.0f),
    ELECTRIC_BLUEBERRY("Electric Blueberry", 3.5f),
    BOWLING_BULB("Bowling Bulb", 3.0f),
    CACTUS("Cactus", 15.0f),
    FIRE_PEASHOOTER("Fire Peashooter", 3.0f),
    STARFRUIT("Starfruit", 3.0f),
    GOO_PEASHOOTER("Goo Peashooter", 3.5f),
    MEGA_GATLING_PEA("Mega Gatling Pea", 4.0f),
    SEA_SHROOM("Sea-shroom", 2.5f),
    PUFF_SHROOM("Puff-shroom", 2.5f),
    FUME_SHROOM("Fume-shroom", 3.0f),
    CABBAGE_PULT("Cabbage-pult", 2.5f),
    KERNEL_PULT("Kernel-pult", 3.0f),
    MELON_PULT("Melon-pult", 3.0f),
    WINTER_MELON("Winter Melon", 3.5f),
    PEPPER_PULT("Pepper-pult", 3.0f),
    POTATO_MINE("Potato Mine", 1.5f),
    PRIMAL_POTATO_MINE("Primal Potato Mine", 1.5f),
    CHERRY_BOMB("Cherry Bomb", 0.0f),
    SQUASH("Squash", 1.5f),
    GRAPESHOT("Grapeshot", 0.0f),
    JALAPENO("Jalapeno", 0.0f),
    DOOM_SHROOM("Doom-shroom", 0.0f),
    TANGLE_KELP("Tangle Kelp", 1.5f),
    ICEBERG_LETTUCE("Iceberg Lettuce", 3.0f),
    BONK_CHOY("Bonk Choy", 4.0f),
    PHAT_BEET("Phat Beet", 3.0f),
    CHOMPER("Chomper", 2.0f),
    WASABI_WHIP("Wasabi Whip", 4.0f),
    KIWIBEAST("Kiwibeast", 3.5f),
    WALL_NUT("Wall-nut", 4.0f),
    TALL_NUT("Tall-nut", 4.0f),
    ENDURIAN("Endurian", 3.5f),
    GARLIC("Garlic", 3.0f),
    SWEET_POTATO("Sweet Potato", 3.5f),
    EXPLODE_O_NUT("Explode-o-nut", 4.0f),
    PUMPKIN("Pumpkin", 4.0f),
    SUN_BEAN("Sun Bean", 1.5f),
    TORCHWOOD("Torchwood", 6.0f),
    MAGNET_SHROOM("Magnet-shroom", 3.0f),
    HYPNO_SHROOM("Hypno-shroom", 1.5f),
    CAT_TAIL("Cat-tail", 3.5f),
    IMITATER("Imitater", 0.0f),
    ICE_SHROOM("Ice-shroom", 0.0f),
    LILY_PAD("Lily Pad", 2.0f),
    HOT_POTATO("Hot Potato", 0.0f),
    GRAVE_BUSTER("Grave Buster", 0.0f),
    ENLIGHTEN_MINT("Enlighten-mint", 0.0f),
    APPEASE_MINT("Appease-mint", 0.0f),
    ARMA_MINT("Arma-mint", 0.0f),
    BOMBARD_MINT("Bombard-mint", 0.0f),
    ENFORCE_MINT("Enforce-mint", 0.0f),
    REINFORCE_MINT("Reinforce-mint", 0.0f),
    ENCHANT_MINT("Enchant-mint", 0.0f),
    PIERCE_MINT("Pierce-mint", 0.0f),
    CAT_TAIL_MINT("catTail-mint", 0.0f)
    ;

    private final String label;
    private final float plantFoodDuration;

    PlantName(String label, float plantFoodDuration) {
        this.label = label;
        this.plantFoodDuration = plantFoodDuration;
    }

    public String getLabel() {
        return label;
    }

    public float getPlantFoodDuration() {
        return plantFoodDuration;
    }

    @Override
    public String toString() {
        return label;
    }

    public static PlantName fromLabel(String label) {
        if (label == null) return null;
        for (PlantName plant : values()) {
            if (plant.label.equalsIgnoreCase(label.trim())) {
                return plant;
            }
        }
        return null;
    }

    public static PlantName parsePlantName(String input) {
        return fromLabel(input);
    }

    public boolean hasPlantFoodEffect() {
        return switch (this) {
            case GOLD_BLOOM, CHERRY_BOMB, GRAPESHOT, JALAPENO, DOOM_SHROOM,
                 IMITATER, ICE_SHROOM, HOT_POTATO, GRAVE_BUSTER,
                 ENLIGHTEN_MINT, APPEASE_MINT, ARMA_MINT, BOMBARD_MINT,
                 ENFORCE_MINT, REINFORCE_MINT, ENCHANT_MINT, PIERCE_MINT,
                 CAT_TAIL_MINT -> false;
            default -> true;
        };
    }
}
