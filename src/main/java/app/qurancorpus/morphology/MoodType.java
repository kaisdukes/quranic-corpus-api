package app.qurancorpus.morphology;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public enum MoodType {
    Indicative("IND"),
    Subjunctive("SUBJ"),
    Jussive("JUS");

    private static final Map<String, MoodType> tagMap
            = stream(values()).collect(toMap(x -> x.tag, identity()));

    private final String tag;

    MoodType(String tag) {
        this.tag = tag;
    }

    public String tag() {
        return tag;
    }

    @Override
    public String toString() {
        return tag;
    }

    public static MoodType parse(String tag) {
        return tagMap.get(tag);
    }
}