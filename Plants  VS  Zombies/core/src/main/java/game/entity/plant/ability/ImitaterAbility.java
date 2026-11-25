package game.entity.plant.ability;



import app.enums.plant.PlantGameplayPhase;

import app.enums.plant.PlantName;

import game.engine.time.GameClock;

import game.entity.plant.Plant;

import game.service.plant.ImitaterReplacement;

import game.service.plant.PlantService;

import graphics.general.event.PlantVisualEventType;



public class ImitaterAbility extends AbstractPlantAbility {

    public static final int REPLACEMENT_DELAY_TICKS = 14;



    public enum Phase {

        IDLE,

        IN_PROGRESS,

        FINISHED

    }



    private Phase phase = Phase.IDLE;

    private PlantName replacement;

    private long transformStartTick;

    private boolean attackPlayed;



    @Override

    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {

        if (plant == null || phase != Phase.IN_PROGRESS || replacement == null) {

            return;

        }

        if (!attackPlayed) {

            attackPlayed = true;

            plant.setGameplayPhase(PlantGameplayPhase.ATTACKING);

            PlantAbilitySupport.play(plant, PlantVisualEventType.ATTACK_TRIGGERED);

        }

        if (currentTick - transformStartTick >= REPLACEMENT_DELAY_TICKS) {

            finish(plant, plantService, currentTick);

        }

    }



    public boolean canBeginReplacement() {

        return phase == Phase.IDLE;

    }



    public boolean beginReplacement(PlantName plantName, long currentTick) {

        if (!canBeginReplacement() || plantName == null || plantName == PlantName.IMITATER) {

            return false;

        }

        replacement = plantName;

        transformStartTick = currentTick;

        phase = Phase.IN_PROGRESS;

        attackPlayed = false;

        return true;

    }



    public Phase getPhase() {

        return phase;

    }



    public PlantName getReplacement() {

        return replacement;

    }



    public boolean isInProgress() {

        return phase == Phase.IN_PROGRESS;

    }



    public boolean isFinished() {

        return phase == Phase.FINISHED;

    }



    @Override

    public boolean handleDeath(Plant plant, PlantService plantService, long currentTick) {

        if (phase == Phase.IN_PROGRESS) {

            phase = Phase.FINISHED;

            replacement = null;

        }

        return false;

    }



    private void finish(Plant plant, PlantService plantService, long currentTick) {

        if (phase != Phase.IN_PROGRESS) {

            return;

        }

        phase = Phase.FINISHED;

        PlantName chosen = replacement;

        replacement = null;

        ImitaterReplacement.complete(plant, chosen, plantService, currentTick);

    }

}

