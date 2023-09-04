package app.qurancorpus.morphology;

public enum NumberType {
    Singular("S"),
    Dual("D"),
    Plural("P");

    final String tag;

    NumberType(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return tag;
    }
}