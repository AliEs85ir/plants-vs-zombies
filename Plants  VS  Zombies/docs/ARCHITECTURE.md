# Plants vs. Zombies — Project Architecture Reference

> This document is based on direct source inspection. Every statement reflects observed
> source code. Speculation is explicitly marked as such.

---

## A. High-Level Architecture

The project is a Java/libGDX desktop game built as a two-module Gradle project:
- `lwjgl3/` — desktop launcher (`Lwjgl3Launcher`, `StartupHelper`)
- `core/` — all game logic, simulation, rendering, UI, persistence

Source packages under `core/src/main/java/`:

| Package | Responsibility |
|---|---|
| `plants_vs_zombies` | Application entry point (`PlantsVSZombies` — libGDX `Game`) |
| `app/` | Application shell: menus, controllers, model, persistence |
| `game/` | Game engine, simulation, entities, world, chapter/season system |
| `graphics/` | Rendering engine, animation, visual events |
| `common/` | Shared utilities, `Result`, `Position`, `Input` |

### Layers

```
┌─────────────────────────────────────────────┐
│  Launcher (lwjgl3)                          │
├─────────────────────────────────────────────┤
│  Application shell (app/)                  │
│    AppLoop · MenuController · Repository   │
├─────────────────────────────────────────────┤
│  Menu / UI layer                            │
│    LibGDX screens (LoginScreen etc.)        │
│    Console AppMenu implementations          │
├─────────────────────────────────────────────┤
│  Session / Chapter layer                    │
│    ChapterRegistry · ChapterFactory         │
│    PendingGameSession · ChapterWorldBuilder │
├─────────────────────────────────────────────┤
│  Real-time engine (RealTimeGameEngine)      │
│    PhysicService (Box2D) · GraphicsEngine   │
│    GameClock · BodyFactory                  │
├─────────────────────────────────────────────┤
│  Simulation engine (GameSimulationEngine)   │
│    TickProcessor · ScheduledTaskManager     │
│    TickSystem implementations               │
├─────────────────────────────────────────────┤
│  Entity / Model layer                       │
│    Plant · Zombie · Projectile · LawnMower  │
│    Sun · Obstacle · DarkDragon              │
├─────────────────────────────────────────────┤
│  Physics (Box2D)                            │
│    WorldContactListener · BodyFactory       │
├─────────────────────────────────────────────┤
│  Graphics / PAM rendering                  │
│    GraphicsEngine · AnimationLibrary        │
│    PamPlayer (libPVZ)                       │
├─────────────────────────────────────────────┤
│  Persistence                                │
│    Repository · QuestSaveService            │
│    users.json · quest_progress.json         │
└─────────────────────────────────────────────┘
```

---

## B. Execution Flows

### B1. Application Startup

```
Lwjgl3Launcher.main()
  └─ new LwjglApplication(new PlantsVSZombies(), config)
       └─ PlantsVSZombies.create()
            └─ AppLoop.init()
                 ├─ PlantDefinitionLoader.load()       // must be first
                 ├─ ZombieJsonParserService.loadZombiesFromJson("zombies.json")
                 ├─ ZombieJsonParserService.loadArmorsFromJson("armor.json")
                 ├─ Repository.loadLoginedUser()        // reads users.json
                 ├─ App.setCurrentUser(user)
                 ├─ [if user found]
                 │    ├─ QuestLoad.load()               // reads quest_progress.json
                 │    ├─ gameMenuController.manageEnteringChapter("1.2")
                 │    │    └─ ChapterRegistry.getChapterByName("1.2")
                 │    │    └─ App.getPendingSession().setSelectedChapter(chapter)
                 │    ├─ PlantSelectionController.addPlant(x21 hardcoded plants)
                 │    └─ PlantSelectionController.startGame()  → see B2
                 └─ background console thread started (daemon)

PlantsVSZombies.render() [called every frame by libGDX]
  ├─ AppLoop.update()         // currently empty
  └─ currentScreen.render(delta)
```

