package app.qurancorpus.morphology;

public enum AspectType {
    Perfect("PERF"),
    Imperfect("IMPF"),
    Imperative("IMPV");

    final String tag;

    AspectType(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return tag;
    }
}