package app.enums.commands;

public enum GameMenuCommands implements Commands{
    EnterChapter("^\\s*enter\\s+chapter\\s+-c\\s+(?<chaptername>.*?)\\s*$"),
    CoinWallet("^\\s*show\\s+coin-wallet\\s*$"),
    GemWallet("^\\s*show\\s+gem-wallet\\s*$"),
    CheatAddCoin("^\\s*cheat\\s+add\\s+(?<amount>\\d+)\\s+(?<currency>.*?)\\s*$")
    ;
    private final String pattern;

    GameMenuCommands(String pattern) {
        this.pattern = pattern;
    }

    @Override
    // >>> getting pattern to match it with regex
    public String getPattern() {
        return pattern;
    }
}