### B2. Game / Chapter Creation

```
PlantSelectionController.startGame()
  ├─ ChapterWorldBuilder.buildWorld(chapter)   // creates GameWorld + Grid + LawnMowers + Obstacles
  ├─ world.loadSelectedGamePlants(plantNames)  // stores PlantName list
  ├─ new GameSimulationEngine(world)
  │    ├─ loadSystems():
  │    │    SunSimulationSystem
  │    │    ZombieSimulationSystem
  │    │    WaveSimulationSystem
  │    │    PlantSimulationSystem
  │    │    ProjectileSimulationSystem
  │    │    LawnMowerSimulationSystem
  │    │    EnvironmentSystem
  │    └─ for each ChapterRule: rule.onEngineStart(engine)  // may add/remove systems
  ├─ App.setCurrentGameEngine(engine)          // engine now accessible globally
  └─ Gdx.app.postRunnable(...)                 // scheduled on GL thread, next frame:
       ├─ new PlayScreen(chapter)
       │    ├─ new GraphicsEngine(AssetManager.player)   // FIRST AssetManager reference
       │    │    └─ AnimationLibrary(player, textureBank)
       │    ├─ new RealTimeGameEngine(graphicsEngine)
       │    │    ├─ new PhysicService()          // Box2D World + ContactListener
       │    │    ├─ BodyFactory.initialize(b2world, gameWorld)
       │    │    └─ RuntimeEntityInitializer.initializeExistingEntities(...)
       │    │         ├─ for each ChapterRule: rule.onChapterStart(world) // BossRule spawns dragon here
       │    │         ├─ LawnMowers → Box2D bodies assigned
       │    │         └─ Obstacles  → Box2D bodies assigned
       │    └─ App.setRealTimeEngine(realTimeEngine)
       └─ game.setScreen(playScreen)
```

**Critical initialization order constraints** (see also Section D):
1. `PlantDefinitionLoader.load()` → before any `PlantFactory` or `AbilityFactory` call
2. `ZombieJsonParserService.load*()` → before `ZombieRegistry.getZombie()` calls in `ChapterFactory`
3. `new GameSimulationEngine(world)` + `App.setCurrentGameEngine()` → before `PlayScreen` construction
4. `BodyFactory.initialize()` → before any entity body creation
5. `rule.onEngineStart()` → runs during `GameSimulationEngine` constructor, before `BodyFactory` exists
6. `rule.onChapterStart()` → runs during `RuntimeEntityInitializer`, after `BodyFactory` is ready

### B3. Simulation Tick Flow

```
PlantsVSZombies.render(delta)
  └─ PlayScreen.render(delta)
       ├─ RealTimeGameEngine.update(delta)
       │    ├─ PhysicService.updateWorld(delta)    // Box2D step (always 1/60s, ignores delta)
       │    └─ int ticks = GameClock.update(delta) // accumulates real time → 0..5 ticks
       │         └─ for each tick:
       │              GameSimulationEngine.advanceTime()
       │                ├─ TickProcessor.update(currentTick)
       │                │    ├─ SunSimulationSystem.update()
       │                │    ├─ ZombieSimulationSystem.update()
       │                │    ├─ WaveSimulationSystem.update()
       │                │    ├─ PlantSimulationSystem.update()
       │                │    ├─ ProjectileSimulationSystem.update()
       │                │    ├─ LawnMowerSimulationSystem.update()
       │                │    └─ EnvironmentSystem.update()
       │                └─ ScheduledTaskManager.executeTasks(currentTick)
       └─ GraphicsEngine.render(batch, camera, ...)
            └─ b2dr.render(b2world, camera)        // always rendered, no debug flag
```

**Tick rate**: `GameClock.TICKS_PER_SECOND = 10`. One tick = 0.1 real seconds.
`GameClock.MAX_TICKS_PER_FRAME = 5` caps maximum ticks per frame to prevent spiral-of-death.

