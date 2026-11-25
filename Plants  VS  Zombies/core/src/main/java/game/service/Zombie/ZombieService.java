package game.service.Zombie;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import common.model.Position;
import common.model.Vector;
import game.component.contacts.ContactBits;
import game.entity.Entity;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.entity.zombie.ZombieArmor;
import game.entity.zombie.ZombieFactory;
import game.world.GameWorld;
import game.world.playground.Cell;
import game.world.playground.Grid;
import graphics.general.effects.anchers.KinematicAnchor;

import java.util.*;
import java.util.stream.Collectors;

public class ZombieService {
    private final GameWorld world;
    private final WaveService waveService;

    public ZombieService(GameWorld world) {
        this.world = world;
        waveService = new WaveService(world, this, world.getCurrentChapter());
    }


    public Zombie spawnZombie(String alias, Position position) {
        Zombie newZombie = ZombieFactory.buildZombie(alias, world, position);

        if(newZombie != null) {
            world.addZombie(newZombie);
            world.addEvent("Zombie spawned at position " + position.toString());
            game.audio.Sfx.zombieSpawned(newZombie);
            return newZombie;
        }
        return null;
    }
    public Zombie spawnZombie(String alias, Vector2 vector2) {
        Position position = new Position(vector2.x, vector2.y);
        return spawnZombie(alias, position);
    }

    public Plant getFirstPlantInRow(int lane) {
        Plant target = null;
        for(Plant plant : world.getPlants()) {
            if((int)plant.getPosition().getY() == lane) {
                if(target == null || target.getPosition().getX() < plant.getPosition().getX()) {
                    target = plant;
                }
            }
        }
        return target;
    }


    public List<Cell> giveRandomCells(int count) {
        List<Cell> plantableCells = Arrays.stream(world.getGrid().getAllCells())
            .filter(cell -> cell != null && cell.isPlantable())
            .collect(Collectors.toList());
        Collections.shuffle(plantableCells);
        int limit = Math.min(count, plantableCells.size());
        return new ArrayList<>(plantableCells.subList(0, limit));

    }

    public GameWorld getWorld() {
        return world;
    }

    public WaveService getWaveService() {
        return waveService;
    }

    public static KinematicAnchor getRandomKinematicAnchor(Zombie zombie) {
        Random random = new Random();
        Vector2 start = zombie.getPosition().toVector().add(new Vector2(0, 100));
        int randomRotation = random.nextInt(10);
        float groundY = (float) zombie.getPosition().getY() - (random.nextFloat() * 300);
        float dirX = random.nextBoolean() ? 1 : -1;
        float velX = (random.nextFloat() * 100 + 200) * dirX;
        float velY = (random.nextFloat() * 200 + 350);
        return new KinematicAnchor(start, new Vector2(velX, velY), randomRotation, groundY);
    }

    public static void addArmorBreakEffect(ZombieArmor armor, Zombie zombie) {
        KinematicAnchor anchor = getRandomKinematicAnchor(zombie);
        switch (armor.getName()) {
            case "BucketDefault" -> {
                zombie.pushEffect(zombie.getName() + " BUCKET", anchor);
            game.audio.Sfx.play(game.audio.AudioId.SFX_MAGNETSHROOM);
            }
            case "ConeDefault" -> {
                zombie.pushEffect(zombie.getName() + " CONE", anchor);
            game.audio.Sfx.playRandom(game.audio.AudioId.SFX_SHIELDHIT, game.audio.AudioId.SFX_SHIELDHIT2);
            }
            case "BrickDefault" -> {
                zombie.pushEffect(zombie.getName() + " BRICK", anchor);
            game.audio.Sfx.play(game.audio.AudioId.SFX_HYDRAULIC_SHORT);
            }
        }
    }
}
