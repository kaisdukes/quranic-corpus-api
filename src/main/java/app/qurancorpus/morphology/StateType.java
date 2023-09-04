package app.qurancorpus.morphology;

public enum StateType {
    Definite("DEF"),
    Indefinite("INDEF");

    final String tag;

    StateType(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return tag;
    }
}