### B4. Plant Placement

```
PlayScreenController.handleWorldClick(worldX, worldY)
  └─ PlantPlacementService.placePlant(selectedPlantName, worldX, worldY)
       ├─ Grid.getCellAt(worldX, worldY)
       ├─ Cell.isPlantable() check  (also bypasses if Cell has no plant)
       └─ PlantFactory.create(name, position, currentTick, cell)
            ├─ PlantRegistry.get(name)             → PlantDefinition
            ├─ new PlantHealth(maxHealth + UpgradePlant.getExtraHP(name))
            ├─ AbilityFactory.getAbilities(name)   → fresh ArrayList<PlantAbility>
            │    └─ internally calls PlantFactory.create(name) [1-arg] to read actionInterval
            ├─ BodyFactory.getInstance().createPlantBody(x, y, w/2.3, h/2.3)
            │    → StaticBody, categoryBits = lane bit, maskBits = lane bit
            └─ body.setUserData(plant)
```

### B5. Plant Ability / Update Flow

```
PlantSimulationSystem.update(currentTick)
  ├─ for each Plant in plants list:
  │    └─ plant.update(plantService, currentTick)
  │         └─ for each PlantAbility in abilities:
  │              ability.update(plant, plantService, currentTick)
  │                → [e.g. AttackAbility.doUpdate()]
  │                   ├─ checks cooldown, target-in-lane, plant-food state
  │                   └─ taskManager.scheduleRelative(tick, delay, () -> addProjectile(...))
  └─ gameWorld.getPlants().removeIf(Plant::isDead)
```

**WARNING**: If any entry in the abilities list is `null`, `ability.update(...)` throws NPE.
This affects plants: `IMITATER`, `HOT_POTATO`, `GRAVE_BUSTER` (see KNOWN_ISSUES.md BUG-001).

### B6. Zombie Update Flow

```
ZombieSimulationSystem.update(currentTick)
  └─ for each Zombie:
       └─ zombie.update(zombieService, currentTick)
            ├─ apply effects
            ├─ evaluateState()  (WALKING / EATING / DEAD based on targetPlant + health)
            ├─ movementStrategy.move(zombie, ...)  (if WALKING)
            ├─ attackStrategy.attack(zombie, ...)  (if EATING)
            └─ check hasReachedHouse() → position check vs loseLineX
                 └─ if past lose line → world.triggerZombieVictory()

DarkDragon.update() [overrides Zombie.update()]:
  ├─ if isDead() → return early  (behavior tree death sequence never runs)
  ├─ super.update(zombieService, currentTick)
  ├─ updatePhase()     (PHASE_1/2/3 by HP%)
  ├─ behaviorTree.step()
  └─ updateMovement()  (BossMovementStrategy: vertical patrol)
```

### B7. Projectile Creation

```
AttackAbility [scheduled lambda runs N ticks later]
  └─ plantService.addProjectile(
       ProjectileFactory.createShooterProjectile(plant, damage, pos, speed, angle))
            ├─ new ShooterProjectile(...)
            ├─ BodyFactory.getInstance().createProjectileBody(x, y, radius)
            │    → DynamicBody, bullet=true, isSensor=true
            │      categoryBits = lane bit, maskBits = lane bit
            └─ body.setUserData(projectile)
```

### B8. Box2D Collision Flow

```
PhysicService.updateWorld()   [each frame, step = 1/60s]
  └─ Box2D detects contacts between bodies sharing the same lane bit
       └─ WorldContactListener.beginContact(contact)
            ├─ Zombie ↔ Plant     → zombie.setTargetPlant(plant)
            ├─ Projectile ↔ Zombie → projectile.handleHit(zombie, tick)
            ├─ Projectile ↔ Plant  → projectile.handleHit(plant, tick)  [if owner not Plant]
            ├─ Projectile ↔ Obstacle → projectile.handleHit(obstacle, tick)
            └─ LawnMower ↔ Zombie → push HIT_ZOMBIE visual event, lawnMower.setTargetZombie(zombie)

WorldContactListener.endContact(contact)
  └─ Zombie ↔ Plant → zombie.setTargetPlant(null)  if same plant

WorldContactListener.preSolve(contact)
  └─ if both userData are Entity instances and in different lanes → contact.setEnabled(false)
     [WARNING: calls App.getRealTimeEngine().getWorld().getGrid() — NPE risk if engine is null]
```

