# Plants vs. Zombies — Known Issues Registry

> All items are based on direct source code inspection. Classification follows strict
> definitions: CONFIRMED BUG = demonstrably incorrect behavior; INCOMPLETE FEATURE =
> structural presence with unfinished or disconnected implementation; ARCHITECTURAL
> CONCERN = works or may work but carries design/lifecycle risk; UNRESOLVED = insufficient
> evidence to determine behavior.
>
> This registry does not impose a fix priority. Fix an item when the task explicitly
> targets it or when modifying the affected area.

---

## A. Confirmed Bugs

### BUG-001 — Null abilities for IMITATER, HOT_POTATO, GRAVE_BUSTER
- **Location**: `game/service/plant/AbilityFactory.java`, `getAbilities()`
- **Description**: The switch statement adds `null` to the returned `ArrayList<PlantAbility>` for `PlantName.IMITATER`, `HOT_POTATO`, and `GRAVE_BUSTER`. `Plant.update()` iterates this list without null checks and calls `ability.update(...)` on each element.
- **Evidence**: `abilities.add(null)` is present in three case branches. `Plant.update()` loops: `for (PlantAbility ability : abilities) { ability.update(...); }`.
- **Impact**: `NullPointerException` at runtime the moment any of these three plants is placed in an active game.
- **Consider when**: Implementing `IMITATER`, `HOT_POTATO`, `GRAVE_BUSTER` functionality, or fixing any ability-related code.

---

### BUG-002 — Plant.getStage() uses wrong API to index abilities list
- **Location**: `game/entity/plant/Plant.java`, `getStage()`
- **Description**: `SunProductionAbility.class.getModifiers()` is used as an ArrayList index. `Class.getModifiers()` returns a Java access-modifier bitmask (e.g., `1` for `public`). For `SunProductionAbility` (which is `public`), this returns `1`, which happens to match the list index because `AbilityFactory` places `PlantFoodAbility` at index 0 and `SunProductionAbility` at index 1 for `SUN_SHROOM`.
- **Evidence**: Source line: `SunProductionAbility sunProductionAbility = (SunProductionAbility) abilities.get(SunProductionAbility.class.getModifiers());`
- **Impact**: Accidental correctness. Will break if: the access modifier of `SunProductionAbility` changes (e.g., class becomes `public final` → `getModifiers()` returns 17), or if the ability list order changes for `SUN_SHROOM`.
- **Consider when**: Modifying `SUN_SHROOM` abilities, modifying `AbilityFactory` list order, or changing `SunProductionAbility` access modifiers.

---

### BUG-003 — Chapter 1.2 uses wrong season and map
- **Location**: `game/world/chapter/ChapterFactory.java`, `createAncientEgyptChapter2()`
- **Description**: The chapter registered as `ChapterId(ANCIENT_EGYPT, 2)` (resolved by `"1.2"`) constructs using `new DarkAgesSeason()` and loads `"Maps/darkboss_map.tmx"`. The season object, visual theme, and map are inconsistent with the registry key. The debug-mode startup hardcodes `"1.2"`, so this is the chapter always launched.
- **Evidence**: Source: `Season season = new DarkAgesSeason();` and `"Maps/darkboss_map.tmx"` in `createAncientEgyptChapter2()`.
- **Impact**: Every debug-mode game session uses the Dark Ages boss map and season regardless of the registry key. Visual and thematic mismatch.
- **Consider when**: Modifying chapter definitions, changing the debug startup chapter, or implementing chapter selection UI.

---

### BUG-004 — DarkDragon death sequence never executes
- **Location**: `game/entity/zombie/boss/DarkDragon.java`, `update()`
- **Description**: `DarkDragon.update()` has an early return if `isDead()` is true before `behaviorTree.step()` is called. The behavior tree's death sequence (`CheckDead → StartDeath → WaitTask`) therefore never runs. The normal `Zombie.die()` path applies instead, but boss-specific death behavior (animation, post-death events) does not execute.
- **Evidence**: `if (isDead()) { return; }` precedes `behaviorTree.step()` in source.
- **Impact**: Boss death animations and the post-death handling (marked `// TODO`) are skipped. The boss dies silently through the base `Zombie` death path.
- **Consider when**: Implementing or modifying boss death behavior, death animations, or post-boss-death events.

