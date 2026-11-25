# Plants vs. Zombies — Coding Rules

> These rules are derived from the actual codebase structure, constraints discovered
> during architectural investigation, and the lifecycle dependencies confirmed by
> source inspection. They are not generic advice — they address specific risks in
> this project.

---

## 1. Scope Control

**1.1** Change only what the task requires. Do not perform unrelated cleanup, rename variables, or reformat code in files you are editing for another reason.

**1.2** Do not refactor a class merely because it is imperfect. Technical debt is documented in `KNOWN_ISSUES.md`. Resolve it only when the task explicitly targets it.

**1.3** When the task requires changing one layer (e.g., simulation), do not silently restructure another layer (e.g., persistence or rendering) unless explicitly required.

---

## 2. Investigation Before Modification

**2.1** Before editing any file, identify all callers, implementors, and downstream users of the code being changed. Use search to find usages before modifying public APIs or static methods.

**2.2** Before changing any factory, registry, or system registration, verify the initialization order documented in `ARCHITECTURE.md` Section D. Out-of-order initialization causes crashes that are hard to diagnose.

**2.3** Before modifying a `TickSystem`, read `GameSimulationEngine.loadSystems()` to understand the system ordering and which rules may add or remove that system.

**2.4** Before modifying any entity (Plant, Zombie, Projectile, LawnMower), check whether it has a Box2D body (`entity.getBody()`). Body creation, lifecycle, and user data must remain consistent with `WorldContactListener` assumptions.

**2.5** Before modifying `ChapterRule` implementations or adding a new rule, re-read ARCHITECTURE.md Section B10 to understand the two-phase rule lifecycle: `onEngineStart` (before `BodyFactory` exists) vs. `onChapterStart` (after `BodyFactory` exists). Do not create Box2D bodies inside `onEngineStart`.

**2.6** Before modifying `AbilityFactory`, check every plant that uses the changed ability type. `AbilityFactory.getAbilities()` is a large switch statement; missing or `null` ability entries cause NPEs in `Plant.update()`.

---

## 3. Behavior Preservation

**3.1** Existing gameplay behavior is the default contract unless the task explicitly requires changing it. Do not silently alter tick timing, damage values, collision logic, wave configuration, or plant/zombie stats.

**3.2** Do not change `GameClock.TICKS_PER_SECOND` or the `MAX_TICKS_PER_FRAME` cap without understanding all dependent timing calculations (cooldowns, wave intervals, animation delays, `ScheduledTaskManager` delays).

**3.3** Do not modify `ContactBits` lane bit values or `BodyFactory` filter assignments without tracing all entity types that depend on lane isolation for correct collision behavior.

**3.4** Do not change the order of systems in `GameSimulationEngine.loadSystems()` without verifying that no system relies on the result of a preceding system's update within the same tick.

**3.5** The `ScheduledTaskManager` executes tasks **after** all tick systems complete within each tick (see `GameSimulationEngine.advanceTime()`). Tasks that spawn entities (e.g., projectiles) must remain post-tick to avoid modifying collections while systems iterate them.

---

## 4. Architecture Responsibilities

**4.1** Respect existing layer responsibilities:
- **`app/`** owns application flow, menu navigation, and persistence I/O.
- **`game/`** owns simulation logic, entity state, and world state.
- **`graphics/`** owns rendering and visual event handling.
- Do not put rendering logic into simulation classes or simulation logic into renderers.

**4.2** Do not add new static state to `App` without understanding the concurrency risk (see AC-001 in KNOWN_ISSUES.md). `App` is accessed from both the GL thread and the background console thread.

**4.3** Do not introduce new singleton patterns or static initializers without verifying that the initialization order constraint (Section D of ARCHITECTURE.md) is preserved.

**4.4** Do not add new `TickSystem` implementations and register them only within `loadSystems()` if they are conditional on chapter type. Use `ChapterRule.onEngineStart()` to add conditional systems, matching the existing pattern.

**4.5** Use existing factories and registries for entity creation. Do not instantiate `Plant`, `Zombie`, or `Projectile` directly from outside their respective factories.

---

## 5. Duplication

**5.1** Quest data is persisted to `quest_progress.json` via `QuestSaveService`. `users.json` also contains a stale `questManager` field (see AC-004 in KNOWN_ISSUES.md). Do not add a third persistence location for quest data. If fixing quest persistence, choose one authoritative store.

**5.2** `PlantDefinitionLoader.load()` is the single source of plant stats. Do not add stats to `assets/plants/*.json` — those files are PAM animation data only, not gameplay data.

**5.3** Do not create parallel menu object sets. `MenuController` already has a static `Map<Menu, AppMenu>` used by the console thread. Do not create a second map.

---

## 6. Static State and Lifecycle

**6.1** `App.getCurrentGameEngine()` and `App.getRealTimeEngine()` are null before gameplay starts and may become null again after the game ends. Every class that uses them must guard against null access in non-gameplay contexts.

**6.2** `AssetManager` fields (`player`, `textureBank`, `skin`) are `static final` and initialized at first class reference using `Gdx.files`. Never reference `AssetManager` before `PlantsVSZombies.create()` completes (i.e., not in static initializers or early constructors).

