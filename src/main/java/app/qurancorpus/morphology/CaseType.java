package app.qurancorpus.morphology;

public enum CaseType {
    Nominative("NOM"),
    Genitive("GEN"),
    Accusative("ACC");

    final String tag;

    CaseType(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return tag;
    }
}