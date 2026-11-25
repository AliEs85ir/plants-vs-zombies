package graphics.general.modifier.visualModifiers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import game.entity.zombie.Zombie;
import game.world.playground.Grid;

public class RedModifier implements VisualModifier{
    private final Zombie zombie;
    private Grid grid;
    private final double EFFECT_START_DISTANCE = 400.0;
    private float gbChannel = 1f;

    public RedModifier(Zombie zombie) {
        this.zombie = zombie;
        this.grid = zombie.getWorld().getGrid();
    }

    @Override
    public void update(float delta) {
        double zombieX = zombie.getPosition().getX();
        double playGroundX = grid.getConfig().getPlaygroundX();

        double distance = Math.abs(zombieX - playGroundX);

        if (distance >= EFFECT_START_DISTANCE) {
            gbChannel = 1f;
        } else {
            gbChannel = (float) (distance / EFFECT_START_DISTANCE);

            gbChannel = Math.max(0f, gbChannel);
        }
    }

    @Override
    public void apply(SpriteBatch batch) {
        batch.setColor(
            1f,
            gbChannel,
            gbChannel,
            1f
        );
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