**Not handled in WorldContactListener** (position-based instead):
- Sun collection: `SunCollector.collect(mouseWorld)` — distance check ≤ 40f
- Zombie losing: `mapConfig.isPastLoseLine(worldX)` in `ZombieSimulationSystem`

### B9. Rendering Flow

```
PlayScreen.render(delta)
  ├─ stage.act(delta)           // Scene2D HUD update
  ├─ renderMap()                // OrthogonalTiledMapRenderer
  ├─ game.batch.begin()
  │    ├─ GraphicsEngine.render(batch, camera, currentTick, alpha)
  │    │    // renders: plants, zombies, projectiles, suns, lawnmowers, obstacles, effects
  │    └─ game.batch.end()
  ├─ renderHover(delta)         // cell hover highlight
  ├─ renderPhysics()
  │    └─ b2dr.render(b2world, camera * PIXEL_PER_METER)   // ALWAYS rendered
  └─ stage.draw()               // Scene2D HUD
```

### B10. Chapter / Rule Initialization

```
ChapterRegistry.getChapterByName("S.C")
  └─ ChapterId.parseNormalChapter()  → (SeasonType, chapterIndex)
  └─ CHAPTERS.get(id).get()          → ChapterFactory.createXxxChapter()
       └─ new Chapter(..., rules, mapPath)
            └─ TmxMapLoader().load(mapPath)   // loads TiledMap immediately

GameSimulationEngine(world)
  └─ rule.onEngineStart(engine)   // for each rule
       [BossRule]    → engine.removeSystem(WaveSimulationSystem.class)
       [NoSunRule]   → engine.removeSystem(SunSimulationSystem.class)
       [NightOpsRules] → engine.removeSystem(SunSimulationSystem.class)
       [ConveyorBeltRules] → engine.addSystem(new ConveyorBeltSystem(null))  ← BUG
       [TimedWarRules / DeadLineRules] → no system changes

RuntimeEntityInitializer.initializeExistingEntities(world, ...)
  └─ rule.onChapterStart(world)   // for each rule
       [BossRule]       → ZombieService.spawnZombie("ZombieBossDragon", spawnPos)
       [ConveyorBeltRules] → new ConveyorBeltService(world, plantPool); service.rollPlant()
       [TimedWarRules]  → world.addEvent(intro message)
       [DeadLineRules]  → world.addEvent(warning message)
       [NightOpsRules]  → world.addEvent(warning message)
```

---

## C. Important Dependency Relationships

