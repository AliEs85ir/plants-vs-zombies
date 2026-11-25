package graphics;

import app.enums.lawnmower.LawnMowerType;
import app.enums.obstacle.ObstacleType;
import app.enums.drop.DropType;
import app.enums.plant.PlantName;
import app.enums.sun.SunType;
import app.model.App;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import game.entity.lawnmower.LawnMower;
import game.entity.obstacle.Obstacle;
import game.entity.drop.Drop;
import game.entity.plant.Plant;
import game.entity.projectile.Projectile;
import game.entity.sun.Sun;
import game.entity.zombie.Zombie;
import game.entity.zombie.data.properties.Property;
import game.dialogue.Speaker;
import game.world.chapter.Chapter;
import game.world.chapter.ChapterType;
import game.world.chapter.rule.ChapterRule;
import game.world.chapter.rule.NpcDialogueRule;
import game.world.GameWorld;
import game.world.playground.Grid;
import graphics.general.animation.*;
import graphics.general.animation.loader.PlantAnimationProfileLoader;
import graphics.general.effects.*;
import graphics.general.instance.RenderInstance;
import graphics.general.renderer.*;
import graphics.general.resolver.*;
import graphics.drop.DropRenderInstance;
import graphics.drop.DropRenderer;
import graphics.lawnmower.*;
import graphics.npc.*;
import graphics.obstacle.ObstacleAnimationProfile;
import graphics.obstacle.ObstacleAnimationRegistry;
import graphics.obstacle.ObstacleAnimationSelector;
import graphics.obstacle.ObstacleRenderInstance;
import graphics.plant.*;
import graphics.projectile.*;
import graphics.sun.*;
import graphics.world.BeachEnvironmentRenderer;
import graphics.zombie.*;
import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.pam.PamPlayer;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GraphicsEngine {
    private final AnimationLibrary animationLibrary;

    private final PlantAnimationRegistry plantAnimationRegistry;
    private final PlantAnimationSelector plantAnimationSelector;
    private final PlantVisualStateResolver plantStateResolver;
    private final PlantRenderer plantRenderer;

    private final ZombieAnimationRegistry zombieAnimationRegistry;
    private final ZombieAnimationSelector zombieAnimationSelector;
    private final ZombieVisualStateResolver zombieStateResolver;
    private final ZombieRenderer zombieRenderer;

    private final LMAnimationRegistry lmAnimationRegistry;
    private final LMAnimationSelector lmAnimationSelector;
    private final LawnMowerVisualStateResolver lmStateResolver;
    private final LawnMowerRenderer lawnMowerRenderer;

    private final SunAnimationRegistry sunAnimationRegistry;
    private final SunAnimationSelector sunAnimationSelector;
    private final SunVisualStateResolver sunStateResolver;
    private final SunRenderer sunRenderer;
    private final DropRenderer dropRenderer;

    private final ObstacleAnimationRegistry obstacleAnimationRegistry;
    private final ObstacleAnimationSelector obstacleAnimationSelector;
    private final ObstacleVisualStateResolver obstacleStateResolver;
    private final ObstacleRenderer obstacleRenderer;

    private final ProjectileAnimationRegistry projectileAnimationRegistry;
    private final ProjectileAnimationSelector projectileAnimationSelector;
    private final ProjectileVisualStateResolver projectileStateResolver;
    private final ProjectileRenderer projectileRenderer;

    private final NpcAnimationRegistry npcAnimationRegistry;
    private final NpcAnimationSelector npcAnimationSelector;
    private final NpcVisualStateResolver npcStateResolver;
    private final NpcRenderer npcRenderer;

    private final Map<Plant, PlantRenderInstance> activePlantInstances;
    private final Map<Zombie, ZombieRenderInstance> activeZombieInstances;
    private final Map<LawnMower, LMRenderInstance> activeLawnMowerInstances;
    private final Map<Sun, SunRenderInstance> activeSunInstances;
    private final Map<Drop, DropRenderInstance> activeDropInstances;
    private final Map<Projectile, ProjectileRenderInstance> activeProjectileInstances;
    private final Map<Obstacle, ObstacleRenderInstance> activeObstacleInstances;
    private final Map<NpcPresence, NpcRenderInstance> activeNpcInstances;

    private final EffectRegistry effectRegistry;
    private final EffectManager effectManager;
    private final EffectRenderer effectRenderer;
    private BeachEnvironmentRenderer beachEnvironmentRenderer;

    public GraphicsEngine(PamPlayer pamPlayer) {
        animationLibrary = new AnimationLibrary(pamPlayer);

        plantAnimationRegistry = new PlantAnimationRegistry();
        plantAnimationSelector = new PlantAnimationSelector();
        plantStateResolver = new PlantVisualStateResolver();
        plantRenderer = new PlantRenderer(pamPlayer);

        zombieAnimationRegistry = new ZombieAnimationRegistry();
        zombieAnimationSelector = new ZombieAnimationSelector();
        zombieStateResolver = new ZombieVisualStateResolver();
        zombieRenderer = new ZombieRenderer(pamPlayer);

        lmAnimationRegistry = new LMAnimationRegistry();
        lmAnimationSelector = new LMAnimationSelector();
        lmStateResolver = new LawnMowerVisualStateResolver();
        lawnMowerRenderer = new LawnMowerRenderer(pamPlayer);

        sunAnimationRegistry = new SunAnimationRegistry();
        sunAnimationSelector = new SunAnimationSelector();
        sunStateResolver = new SunVisualStateResolver();
        sunRenderer = new SunRenderer(pamPlayer);
        dropRenderer = new DropRenderer(pamPlayer, animationLibrary);

        projectileAnimationRegistry = new ProjectileAnimationRegistry();
        projectileAnimationSelector = new ProjectileAnimationSelector();
        projectileStateResolver = new ProjectileVisualStateResolver();
        projectileRenderer = new ProjectileRenderer(pamPlayer);

        obstacleAnimationRegistry = new ObstacleAnimationRegistry();
        obstacleAnimationSelector = new ObstacleAnimationSelector();
        obstacleStateResolver = new ObstacleVisualStateResolver();
        obstacleRenderer = new ObstacleRenderer(pamPlayer);

        npcAnimationRegistry = new NpcAnimationRegistry();
        npcAnimationSelector = new NpcAnimationSelector();
        npcStateResolver = new NpcVisualStateResolver();
        npcRenderer = new NpcRenderer(pamPlayer);

        activePlantInstances = new HashMap<>();
        activeZombieInstances = new HashMap<>();
        activeLawnMowerInstances = new HashMap<>();
        activeSunInstances = new HashMap<>();
        activeDropInstances = new HashMap<>();
        activeProjectileInstances = new HashMap<>();
        activeObstacleInstances = new HashMap<>();
        activeNpcInstances = new HashMap<>();

        this.effectRegistry = new EffectRegistry();
        EffectDefinitionLoader.loadInto("effect/effects.json", effectRegistry);
        this.effectRenderer = new EffectRenderer(pamPlayer);
        this.effectManager = new EffectManager(animationLibrary, effectRegistry, effectRenderer);
    }

    public void prepareLevelAssets(Chapter chapter) {
        Set<String> levelZombies = chapter
            .getAvailableZombies()
            .stream()
            .filter(Objects::nonNull)
            .map(Property::getAlias)
            .collect(Collectors.toSet());

        Set<PlantName> levelPlants = new HashSet<>(chapter.getAvailablePlants());

        animationLibrary.unloadAllExcept(levelPlants);
        for (PlantName plantName : levelPlants) {
            ensurePlantProfileLoaded(plantName);
            ProjectileAnimationProfile projProfile = projectileAnimationRegistry.getProfile(plantName);
            if (projProfile != null) {
                animationLibrary.loadAssets(plantName, projProfile.getPamPath());
            }
        }


        animationLibrary.unloadAllExcept(levelZombies);
        for (String alias : levelZombies) {
            ZombieAnimationProfile profile = zombieAnimationRegistry.getProfile(alias);
            if (profile != null) {
                animationLibrary.loadAssets(alias, profile.getPamPath());
            }
        }

        if (chapter.getLawnMowerType() != null) {
            LawnMowerType lmType = chapter.getLawnMowerType();
            LMAnimationProfile lmProfile = lmAnimationRegistry.getProfile(lmType);
            if (lmProfile != null) {
                animationLibrary.loadAssets(lmType, lmProfile.getPamPath());
            }
        }

        List<ObstacleType> obstacleTypes = chapter.getObstacleTypes();
        for(ObstacleType type : obstacleTypes) {
            ObstacleAnimationProfile obstacleProfile = obstacleAnimationRegistry.getProfile(type);
            if(obstacleProfile != null) {
                animationLibrary.loadAssets(type, obstacleProfile.getPamPath());
            }
        }

        for (SunAnimationProfile sunProfile : sunAnimationRegistry.getAllProfiles()) {
            animationLibrary.loadAssets(sunProfile.getSunType(), sunProfile.getPamPath());
        }
        for (DropType dropType : DropType.values()) {
            if (dropType.usesPam()) {
                animationLibrary.loadAssets(dropType, dropType.getPamPath());
            }
        }


        loadBossAssets(chapter);
        loadNpcAssets(chapter);
        prepareBeachEnvironment(chapter);
    }

    private void prepareBeachEnvironment(Chapter chapter) {
        beachEnvironmentRenderer = null;
        if (chapter == null || chapter.getSeason() == null || chapter.getSeason().getTideController() == null) {
            return;
        }
        beachEnvironmentRenderer = new BeachEnvironmentRenderer(
            animationLibrary,
            chapter.getSeason().getTideController()
        );
        beachEnvironmentRenderer.load();
    }

    private void loadNpcAssets(Chapter chapter) {
        if (chapter == null || chapter.getRules() == null) {
            return;
        }
        boolean needed = false;
        for (ChapterRule rule : chapter.getRules()) {
            if (rule instanceof NpcDialogueRule npcRule && npcRule.isEnabled()) {
                needed = true;
                break;
            }
        }
        if (!needed) {
            return;
        }
        for (Speaker speaker : Speaker.values()) {
            NpcAnimationProfile profile = npcAnimationRegistry.getProfile(speaker);
            if (profile != null) {
                animationLibrary.loadAssets(speaker, profile.getPamPath());
            }
        }
    }

    private void loadBossAssets(Chapter chapter) {
        if(chapter.getChapterType() != ChapterType.BOSS) {
            return;
        }
        switch (chapter.getSeason().getSeasonType()) {
            case DARK_AGES -> {
                ZombieAnimationProfile profile = zombieAnimationRegistry.getProfile("ZombieBossDragon");
                animationLibrary.loadAssets("ZombieBossDragon", profile.getPamPath());
            }
            case ANCIENT_EGYPT -> {
                ZombieAnimationProfile profile = zombieAnimationRegistry.getProfile("ZombieBossEgypt");
                animationLibrary.loadAssets("ZombieBossEgypt", profile.getPamPath());
            }
        }
    }

    public void registerPlant(Plant plant) {
        if (plant == null || activePlantInstances.containsKey(plant)) return;
        PlantName plantName = plant.getDefinition().getName();
        ensurePlantProfileLoaded(plantName);
        activePlantInstances.put(plant, new PlantRenderInstance(plant));
    }

    private PlantAnimationProfile ensurePlantProfileLoaded(PlantName plantName) {
        PlantAnimationProfile profile = plantAnimationRegistry.getProfile(plantName);
        if (profile == null) {
            profile = PlantAnimationProfileLoader.fallbackFor(plantName);
            plantAnimationRegistry.replaceProfile(profile);
        }
        if (animationLibrary.tryEnsureLoaded(profile.getPamPath())) {
            return profile;
        }
        Gdx.app.error(
            "GraphicsEngine",
            "Missing plant PAM " + profile.getPamPath() + ", using peashooter for " + plantName
        );
        PlantAnimationProfile fallback = PlantAnimationProfileLoader.fallbackFor(plantName);
        plantAnimationRegistry.replaceProfile(fallback);
        animationLibrary.tryEnsureLoaded(fallback.getPamPath());
        return fallback;
    }

    public void unregisterPlant(Plant plant) {
        if (plant != null) activePlantInstances.remove(plant);
    }

    public void registerZombie(Zombie zombie) {
        if (zombie == null || activeZombieInstances.containsKey(zombie)) return;
        String name = zombie.getName();
        ZombieAnimationProfile profile = zombieAnimationRegistry.getProfile(name);
        if (profile == null) {
            Gdx.app.error("GraphicsEngine", "No animation profile for zombie alias: " + name);
            return;
        }
        animationLibrary.loadAssets(name, profile.getPamPath());
        activeZombieInstances.put(zombie, new ZombieRenderInstance(zombie, profile));
    }

    public void unregisterZombie(Zombie zombie) {
        if (zombie != null) activeZombieInstances.remove(zombie);
    }

    public void registerLawnmower(LawnMower lawnMower) {
        if (lawnMower == null || activeLawnMowerInstances.containsKey(lawnMower)) return;
        activeLawnMowerInstances.put(lawnMower, new LMRenderInstance(lawnMower));
    }

    public void unregisterLawnmower(LawnMower lawnMower) {
        if (lawnMower != null) activeLawnMowerInstances.remove(lawnMower);
    }

    public void registerSun(Sun sun) {
        if (sun == null || activeSunInstances.containsKey(sun)) return;
        SunType type = sun.getType();
        SunAnimationProfile profile = sunAnimationRegistry.getProfile(type);
        if (profile != null) {
            animationLibrary.loadAssets(type, profile.getPamPath());
        }
        activeSunInstances.put(sun, new SunRenderInstance(sun));
    }

    public void unregisterSun(Sun sun) {
        if (sun != null) activeSunInstances.remove(sun);
    }

    public void registerDrop(Drop drop) {
        if (drop == null || activeDropInstances.containsKey(drop)) {
            return;
        }
        DropType type = drop.getType();
        if (type.usesPam()) {
            animationLibrary.loadAssets(type, type.getPamPath());
        }
        activeDropInstances.put(drop, new DropRenderInstance(drop));
    }

    public void unregisterDrop(Drop drop) {
        if (drop != null) {
            activeDropInstances.remove(drop);
        }
    }

    public void clearDrops() {
        activeDropInstances.clear();
    }

    public void registerProjectile(Projectile projectile) {
        if (projectile == null || activeProjectileInstances.containsKey(projectile)) return;
        PlantName plantName = projectile.getAnimationPlantName();

        ProjectileAnimationProfile profile = projectileAnimationRegistry.getProfile(plantName);
        if (profile != null) {
            animationLibrary.loadAssets(plantName, profile.getPamPath());
        }
        activeProjectileInstances.put(projectile, new ProjectileRenderInstance(projectile));
    }

    public void unregisterProjectile(Projectile projectile) {
        if (projectile != null) activeProjectileInstances.remove(projectile);
    }

    public void refreshProjectileVisual(Projectile projectile) {
        if (projectile == null) {
            return;
        }
        unregisterProjectile(projectile);
        registerProjectile(projectile);
    }

    public void registerObstacle(Obstacle obstacle) {
        if(obstacle == null || activeObstacleInstances.containsKey(obstacle)) {
            return;
        }
        activeObstacleInstances.put(obstacle, new ObstacleRenderInstance(obstacle));
    }

    public void unregisterObstacle(Obstacle obstacle) {
        if(obstacle != null) activeObstacleInstances.remove(obstacle);
    }

    public void registerNpc(NpcPresence presence) {
        if (presence == null || activeNpcInstances.containsKey(presence)) {
            return;
        }
        Speaker speaker = presence.getSpeaker();
        NpcAnimationProfile profile = npcAnimationRegistry.getProfile(speaker);
        if (profile != null) {
            animationLibrary.loadAssets(speaker, profile.getPamPath());
        }
        activeNpcInstances.put(presence, new NpcRenderInstance(presence));
    }

    public void unregisterNpc(NpcPresence presence) {
        if (presence != null) {
            activeNpcInstances.remove(presence);
        }
    }

    public void advanceGroundTrackedZombiesBeforePhysics() {
        if (App.getRealTimeEngine() == null || !App.getRealTimeEngine().canAdvanceSimulation()) {
            return;
        }
        for (ZombieRenderInstance instance : activeZombieInstances.values()) {
            instance.advanceGroundTrack();
        }
    }

    public void updateAndRender(SpriteBatch batch, float delta) {
        Grid grid = App.getCurrentGameEngine().getGameWorld().getGrid();
        int rowCount = grid.getRowCount();
        renderBackgroundObstacles(batch, delta);
        renderBeachEnvironment(batch, delta, grid);
        effectManager.render(batch, EffectLayer.BEHIND);

        List<List<PlantRenderInstance>> plantsByLane = createLaneBuckets(rowCount);
        List<List<ObstacleRenderInstance>> fgObstaclesByLane = createLaneBuckets(rowCount);
        List<List<ZombieRenderInstance>> zombiesByLane = createLaneBuckets(rowCount);
        List<List<LMRenderInstance>> mowersByLane = createLaneBuckets(rowCount);

        updateAndBucketPlants(delta, grid, plantsByLane);
        updateAndBucketForegroundObstacles(delta, grid, fgObstaclesByLane);
        updateAndBucketZombies(delta, grid, zombiesByLane);
        updateAndBucketLawnmowers(delta, grid, mowersByLane);

        effectManager.resolveClips();
        effectManager.update(delta);

        for (int lane = rowCount - 1; lane >= 0; lane--) {
            renderInstances(batch, plantsByLane.get(lane), plantRenderer);
            renderInstances(batch, fgObstaclesByLane.get(lane), obstacleRenderer);
            renderInstances(batch, zombiesByLane.get(lane), zombieRenderer);
            renderInstances(batch, mowersByLane.get(lane), lawnMowerRenderer);
        }

        renderProjectiles(batch, delta);
        renderSuns(batch, delta);
        renderDrops(batch, delta);

        effectManager.render(batch, EffectLayer.DEFAULT);
        effectManager.render(batch, EffectLayer.FRONT);
        effectManager.render(batch, EffectLayer.UI);

        renderNpcs(batch, delta);
    }

    private void renderNpcs(SpriteBatch batch, float delta) {
        if (activeNpcInstances.isEmpty()) {
            return;
        }
        for (NpcRenderInstance instance : activeNpcInstances.values()) {
            instance.update(delta);
            updateInstanceState(instance, npcStateResolver);
            updateInstanceAnimation(
                instance,
                i -> i.getNpc().getSpeaker(),
                npcAnimationRegistry,
                npcAnimationSelector
            );
            npcRenderer.render(batch, instance);
        }
    }

    private <T> List<List<T>> createLaneBuckets(int rowCount) {
        List<List<T>> buckets = new ArrayList<>(rowCount);
        for (int i = 0; i < rowCount; i++) {
            buckets.add(new ArrayList<>());
        }
        return buckets;
    }

    private void renderBeachEnvironment(SpriteBatch batch, float delta, Grid grid) {
        if (beachEnvironmentRenderer != null) {
            beachEnvironmentRenderer.render(batch, grid, delta);
        }
    }

    private void renderBackgroundObstacles(SpriteBatch batch, float delta) {
        for (ObstacleRenderInstance instance : activeObstacleInstances.values()) {
            if (instance.getObstacle().isBackGround()) {
                instance.update(delta);
                updateInstanceState(instance, obstacleStateResolver);
                updateInstanceAnimation(instance, i -> i.getObstacle().getType(), obstacleAnimationRegistry, obstacleAnimationSelector);

                obstacleRenderer.render(batch, instance);
            }
        }
    }

    private void updateAndBucketForegroundObstacles(float delta, Grid grid, List<List<ObstacleRenderInstance>> fgBuckets) {
        for (ObstacleRenderInstance instance : activeObstacleInstances.values()) {
            if (!instance.getObstacle().isBackGround()) {
                instance.update(delta);
                updateInstanceState(instance, obstacleStateResolver);
                updateInstanceAnimation(instance, i -> i.getObstacle().getType(), obstacleAnimationRegistry, obstacleAnimationSelector);

                int lane = grid.getEntityLane(instance.getObstacle());
                if (lane >= 0 && lane < fgBuckets.size()) {
                    fgBuckets.get(lane).add(instance);
                }
            }
        }
    }


    private void updateAndBucketPlants(float delta, Grid grid, List<List<PlantRenderInstance>> buckets) {
        Iterator<Map.Entry<Plant, PlantRenderInstance>> it = activePlantInstances.entrySet().iterator();
        while (it.hasNext()) {
            PlantRenderInstance instance = it.next().getValue();
            instance.update(delta);
            updateInstanceState(instance, plantStateResolver);

            if (instance.getCurrentVisualState() == PlantVisualState.DEATH) {
                instance.ensureDeathAnimationFinishes();
            } else {
                updateInstanceAnimation(instance, i -> i.getPlant().getDefinition().getName(), plantAnimationRegistry, plantAnimationSelector);
            }

            if (instance.getRenderPosition() != null) {
                drainCues(instance, p -> new Vector2((float) instance.getRenderPosition().getX(), (float) instance.getRenderPosition().getY()));
            }

            if (instance.isReadyForRemoval()) {
                it.remove();
            } else {
                int lane = grid.getEntityLane(instance.getPlant());
                if (lane >= 0 && lane < buckets.size()) buckets.get(lane).add(instance);
            }
        }
        for (List<PlantRenderInstance> lanePlants : buckets) {
            PlantDrawOrder.sort(lanePlants);
        }
    }

    private void updateAndBucketZombies(float delta, Grid grid, List<List<ZombieRenderInstance>> buckets) {
        for (ZombieRenderInstance instance : activeZombieInstances.values()) {
            instance.update(delta);
            updateInstanceState(instance, zombieStateResolver);
            updateInstanceAnimation(instance, i -> i.getZombie().getName(), zombieAnimationRegistry, zombieAnimationSelector);

            drainCues(instance, z -> new Vector2((float) z.getPosition().getX(), (float) z.getPosition().getY()));

            int lane = grid.getEntityLane(instance.getZombie());
            if (lane >= 0 && lane < buckets.size()) buckets.get(lane).add(instance);
        }
    }

    private void updateAndBucketLawnmowers(float delta, Grid grid, List<List<LMRenderInstance>> buckets) {
        for (LMRenderInstance instance : activeLawnMowerInstances.values()) {
            instance.update(delta);
            updateInstanceState(instance, lmStateResolver);
            updateInstanceAnimation(instance, i -> i.getlawnmower().getType(), lmAnimationRegistry, lmAnimationSelector);

            int lane = grid.getEntityLane(instance.getlawnmower());
            if (lane >= 0 && lane < buckets.size()) buckets.get(lane).add(instance);
        }
    }

    private <I> void renderInstances(SpriteBatch batch, List<I> instances, EntityRenderer<I> renderer) {
        for (I instance : instances) {
            renderer.render(batch, instance);
        }
    }


    private void renderPlants(SpriteBatch batch, float delta) {
        if (activePlantInstances.isEmpty()) return;
        Iterator<Map.Entry<Plant, PlantRenderInstance>> it =
            activePlantInstances.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Plant, PlantRenderInstance> entry = it.next();
            PlantRenderInstance instance = entry.getValue();
            instance.update(delta);
            updateInstanceState(instance, plantStateResolver);

            if (instance.getCurrentVisualState() == PlantVisualState.DEATH) {
                instance.ensureDeathAnimationFinishes();
            } else {
                updateInstanceAnimation(instance, i -> i.getPlant().getDefinition().getName(),
                    plantAnimationRegistry, plantAnimationSelector);
            }

            plantRenderer.render(batch, instance);

            if (instance.getRenderPosition() != null) {
                drainCues(instance, p -> new Vector2(
                    (float) instance.getRenderPosition().getX(),
                    (float) instance.getRenderPosition().getY()
                ));
            }

            if (instance.isReadyForRemoval()) {
                it.remove();
            }
        }
    }

    private void renderSuns(SpriteBatch batch, float delta) {
        if (activeSunInstances.isEmpty()) return;
        List<SunRenderInstance> instances = new ArrayList<>(activeSunInstances.values());
        for (SunRenderInstance instance : instances) {
            instance.update(delta);
            updateInstanceState(instance, sunStateResolver);
            updateInstanceAnimation(instance, i -> i.getModel().getType(),
                sunAnimationRegistry, sunAnimationSelector);
            sunRenderer.render(batch, instance);
        }
    }

    private void renderDrops(SpriteBatch batch, float delta) {
        if (activeDropInstances.isEmpty()) {
            return;
        }
        List<DropRenderInstance> instances = new ArrayList<>(activeDropInstances.values());
        GameWorld world = App.getCurrentGameEngine() == null ? null : App.getCurrentGameEngine().getGameWorld();
        for (DropRenderInstance instance : instances) {
            Drop drop = instance.getDrop();
            if (world != null && !world.getDrops().contains(drop)) {
                activeDropInstances.remove(drop);
                continue;
            }
            instance.update(delta);
            if (drop.isFinished()) {
                if (world != null) {
                    world.removeDrop(drop);
                }
                activeDropInstances.remove(drop);
                continue;
            }
            dropRenderer.render(batch, instance);
        }
    }

    private void renderProjectiles(SpriteBatch batch, float delta) {
        if (activeProjectileInstances.isEmpty()) return;
        Iterator<Map.Entry<Projectile, ProjectileRenderInstance>> it =
            activeProjectileInstances.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Projectile, ProjectileRenderInstance> entry = it.next();
            ProjectileRenderInstance instance = entry.getValue();
            instance.update(delta);
            updateInstanceState(instance, projectileStateResolver);
            updateInstanceAnimation(instance,
                i -> i.getModel().getAnimationPlantName(),
                projectileAnimationRegistry, projectileAnimationSelector);
            projectileRenderer.render(batch, instance);

            if (instance.isReadyForRemoval()) {
                it.remove();
            }
        }
    }

    private <M, S extends Enum<S>, I extends RenderInstance<M, S, ?, ?>>
    void updateInstanceState(I instance, VisualStateResolver<M, S, I> resolver) {
        S state = resolver.resolve(
            instance.getModel(),
            instance,
            instance.getCurrentVisualState(),
            instance.getAnimationController().isFinished()
        );
        instance.setCurrentVisualState(state);
    }

    @SuppressWarnings("unchecked")
    private <K, S extends Enum<S>, V extends AnimationVariant<?, ?>,
        P extends AnimationProfile<K, S, V>,
        I extends RenderInstance<?, S, ?, V>>
    void updateInstanceAnimation(
        I instance,
        Function<I, K> keyExtractor,
        AnimationRegistry<K, S, V, P> registry,
        AnimationSelector<K, S, V, I> selector
    ) {
        AnimationController<V> controller = instance.getAnimationController();

        if (shouldAdvanceFinishedClip(instance, controller)) {
            instance.setAnimationStage(instance.getAnimationStage() + 1);
        }

        K entityKey = keyExtractor.apply(instance);
        P profile = registry.getProfile(entityKey);
        if (profile == null) return;

        V variant = selector.selectVariant(profile, instance, instance.getCurrentVisualState());
        if (variant == null) return;

        String effectivePamPath = variant.getPamPath() != null ? variant.getPamPath() : profile.getPamPath();
        boolean pamLoaded = animationLibrary.tryEnsureLoaded(effectivePamPath);
        if (!pamLoaded && entityKey instanceof PlantName plantName) {
            PlantAnimationProfile fallback = PlantAnimationProfileLoader.fallbackFor(plantName);
            plantAnimationRegistry.replaceProfile(fallback);
            profile = (P) fallback;
            variant = selector.selectVariant(profile, instance, instance.getCurrentVisualState());
            if (variant == null) {
                return;
            }
            effectivePamPath = variant.getPamPath() != null ? variant.getPamPath() : profile.getPamPath();
            pamLoaded = animationLibrary.tryEnsureLoaded(effectivePamPath);
        }
        if (!pamLoaded) {
            return;
        }

        ClipKey<K> clipKey = new ClipKey<>(entityKey, variant.getClipName(), effectivePamPath);
        ClipRef clip = animationLibrary.getClip(clipKey);
        if (clip == null) {
            clip = animationLibrary.resolveClip(effectivePamPath, variant.getClipName());
            if (clip == null && entityKey instanceof PlantName plantName
                && !PlantAnimationProfileLoader.DEFAULT_PAM_PATH.equals(effectivePamPath)) {
                PlantAnimationProfile fallback = PlantAnimationProfileLoader.fallbackFor(plantName);
                plantAnimationRegistry.replaceProfile(fallback);
                profile = (P) fallback;
                variant = selector.selectVariant(profile, instance, instance.getCurrentVisualState());
                if (variant == null) {
                    return;
                }
                effectivePamPath = variant.getPamPath() != null ? variant.getPamPath() : profile.getPamPath();
                clipKey = new ClipKey<>(entityKey, variant.getClipName(), effectivePamPath);
                clip = animationLibrary.resolveClip(effectivePamPath, variant.getClipName());
            }
            if (clip == null) {
                return;
            }
            animationLibrary.putClip(clipKey, clip);
        }

        controller.changeVariant(variant, clip, effectivePamPath);
    }

    private boolean shouldAdvanceFinishedClip(
        RenderInstance<?, ?, ?, ?> instance,
        AnimationController<?> controller
    ) {
        if (!controller.isFinished()
            || controller.getCurrentVariant() == null
            || controller.getCurrentVariant().isLoop()) {
            return false;
        }
        return !(instance instanceof PlantRenderInstance plantInstance
            && plantInstance.ownsPlantFoodAnimation());
    }

    private <M, S extends Enum<S>, V extends AnimationVariant<?, ?>>
    void drainCues(RenderInstance<M, S, ?, V> instance, Function<M, Vector2> pos) {
        for (EffectCue cue : instance.getAnimationController().pollReadyCues()) {
            effectManager.spawnFromCue(cue, instance.getModel(), pos);
        }
    }

    public PlantRenderInstance getPlantInstance(Plant plant) {
        return activePlantInstances.get(plant);
    }

    public ZombieRenderInstance getZombieInstance(Zombie zombie) {
        return activeZombieInstances.get(zombie);
    }

    public LMRenderInstance getLMInstance(LawnMower lawnMower) {
        return activeLawnMowerInstances.get(lawnMower);
    }

    public SunRenderInstance getSunInstance(Sun sun) {
        return activeSunInstances.get(sun);
    }
    public ObstacleRenderInstance getObstacleInstance(Obstacle obstacle) {
        return activeObstacleInstances.get(obstacle);
    }

    public NpcRenderInstance getNpcInstance(NpcPresence presence) {
        return activeNpcInstances.get(presence);
    }

    public ProjectileRenderInstance getProjectileInstance(Projectile projectile) {
        return activeProjectileInstances.get(projectile);
    }

    public EffectManager getEffectManager() {
        return effectManager;
    }

    public String resolveZombiePamPath(Zombie zombie) {
        if (zombie == null) {
            return null;
        }
        ZombieRenderInstance instance = getZombieInstance(zombie);
        if (instance != null) {
            String current = instance.getAnimationController().getCurrentPamPath();
            if (current != null && !current.isBlank()) {
                return current;
            }
        }
        ZombieAnimationProfile profile = zombieAnimationRegistry.getProfile(zombie.getName());
        return profile == null ? null : profile.getPamPath();
    }
}
