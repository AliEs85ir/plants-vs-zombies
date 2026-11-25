package game.entity.effect;

public abstract class PermanentEffect
        extends Effect
{
    protected boolean expired = false;

    @Override
    public boolean isExpired(long currentTick) {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }
}
