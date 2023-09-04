package app.qurancorpus.morphology;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public enum PronounType {
    Object("obj"),
    SecondObject("obj2"),
    Subject("subj");

    private static final Map<String, PronounType> tagMap
            = stream(values()).collect(toMap(x -> x.tag, identity()));

    private final String tag;

    PronounType(String tag) {
        this.tag = tag;
    }

    public String tag() {
        return tag;
    }

    @Override
    public String toString() {
        return tag;
    }

    public static PronounType parse(String tag) {
        return tagMap.get(tag);
    }
}