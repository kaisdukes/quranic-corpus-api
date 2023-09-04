package app.qurancorpus.morphology;

public enum PersonType {
    First("1"),
    Second("2"),
    Third("3");

    final String tag;

    PersonType(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return tag;
    }
}