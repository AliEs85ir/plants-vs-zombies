package game.entity.zombie.data.properties;

import app.enums.zombie.ImpType;
import common.model.Offset;
import game.entity.zombie.data.HealthThresholdToImpData;

import java.util.List;

public class GargantuarProperty extends ZombieProperty{
    private int armDropFraction;
    private boolean canBeFlickedOff;
    private boolean explodesWhenMowed;
    private boolean flickIsLaneRestricted;
    private Offset almanacOffset;
    private int almanacScale;
    private int impApex;
    private float impFlightTime;
    private int impTargetColumn;
    private ImpType impType;
    private boolean isValidPinchTarget;
    private int minPosXThrowTarget;
    private boolean skipHeadDropState;
    private int SmashDamage;
    private float SmashDuration;
    private String soundOnAsh;
    private String SoundOnElectrocute;
    private String soundOnIdle;
    private String StaticArtImageAsset;
    private int throwImpDuration;
    private List<HealthThresholdToImpData> healthThresholdToImpAmmoLayers;

    @Override
    public int getSmashDamage() {
        return SmashDamage;
    }

    @Override
    public float getSmashDuration() {
        return SmashDuration;
    }
}
