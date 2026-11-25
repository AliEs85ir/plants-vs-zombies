package app.enums.commands;

public enum SignupMenuCommands implements Commands{
    REGISTER("\\s*register\\s+-u\\s+(?<username>.*?)\\s*-p\\s+(?<password1>\\S+)\\s*(?<password2>\\S+)\\s*-n\\s+(?<nickname>.*?)\\s*-e\\s+(?<email>.*?)\\s*-g\\s*(?<gender>.*?)\\s*"),
    PICK_QUESTION("\\s*pick\\s+question\\s+-q\\s*(?<number>.*?)\\s*-a\\s+(?<answer>.*?)\\s*-c\\s*(?<confirm>.*?)\\s*"),
    ;
    private final String pattern;

    SignupMenuCommands(String pattern) {
        this.pattern = pattern;
    }

    @Override
    // >>> getting pattern to match it with regex
    public String getPattern() {
        return pattern;
    }
}
