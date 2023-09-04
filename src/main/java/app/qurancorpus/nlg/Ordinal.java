package app.qurancorpus.nlg;

public class Ordinal {

    private Ordinal() {
    }

    private static final String[] ORDINALS = {
            "zeroth",
            "first",
            "second",
            "third",
            "fourth",
            "fifth",
            "sixth",
            "seventh",
            "eighth",
            "ninth",
            "tenth",
            "eleventh",
            "twelfth",
            "thirteenth",
            "fourteenth",
            "fifteenth",
            "sixteenth",
            "seventeenth",
            "eighteenth",
            "nineteenth",
            "twentieth"
    };

    public static String getShortName(int value) {
        var digit = value % 100;
        return value +
                (digit >= 11 && digit <= 13
                        ? "th"
                        : switch (value % 10) {
                    case 1 -> "st";
                    case 2 -> "nd";
                    case 3 -> "rd";
                    default -> "th";
                });
    }

    public static String getLongName(int value) {
        return value < ORDINALS.length ? ORDINALS[value] : getShortName(value);
    }
}