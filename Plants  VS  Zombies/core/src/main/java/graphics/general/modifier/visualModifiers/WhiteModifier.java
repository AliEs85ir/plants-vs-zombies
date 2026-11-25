package graphics.general.modifier.visualModifiers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class WhiteModifier implements VisualModifier{

    // --- SHADER CODE ---
    // The vertex shader is just the standard LibGDX one.
    private static final String VERTEX_SHADER =
        "attribute vec4 a_position;\n" +
            "attribute vec4 a_color;\n" +
            "attribute vec2 a_texCoord0;\n" +
            "uniform mat4 u_projTrans;\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "void main() {\n" +
            "   v_color = a_color;\n" +
            "   v_texCoords = a_texCoord0;\n" +
            "   gl_Position = u_projTrans * a_position;\n" +
            "}\n";

    // The fragment shader handles mixing the texture color with pure white.
    private static final String FRAGMENT_SHADER =
        "#ifdef GL_ES\n" +
            "precision mediump float;\n" +
            "#endif\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "uniform sampler2D u_texture;\n" +
            "uniform float u_whiteBlend;\n" + // 1.0 = pure white, 0.0 = normal
            "void main() {\n" +
            "   vec4 texColor = texture2D(u_texture, v_texCoords);\n" +
            "   // Mix original color with pure white based on our blend value\n" +
            "   vec3 finalColor = mix(texColor.rgb * v_color.rgb, vec3(1.0, 1.0, 1.0), u_whiteBlend);\n" +
            "   gl_FragColor = vec4(finalColor, texColor.a * v_color.a);\n" + // Keep original transparency!
            "}\n";

    private static ShaderProgram whiteShader;

    // --- MODIFIER LOGIC ---
    private float time;
    private final float duration;

    public WhiteModifier() {
        this.duration = 8.0f; // Time in seconds the flash lasts
        this.time = 0f;
        initShader();
    }

    public static ShaderProgram whiteShader() {
        initShader();
        return whiteShader;
    }

    private static void initShader() {
        if (whiteShader == null) {
            whiteShader = new ShaderProgram(VERTEX_SHADER, FRAGMENT_SHADER);
            if (!whiteShader.isCompiled()) {
                System.err.println("White shader failed to compile: " + whiteShader.getLog());
            }
        }
    }

    @Override
    public void update(float delta) {
        time += delta;
    }

    @Override
    public void apply(SpriteBatch batch) {
        if (isFinished()) {
            return; // Do nothing if the effect is over
        }

        // Progress goes from 0.0 to 1.0
        float progress = time / duration;

        // whiteBlendAmount goes from 1.0 (pure white) down to 0.0 (normal)
        float whiteBlendAmount = 1f - progress;

        // Apply the custom shader to the batch
        batch.flush(); // Crucial: Flush the batch before changing shaders!
        whiteShader.bind(); // Bind shader to set our custom uniform variable
        whiteShader.setUniformf("u_whiteBlend", whiteBlendAmount);
        batch.setShader(whiteShader);
    }

    @Override
    public boolean isFinished() {
        return time >= duration;
    }
}
