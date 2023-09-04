package app.qurancorpus.morphology;

public enum FormType {
    First("I"),
    Second("II"),
    Third("III"),
    Fourth("IV"),
    Fifth("V"),
    Sixth("VI"),
    Seventh("VII"),
    Eighth("VIII"),
    Ninth("IX"),
    Tenth("X"),
    Eleventh("XI"),
    Twelfth("XII");

    final String tag;

    FormType(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return tag;
    }
}