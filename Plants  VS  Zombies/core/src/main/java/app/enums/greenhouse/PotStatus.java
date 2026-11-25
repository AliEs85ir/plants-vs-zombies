package app.enums.greenhouse;

public enum PotStatus
{
    LOCKED ("Locked") ,
    NO_POT ("No Pot") ,
    EMPTY ("Empty") ,
    GROWING ("Growing") ,
    READY ("Ready")
    ;

    private final String label;

    PotStatus(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }

    public static PotStatus parsePotStatus(String input) {
        if (input == null)
            return null;

        input = input.trim();

        for (PotStatus status : PotStatus.values()) {
            if (status.toString().equalsIgnoreCase(input))
                return status;
            if (status.name().equalsIgnoreCase(input))
                return status;
        }

        return null;
    }
}
