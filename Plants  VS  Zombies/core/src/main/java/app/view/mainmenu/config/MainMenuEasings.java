package app.view.mainmenu.config;

import com.badlogic.gdx.math.Interpolation;

import java.util.Locale;

public final class MainMenuEasings {
    private MainMenuEasings() {
    }

    public static Interpolation parse(String name) {
        if (name == null || name.isBlank()) {
            return Interpolation.sineOut;
        }
        String key = name.trim().toLowerCase(Locale.ROOT).replace("_", "").replace("-", "");
        return switch (key) {
            case "linear" -> Interpolation.linear;
            case "fade" -> Interpolation.fade;
            case "smooth" -> Interpolation.smooth;
            case "smoother" -> Interpolation.smoother;
            case "sine", "sineinout" -> Interpolation.sine;
            case "sinein" -> Interpolation.sineIn;
            case "sineout" -> Interpolation.sineOut;
            case "pow2", "quad", "pow2inout" -> Interpolation.pow2;
            case "pow2in", "quadin" -> Interpolation.pow2In;
            case "pow2out", "quadout" -> Interpolation.pow2Out;
            case "pow3out", "cubicout" -> Interpolation.pow3Out;
            case "swingout" -> Interpolation.swingOut;
            case "circleout" -> Interpolation.circleOut;
            default -> Interpolation.sineOut;
        };
    }
}
