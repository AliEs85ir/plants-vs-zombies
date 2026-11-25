package graphics.obstacle;

import app.enums.obstacle.ObstacleType;
import graphics.general.animation.AnimationRegistry;
import graphics.general.animation.loader.ObstacleAnimationProfileLoader;

public class ObstacleAnimationRegistry
    extends AnimationRegistry<
    ObstacleType,
    ObstacleVisualState,
    ObstacleAnimationVariant,
    ObstacleAnimationProfile
    > {

    public ObstacleAnimationRegistry() {
        registerInitialProfiles();
    }

    private void registerInitialProfiles() {
        ObstacleAnimationProfile egyptGrave = ObstacleAnimationProfileLoader.load("obstacles/egypt_grave.json");
        ObstacleAnimationProfile permanentScorch = ObstacleAnimationProfileLoader.load("obstacles/scorched_ground_1.json");
        ObstacleAnimationProfile temporaryScorch = ObstacleAnimationProfileLoader.load("obstacles/scorched_ground_2.json");
        ObstacleAnimationProfile octopus = ObstacleAnimationProfileLoader.load("obstacles/octopus.json");
        ObstacleAnimationProfile missile = ObstacleAnimationProfileLoader.load("obstacles/missile.json");
        ObstacleAnimationProfile protectTile = ObstacleAnimationProfileLoader.load("obstacles/protect_tile.json");
        ObstacleAnimationProfile flowerLine = ObstacleAnimationProfileLoader.load("obstacles/flowerLine.json");
        ObstacleAnimationProfile goldTile = ObstacleAnimationProfileLoader.load("obstacles/gold_tile.json");
        ObstacleAnimationProfile randomVase = ObstacleAnimationProfileLoader.load("obstacles/randomVase.json");
        ObstacleAnimationProfile plantVase = ObstacleAnimationProfileLoader.load("obstacles/plantVase.json");
        ObstacleAnimationProfile gargantuarVase = ObstacleAnimationProfileLoader.load("obstacles/gargantuarVase.json");
        ObstacleAnimationProfile snowball = ObstacleAnimationProfileLoader.load("obstacles/snowball.json");
        ObstacleAnimationProfile fireball = ObstacleAnimationProfileLoader.load("obstacles/fireball.json");
        ObstacleAnimationProfile tileSliderUp = ObstacleAnimationProfileLoader.load("obstacles/TileSliderUp.json");
        ObstacleAnimationProfile tileSliderDown = ObstacleAnimationProfileLoader.load("obstacles/TileSliderDown.json");
        ObstacleAnimationProfile iceBlock = ObstacleAnimationProfileLoader.load("obstacles/IceBlock.json");
        ObstacleAnimationProfile sunGrave = ObstacleAnimationProfileLoader.load("obstacles/SunGraveStone.json");
        ObstacleAnimationProfile plantFoodGrave = ObstacleAnimationProfileLoader.load("obstacles/PlantFoodGravestone.json");
        ObstacleAnimationProfile tutorialGrave = ObstacleAnimationProfileLoader.load("obstacles/TutorialGravestone.json");
        ObstacleAnimationProfile necromancyTile = ObstacleAnimationProfileLoader.load("obstacles/NecromancyTile.json");
        ObstacleAnimationProfile puddle = ObstacleAnimationProfileLoader.load("obstacles/puddle.json");
        ObstacleAnimationProfile gargantuarPuddle = ObstacleAnimationProfileLoader.load("obstacles/gargantuar_puddle.json");
        ObstacleAnimationProfile wallnut = ObstacleAnimationProfileLoader.load("obstacles/wallnut.json");
        ObstacleAnimationProfile tallnut = ObstacleAnimationProfileLoader.load("obstacles/tallnut.json");
        ObstacleAnimationProfile explodeONut = ObstacleAnimationProfileLoader.load("obstacles/explode-o-nut.json");
        ObstacleAnimationProfile crater = ObstacleAnimationProfileLoader.load("obstacles/crater.json");
        ObstacleAnimationProfile glacier = ObstacleAnimationProfileLoader.load("obstacles/mammoth_glacier.json");
        ObstacleAnimationProfile bossIce = ObstacleAnimationProfileLoader.load("obstacles/boss_ice.json");
        ObstacleAnimationProfile slingshot = ObstacleAnimationProfileLoader.load("obstacles/Slingshot.json");
        ObstacleAnimationProfile cabinet = ObstacleAnimationProfileLoader.load("obstacles/cabinet.json");
        ObstacleAnimationProfile pushableIce = ObstacleAnimationProfileLoader.load("obstacles/PushableIce.json");
        ObstacleAnimationProfile hook = ObstacleAnimationProfileLoader.load("obstacles/hook.json");
        ObstacleAnimationProfile squash = ObstacleAnimationProfileLoader.load("obstacles/squash.json");
        ObstacleAnimationProfile brain = ObstacleAnimationProfileLoader.load("obstacles/Brain.json");


        ObstacleAnimationProfile peashooter = ObstacleAnimationProfileLoader.load("obstacles/projectile/peashooter.json");
        ObstacleAnimationProfile snowpea = ObstacleAnimationProfileLoader.load("obstacles/projectile/snowpea.json");


        registerProfile(egyptGrave);
        registerProfile(permanentScorch);
        registerProfile(temporaryScorch);
        registerProfile(octopus);
        registerProfile(missile);
        registerProfile(protectTile);
        registerProfile(flowerLine);
        registerProfile(goldTile);
        registerProfile(randomVase);
        registerProfile(plantVase);
        registerProfile(gargantuarVase);
        registerProfile(snowball);
        registerProfile(tileSliderDown);
        registerProfile(tileSliderUp);
        registerProfile(iceBlock);
        registerProfile(sunGrave);
        registerProfile(plantFoodGrave);
        registerProfile(tutorialGrave);
        registerProfile(necromancyTile);
        registerProfile(puddle);
        registerProfile(gargantuarPuddle);
        registerProfile(wallnut);
        registerProfile(tallnut);
        registerProfile(explodeONut);
        registerProfile(fireball);
        registerProfile(crater);
        registerProfile(glacier);
        registerProfile(bossIce);
        registerProfile(slingshot);
        registerProfile(cabinet);
        registerProfile(pushableIce);
        registerProfile(hook);
        registerProfile(peashooter);
        registerProfile(snowpea);
        registerProfile(squash);
        registerProfile(brain);
    }
}