```
PlantFactory
  → PlantRegistry (must be populated first)
  → PlantDefinition (stats: health, cost, cooldown, etc.)
  → AbilityFactory (creates abilities list)
  → BodyFactory.getInstance() (must be initialized first)

AbilityFactory
  → PlantFactory.create(name) [1-arg, no body] to read actionInterval
  → PlantRegistry (indirectly, via PlantFactory)

AttackAbility (and other abilities)
  → App.getCurrentGameEngine().getTaskManager()   [global — must be non-null]
  → App.getRealTimeEngine().getGraphicsEngine()   [global — must be non-null]
  → App.getCurrentGameEngine().getGameWorld()

WorldContactListener
  → App.getCurrentGameEngine().getGameClock()     [in handleProjectileHit]
  → App.getRealTimeEngine().getGraphicsEngine()   [in handleBeginContact(LawnMower)]
  → App.getRealTimeEngine().getWorld().getGrid()  [in preSolve — NPE risk]

GameSimulationEngine
  → GameWorld
  → TickProcessor (ordered list of TickSystems)
  → ScheduledTaskManager
  → ChapterRule implementations (onEngineStart)

RealTimeGameEngine
  → App.getCurrentGameEngine()   [reads simulation engine from global state]
  → PhysicService
  → BodyFactory (initializes it)
  → GraphicsEngine
  → RuntimeEntityInitializer

ChapterRegistry
  → ChapterFactory (one shared instance)
  → ZombieRegistry (accessed at chapter construction time)
  → Season subclasses
  → TmxMapLoader (loads .tmx at construction time)

QuestLoad
  → QuestSaveService (reads/writes quest_progress.json)
  → App.getCurrentUser() (sets questManager on user)

AssetManager
  → Gdx.files (must be valid at first class reference)
  → TextureBank, PamPlayer, PvzSkin (all static final)
```

---

## D. Lifecycle and Initialization Constraints

These constraints are mandatory. Violating them causes crashes or incorrect behavior.

| Constraint | Detail |
|---|---|
| `PlantDefinitionLoader.load()` before any entity creation | `PlantRegistry` is empty until called. `PlantFactory.create()` returns null definition. |
| `ZombieJsonParserService.load*()` before `ChapterRegistry` is accessed | `ZombieRegistry.getZombie()` is called at chapter construction time. |
| `App.setCurrentGameEngine(engine)` before `PlayScreen` | `RealTimeGameEngine` reads engine from `App` in its constructor. |
| `BodyFactory.initialize()` before entity bodies are created | `BodyFactory.getInstance()` throws `IllegalStateException` if called before `initialize()`. |
| `rule.onEngineStart()` runs during `GameSimulationEngine()` | At this point `BodyFactory` does NOT yet exist. Rules may not create bodies here. |
| `rule.onChapterStart()` runs during `RuntimeEntityInitializer` | At this point `BodyFactory` IS initialized. Spawning entities (bodies) is safe here. |
| `AssetManager` class first referenced after `PlantsVSZombies.create()` | Static fields use `Gdx.files.internal()`. If class loads before libGDX is ready, `ExceptionInInitializerError` is thrown. |
| `ConveyorBeltRules.onEngineStart()` passes null service | The `ConveyorBeltSystem` is created with a null service reference (bug — see KNOWN_ISSUES.md BUG-008). |

---

## E. Global / Static State

The `App` class (`app/model/App.java`) is the central static container for all live game state. It is accessed from simulation systems, abilities, contact listeners, and controllers.

| Field | Type | Notes |
|---|---|---|
| `App.currentUser` | `User` | Set at startup from `Repository`. Never null in gameplay. |
| `App.currentGameEngine` | `GameSimulationEngine` | Set before `PlayScreen` creation. Accessed by all ability classes and contact listener. |
| `App.realTimeEngine` | `RealTimeGameEngine` | Set after `PlayScreen` creation. Accessed by `WorldContactListener.preSolve()` — NPE risk during startup/shutdown. |
| `App.currentMenu` | `Menu` | Read/written by both GL thread and background console thread without synchronization. |
| `App.pendingGameSession` | `PendingGameSession` | Holds selected chapter and plants. Cleared by `GameMenuController.manageEnteringChapter()`. |

**Important**: `App` fields are neither `volatile` nor `synchronized`. The background console thread accesses `App.currentMenu` and user state concurrently with the GL thread. This is a known data race (see KNOWN_ISSUES.md AC-001).

**When modifying any class that uses `App.getCurrentGameEngine()` or `App.getRealTimeEngine()`**: these can be null outside of active gameplay. Always check the call site's lifecycle context.

---

## F. Extension Points

These are the legitimate architectural extension points in the codebase. Use existing mechanisms — do not invent parallel structures.