---

### BUG-005 — ConveyorBeltRules passes null service to ConveyorBeltSystem
- **Location**: `game/world/chapter/rule/speciallevelRules/ConveyorBeltRules.java`
- **Description**: `onEngineStart()` calls `engine.addSystem(new ConveyorBeltSystem(this.service))` but `this.service` is `null` at that point — the service is created only later in `onChapterStart()`. The `ConveyorBeltSystem` receives and stores a null `ConveyorBeltService` reference.
- **Evidence**: In source, `service` field is `null` at class creation; `onEngineStart()` passes `this.service` (null) to the system constructor; `onChapterStart()` later assigns `this.service = new ConveyorBeltService(...)`.
- **Impact**: Any tick-based operation in `ConveyorBeltSystem` that uses the service reference will fail. Conveyor belt chapters (Frostbite Caves ch.3, Wall-Nut Bowling) are non-functional.
- **Consider when**: Fixing or implementing the conveyor belt game mode.

---

### BUG-006 — Graphical LoginScreen navigation is broken
- **Location**: `app/view/screen/LoginScreen.java`
- **Description**: The login button's success handler contains `// TODO: go to MainMenuScreen` and does not call `game.setScreen(...)`. The back button has no click listener at all. A user who opens `LoginScreen` cannot navigate away from it after logging in or going back.
- **Evidence**: Login button listener: `if (result.success()) { /* TODO: go to MainMenuScreen */ }`. Back button: `activateListeners()` creates no listener for `backButton`.
- **Impact**: The graphical login flow is non-functional. Currently worked around by the debug auto-login path.
- **Consider when**: Implementing or connecting the graphical login/signup screens.

---

### BUG-007 — WorldContactListener.preSolve() accesses global engine without null check
- **Location**: `game/component/contacts/WorldContactListener.java`, `preSolve()`
- **Description**: `preSolve()` calls `App.getRealTimeEngine().getWorld().getGrid()`. `App.getRealTimeEngine()` is null before the game is running and may be null during engine teardown. This is invoked from a Box2D callback during `PhysicService.updateWorld()`.
- **Evidence**: Source: `Grid grid = App.getRealTimeEngine().getWorld().getGrid();` — no null guard.
- **Impact**: `NullPointerException` in the physics callback if `preSolve` fires while `App.realTimeEngine` is null. Unlikely under normal play but a risk during engine startup/shutdown transitions.
- **Consider when**: Changing engine startup/shutdown sequences, or adding teardown logic.

---

### BUG-008 — AttackAbility computes plant row without playgroundY offset
- **Location**: `game/entity/plant/ability/AttackAbility.java`, `performAttack()`
- **Description**: `int plantRow = (int) (plant.getPosition().getY() / grid.getConfig().getCellHeight())` divides the absolute world Y-coordinate by cell height without subtracting `playgroundY` first. If the playground does not start at Y=0 in the TiledMap, the row calculation is incorrect for all plants, potentially targeting zombies in the wrong row.
- **Evidence**: `MapConfig.gridToWorld()` correctly uses `playgroundY + row * cellHeight + cellHeight/2`, confirming that positions are in absolute world space. `performAttack()` does not apply the same inverse.
- **Impact**: Incorrect target-row detection for multi-row plants (Threepeater, Rotobaga, Starfruit). Whether `playgroundY == 0` in the current TiledMap is not confirmed; if it is 0, the bug has no practical effect.
- **Consider when**: Modifying attack targeting logic, adding new TiledMaps with non-zero playground Y origins.

---

## B. Incomplete Features

### INC-001 — Plant.levelUp() not implemented
- **Location**: `game/entity/plant/Plant.java`, `levelUp()`
- **Description**: Method body contains only `// TODO: implement`. Plant leveling up is non-functional.
- **Impact**: No impact until a caller invokes `levelUp()`. No callers currently confirmed.

---

### INC-002 — Zombie.manageSpawnPlantFood() is empty
- **Location**: `game/entity/zombie/Zombie.java`, `manageSpawnPlantFood()`
- **Description**: Called when a zombie dies (`dead = true`), but the method body is empty. Zombies never drop Plant Food.
- **Impact**: No Plant Food drops in gameplay.

---

