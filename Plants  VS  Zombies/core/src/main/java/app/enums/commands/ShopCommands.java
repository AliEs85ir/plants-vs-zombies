package app.enums.commands;

public enum ShopCommands implements Commands{
    List("^\\s*shop\\s+list\\s*$"),
    Daily("^\\s*shop\\s+daily\\s*$"),
    Buy("^\\s*shop\\s+buy\\s+-i\\s+(?<itemID>[^\\s]+)\\s+-n\\s+(?<count>\\d+)(?:\\s+-t\\s+(?<type>[^\\s]+))?\\s*$"),
    BuyDaily("^\\s*shop\\s+buy\\s+daily\\s*$"),
    ;

    private final String pattern;

    ShopCommands(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String getPattern() {
        return pattern;
    }
}
