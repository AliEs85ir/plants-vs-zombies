package graphics.general.modifier.visualModifiers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FlashModifier implements VisualModifier {


    private float time;
    private final float duration;
    private final float alpha;


    public FlashModifier(float duration, float alpha){

        this.duration = duration;
        this.alpha = alpha;
        this.time = 0f;
    }


    @Override
    public void update(float delta){

        time += delta;

    }


    @Override
    public void apply(SpriteBatch batch){

        if(isFinished()){
            return;
        }


        float currentAlpha =
            alpha * (1f - time / duration);


        batch.setColor(
            1f,
            1f,
            1f,
            currentAlpha
        );

    }


    @Override
    public boolean isFinished(){

        return time >= duration;

    }
}
