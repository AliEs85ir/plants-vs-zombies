package graphics.zombie;

import graphics.general.animation.AnimationRegistry;
import graphics.general.animation.loader.ZombieAnimationProfileLoader;

public class ZombieAnimationRegistry
    extends AnimationRegistry<
    String,
    ZombieVisualState,
    ZombieAnimationVariant,
    ZombieAnimationProfile
    > {

    public ZombieAnimationRegistry() {
        registerInitialProfiles();
    }

    private void registerInitialProfiles() {
        ZombieAnimationProfile basicPirate = ZombieAnimationProfileLoader.load("zombies/Basic pirate.json");
        ZombieAnimationProfile basicHalloweenFlag = ZombieAnimationProfileLoader.load("zombies/Basic Halloween Flag.json");
        ZombieAnimationProfile basicHalloween = ZombieAnimationProfileLoader.load("zombies/Basic Halloween.json");
        ZombieAnimationProfile basicEgypt = ZombieAnimationProfileLoader.load("zombies/Basic Egypt.json");
        ZombieAnimationProfile basicIceAge = ZombieAnimationProfileLoader.load("zombies/Basic IceAge.json");

        ZombieAnimationProfile allStar = ZombieAnimationProfileLoader.load("zombies/all-star.json");
        ZombieAnimationProfile newspaper = ZombieAnimationProfileLoader.load("zombies/newspaper.json");
        ZombieAnimationProfile ra = ZombieAnimationProfileLoader.load("zombies/ra.json");
        ZombieAnimationProfile sunProducer = ZombieAnimationProfileLoader.load("zombies/SunProducer.json");
        ZombieAnimationProfile skull = ZombieAnimationProfileLoader.load("zombies/crystal-skull.json");
        ZombieAnimationProfile prospector = ZombieAnimationProfileLoader.load("zombies/prospector.json");
        ZombieAnimationProfile hunter = ZombieAnimationProfileLoader.load("zombies/hunter.json");
        ZombieAnimationProfile dragon = ZombieAnimationProfileLoader.load("zombies/DarkDragon.json");
        ZombieAnimationProfile octopus = ZombieAnimationProfileLoader.load("zombies/octopus-throw.json");
        ZombieAnimationProfile sphinx = ZombieAnimationProfileLoader.load("zombies/sphinx-inator.json");
        ZombieAnimationProfile fisherman = ZombieAnimationProfileLoader.load("zombies/fisherman.json");
        ZombieAnimationProfile snorkel = ZombieAnimationProfileLoader.load("zombies/snorkel.json");
        ZombieAnimationProfile tombRaiser = ZombieAnimationProfileLoader.load("zombies/tomb_raiser.json");
        ZombieAnimationProfile mammoth = ZombieAnimationProfileLoader.load("zombies/Mammoth.json");
        ZombieAnimationProfile shark = ZombieAnimationProfileLoader.load("zombies/Shark.json");
        ZombieAnimationProfile tutorialFlag = ZombieAnimationProfileLoader.load("zombies/Tutorial Flag.json");
        ZombieAnimationProfile egyptFlag = ZombieAnimationProfileLoader.load("zombies/Egypt Flag.json");
        ZombieAnimationProfile egyptFlag2 = ZombieAnimationProfileLoader.load("zombies/Egypt Flag2.json");
        ZombieAnimationProfile egyptCamel = ZombieAnimationProfileLoader.load("zombies/Egypt Camel.json");
        ZombieAnimationProfile iceYeti = ZombieAnimationProfileLoader.load("zombies/Ice Yeti.json");
        ZombieAnimationProfile westFlag = ZombieAnimationProfileLoader.load("zombies/West Flag.json");
        ZombieAnimationProfile westFlag2 = ZombieAnimationProfileLoader.load("zombies/West Flag2.json");
        ZombieAnimationProfile westFlag3 = ZombieAnimationProfileLoader.load("zombies/West Flag3.json");
        ZombieAnimationProfile westFlag4 = ZombieAnimationProfileLoader.load("zombies/West Flag4.json");
        ZombieAnimationProfile westFlag5 = ZombieAnimationProfileLoader.load("zombies/West Flag5.json");
        ZombieAnimationProfile westFlag6 = ZombieAnimationProfileLoader.load("zombies/West Flag6.json");
        ZombieAnimationProfile westFlag7 = ZombieAnimationProfileLoader.load("zombies/West Flag7.json");
        ZombieAnimationProfile westFlag11 = ZombieAnimationProfileLoader.load("zombies/West Flag11.json");
        ZombieAnimationProfile westFlag10 = ZombieAnimationProfileLoader.load("zombies/West Flag10.json");
        ZombieAnimationProfile westFlag9 = ZombieAnimationProfileLoader.load("zombies/West Flag9.json");
        ZombieAnimationProfile westFlag8 = ZombieAnimationProfileLoader.load("zombies/West Flag8.json");
        ZombieAnimationProfile peashooterZombie = ZombieAnimationProfileLoader.load("zombies/PeashooterZombie.json");
        ZombieAnimationProfile walnutZombie = ZombieAnimationProfileLoader.load("zombies/WalnutZombie.json");
        ZombieAnimationProfile squashZombie = ZombieAnimationProfileLoader.load("zombies/SquashZombie.json");
        ZombieAnimationProfile basicWest = ZombieAnimationProfileLoader.load("zombies/Basic West.json");
        ZombieAnimationProfile basicWest2 = ZombieAnimationProfileLoader.load("zombies/Basic West2.json");
        ZombieAnimationProfile valentinesBasic = ZombieAnimationProfileLoader.load("zombies/Valentines Basic.json");
        ZombieAnimationProfile birthdayBasic = ZombieAnimationProfileLoader.load("zombies/Birthday Basic.json");
        ZombieAnimationProfile birthdayBasic2 = ZombieAnimationProfileLoader.load("zombies/Birthday Basic2.json");
        ZombieAnimationProfile birthdayBasic3 = ZombieAnimationProfileLoader.load("zombies/Birthday Basic3.json");
        ZombieAnimationProfile arcade = ZombieAnimationProfileLoader.load("zombies/arcade.json");
        ZombieAnimationProfile troglobite = ZombieAnimationProfileLoader.load("zombies/troglobite.json");
        ZombieAnimationProfile wizard1 = ZombieAnimationProfileLoader.load("zombies/wizard1.json");
        ZombieAnimationProfile king = ZombieAnimationProfileLoader.load("zombies/king.json");

        ZombieAnimationProfile imp1 = ZombieAnimationProfileLoader.load("zombies/imp/imp1.json");
        ZombieAnimationProfile imp2 = ZombieAnimationProfileLoader.load("zombies/imp/imp2.json");
        ZombieAnimationProfile imp3 = ZombieAnimationProfileLoader.load("zombies/imp/imp3.json");
        ZombieAnimationProfile imp4 = ZombieAnimationProfileLoader.load("zombies/imp/imp4.json");
        ZombieAnimationProfile imp5 = ZombieAnimationProfileLoader.load("zombies/imp/imp5.json");
        ZombieAnimationProfile imp6 = ZombieAnimationProfileLoader.load("zombies/imp/imp6.json");
        ZombieAnimationProfile imp7 = ZombieAnimationProfileLoader.load("zombies/imp/imp7.json");
        ZombieAnimationProfile imp8 = ZombieAnimationProfileLoader.load("zombies/imp/imp8.json");
        ZombieAnimationProfile imp10 = ZombieAnimationProfileLoader.load("zombies/imp/imp10.json");
        ZombieAnimationProfile imp11 = ZombieAnimationProfileLoader.load("zombies/imp/imp11.json");
        ZombieAnimationProfile imp12 = ZombieAnimationProfileLoader.load("zombies/imp/imp12.json");
        ZombieAnimationProfile imp13 = ZombieAnimationProfileLoader.load("zombies/imp/imp13.json");
        ZombieAnimationProfile impDragon = ZombieAnimationProfileLoader.load("zombies/imp/impDragon.json");

        ZombieAnimationProfile gargantuar1 = ZombieAnimationProfileLoader.load("zombies/gargantuar/gargantuar1.json");
        ZombieAnimationProfile gargantuar2 = ZombieAnimationProfileLoader.load("zombies/gargantuar/gargantuar2.json");
        ZombieAnimationProfile gargantuar4 = ZombieAnimationProfileLoader.load("zombies/gargantuar/gargantuar4.json");
        ZombieAnimationProfile gargantuar5 = ZombieAnimationProfileLoader.load("zombies/gargantuar/gargantuar5.json");
        ZombieAnimationProfile gargantuar7 = ZombieAnimationProfileLoader.load("zombies/gargantuar/gargantuar7.json");
        ZombieAnimationProfile gargantuar8 = ZombieAnimationProfileLoader.load("zombies/gargantuar/gargantuar8.json");
        ZombieAnimationProfile gargantuar11 = ZombieAnimationProfileLoader.load("zombies/gargantuar/gargantuar11.json");
        ZombieAnimationProfile gargantuar12 = ZombieAnimationProfileLoader.load("zombies/gargantuar/gargantuar12.json");
        ZombieAnimationProfile gargantuar13 = ZombieAnimationProfileLoader.load("zombies/gargantuar/gargantuar13.json");

        ZombieAnimationProfile jester1 = ZombieAnimationProfileLoader.load("zombies/jester/jester1.json");
        ZombieAnimationProfile jester2 = ZombieAnimationProfileLoader.load("zombies/jester/jester2.json");
        ZombieAnimationProfile jester3 = ZombieAnimationProfileLoader.load("zombies/jester/jester3.json");
        ZombieAnimationProfile jester4 = ZombieAnimationProfileLoader.load("zombies/jester/jester4.json");


        registerProfile(basicEgypt);
        registerProfile(basicPirate);
        registerProfile(basicHalloween);
        registerProfile(basicHalloweenFlag);

        registerProfile(allStar);

        registerProfile(imp1);
        registerProfile(imp2);
        registerProfile(imp3);
        registerProfile(imp4);
        registerProfile(imp5);
        registerProfile(imp6);
        registerProfile(imp7);
        registerProfile(imp8);
        registerProfile(impDragon);
        registerProfile(imp10);
        registerProfile(imp11);
        registerProfile(imp12);
        registerProfile(imp13);

        registerProfile(gargantuar1);
        registerProfile(gargantuar2);
        registerProfile(gargantuar4);
        registerProfile(gargantuar5);
        registerProfile(gargantuar7);
        registerProfile(gargantuar8);
        registerProfile(gargantuar11);
        registerProfile(gargantuar12);
        registerProfile(gargantuar13);

        registerProfile(newspaper);
        registerProfile(ra);
        registerProfile(skull);
        registerProfile(prospector);
        registerProfile(hunter);

        registerProfile(jester1);
        registerProfile(jester2);
        registerProfile(jester3);
        registerProfile(jester4);

        registerProfile(dragon);
        registerProfile(octopus);
        registerProfile(sphinx);
        registerProfile(fisherman);
        registerProfile(snorkel);
        registerProfile(tombRaiser);
        registerProfile(mammoth);
        registerProfile(basicIceAge);
        registerProfile(shark);
        registerProfile(tutorialFlag);
        registerProfile(egyptFlag);
        registerProfile(egyptFlag2);
        registerProfile(egyptCamel);
        registerProfile(iceYeti);
        registerProfile(westFlag);
        registerProfile(westFlag2);
        registerProfile(basicWest);
        registerProfile(valentinesBasic);
        registerProfile(birthdayBasic);
        registerProfile(birthdayBasic2);
        registerProfile(birthdayBasic3);
        registerProfile(westFlag3);
        registerProfile(westFlag4);
        registerProfile(basicWest2);
        registerProfile(westFlag5);
        registerProfile(westFlag6);
        registerProfile(westFlag7);
        registerProfile(westFlag8);
        registerProfile(westFlag9);
        registerProfile(westFlag10);
        registerProfile(westFlag11);
        registerProfile(arcade);
        registerProfile(troglobite);
        registerProfile(wizard1);
        registerProfile(king);
        registerProfile(peashooterZombie);
        registerProfile(walnutZombie);
        registerProfile(squashZombie);
        registerProfile(sunProducer);
    }
}
