package app.qurancorpus.syntax;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public enum PhraseType {
    Sentence("S"),
    NominalSentence("NS"),
    VerbalSentence("VS"),
    ConditionalSentence("CS"),
    PrepositionPhrase("PP"),
    SubordinateClause("SC");

    private static final Map<String, PhraseType> tagMap
            = stream(values()).collect(toMap(x -> x.tag, identity()));

    private final String tag;

    PhraseType(String tag) {
        this.tag = tag;
    }

    public String tag() {
        return tag;
    }

    @Override
    public String toString() {
        return tag;
    }

    public static PhraseType parse(String tag) {
        return tagMap.get(tag);
    }
}