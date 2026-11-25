package game.entity.zombie.ability;

import common.model.Position;
import game.audio.AudioId;
import game.audio.AudioManager;
import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;
import game.world.GameWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PianoMusicAbility extends AbstractZombieAbility{
    public static final long PIANO_MUSIC_COOLDOWN_TICKS = 100;

    private long lastCastTick = -1;

    @Override
    public void update(Zombie zombie, ZombieService zombieService, long currentTick) {
        if (!canCast(currentTick)) {
            return;
        }

        GameWorld world = zombie.getWorld();
        List<Zombie> zombies = new ArrayList<>(world.getZombies());

        for (Zombie other : zombies) {
            if (other == zombie || other.isDead()) {
                continue;
            }

            shiftToNeighborRow(other);
            world.addEvent("Piano ability got activated! ZOMBIES CHANGED LINE!");
        }

        boolean firstCast = lastCastTick < 0;
        lastCastTick = currentTick;
        if (firstCast) {
            AudioManager.play(AudioId.PIANO_THEME);
        }
    }

    @Override
    public void onDeath(Zombie zombie) {
        AudioManager.stop(AudioId.PIANO_THEME, 0.4f);
    }

    private boolean canCast(long currentTick) {
        return lastCastTick < 0 || (currentTick - lastCastTick) >= PIANO_MUSIC_COOLDOWN_TICKS;
    }

    private void shiftToNeighborRow(Zombie zombie) {
        int y = (int) zombie.getPosition().getY();
        int newY;

        if (y <= 0) {
            newY = 1;
        } else if (y >= 4) {
            newY = 3;
        } else {
            newY = ThreadLocalRandom.current().nextBoolean() ? y - 1 : y + 1;
        }

        Position oldPos = zombie.getPosition();
        zombie.setPosition(new Position(oldPos.getX(), newY));
    }


}
