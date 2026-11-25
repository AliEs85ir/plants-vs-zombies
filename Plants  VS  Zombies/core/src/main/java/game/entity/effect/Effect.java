package game.entity.effect;

import app.enums.effect.EffectType;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;
import game.service.plant.PlantService;

public abstract class Effect
{
    protected EffectType type;
    protected  boolean applied = false;

    public void onApply(Plant plant){}

    public void onApply(Zombie zombie){}

    public void tick(Zombie zombie, ZombieService zombieService, long currentTick){}
    public void tick(Plant plant, PlantService plantService, long currentTick) {}

    public void onRemove(Zombie zombie) {}
    public void onRemove(Plant plant) {}

    public boolean isExpired(long currentTick) {
        return false;
    }

    public void applyModifier(Zombie zombie) {

    }
    public void applyModifier(Plant plant) {

    }

    public EffectType getType() {
        return type;
    }
}
