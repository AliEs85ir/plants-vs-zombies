package game.entity.zombie;

import app.model.App;
import com.badlogic.gdx.math.Vector2;
import game.entity.zombie.data.properties.NewspaperProperty;
import game.entity.zombie.data.properties.OrdinaryZombieProperty;
import game.entity.zombie.data.properties.armor.ArmorProperty;
import game.service.Zombie.ZombieService;
import graphics.general.effects.EffectRequest;
import graphics.general.effects.anchers.KinematicAnchor;
import graphics.general.event.ZombieVisualEventType;
import graphics.general.instance.VisibilityController;

public class ZombieArmor {
    public enum Phase {PHASE1, PHASE2, PHASE3}
    private Phase phase;
    private final ArmorProperty property;
    private double currentHealth;
    private boolean broken;

    public ZombieArmor(ArmorProperty property) {
        this.property = property;
        this.currentHealth = property.getBaseHealth();
        this.broken = false;
        this.phase = Phase.PHASE1;
    }

    public String getName() {
        return property.getAlias();
    }

    public double getCurrentHealth() {
        return currentHealth;
    }

    public double takeDamage(double amount, Zombie zombie) {
        if (broken || amount <= 0) {
            return amount;
        }

        if (amount >= currentHealth) {
            double leftover = amount - currentHealth;
            breakArmor(zombie);
            return leftover;
        }

        currentHealth -= amount;
        updateVisibility(zombie);
        return 0;
    }

    private void breakArmor(Zombie zombie) {

        if(zombie.getPropertyData() instanceof NewspaperProperty) {
            zombie.pushEvent(ZombieVisualEventType.NEWSPAPER_DEFEAT);
        }
        ZombieService.addArmorBreakEffect(this, zombie);
        currentHealth = 0;
        broken = true;
    }

    public ArmorProperty getProperty() {
        return property;
    }

    public boolean isBroken() {
        return broken;
    }

    private void updateVisibility(Zombie zombie) {
        double percentage = currentHealth / property.getBaseHealth();
        if(percentage <= 0.66) {
            this.phase = Phase.PHASE2;
        }
        if(percentage <= 0.33) {
            this.phase = Phase.PHASE3;
        }
    }

    public Phase getPhase() {
        return phase;
    }
}
