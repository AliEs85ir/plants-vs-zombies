package graphics.general.modifier.visualModifiers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class WhiteDamageModifier implements VisualModifier {

    private float time;
    private final float duration;

    public WhiteDamageModifier(float duration) {
        this.duration = duration;
        this.time = 0f;
        WhiteModifier.whiteShader();
    }

    @Override
    public void update(float delta) {
        time += delta;
    }

    @Override
    public void apply(SpriteBatch batch) {
        if (isFinished()) {
            return;
        }

        float progress = time / duration;
        float whiteBlendAmount = 1f - progress;

        ShaderProgram whiteShader = WhiteModifier.whiteShader();
        batch.flush();
        whiteShader.bind();
        whiteShader.setUniformf("u_whiteBlend", whiteBlendAmount);
        batch.setShader(whiteShader);
    }

    @Override
    public boolean isFinished() {
        return time >= duration;
    }
}
