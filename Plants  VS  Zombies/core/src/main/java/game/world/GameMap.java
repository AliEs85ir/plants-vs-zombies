package game.world;

import app.enums.lawnmower.LawnMowerType;
import app.model.App;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import game.entity.obstacle.Obstacle;
import game.entity.drop.Drop;
import game.entity.sun.Sun;
import game.entity.lawnmower.LawnMower;
import game.entity.plant.Plant;
import game.entity.projectile.Projectile;
import game.entity.wave.Wave;
import game.entity.zombie.Zombie;
import game.world.playground.Grid;
import game.world.playground.MapConfig;

import java.util.ArrayList;
import java.util.List;

public class GameMap {
    private final List<Sun> suns;
    private final List<Drop> drops;
    private Grid grid;
    private final ArrayList<Plant> plants;
    private final ArrayList<Zombie> zombies;
    private final ArrayList<Zombie> spawnedZombiesDuringGame;
    private final List<Projectile> projectiles;
    private ArrayList<LawnMower> lawnMowers;
    private ArrayList<Obstacle> obstacles;
    private Wave currentWave;

    private TiledMap tiledMap;
    private MapConfig mapConfig;

    public GameMap() {
        this.suns = new ArrayList<>();
        this.drops = new ArrayList<>();
        this.plants = new ArrayList<>();
        this.zombies = new ArrayList<>();
        this.projectiles = new ArrayList<>();
        this.lawnMowers = new ArrayList<>();
        this.obstacles = new ArrayList<>();
        this.spawnedZombiesDuringGame = new ArrayList<>();
        currentWave = new Wave();
    }

    private void initLawnMowers(LawnMowerType lmType) {
        List<Vector2> spawns = mapConfig.getLawnMowerSpawns();

        for (int i = 0; i < spawns.size(); i++) {
            Vector2 pos = spawns.get(i);

            LawnMower lawnMower = new LawnMower(new Vector2(pos.x, pos.y));
            lawnMower.setType(lmType);
            lawnMower.setRow(grid.getEntityLane(lawnMower));

            lawnMowers.add(lawnMower);
        }
    }

    public List<Sun> getSuns() {
        return suns;
    }

    public void addSun(Sun sun) {
        suns.add(sun);
    }

    public void removeSun(Sun sun) {
        if (sun.getBody() != null && App.getRealTimeEngine() != null)
            App.getRealTimeEngine()
                .getPhysicService()
                .destroyBody(sun.getBody());

        suns.remove(sun);
    }

    public List<Drop> getDrops() {
        return drops;
    }

    public void addDrop(Drop drop) {
        drops.add(drop);
    }

    public void removeDrop(Drop drop) {
        drops.remove(drop);
    }

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public ArrayList<Plant> getPlants() {
        return plants;
    }

    public void addPlant(Plant plant) {
        plants.add(plant);
    }

    public ArrayList<Zombie> getZombies() {
        return zombies;
    }

    public void addZombie(Zombie zombie) {
        spawnedZombiesDuringGame.add(zombie);
    }

    public void flushPendingZombies() {
        if (!spawnedZombiesDuringGame.isEmpty()) {
            zombies.addAll(spawnedZombiesDuringGame);
            for(Zombie zombie : spawnedZombiesDuringGame) {
                App.getRealTimeEngine().getGraphicsEngine().registerZombie(zombie);
            }

            spawnedZombiesDuringGame.clear();
        }
    }

    public List<Projectile> getProjectiles() {
        return projectiles;
    }

    public void addProjectile(Projectile projectile) {
        projectiles.add(projectile);
    }

    public ArrayList<LawnMower> getLawnMowers() {
        return lawnMowers;
    }


    public ArrayList<Zombie> getSpawnedZombiesDuringGame() {
        return spawnedZombiesDuringGame;
    }

    public Wave getCurrentWave() {
        return currentWave;
    }

    public void setCurrentWave(Wave currentWave) {
        this.currentWave = currentWave;
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public MapConfig getMapConfig() {
        return mapConfig;
    }

    public List<Vector2> getZombieSpawns() {
        return mapConfig.getZombieSpawns();
    }

    public List<Vector2> getObstacleCells() {
        return mapConfig.getObstacleCells();
    }

    public float getLoseLineX() {
        return mapConfig.getLoseLineX();
    }

    public Vector2 getCameraCenter() {
        Vector2 center = mapConfig.getCameraCenter();
        // Fallback: center on playground if no "map" layer
        if (center == null) {
            return new Vector2(
                mapConfig.getPlaygroundX() + mapConfig.getPlaygroundWidth() / 2f,
                mapConfig.getPlaygroundY() + mapConfig.getPlaygroundHeight() / 2f
            );
        }
        return center;
    }


    public LawnMower getLawnMowerInRow(int row) {
        for (LawnMower lm : lawnMowers) {
            if (lm.getRow() == row) return lm;
        }
        return null;
    }

    public List<Zombie> getZombiesInRow(int row) {
        List<Zombie> result = new ArrayList<>();
        float cellHeight = mapConfig.getCellHeight();
        float rowTop = mapConfig.getPlaygroundY() + (row + 1) * cellHeight;
        float rowBottom = mapConfig.getPlaygroundY() + row * cellHeight;

        for (Zombie z : zombies) {
            float zY = (float) z.getPosition().getY();
            if (zY >= rowBottom && zY < rowTop) {
                result.add(z);
            }
        }
        return result;
    }

    public boolean hasCrossedLoseLine(Zombie zombie) {
        float loseLine = mapConfig.getLoseLineX();
        if (loseLine == 0) return false;
        return zombie.getPosition().getX() < loseLine;
    }

    public void initFromGrid(TiledMap tiledMap, Grid grid, LawnMowerType lmType) {
        this.tiledMap = tiledMap;
        this.grid = grid;
        this.mapConfig = grid.getConfig();

        if (!mapConfig.getLawnMowerSpawns().isEmpty()) {
            lawnMowers = new ArrayList<>();
            initLawnMowers(lmType);
        }
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public void setObstacles(ArrayList<Obstacle> obstacles) {
        this.obstacles = obstacles;
    }
}