### INC-003 — Lobber plants cannot attack (commented-out code)
- **Location**: `game/entity/plant/ability/AttackAbility.java`, `performAttack()`
- **Description**: The attack branches for `CABBAGE_PULT`, `KERNEL_PULT`, `MELON_PULT`, `WINTER_MELON`, and `PEPPER_PULT` are entirely commented out. These plants have `AttackAbility` in their abilities list but produce no projectiles.
- **Impact**: These five plants are silent during gameplay — they do not attack.
- **Consider when**: Implementing lobber projectile behavior.

---

### INC-004 — ScalingPropertyDeserializer is a stub
- **Location**: `game/service/Zombie/ZombieJsonParserService.java`, inner class `ScalingPropertyDeserializer`
- **Description**: The implementation block for parsing `ScalingProperty` from JSON is entirely commented out. The deserializer returns an empty `new ScalingProperty()` on every invocation. Zombie scaling properties from `zombies.json` are never applied.
- **Impact**: All zombie scaling is ignored. Zombie stats do not scale with chapter/wave difficulty.

---

### INC-005 — MeowPoint scoring system is a stub
- **Location**: `game/service/score/MeowPointManager.java`
- **Description**: `addPoints(String, int)` and `reset()` are empty methods. `totalMeowPoints` is always 0. The scoring system has no gameplay connection.
- **Impact**: No scoring in gameplay. `ScoringGameMode` patterns that depend on this system are also non-functional.

---

### INC-006 — GameStateSimulationSystem is empty and not registered
- **Location**: `game/engine/system/state/GameStateSimulationSystem.java`
- **Description**: `update(long)` is empty. The class is not registered in `GameSimulationEngine.loadSystems()`. Win/loss conditions are handled by `TimedWarRules`, `DeadLineRules`, `ZombieSimulationSystem`, and `WaveSimulationSystem` — not by this class.
- **Impact**: No immediate impact (win/loss still works through other paths). Dead code.

---

### INC-007 — Graphical signup/login flow is disconnected
- **Location**: `app/view/screen/LoginScreen.java`, `app/view/screen/SignupScreen.java`
- **Description**: Both screens are fully structured Scene2D screens but are never instantiated or set via `game.setScreen()`. The only `setScreen()` call in the codebase points to `PlayScreen`. Authentication is handled exclusively through the console thread.
- **Impact**: The graphical auth flow does not work. New users without a saved login cannot reach gameplay through any graphical interface.

---

### INC-008 — DarkDragon special attacks are visual stubs
- **Location**: `game/entity/zombie/boss/DarkDragonAI.java`
- **Description**: `FireAttackAction`, `FireBombAction`, `SummonAction`, and `VulnerabilityAction` inner classes trigger visual events but have no gameplay effects (no damage, no zombie spawning, no player-affecting state changes). `isExecutingAbility = true` is set but never cleared within these stubs.
- **Impact**: Boss chapter plays out with no boss attacks. `isExecutingAbility` may become permanently `true` after the first attack trigger.

---

### INC-009 — Five ChapterRule stubs are empty and unattached
- **Location**: `game/world/chapter/rule/speciallevelRules/`
- **Description**: `SaveOurSeedsRules`, `PlantWhatYouGotRules`, `LovePlantsRules`, `LockedPlantsRules` have all three methods empty. `NormalWorldRule` (in `rule/`) is also empty. None of these are referenced in `ChapterFactory` and therefore have no effect on any chapter.
- **Impact**: None currently. These represent planned modes that are not yet designed.

---

### INC-010 — Four plant types have no gameplay ability
- **Location**: `game/service/plant/AbilityFactory.java`
- **Description**: `MAGNET_SHROOM`, `HYPNO_SHROOM`, `CAT_TAIL`, and `LILY_PAD` are given only `PlantFoodAbility` in their abilities list, with no attack, sun production, or defense ability. They are placeable but do nothing.
- **Impact**: These plants function as inert decorations during gameplay.

---

### INC-011 — Five placeholder chapters have no content
- **Location**: `game/world/chapter/ChapterFactory.java`, `createPlaceholder()`
- **Description**: `BigWaveBeachChapter2`, `BigWaveBeachChapter4`, `FrostBiteCavesChapter1`, `FrostBiteCavesChapter2`, `FrostBiteCavesChapter4` are created via `createPlaceholder()`: empty plant list, empty zombie list, 0–3 waves, generic `map1.tmx`.
- **Impact**: Selecting these chapters produces an empty game with no zombies or plants to choose from.

