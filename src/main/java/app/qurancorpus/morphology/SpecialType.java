package app.qurancorpus.morphology;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public enum SpecialType {
    Kaana("kaAn"),
    Kaada("kaAd"),
    Inna("<in~");

    private static final Map<String, SpecialType> tagMap
            = stream(values()).collect(toMap(x -> x.tag, identity()));

    private final String tag;

    SpecialType(String tag) {
        this.tag = tag;
    }

    public String tag() {
        return tag;
    }

    @Override
    public String toString() {
        return tag;
    }

    public static SpecialType parse(String tag) {
        return tagMap.get(tag);
    }
}