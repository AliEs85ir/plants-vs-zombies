package game.entity.zombie;

import app.enums.obstacle.ObstacleType;
import app.enums.plant.PlantName;
import com.badlogic.gdx.physics.box2d.Body;
import common.model.Position;
import game.component.physic.BodyFactory;
import game.entity.registry.ZombieRegistry;
import game.entity.zombie.ability.*;
import game.entity.zombie.ability.plantzombies.PeashooterAbility;
import game.entity.zombie.ability.plantzombies.SquashAbility;
import game.entity.zombie.ability.plantzombies.WalnutAbility;
import game.entity.zombie.boss.boss.DarkDragon;
import game.entity.zombie.boss.boss.Mammoth;
import game.entity.zombie.boss.boss.Shark;
import game.entity.zombie.boss.boss.SphinxInator;
import game.entity.zombie.data.properties.*;
import game.entity.zombie.data.properties.DarkAges.JugglerProperty;
import game.entity.zombie.data.properties.DarkAges.KingProperty;
import game.entity.zombie.data.properties.DarkAges.WizardProperty;
import game.entity.zombie.data.properties.ancientEgypt.ExplorerProperty;
import game.entity.zombie.data.properties.ancientEgypt.RaProperty;
import game.entity.zombie.data.properties.ancientEgypt.TombRaiserProperty;
import game.entity.zombie.data.properties.beach.FishermanProperty;
import game.entity.zombie.data.properties.beach.OctopusProperty;
import game.entity.zombie.data.properties.iceAge.HunterProperty;
import game.entity.zombie.data.properties.iceAge.IceAgeDodoProperty;
import game.entity.zombie.data.properties.iceAge.TroglobiteProperty;
import game.entity.zombie.strategy.attack.*;
import game.entity.zombie.strategy.movement.*;
import game.entity.zombie.strategy.takedamage.SpinTakingDamage;
import game.entity.zombie.strategy.takedamage.StandardTakingDamage;
import game.entity.zombie.strategy.takedamage.TakingDamageStrategy;
import game.service.drop.DropSpawner;
import game.world.GameWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ZombieFactory {
    public static final float EGYPT_BOSS_WIDTH = 80;
    public static final float EGYPT_BOSS_HEIGHT = 150;
    public static final float DRAGON_HEIGHT = EGYPT_BOSS_HEIGHT;
    public static final float MAMMOTH_HEIGHT = 150;


    public static Zombie buildZombie(String alias,
                              GameWorld world,
                              Position position)
    {
        Property property = ZombieRegistry.getZombie(alias);

        if(property == null) {
            System.err.println("Cant find zombie with alias : " + alias);
            return null;
        }

        Zombie zombie;

        double currentHealth = property.getInitialHealth();
        MovementStrategy movement = new NormalMovement();
        TakingDamageStrategy takeDamage = new StandardTakingDamage();
        AttackStrategy attack = new EatAttack();
        List<ZombieAbility> abilities = new ArrayList<>();


        zombie = new Zombie(
                currentHealth,
                movement,
                attack,
                takeDamage,
                position,
                abilities,
                world,
                null,
                property
        );

        float bodyWidth = 40f;
        float bodyHeight = 80f;

        if(property instanceof PianoProperty) {
            zombie.setAttackStrategy( new InstantKillAttack());
            zombie.addAbility(new PianoMusicAbility());
        }
        else if(property instanceof WizardProperty) {
            zombie.addAbility(new WizardAbility());
        }
        else if(property instanceof OctopusProperty) {
            zombie.addAbility(new OctopusThrowAbility());
        }
        else if(property instanceof HunterProperty) {
            zombie.addAbility(new ProjectileThrowAbility());
        }
        else if (property instanceof JugglerProperty) {
            SpinAbility spinAbility = new SpinAbility();
            zombie.addAbility(spinAbility);
            zombie.setTakingDamageStrategy(new SpinTakingDamage(spinAbility));
        }
        else if(property instanceof NewspaperProperty) {
            zombie.addAbility(new NewspaperAbility());
            ZombieArmor armor = ZombieArmorFactory.create("NewspaperDefault");
            zombie.addArmor(armor);
        }
        else if(property instanceof CrystalSkullProperty) {
            zombie.addAbility(new TurquoiseAbility());
        }
        else if(property instanceof TroglobiteProperty) {
            zombie.addAbility(new PushObjectAbility(ObstacleType.PUSHABLE_ICE, 40, 60));
        }
        else if(property instanceof KingProperty) {
            zombie.addAbility(new KingAuraAbility());
            zombie.setMovementStrategy(new StationaryMovement());
        }
//        else if(property instanceof Barrel) {
//            zombie.addAbility(new PushObjectAbility(new Barrel(zombie.getPosition())));
//        }
//        else if(property instanceof LostCityProperty) {
//
//        }
        else if(property instanceof ArcadeProperty) {
            zombie.addAbility(new PushObjectAbility(ObstacleType.CABINET, 40, 60));
        }
        else if(property instanceof GargantuarProperty) {
            zombie.setAttackStrategy(new SmashAttack());
            abilities.add(new ImpThrowAbility());
            bodyWidth = 70;
            bodyHeight = 90;
        }
        else if(property instanceof IceAgeDodoProperty) {

        }
        else if(property instanceof AllStarProperty) {
            zombie.setMovementStrategy(new ChargeMovement());
            zombie.setMoveSpeedMultiplier(3);
            zombie.setAttackStrategy(new SwapAfterAttack(new SmashAttack(), new EatAttack(), new NormalMovement()));
        }
        else if(property instanceof ProspectorProperty) {
            zombie.addAbility(new DynamiteThrowAbility());
            zombie.addAbility(new FlyingAbility());
        }
        else if(property instanceof ExplorerProperty) {
            zombie.setAttackStrategy(new InstantKillAttack());
        }
        else if(property instanceof RaProperty) {
            zombie.addAbility(new SunStealAbility(property.getMaxClaimedSunCurrency()));
        }
        else if(property instanceof TombRaiserProperty) {
            zombie.addAbility(new TombRaiserAbility());
        }
        else if(property instanceof FishermanProperty) {
            zombie.addAbility(new FishermanAbility());
            zombie.setMovementStrategy(new StationaryMovement());
        }
        else if(property instanceof OrdinaryZombieProperty) {
            switch (alias) {
                case "PeashooterZombie" -> {
                    zombie.addAbility(new PeashooterAbility());
                }
                case "WalnutZombie" -> {
                    zombie.addAbility(new WalnutAbility());
                }
                case "SquashZombie" -> {
                    zombie.addAbility(new SquashAbility());
                }
                case "Canon1" -> {
                    zombie.addAbility(new CanonAbility());
                    zombie.setMovementStrategy(new StationaryMovement());
                    zombie.setAttackStrategy(new InstantKillAttack());
                }
                case "SunProducer" -> {
                    zombie.addAbility(new SunProductionAbility(world));
                    zombie.setMovementStrategy(new StationaryMovement());
                    zombie.setAttackStrategy(new NoAttackStrategy());
                }
            }
            if(alias.startsWith("Basic")) {
                Random random = new Random();
                int chance = random.nextInt(100);
                if(chance < 25) {
                    ZombieArmor armor = ZombieArmorFactory.create("ConeDefault");
                    zombie.addArmor(armor);
                    System.err.println("i gave cone");
                }
                else if(chance < 50) {
                    ZombieArmor armor = ZombieArmorFactory.create("BucketDefault");
                    zombie.addArmor(armor);
                    System.err.println("i gave bucket");
                }
                else if(chance < 75) {
                    ZombieArmor armor = ZombieArmorFactory.create("BrickDefault");
                    zombie.addArmor(armor);
                    System.err.println("i gave brick");
                }
            }
            if(alias.startsWith("Imp")) {
                zombie.setMoveSpeedMultiplier(1.3);
                zombie.setEatSpeedMultiplier(1.2);
            }
        }
        else if(property instanceof BossProperty) {
            switch (alias) {
                case "ZombieBossDragon" -> {
                    zombie = new DarkDragon(
                        currentHealth,
                        movement,
                        attack,
                        takeDamage,
                        position,
                        abilities,
                        world,
                        null,
                        property
                    );
                    bodyHeight = DRAGON_HEIGHT;
                    zombie.setMovementStrategy(new StationaryMovement());
                    zombie.setAttackStrategy(new NoAttackStrategy());
                }
                case "ZombieBossEgypt" -> {
                    zombie = new SphinxInator(
                        currentHealth,
                        movement,
                        attack,
                        takeDamage,
                        position,
                        abilities,
                        world,
                        null,
                        property
                    );
                    zombie.setMovementStrategy(new NoMovement());
                    zombie.setAttackStrategy(new InstantKillAttack());
                    bodyWidth = EGYPT_BOSS_WIDTH;
                    bodyHeight = EGYPT_BOSS_HEIGHT;
                }
                case "ZombieBossMammoth" -> {
                    zombie = new Mammoth(
                        currentHealth,
                        movement,
                        attack,
                        takeDamage,
                        position,
                        abilities,
                        world,
                        null,
                        property
                    );
                    zombie.setMovementStrategy(new StationaryMovement());
                    zombie.setAttackStrategy(new InstantKillAttack());
                    bodyWidth = EGYPT_BOSS_WIDTH;
                    bodyHeight = MAMMOTH_HEIGHT;
                }
                case "ZombieBossBeach" -> {
                    zombie = new Shark(
                        currentHealth,
                        movement,
                        attack,
                        takeDamage,
                        position,
                        abilities,
                        world,
                        null,
                        property
                    );
                    zombie.setMovementStrategy(new NoMovement());
                    zombie.setAttackStrategy(new InstantKillAttack());
                    bodyWidth = EGYPT_BOSS_WIDTH;
                    bodyHeight = EGYPT_BOSS_HEIGHT;
                }
            }
        }

        if (usesInsideWater(alias)) {
            zombie.addAbility(new InsideWaterAbility());
        }
        if (usesBodyPartDrop(alias)) {
            zombie.addAbility(new HandDroppingAbility());
        }
        if (GroundTrackedMovement.appliesTo(alias)) {
            zombie.setMovementStrategy(new GroundTrackedMovement());
        }
        if (!(property instanceof BossProperty)) {
            DropSpawner.assignPlantFoodAtSpawn(zombie);
        }

        Body zombieBody = BodyFactory.getInstance().createZombieBody(
            (float) position.getX(),
            (float) position.getY(),
            bodyWidth,
            bodyHeight
        );
        zombieBody.setUserData(zombie);
        zombie.setBody(zombieBody);
        System.err.println("I SET BODY");
        return zombie;
    }

    private static boolean usesInsideWater(String alias) {
        if (alias == null) {
            return false;
        }
        return alias.startsWith("Basic")
            || alias.contains("Flag")
            || alias.equals("Imp5")
            || alias.equals("ZombieBeachSnorkel")
            || alias.equals("ZombieBeachOctopus");
    }

    private static boolean usesBodyPartDrop(String alias) {
        if (alias == null) {
            return false;
        }
        return alias.startsWith("Basic")
            || alias.contains("Flag")
            || alias.startsWith("Imp")
            || alias.contains("Gargantuar");
    }

}
