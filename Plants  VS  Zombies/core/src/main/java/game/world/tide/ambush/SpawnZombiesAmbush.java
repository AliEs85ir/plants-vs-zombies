package game.world.tide.ambush;

import common.model.Position;
import game.entity.registry.ZombieRegistry;
import game.service.Zombie.ZombieService;
import game.world.GameWorld;
import game.world.playground.Cell;
import game.world.tide.TideState;

import java.util.List;

public final class SpawnZombiesAmbush implements LowTideAmbush {
    private final List<AmbushSpawnSpec> specs;
    private final List<String> aliasFallback;

    public SpawnZombiesAmbush(List<AmbushSpawnSpec> specs, String... aliasFallback) {
        this.specs = List.copyOf(specs);
        this.aliasFallback = aliasFallback != null ? List.of(aliasFallback) : List.of();
    }

    public static SpawnZombiesAmbush mermaidImp() {
        return new SpawnZombiesAmbush(
            List.of(new AmbushSpawnSpec("ZombieBeachImp", 5, new OnePerRowPlacement())),
            "ZombieImp"
        );
    }

    @Override
    public void trigger(GameWorld world, TideState tideState, List<Cell> newlyExposedLand) {
        if (world == null || newlyExposedLand == null || newlyExposedLand.isEmpty()) {
            return;
        }
        ZombieService zombieService = new ZombieService(world);
        for (AmbushSpawnSpec spec : specs) {
            String alias = resolveAlias(spec.getZombieAlias());
            if (alias == null) {
                continue;
            }
            List<Cell> cells = spec.getPlacement().choose(newlyExposedLand, spec.getCount());
            for (Cell cell : cells) {
                zombieService.spawnZombie(
                    alias,
                    new Position(cell.getWorldX(), cell.getWorldY())
                );
            }
        }
    }

    private String resolveAlias(String preferred) {
        if (ZombieRegistry.getZombie(preferred) != null) {
            return preferred;
        }
        for (String fallback : aliasFallback) {
            if (ZombieRegistry.getZombie(fallback) != null) {
                return fallback;
            }
        }
        return null;
    }
}