**6.3** `BodyFactory` is a singleton that must be initialized via `BodyFactory.initialize(world, gameWorld)` before any entity body is created. Creating a body before initialization throws `IllegalStateException`. The initialization happens inside `RealTimeGameEngine`'s constructor.

**6.4** `PlantRegistry` is populated by `PlantDefinitionLoader.load()`. It is not thread-safe and is not designed to be populated incrementally at runtime. Load all definitions once at startup.

**6.5** `ChapterRegistry` static block fires at class-load time. `ZombieRegistry` must be populated before any chapter is constructed, because `ZombieRegistry.getZombie()` is called within `ChapterFactory` methods.

---

## 7. Collections and Nullability

**7.1** Do not add `null` values to the abilities list in `AbilityFactory.getAbilities()`. `Plant.update()` iterates the list without null checks. A null entry causes `NullPointerException` during gameplay. This currently affects `IMITATER`, `HOT_POTATO`, and `GRAVE_BUSTER` (see BUG-001 in KNOWN_ISSUES.md).

**7.2** The `plants` list in `PlantSimulationSystem` is a direct reference to `GameWorld.getPlants()`. `removeIf()` is called at the end of `update()`, after iteration completes. This is safe. Do not change this to remove plants during the iteration loop.

**7.3** The `ZombieSimulationSystem` may iterate over the zombie list while other events add zombies (e.g., DarkDragon summon action, WaveSimulationSystem). If modifying zombie list access patterns, use a defensive copy or verify safe iteration.

**7.4** `ScheduledTaskManager` uses a `PriorityQueue` which is not thread-safe. All task scheduling must originate from the GL/simulation thread. Do not schedule tasks from the background console thread.

---

## 8. Physics

**8.1** Do not destroy Box2D bodies inside `beginContact` or `endContact` callbacks. Box2D does not allow world mutations during callbacks. Defer destruction to post-step (e.g., set a `dead` flag and destroy in the simulation tick).

**8.2** All Box2D body creation must go through `BodyFactory`. Do not call `PhysicService.createBody()` directly from game entities. `BodyFactory` handles lane-bit assignment and normalization by `PIXEL_PER_METER`.

**8.3** Every entity with a Box2D body must set `body.setUserData(entity)`. `WorldContactListener` identifies entities exclusively by `getUserData()` instanceof checks. Missing or wrong user data causes silent collision failures.

**8.4** Lane isolation is enforced by setting `categoryBits = maskBits = laneBit` on each fixture. Entities only collide with others on the same lane. Do not set different category and mask values without understanding the cross-lane behavior intended for that entity type.

**8.5** `PhysicService.updateWorld(delta)` ignores `delta` and always steps `1/60f`. Physics timing is independent of frame rate. Do not assume physics and animation are synchronized at non-60fps rates (see AC-003 in KNOWN_ISSUES.md).

---

## 9. Simulation Timing

**9.1** `GameClock.TICKS_PER_SECOND = 10`. One tick = 0.1 seconds. All cooldowns, intervals, and wave timings in the game use ticks. Do not mix tick-based and second-based values without explicit conversion.

**9.2** `ScheduledTaskManager.scheduleRelative(currentTick, delayTicks, action)` schedules relative to the current tick. Delay of `5` means the task runs 0.5 real seconds later. Tasks execute synchronously on the simulation tick thread — they do not introduce concurrency.

**9.3** `GameClock.update(delta)` caps tick output at `MAX_TICKS_PER_FRAME = 5` per frame. Under heavy load, the simulation will slow down rather than spiral. Do not remove this cap.

---

## 10. Validation After Modification

**10.1** After any modification, verify that the project compiles without errors.

**10.2** After changing a factory or registry, verify that all entities using the changed registration still behave correctly.

**10.3** After changing a `TickSystem`, verify that the system ordering in `GameSimulationEngine.loadSystems()` is still correct and that no rule removes a system your changes depend on.

**10.4** After changing any `ChapterRule`, verify both `onEngineStart` and `onChapterStart` behavior — they run at different points in the initialization sequence with different available context (specifically: `BodyFactory` is only available in `onChapterStart`).

**10.5** Do not claim a change is correct merely because the code compiles. Trace the affected execution paths and check boundary conditions (null entities, empty lists, engine state during startup/shutdown).

---

## 11. Code Style

**11.1** Match the existing indentation style (4-space indentation, `{}` on same line for `if`/`for`, braces always used).

**11.2** Comments in this project use the `// >>>` and `// ->` convention for section headers. Do not introduce JavaDoc where none exists in surrounding code.

**11.3** Do not add comments that restate what the code obviously does (e.g., `// set position`, `// return result`). Comments in this codebase are sparse; match that style.

**11.4** `TODO` comments are used throughout the codebase for incomplete features. If you add a stub or placeholder, mark it with `// TODO:` and a concise description.

**11.5** Factory classes (`PlantFactory`, `ZombieFactory`, `ProjectileFactory`) use static methods and private constructors. Match this pattern when extending them.
