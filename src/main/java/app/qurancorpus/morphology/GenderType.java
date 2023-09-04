package app.qurancorpus.morphology;

public enum GenderType {
    Masculine("M"),
    Feminine("F");

    final String tag;

    GenderType(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return tag;
    }
}