---

### INC-012 — Minigame graphical input is not wired
- **Location**: Minigame screen/controller classes (`VaseBreakerScreen`, `IZombieMenu`, etc.)
- **Description**: VaseBreaker, IZombie, and WallNutBowling have simulation-side implementations (`VaseBreakerMode`, `IZombieMode`, `WallNutBowlingMode`) but their graphical screen and input handlers were found to be empty or disconnected. `ScoringGameMode` reaches `PlayScreen` normally and is the most playable.
- **Impact**: VaseBreaker, IZombie, and WallNutBowling cannot be played through the graphical UI.

---

### INC-013 — DailyZombieSpawnService generates no patterns
- **Location**: `game/service/score/DailyZombieSpawnService.java`
- **Description**: `generatePatternForDate()` is empty. The daily challenge system (`DailyZombieSpawnPattern`) is structurally present but not wired to any game flow.
- **Impact**: No daily challenge functionality.

---

## C. Architectural Concerns

### AC-001 — Background console thread races with GL thread on App state
- **Location**: `app/view/AppLoop.java`; `app/model/App.java`
- **Description**: A daemon `Thread` is started in `AppLoop.init()` that reads `App.getCurrentMenu()` and calls `menu.checkCommand()` in a tight loop. Both `App.currentMenu` and user-state fields are plain (non-volatile, non-synchronized) instance/static fields. The GL thread also reads and writes these fields.
- **Evidence**: Source confirms the daemon thread; `App` fields have no synchronization.
- **Impact**: Data races. In practice, the console thread does blocking I/O (`Scanner.nextLine()`), which limits actual race frequency. However, mutations from console commands (e.g., `App.setCurrentMenu()`) are not guaranteed to be visible on the GL thread and vice versa.
- **Consider when**: Modifying `App` fields or console command handlers.

---

### AC-002 — App as unguarded global state bus
- **Location**: `app/model/App.java`; used throughout `game/` and `graphics/`
- **Description**: `App.getCurrentGameEngine()`, `App.getRealTimeEngine()`, `App.getCurrentUser()`, and `App.getCurrentMenu()` are accessed from simulation systems, ability classes, rendering, collision handlers, and UI controllers. None of these accesses guard against null.
- **Impact**: Any code path that runs outside of active gameplay (before engine start, after game ends) that touches `App.getCurrentGameEngine()` or `App.getRealTimeEngine()` will NPE.
- **Consider when**: Modifying any class in `game/engine/system/`, `game/entity/*/ability/`, `graphics/`, or `app/controller/`.

---

### AC-003 — Box2D physics timestep does not use actual frame delta
- **Location**: `game/component/physic/PhysicService.java`, `updateWorld(float delta)`
- **Description**: `b2world.step(timeStep, 6, 2)` always uses a hardcoded `timeStep = 1/60f`, ignoring the `delta` argument. The `accumulator` field is declared but unused.
- **Impact**: At frame rates other than 60fps, physics and visual animation advance at different rates. Under high frame rates, physics may advance faster than the clock; under low frame rates, physics will stutter independently of simulation ticks.
- **Consider when**: Implementing variable timestep physics, frame-rate independence, or interpolation.

---

### AC-004 — Quest data is duplicated across two files
- **Location**: `app/repository/Repository.java`; `game/service/quest/QuestSaveService.java`
- **Description**: `users.json` serializes a `questManager` field per user via `Repository.QuestManagerAdapter`. `QuestSaveService` independently maintains `quest_progress.json`. At runtime, `QuestLoad.load()` immediately overwrites the user's `questManager` from `quest_progress.json`, making the `users.json` copy permanently stale.
- **Evidence**: Confirmed from disk: `users.json` shows empty quests dated 2026-08-04; `quest_progress.json` shows populated quests dated 2026-08-16.
- **Impact**: Inconsistent data. The `users.json` quest data is never up-to-date. Multi-user scenarios on the same machine share a single `quest_progress.json`.
- **Consider when**: Fixing quest persistence, implementing per-user quest saves, or touching `Repository`.

---

