package app.qurancorpus.morphology;

public enum VoiceType {
    Active("ACT"),
    Passive("PASS");

    final String tag;

    VoiceType(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return tag;
    }
}