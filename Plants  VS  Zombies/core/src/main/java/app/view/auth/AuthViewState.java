package app.view.auth;

public enum AuthViewState {
    SIGN_UP_STEP_1,
    SIGN_UP_STEP_2,
    SIGN_UP_STEP_3,
    LOGIN,
    FORGOT_PASSWORD,
    PASSWORD_RESET;

    public boolean isSignUp() {
        return this == SIGN_UP_STEP_1 || this == SIGN_UP_STEP_2 || this == SIGN_UP_STEP_3;
    }

    public int signUpStepNumber() {
        return switch (this) {
            case SIGN_UP_STEP_1 -> 1;
            case SIGN_UP_STEP_2 -> 2;
            case SIGN_UP_STEP_3 -> 3;
            default -> 0;
        };
    }
}