| Extension point | Where | How to extend |
|---|---|---|
| **Plant definitions** | `PlantDefinitionLoader.load()` | Add a new `PlantDefinitionBuilder` block and `PlantRegistry.register()` call. Also add to `AbilityFactory.getAbilities()`. |
| **PlantAbility types** | `game/entity/plant/ability/` | Implement `PlantAbility` (or extend `AbstractPlantAbility`). Add to `AbilityFactory`. |
| **Zombie types** | `game/service/Zombie/ZombieJsonParserService` + `zombies.json` | Add JSON entry; register in `ZombieRegistry`. |
| **Zombie strategies** | `game/entity/zombie/strategy/` | Implement `MovementStrategy`, `AttackStrategy`, or `TakingDamageStrategy`. |
| **Simulation systems** | `game/engine/system/` + `GameSimulationEngine.loadSystems()` | Implement `TickSystem`. Add via `tickProcessor.addSystem()` or via a `ChapterRule.onEngineStart()`. |
| **Chapter rules** | `game/world/chapter/rule/` | Implement `ChapterRule`. Attach via `ChapterFactory.createChapter(rules: List.of(...))`. |
| **Chapters** | `ChapterFactory` + `ChapterRegistry` | Add a method to `ChapterFactory`; register in `ChapterRegistry`'s static block. |
| **Minigame modes** | `game/world/minigame/` | Implement `Minigame`. Wire in `ChapterFactory.createXxxChapter()`. |
| **Projectile types** | `game/entity/projectile/` + `ProjectileFactory` | Extend `Projectile` or reuse `ShooterProjectile`. Add factory method. |
| **Scheduled tasks** | `ScheduledTaskManager.scheduleRelative()` | Pass a lambda. Only valid during active gameplay (requires non-null `App.getCurrentGameEngine()`). |
| **Persistence** | `Repository` (users.json) / `QuestSaveService` (quest_progress.json) | Add Gson adapters and serialization logic. |
| **Visual events** | `GraphicsEngine` + `PlantVisualEventType` / `ZombieVisualEventType` | Add enum values; handle in the relevant renderer. |

---

## G. Areas Requiring Targeted Investigation

These areas were identified during the two-pass architectural investigation as having incomplete information or high-risk interactions. **Inspect before modifying.**

| Area | Reason |
|---|---|
| **Minigame rendering / input wiring** | `VaseBreakerMode`, `IZombieMode`, `WallNutBowlingMode` exist at the simulation level but their graphical screen/input handlers (`VaseBreakerScreen`, `IZombieMenu`, etc.) were found to be empty or disconnected. Do not assume structural presence means functional implementation. |
| **Post-game flow** | After `world.triggerPlantVictory()` / `triggerZombieVictory()` is called, the screen transition, score saving, quest update, and level-unlock sequence was not fully traced. Inspect `PlayScreen`, `GameWorld`, and the relevant controllers before modifying win/loss behavior. |
| **DarkDragon attack internals** | `FireAttackAction`, `FireBombAction`, `SummonAction`, `VulnerabilityAction` in `DarkDragonAI` were confirmed to be stubs. Their intended implementation is unknown. The `isExecutingAbility` flag is set but never cleared. Inspect before touching boss combat. |
| **ConveyorBeltSystem tick logic** | `ConveyorBeltRules` initializes `ConveyorBeltSystem` with a null service (BUG-008). The system's internal behavior when service is null is unknown. |
| **ScoringGame patterns** | `ExactDamagePattern`, `LastStandRecoveryPattern`, `LowSunVictoryPattern` in `game/world/minigame/scoringgame/patterns/` were not read. |
| **GraphicsEngine / entity rendering internals** | Rendering for new entity types requires understanding how visual events, animation state machines, and `PamPlayer` clip refs are managed. Read `GraphicsEngine` fully before adding entity renderers. |
| **`GameWorld.triggerPlantVictory()` / `triggerZombieVictory()`** | The state changes and downstream effects of these calls are not fully traced. |