### AC-005 — Box2D debug renderer always active
- **Location**: `app/view/game/PlayScreen.java`, `renderPhysics()`
- **Description**: `b2dr.render(engine.getB2dWorld(), camera.combined.cpy().scl(PhysicService.PIXEL_PER_METER))` is called unconditionally every frame. There is no `if (debugMode)` guard.
- **Impact**: Physics wire-frame overlay is always visible in the released/final game. Performance cost of `Box2DDebugRenderer` is non-trivial.
- **Consider when**: Preparing for any non-debug build or user-facing release.

---

### AC-006 — PendingGameSession.clear() not called after game ends
- **Location**: `app/model/game/PendingGameSession.java`
- **Description**: `PendingGameSession` holds the selected chapter and plant list across the session lifecycle. `clear()` is called in `GameMenuController.manageEnteringChapter()` at the start of chapter selection, but it is not confirmed that it is called after the game ends (win, lose, or abort). A subsequent `startGame()` call may reuse stale chapter/plant data from a previous session if the user navigates back to chapter selection via a non-standard path.
- **Impact**: Low risk in current debug flow (single game per session). Risk increases if a proper game-over → menu → new game flow is implemented.
- **Consider when**: Implementing post-game flow, game restart, or menu navigation after game ends.

---

### AC-007 — ChapterId overrides equals() but not hashCode() in a record
- **Location**: `game/world/chapter/ChapterId.java`
- **Description**: `ChapterId` is a Java `record` that manually overrides `equals()` using `==` for the `SeasonType` enum. It does not override `hashCode()`. The auto-generated record `hashCode()` uses the same two components. Since enum `==` is equivalent to `equals()` for enums, `hashCode()` and the manual `equals()` are consistent.
- **Impact**: None currently. The HashMap in `ChapterRegistry` works correctly. However, the manual override is unnecessary and fragile — if a future developer changes the comparison logic in `equals()` without also changing `hashCode()`, the contract will break.
- **Consider when**: Modifying `ChapterId` or `ChapterRegistry`.

---

## D. Unresolved

### UNR-001 — Post-game win/loss flow is not traced
- **Location**: `GameWorld.triggerPlantVictory()`, `triggerZombieVictory()`, `PlayScreen`
- **Description**: The downstream effects of triggering a win or loss (screen transition, quest update, XP/currency award, level-unlock save) were not fully traced during investigation. `RealTimeGameEngine.update()` stops advancing when `GameState == WON/LOST`, but what happens to the screen after that is unknown.
- **Impact**: Unknown. Do not implement post-game features without inspecting these paths.

---

### UNR-002 — DarkDragon attack task implementations
- **Location**: `game/entity/zombie/boss/DarkDragonAI.java`
- **Description**: `FireAttackAction.execute()`, `FireBombAction.execute()`, `SummonAction.execute()`, and `VulnerabilityAction.execute()` are confirmed stubs. Their intended implementations are unknown. `isExecutingAbility` is set but never cleared in any of these stubs.
- **Impact**: Unknown until implementation begins. The `isExecutingAbility` flag state may interfere with behavior tree execution.

---

### UNR-003 — ScoringGame patterns internal behavior
- **Location**: `game/world/minigame/scoringgame/patterns/`
- **Description**: `ExactDamagePattern`, `LastStandRecoveryPattern`, and `LowSunVictoryPattern` were not inspected. Whether they hook into gameplay events (`sunSpent`, damage callbacks, etc.) is unknown.
- **Impact**: Unknown. Inspect before modifying `ScoringGameMode`.

---

### UNR-004 — ConveyorBeltSystem tick behavior with null service
- **Location**: `game/engine/system/special_levels/ConveyorBeltSystem.java`
- **Description**: Due to BUG-005, `ConveyorBeltSystem` is constructed with a null service. The system's behavior when `update(tick)` is called with a null service reference was not inspected.
- **Impact**: Unknown. The system may NPE on first tick, silently no-op, or have a null check. Inspect before working on conveyor belt chapters.

---

### UNR-005 — Minigame rendering and input integration
- **Location**: Minigame screen/controller classes
- **Description**: It is confirmed that VaseBreaker, IZombie, and WallNutBowling have screen/controller classes that are empty or contain only stubs. Whether any partial graphical input (e.g., clicking vases) is wired was not confirmed.
- **Impact**: Assume these are not functional until inspected.
