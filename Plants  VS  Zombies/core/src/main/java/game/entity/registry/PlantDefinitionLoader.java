package game.entity.registry;

import app.enums.plant.*;
import game.entity.plant.PlantDefinition;
import game.entity.plant.PlantStackLayer;
import game.entity.plant.PlantTags;
import game.entity.plant.ability.EatingMeleeAbility;
import game.service.plant.PlantDefinitionBuilder;

public final class PlantDefinitionLoader {

    private PlantDefinitionLoader() {}

    public static void load() {
        final int TICKS_PER_SECOND = 10;

        // ============================================================
        // 1. Sunflower
        // ============================================================
        PlantTags sunflowerTags = new PlantTags();
        sunflowerTags.addTag(PlantTag.DAY);

        PlantDefinition sunflower = new PlantDefinitionBuilder()
                .name(PlantName.SUNFLOWER)
                .category(PlantCategory.SUN_PRODUCER)
                .sunCost(50)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(sunflowerTags)
                .attackDamage(0)
                .actionInterval(24.0f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(sunflower);

        // ============================================================
        // 2. Twin Sunflower
        // ============================================================
        PlantTags twinSunflowerTags = new PlantTags();
        twinSunflowerTags.addTag(PlantTag.DAY);

        PlantDefinition twinSunflower = new PlantDefinitionBuilder()
                .name(PlantName.TWIN_SUNFLOWER)
                .category(PlantCategory.SUN_PRODUCER)
                .sunCost(125)
                .maxHealth(300)
                .cooldown(15 * TICKS_PER_SECOND)
                .tags(twinSunflowerTags)
                .attackDamage(0)
                .actionInterval(5.0f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(twinSunflower);

        // ============================================================
        // 3. Sun-shroom
        // ============================================================
        PlantTags sunShroomTags = new PlantTags();
        sunShroomTags.addTag(PlantTag.SHROOM);
        sunShroomTags.addTag(PlantTag.WRAMP_UP);
        sunShroomTags.addTag(PlantTag.NIGHT);

        PlantDefinition sunShroom = new PlantDefinitionBuilder()
                .name(PlantName.SUN_SHROOM)
                .category(PlantCategory.SUN_PRODUCER)
                .sunCost(25)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(sunShroomTags)
                .attackDamage(0)
                .actionInterval(24.0f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(sunShroom);

        // ============================================================
        // 4. Primal Sunflower
        // ============================================================
        PlantTags primalSunflowerTags = new PlantTags();

        PlantDefinition primalSunflower = new PlantDefinitionBuilder()
                .name(PlantName.PRIMAL_SUNFLOWER)
                .category(PlantCategory.SUN_PRODUCER)
                .sunCost(75)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(primalSunflowerTags)
                .attackDamage(0)
                .actionInterval(24.0f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(primalSunflower);

        // ============================================================
        // 5. Gold Bloom
        // ============================================================
        PlantTags goldBloomTags = new PlantTags();

        PlantDefinition goldBloom = new PlantDefinitionBuilder()
                .name(PlantName.GOLD_BLOOM)
                .category(PlantCategory.SUN_PRODUCER)
                .sunCost(0)
                .maxHealth(0)
                .cooldown(75 * TICKS_PER_SECOND)
                .tags(goldBloomTags)
                .attackDamage(0)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(goldBloom);

        // ============================================================
        // 6. Peashooter
        // ============================================================
        PlantTags peashooterTags = new PlantTags();
        peashooterTags.addTag(PlantTag.PEA);

        PlantDefinition peashooter = new PlantDefinitionBuilder()
                .name(PlantName.PEASHOOTER)
                .category(PlantCategory.SHOOTER)
                .sunCost(100)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(peashooterTags)
                .attackDamage(20)
                .actionInterval(1.5f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(peashooter);

        // ============================================================
        // 7. Repeater
        // ============================================================
        PlantTags repeaterTags = new PlantTags();
        repeaterTags.addTag(PlantTag.PEA);

        PlantDefinition repeater = new PlantDefinitionBuilder()
                .name(PlantName.REPEATER)
                .category(PlantCategory.SHOOTER)
                .sunCost(200)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(repeaterTags)
                .attackDamage(20)
                .actionInterval(1.5f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(repeater);

        // ============================================================
        // 8. Threepeater
        // ============================================================
        PlantTags threepeaterTags = new PlantTags();
        threepeaterTags.addTag(PlantTag.PEA);

        PlantDefinition threepeater = new PlantDefinitionBuilder()
                .name(PlantName.THREEPEATER)
                .category(PlantCategory.SHOOTER)
                .sunCost(300)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(threepeaterTags)
                .attackDamage(20)
                .actionInterval(1.5f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(threepeater);

        // ============================================================
        // 9. Snow Pea
        // ============================================================
        PlantTags snowPeaTags = new PlantTags();
        snowPeaTags.addTag(PlantTag.ICE);
        snowPeaTags.addTag(PlantTag.PEA);

        PlantDefinition snowPea = new PlantDefinitionBuilder()
                .name(PlantName.SNOW_PEA)
                .category(PlantCategory.SHOOTER)
                .sunCost(150)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(snowPeaTags)
                .attackDamage(20)
                .actionInterval(1.5f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(snowPea);

        // ============================================================
        // 10. Rotobaga
        // ============================================================
        PlantTags rotobagaTags = new PlantTags();

        PlantDefinition rotobaga = new PlantDefinitionBuilder()
                .name(PlantName.ROTOBAGA)
                .category(PlantCategory.SHOOTER)
                .sunCost(150)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(rotobagaTags)
                .attackDamage(10)
                .actionInterval(2.5f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(rotobaga);

        // ============================================================
        // 11. Pea Pod
        // ============================================================
        PlantTags peaPodTags = new PlantTags();
        peaPodTags.addTag(PlantTag.PEA);
        peaPodTags.addTag(PlantTag.STACK);

        PlantDefinition peaPod = new PlantDefinitionBuilder()
                .name(PlantName.PEA_POD)
                .category(PlantCategory.SHOOTER)
                .sunCost(125)
                .maxHealth(300)
                .cooldown(1 * TICKS_PER_SECOND)
                .tags(peaPodTags)
                .attackDamage(20)
                .actionInterval(1.5f * TICKS_PER_SECOND)
                .stackLayer(PlantStackLayer.REPEATABLE)
                .maxPerCell(5)
                .build();
        PlantRegistry.register(peaPod);

        // ============================================================
        // 12. Split Pea
        // ============================================================
        PlantTags splitPeaTags = new PlantTags();
        splitPeaTags.addTag(PlantTag.PEA);

        PlantDefinition splitPea = new PlantDefinitionBuilder()
                .name(PlantName.SPLIT_PEA)
                .category(PlantCategory.SHOOTER)
                .sunCost(125)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(splitPeaTags)
                .attackDamage(20)
                .actionInterval(1.5f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(splitPea);

        // ============================================================
        // 13. Citron
        // ============================================================
        PlantTags citronTags = new PlantTags();
        citronTags.addTag(PlantTag.CHARGE);

        PlantDefinition citron = new PlantDefinitionBuilder()
                .name(PlantName.CITRON)
                .category(PlantCategory.SHOOTER)
                .sunCost(350)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(citronTags)
                .attackDamage(800)
                .actionInterval(12.0f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(citron);

        // ============================================================
        // 14. Caulipower
        // ============================================================
        PlantTags caulipowerTags = new PlantTags();
        caulipowerTags.addTag(PlantTag.MAGIC);
        caulipowerTags.addTag(PlantTag.CHARGE);

        PlantDefinition caulipower = new PlantDefinitionBuilder()
                .name(PlantName.CAULIPOWER)
                .category(PlantCategory.HOMING)
                .sunCost(250)
                .maxHealth(300)
                .cooldown(15 * TICKS_PER_SECOND)
                .tags(caulipowerTags)
                .attackDamage(800)
                .actionInterval(12.0f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(caulipower);

        // ============================================================
        // 15. Electric Blueberry
        // ============================================================
        PlantTags electricBlueberryTags = new PlantTags();
        electricBlueberryTags.addTag(PlantTag.CHARGE);

        PlantDefinition electricBlueberry = new PlantDefinitionBuilder()
                .name(PlantName.ELECTRIC_BLUEBERRY)
                .category(PlantCategory.HOMING)
                .sunCost(150)
                .maxHealth(300)
                .cooldown(15 * TICKS_PER_SECOND)
                .tags(electricBlueberryTags)
                .attackDamage(5000)
                .actionInterval(12.0f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(electricBlueberry);

        // ============================================================
        // 16. Bowling Bulb
        // ============================================================
        PlantTags bowlingBulbTags = new PlantTags();
        bowlingBulbTags.addTag(PlantTag.CHARGE);

        PlantDefinition bowlingBulb = new PlantDefinitionBuilder()
                .name(PlantName.BOWLING_BULB)
                .category(PlantCategory.SHOOTER)
                .sunCost(200)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(bowlingBulbTags)
                .attackDamage(40)
                .actionInterval(2.0f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(bowlingBulb);

        // ============================================================
        // 17. Cactus
        // ============================================================
        PlantTags cactusTags = new PlantTags();

        PlantDefinition cactus = new PlantDefinitionBuilder()
                .name(PlantName.CACTUS)
                .category(PlantCategory.STRIKE_THROUGH)
                .sunCost(175)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(cactusTags)
                .attackDamage(120) //!Changed
                .actionInterval(1.5f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(cactus);

        // ============================================================
        // 18. Fire Peashooter
        // ============================================================
        PlantTags firePeashooterTags = new PlantTags();
        firePeashooterTags.addTag(PlantTag.FIRE);
        firePeashooterTags.addTag(PlantTag.PEA);

        PlantDefinition firePeashooter = new PlantDefinitionBuilder()
                .name(PlantName.FIRE_PEASHOOTER)
                .category(PlantCategory.SHOOTER)
                .sunCost(175)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(firePeashooterTags)
                .attackDamage(40)
                .actionInterval(1.5f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(firePeashooter);

        // ============================================================
        // 19. Starfruit
        // ============================================================
        PlantTags starfruitTags = new PlantTags();

        PlantDefinition starfruit = new PlantDefinitionBuilder()
                .name(PlantName.STARFRUIT)
                .category(PlantCategory.SHOOTER)
                .sunCost(150)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(starfruitTags)
                .attackDamage(20)
                .actionInterval(1.5f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(starfruit);

        // ============================================================
        // 20. Goo Peashooter
        // ============================================================
        PlantTags gooPeashooterTags = new PlantTags();
        gooPeashooterTags.addTag(PlantTag.POISON);

        PlantDefinition gooPeashooter = new PlantDefinitionBuilder()
                .name(PlantName.GOO_PEASHOOTER)
                .category(PlantCategory.SHOOTER)
                .sunCost(125)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(gooPeashooterTags)
                .attackDamage(40) //!Changed
                .actionInterval(3.0f * TICKS_PER_SECOND) //!Changed
                .build();
        PlantRegistry.register(gooPeashooter);

        // ============================================================
        // 21. Mega Gatling Pea
        // ============================================================
        PlantTags megaGatlingPeaTags = new PlantTags();
        megaGatlingPeaTags.addTag(PlantTag.PEA);

        PlantDefinition megaGatlingPea = new PlantDefinitionBuilder()
                .name(PlantName.MEGA_GATLING_PEA)
                .category(PlantCategory.SHOOTER)
                .sunCost(400)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(megaGatlingPeaTags)
                .attackDamage(20)
                .actionInterval(1.5f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(megaGatlingPea);

        // ============================================================
        // 22. Sea-shroom
        // ============================================================
        PlantTags seaShroomTags = new PlantTags();
        seaShroomTags.addTag(PlantTag.SHROOM);
        seaShroomTags.addTag(PlantTag.WATER);

        PlantDefinition seaShroom = new PlantDefinitionBuilder()
                .name(PlantName.SEA_SHROOM)
                .category(PlantCategory.SHOOTER)
                .sunCost(0)
                .maxHealth(300)
                .cooldown(15 * TICKS_PER_SECOND)
                .tags(seaShroomTags)
                .attackDamage(20)
                .actionInterval(1.5f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(seaShroom);

        // ============================================================
        // 23. Puff-shroom
        // ============================================================
        PlantTags puffShroomTags = new PlantTags();
        puffShroomTags.addTag(PlantTag.SHROOM);

        PlantDefinition puffShroom = new PlantDefinitionBuilder()
                .name(PlantName.PUFF_SHROOM)
                .category(PlantCategory.SHOOTER)
                .sunCost(0)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(puffShroomTags)
                .attackDamage(20)
                .actionInterval(1.5f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(puffShroom);

        // ============================================================
        // 24. Fume-shroom
        // ============================================================
        PlantTags fumeShroomTags = new PlantTags();
        fumeShroomTags.addTag(PlantTag.SHROOM);

        PlantDefinition fumeShroom = new PlantDefinitionBuilder()
                .name(PlantName.FUME_SHROOM)
                .category(PlantCategory.STRIKE_THROUGH)
                .sunCost(125)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(fumeShroomTags)
                .attackDamage(20)
                .actionInterval(1.5f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(fumeShroom);

        // ============================================================
        // 25. Cabbage-pult
        // ============================================================
        PlantTags cabbagePultTags = new PlantTags();

        PlantDefinition cabbagePult = new PlantDefinitionBuilder()
                .name(PlantName.CABBAGE_PULT)
                .category(PlantCategory.LOBBER)
                .sunCost(100)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(cabbagePultTags)
                .attackDamage(40)
                .actionInterval(2.9f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(cabbagePult);

        // ============================================================
        // 26. Kernel-pult
        // ============================================================
        PlantTags kernelPultTags = new PlantTags();

        PlantDefinition kernelPult = new PlantDefinitionBuilder()
                .name(PlantName.KERNEL_PULT)
                .category(PlantCategory.LOBBER)
                .sunCost(100)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(kernelPultTags)
                .attackDamage(20)
                .actionInterval(2.9f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(kernelPult);

        // ============================================================
        // 27. Melon-pult
        // ============================================================
        PlantTags melonPultTags = new PlantTags();
        melonPultTags.addTag(PlantTag.AOE);

        PlantDefinition melonPult = new PlantDefinitionBuilder()
                .name(PlantName.MELON_PULT)
                .category(PlantCategory.LOBBER)
                .sunCost(325)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(melonPultTags)
                .attackDamage(80)
                .actionInterval(2.9f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(melonPult);

        // ============================================================
        // 28. Winter Melon
        // ============================================================
        PlantTags winterMelonTags = new PlantTags();
        winterMelonTags.addTag(PlantTag.ICE);
        winterMelonTags.addTag(PlantTag.AOE);

        PlantDefinition winterMelon = new PlantDefinitionBuilder()
                .name(PlantName.WINTER_MELON)
                .category(PlantCategory.LOBBER)
                .sunCost(500)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(winterMelonTags)
                .attackDamage(80)
                .actionInterval(2.9f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(winterMelon);

        // ============================================================
        // 29. Pepper-pult
        // ============================================================
        PlantTags pepperPultTags = new PlantTags();
        pepperPultTags.addTag(PlantTag.FIRE);
        pepperPultTags.addTag(PlantTag.AOE);

        PlantDefinition pepperPult = new PlantDefinitionBuilder()
                .name(PlantName.PEPPER_PULT)
                .category(PlantCategory.LOBBER)
                .sunCost(200)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(pepperPultTags)
                .attackDamage(50)
                .actionInterval(2.9f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(pepperPult);

        // ============================================================
        // 30. Potato Mine
        // ============================================================
        PlantTags potatoMineTags = new PlantTags();
        potatoMineTags.addTag(PlantTag.TRAP);
        potatoMineTags.addTag(PlantTag.CHARGE);

        PlantDefinition potatoMine = new PlantDefinitionBuilder()
                .name(PlantName.POTATO_MINE)
                .category(PlantCategory.EXPLOSIVE)
                .sunCost(25)
                .maxHealth(300)
                .cooldown(25 * TICKS_PER_SECOND)
                .tags(potatoMineTags)
                .attackDamage(1800)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(potatoMine);

        // ============================================================
        // 31. Primal Potato Mine
        // ============================================================
        PlantTags primalPotatoMineTags = new PlantTags();
        primalPotatoMineTags.addTag(PlantTag.TRAP);
        primalPotatoMineTags.addTag(PlantTag.CHARGE);

        PlantDefinition primalPotatoMine = new PlantDefinitionBuilder()
                .name(PlantName.PRIMAL_POTATO_MINE)
                .category(PlantCategory.EXPLOSIVE)
                .sunCost(50)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(primalPotatoMineTags)
                .attackDamage(2400)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(primalPotatoMine);

        // ============================================================
        // 32. Cherry Bomb
        // ============================================================
        PlantTags cherryBombTags = new PlantTags();

        PlantDefinition cherryBomb = new PlantDefinitionBuilder()
                .name(PlantName.CHERRY_BOMB)
                .category(PlantCategory.EXPLOSIVE)
                .sunCost(150)
                .maxHealth(0)
                .cooldown(35 * TICKS_PER_SECOND)
                .tags(cherryBombTags)
                .attackDamage(1800)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(cherryBomb);

        // ============================================================
        // 33. Squash
        // ============================================================
        PlantTags squashTags = new PlantTags();
        squashTags.addTag(PlantTag.TRAP);

        PlantDefinition squash = new PlantDefinitionBuilder()
                .name(PlantName.SQUASH)
                .category(PlantCategory.EXPLOSIVE)
                .sunCost(50)
                .maxHealth(300)
                .cooldown(20 * TICKS_PER_SECOND)
                .tags(squashTags)
                .attackDamage(1800)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(squash);

        // ============================================================
        // 34. Grapeshot
        // ============================================================
        PlantTags grapeshotTags = new PlantTags();

        PlantDefinition grapeshot = new PlantDefinitionBuilder()
                .name(PlantName.GRAPESHOT)
                .category(PlantCategory.EXPLOSIVE)
                .sunCost(150)
                .maxHealth(0)
                .cooldown(35 * TICKS_PER_SECOND)
                .tags(grapeshotTags)
                .attackDamage(1800)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(grapeshot);

        // ============================================================
        // 35. Jalapeno
        // ============================================================
        PlantTags jalapenoTags = new PlantTags();
        jalapenoTags.addTag(PlantTag.FIRE);

        PlantDefinition jalapeno = new PlantDefinitionBuilder()
                .name(PlantName.JALAPENO)
                .category(PlantCategory.EXPLOSIVE)
                .sunCost(125)
                .maxHealth(0)
                .cooldown(35 * TICKS_PER_SECOND)
                .tags(jalapenoTags)
                .attackDamage(1800)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(jalapeno);

        // ============================================================
        // 36. Doom-shroom
        // ============================================================
        PlantTags doomShroomTags = new PlantTags();
        doomShroomTags.addTag(PlantTag.SHROOM);
        doomShroomTags.addTag(PlantTag.WRAMP_UP);

        PlantDefinition doomShroom = new PlantDefinitionBuilder()
                .name(PlantName.DOOM_SHROOM)
                .category(PlantCategory.EXPLOSIVE)
                .sunCost(125)
                .maxHealth(300)
                .cooldown(15 * TICKS_PER_SECOND)
                .tags(doomShroomTags)
                .attackDamage(1800)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(doomShroom);

        // ============================================================
        // 37. Tangle Kelp
        // ============================================================
        PlantTags tangleKelpTags = new PlantTags();
        tangleKelpTags.addTag(PlantTag.TRAP);
        tangleKelpTags.addTag(PlantTag.WATER);

        PlantDefinition tangleKelp = new PlantDefinitionBuilder()
                .name(PlantName.TANGLE_KELP)
                .category(PlantCategory.EXPLOSIVE)
                .sunCost(25)
                .maxHealth(300)
                .cooldown(15 * TICKS_PER_SECOND)
                .tags(tangleKelpTags)
                .attackDamage(0)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(tangleKelp);

        // ============================================================
        // 38. Iceberg Lettuce
        // ============================================================
        PlantTags icebergLettuceTags = new PlantTags();
        icebergLettuceTags.addTag(PlantTag.TRAP);
        icebergLettuceTags.addTag(PlantTag.ICE);

        PlantDefinition icebergLettuce = new PlantDefinitionBuilder()
                .name(PlantName.ICEBERG_LETTUCE)
                .category(PlantCategory.EXPLOSIVE)
                .sunCost(0)
                .maxHealth(300)
                .cooldown(20 * TICKS_PER_SECOND)
                .tags(icebergLettuceTags)
                .attackDamage(0)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(icebergLettuce);

        // ============================================================
        // 39. Bonk Choy
        // ============================================================
        PlantTags bonkChoyTags = new PlantTags();

        PlantDefinition bonkChoy = new PlantDefinitionBuilder()
                .name(PlantName.BONK_CHOY)
                .category(PlantCategory.MELEE)
                .sunCost(150)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(bonkChoyTags)
                .attackDamage(15)
                .actionInterval(0.25f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(bonkChoy);

        // ============================================================
        // 40. Phat Beet
        // ============================================================
        PlantTags phatBeetTags = new PlantTags();
        phatBeetTags.addTag(PlantTag.AOE);

        PlantDefinition phatBeet = new PlantDefinitionBuilder()
                .name(PlantName.PHAT_BEET)
                .category(PlantCategory.MELEE)
                .sunCost(150)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(phatBeetTags)
                .attackDamage(15)
                .actionInterval(2.0f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(phatBeet);

        // ============================================================
        // 41. Chomper
        // ============================================================
        PlantTags chomperTags = new PlantTags();

        PlantDefinition chomper = new PlantDefinitionBuilder()
                .name(PlantName.CHOMPER)
                .category(PlantCategory.MELEE)
                .sunCost(150)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(chomperTags)
                .attackDamage(0)
                .actionInterval(10.0f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(chomper);

        // ============================================================
        // 42. Wasabi Whip
        // ============================================================
        PlantTags wasabiWhipTags = new PlantTags();
        wasabiWhipTags.addTag(PlantTag.FIRE);

        PlantDefinition wasabiWhip = new PlantDefinitionBuilder()
                .name(PlantName.WASABI_WHIP)
                .category(PlantCategory.MELEE)
                .sunCost(150)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(wasabiWhipTags)
                .attackDamage(40)
                .actionInterval(2.0f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(wasabiWhip);

        // ============================================================
        // 43. Kiwibeast
        // ============================================================
        PlantTags kiwibeastTags = new PlantTags();
        kiwibeastTags.addTag(PlantTag.AOE);
        kiwibeastTags.addTag(PlantTag.WRAMP_UP);

        PlantDefinition kiwibeast = new PlantDefinitionBuilder()
                .name(PlantName.KIWIBEAST)
                .category(PlantCategory.MELEE)
                .sunCost(175)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(kiwibeastTags)
                .attackDamage(15)
                .actionInterval(2.0f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(kiwibeast);

        // ============================================================
        // 44. Wall-nut
        // ============================================================
        PlantTags wallNutTags = new PlantTags();

        PlantDefinition wallNut = new PlantDefinitionBuilder()
                .name(PlantName.WALL_NUT)
                .category(PlantCategory.WALL_NUT)
                .sunCost(50)
                .maxHealth(4000)
                .cooldown(20 * TICKS_PER_SECOND)
                .tags(wallNutTags)
                .attackDamage(0)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(wallNut);

        // ============================================================
        // 45. Tall-nut
        // ============================================================
        PlantTags tallNutTags = new PlantTags();

        PlantDefinition tallNut = new PlantDefinitionBuilder()
                .name(PlantName.TALL_NUT)
                .category(PlantCategory.WALL_NUT)
                .sunCost(125)
                .maxHealth(8000)
                .cooldown(20 * TICKS_PER_SECOND)
                .tags(tallNutTags)
                .attackDamage(0)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(tallNut);

        // ============================================================
        // 46. Endurian
        // ============================================================
        PlantTags endurianTags = new PlantTags();

        PlantDefinition endurian = new PlantDefinitionBuilder()
                .name(PlantName.ENDURIAN)
                .category(PlantCategory.WALL_NUT)
                .sunCost(100)
                .maxHealth(3000)
                .cooldown(15 * TICKS_PER_SECOND)
                .tags(endurianTags)
                .attackDamage(20)
                .actionInterval(EatingMeleeAbility.ATTACK_INTERVAL_TICKS)
                .build();
        PlantRegistry.register(endurian);

        // ============================================================
        // 47. Garlic
        // ============================================================
        PlantTags garlicTags = new PlantTags();
        garlicTags.addTag(PlantTag.MOVE_ZOMBIES);

        PlantDefinition garlic = new PlantDefinitionBuilder()
                .name(PlantName.GARLIC)
                .category(PlantCategory.WALL_NUT)
                .sunCost(50)
                .maxHealth(300)
                .cooldown(20 * TICKS_PER_SECOND)
                .tags(garlicTags)
                .attackDamage(0)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(garlic);

        // ============================================================
        // 48. Sweet Potato
        // ============================================================
        PlantTags sweetPotatoTags = new PlantTags();
        sweetPotatoTags.addTag(PlantTag.MOVE_ZOMBIES);

        PlantDefinition sweetPotato = new PlantDefinitionBuilder()
                .name(PlantName.SWEET_POTATO)
                .category(PlantCategory.WALL_NUT)
                .sunCost(150)
                .maxHealth(3000)
                .cooldown(20 * TICKS_PER_SECOND)
                .tags(sweetPotatoTags)
                .attackDamage(0)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(sweetPotato);

        // ============================================================
        // 49. Explode-o-nut
        // ============================================================
        PlantTags explodeONutTags = new PlantTags();
        explodeONutTags.addTag(PlantTag.EXPLOSIVE);

        PlantDefinition explodeONut = new PlantDefinitionBuilder()
                .name(PlantName.EXPLODE_O_NUT)
                .category(PlantCategory.WALL_NUT)
                .sunCost(50)
                .maxHealth(4000)
                .cooldown(20 * TICKS_PER_SECOND)
                .tags(explodeONutTags)
                .attackDamage(1800)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(explodeONut);

        // ============================================================
        // 50. Pumpkin
        // ============================================================
        PlantTags pumpkinTags = new PlantTags();
        pumpkinTags.addTag(PlantTag.STACK);

        PlantDefinition pumpkin = new PlantDefinitionBuilder()
                .name(PlantName.PUMPKIN)
                .category(PlantCategory.WALL_NUT)
                .sunCost(150)
                .maxHealth(4000)
                .cooldown(20 * TICKS_PER_SECOND)
                .tags(pumpkinTags)
                .attackDamage(0)
                .actionInterval(0.0f)
                .stackLayer(PlantStackLayer.SHIELD)
                .maxPerCell(1)
                .build();
        PlantRegistry.register(pumpkin);

        // ============================================================
        // 51. Sun Bean
        // ============================================================
        PlantTags sunBeanTags = new PlantTags();
        sunBeanTags.addTag(PlantTag.SUN);

        PlantDefinition sunBean = new PlantDefinitionBuilder()
                .name(PlantName.SUN_BEAN)
                .category(PlantCategory.WALL_NUT)
                .sunCost(50)
                .maxHealth(1000)
                .cooldown(20 * TICKS_PER_SECOND)
                .tags(sunBeanTags)
                .attackDamage(0)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(sunBean);

        // ============================================================
        // 52. Torchwood
        // ============================================================
        PlantTags torchwoodTags = new PlantTags();
        torchwoodTags.addTag(PlantTag.FIRE);

        PlantDefinition torchwood = new PlantDefinitionBuilder()
                .name(PlantName.TORCHWOOD)
                .category(PlantCategory.MODIFIER)
                .sunCost(175)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(torchwoodTags)
                .attackDamage(0)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(torchwood);

        // ============================================================
        // 53. Magnet-shroom
        // ============================================================
        PlantTags magnetShroomTags = new PlantTags();
        magnetShroomTags.addTag(PlantTag.SHROOM);
        magnetShroomTags.addTag(PlantTag.MAGIC);

        PlantDefinition magnetShroom = new PlantDefinitionBuilder()
                .name(PlantName.MAGNET_SHROOM)
                .category(PlantCategory.HOMING)
                .sunCost(100)
                .maxHealth(300)
                .cooldown(15 * TICKS_PER_SECOND)
                .tags(magnetShroomTags)
                .attackDamage(0)
                .actionInterval(10.0f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(magnetShroom);

        // ============================================================
        // 54. Hypno-shroom
        // ============================================================
        PlantTags hypnoShroomTags = new PlantTags();
        hypnoShroomTags.addTag(PlantTag.SHROOM);
        hypnoShroomTags.addTag(PlantTag.MAGIC);

        PlantDefinition hypnoShroom = new PlantDefinitionBuilder()
                .name(PlantName.HYPNO_SHROOM)
                .category(PlantCategory.MODIFIER)
                .sunCost(125)
                .maxHealth(300)
                .cooldown(20 * TICKS_PER_SECOND)
                .tags(hypnoShroomTags)
                .attackDamage(0)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(hypnoShroom);

        // ============================================================
        // 55. Cat-tail
        // ============================================================
        PlantTags catTailTags = new PlantTags();

        PlantDefinition catTail = new PlantDefinitionBuilder()
                .name(PlantName.CAT_TAIL)
                .category(PlantCategory.HOMING)
                .sunCost(175)
                .maxHealth(300)
                .cooldown(20 * TICKS_PER_SECOND)
                .tags(catTailTags)
                .attackDamage(15)
                .actionInterval(1.5f * TICKS_PER_SECOND)
                .build();
        PlantRegistry.register(catTail);

        // ============================================================
        // 56. Imitater
        // ============================================================
        PlantTags imitaterTags = new PlantTags();

        PlantDefinition imitater = new PlantDefinitionBuilder()
                .name(PlantName.IMITATER)
                .category(PlantCategory.MODIFIER)
                .sunCost(0)
                .maxHealth(300)
                .cooldown(20 * TICKS_PER_SECOND)
                .tags(imitaterTags)
                .attackDamage(0)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(imitater);

        // ============================================================
        // 57. Ice-shroom
        // ============================================================
        PlantTags iceShroomTags = new PlantTags();
        iceShroomTags.addTag(PlantTag.SHROOM);
        iceShroomTags.addTag(PlantTag.ICE);

        PlantDefinition iceShroom = new PlantDefinitionBuilder()
                .name(PlantName.ICE_SHROOM)
                .category(PlantCategory.EXPLOSIVE)
                .sunCost(75)
                .maxHealth(0)
                .cooldown(50 * TICKS_PER_SECOND)
                .tags(iceShroomTags)
                .attackDamage(0)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(iceShroom);

        // ============================================================
        // 58. Lily Pad
        // ============================================================
        PlantTags lilyPadTags = new PlantTags();
        lilyPadTags.addTag(PlantTag.WATER);
        lilyPadTags.addTag(PlantTag.STACK);

        PlantDefinition lilyPad = new PlantDefinitionBuilder()
                .name(PlantName.LILY_PAD)
                .category(PlantCategory.MODIFIER)
                .sunCost(25)
                .maxHealth(300)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(lilyPadTags)
                .attackDamage(0)
                .actionInterval(0.0f)
                .stackLayer(PlantStackLayer.PLATFORM)
                .maxPerCell(1)
                .build();
        PlantRegistry.register(lilyPad);

        // ============================================================
        // 59. Hot Potato
        // ============================================================
        PlantTags hotPotatoTags = new PlantTags();
        hotPotatoTags.addTag(PlantTag.FIRE);
        hotPotatoTags.addTag(PlantTag.ICE_TERRAIN);

        PlantDefinition hotPotato = new PlantDefinitionBuilder()
                .name(PlantName.HOT_POTATO)
                .category(PlantCategory.EXPLOSIVE)
                .sunCost(0)
                .maxHealth(0)
                .cooldown(5 * TICKS_PER_SECOND)
                .tags(hotPotatoTags)
                .attackDamage(0)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(hotPotato);

        // ============================================================
        // 60. Grave Buster
        // ============================================================
        PlantTags graveBusterTags = new PlantTags();
        graveBusterTags.addTag(PlantTag.GRAVE);

        PlantDefinition graveBuster = new PlantDefinitionBuilder()
                .name(PlantName.GRAVE_BUSTER)
                .category(PlantCategory.EXPLOSIVE)
                .sunCost(0)
                .maxHealth(0)
                .cooldown(10 * TICKS_PER_SECOND)
                .tags(graveBusterTags)
                .attackDamage(0)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(graveBuster);

        // ============================================================
        // 61. Enlighten-mint
        // ============================================================
        PlantTags enlightenMintTags = new PlantTags();

        PlantDefinition enlightenMint = new PlantDefinitionBuilder()
                .name(PlantName.ENLIGHTEN_MINT)
                .category(PlantCategory.SUN_PRODUCER)
                .sunCost(0)
                .maxHealth(0)
                .cooldown(85 * TICKS_PER_SECOND)
                .tags(enlightenMintTags)
                .attackDamage(0)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(enlightenMint);

        // ============================================================
        // 62. Appease-mint
        // ============================================================
        PlantTags appeaseMintTags = new PlantTags();

        PlantDefinition appeaseMint = new PlantDefinitionBuilder()
                .name(PlantName.APPEASE_MINT)
                .category(PlantCategory.SHOOTER)
                .sunCost(0)
                .maxHealth(0)
                .cooldown(85 * TICKS_PER_SECOND)
                .tags(appeaseMintTags)
                .attackDamage(0)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(appeaseMint);

        // ============================================================
        // 63. Arma-mint
        // ============================================================
        PlantTags armaMintTags = new PlantTags();

        PlantDefinition armaMint = new PlantDefinitionBuilder()
                .name(PlantName.ARMA_MINT)
                .category(PlantCategory.LOBBER)
                .sunCost(0)
                .maxHealth(0)
                .cooldown(85 * TICKS_PER_SECOND)
                .tags(armaMintTags)
                .attackDamage(0)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(armaMint);

        // ============================================================
        // 64. Bombard-mint
        // ============================================================
        PlantTags bombardMintTags = new PlantTags();

        PlantDefinition bombardMint = new PlantDefinitionBuilder()
                .name(PlantName.BOMBARD_MINT)
                .category(PlantCategory.EXPLOSIVE)
                .sunCost(0)
                .maxHealth(0)
                .cooldown(85 * TICKS_PER_SECOND)
                .tags(bombardMintTags)
                .attackDamage(0)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(bombardMint);

        // ============================================================
        // 65. Enforce-mint
        // ============================================================
        PlantTags enforceMintTags = new PlantTags();

        PlantDefinition enforceMint = new PlantDefinitionBuilder()
                .name(PlantName.ENFORCE_MINT)
                .category(PlantCategory.MELEE)
                .sunCost(0)
                .maxHealth(0)
                .cooldown(85 * TICKS_PER_SECOND)
                .tags(enforceMintTags)
                .attackDamage(0)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(enforceMint);

        // ============================================================
        // 66. Reinforce-mint
        // ============================================================
        PlantTags reinforceMintTags = new PlantTags();

        PlantDefinition reinforceMint = new PlantDefinitionBuilder()
                .name(PlantName.REINFORCE_MINT)
                .category(PlantCategory.WALL_NUT)
                .sunCost(0)
                .maxHealth(0)
                .cooldown(85 * TICKS_PER_SECOND)
                .tags(reinforceMintTags)
                .attackDamage(0)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(reinforceMint);

        // ============================================================
        // 67. Enchant-mint
        // ============================================================
        PlantTags enchantMintTags = new PlantTags();

        PlantDefinition enchantMint = new PlantDefinitionBuilder()
                .name(PlantName.ENCHANT_MINT)
                .category(PlantCategory.MODIFIER)
                .sunCost(0)
                .maxHealth(0)
                .cooldown(85 * TICKS_PER_SECOND)
                .tags(enchantMintTags)
                .attackDamage(0)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(enchantMint);

        // ============================================================
        // 68. Pierce-mint
        // ============================================================
        PlantTags pierceMintTags = new PlantTags();

        PlantDefinition pierceMint = new PlantDefinitionBuilder()
                .name(PlantName.PIERCE_MINT)
                .category(PlantCategory.STRIKE_THROUGH)
                .sunCost(0)
                .maxHealth(0)
                .cooldown(85 * TICKS_PER_SECOND)
                .tags(pierceMintTags)
                .attackDamage(0)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(pierceMint);

        // ============================================================
        // 69. Cat-tail-mint
        // ============================================================
        PlantTags catTailMintTags = new PlantTags();

        PlantDefinition catTailMint = new PlantDefinitionBuilder()
                .name(PlantName.CAT_TAIL_MINT)
                .category(PlantCategory.HOMING)
                .sunCost(0)
                .maxHealth(0)
                .cooldown(85 * TICKS_PER_SECOND)
                .tags(catTailMintTags)
                .attackDamage(0)
                .actionInterval(0.0f)
                .build();
        PlantRegistry.register(catTailMint);
    }
}
