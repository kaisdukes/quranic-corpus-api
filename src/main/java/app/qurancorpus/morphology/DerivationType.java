package app.qurancorpus.morphology;

public enum DerivationType {
    ActiveParticiple("ACT PCPL"),
    PassiveParticiple("PASS PCPL"),
    VerbalNoun("VN");

    final String tag;

    DerivationType(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return tag;
    }
}