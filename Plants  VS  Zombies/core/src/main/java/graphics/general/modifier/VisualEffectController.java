package graphics.general.modifier;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import graphics.general.modifier.visualModifiers.GreenModifier;
import graphics.general.modifier.visualModifiers.IceSlowModifier;
import graphics.general.modifier.visualModifiers.PulseTintModifier;
import graphics.general.modifier.visualModifiers.TintModifier;
import graphics.general.modifier.visualModifiers.VisualModifier;

import java.util.*;

public class VisualEffectController {

    private final Map<Class<? extends VisualModifier>, VisualModifier> modifiers = new HashMap<>();

    public void add(VisualModifier modifier){
        modifiers.putIfAbsent(modifier.getClass(), modifier);
    }

    public void addOrReplace(VisualModifier modifier){
        modifiers.put(modifier.getClass(), modifier);
    }

    public void remove(Class<? extends VisualModifier> type) {
        modifiers.remove(type);
    }

    public boolean has(Class<? extends VisualModifier> type) {
        return modifiers.containsKey(type);
    }


    public void update(float delta){

        Iterator<VisualModifier> iterator = modifiers.values().iterator();

        while(iterator.hasNext()){

            VisualModifier modifier = iterator.next();

            modifier.update(delta);

            if(modifier.isFinished()){
                iterator.remove();
            }
        }
    }

    public void beforeRender(SpriteBatch batch, RenderContext context) {
        for (VisualModifier modifier : modifiers.values()) {
            modifier.beforeRender(batch, context);
        }
    }

    public void afterRender(SpriteBatch batch, RenderContext context) {
        for (VisualModifier modifier : modifiers.values()) {
            modifier.afterRender(batch, context);
        }
    }


    public void apply(SpriteBatch batch){
        VisualModifier tint = modifiers.get(TintModifier.class);
        VisualModifier pulse = modifiers.get(PulseTintModifier.class);
        VisualModifier iceSlow = modifiers.get(IceSlowModifier.class);
        VisualModifier green = modifiers.get(GreenModifier.class);
        for (VisualModifier modifier : modifiers.values()) {
            if (modifier == tint || modifier == pulse || modifier == iceSlow || modifier == green) {
                continue;
            }
            modifier.apply(batch);
        }
        if (green != null) {
            green.apply(batch);
        }
        if (iceSlow != null) {
            iceSlow.apply(batch);
        }
        if (tint != null) {
            tint.apply(batch);
        }
        if (pulse != null) {
            pulse.apply(batch);
        }
    }
}
