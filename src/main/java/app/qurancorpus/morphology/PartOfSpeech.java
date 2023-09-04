package app.qurancorpus.morphology;

import java.util.Map;

import static java.text.MessageFormat.format;
import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public enum PartOfSpeech {
    Noun("N"),
    ProperNoun("PN"),
    Pronoun("PRON"),
    Demonstrative("DEM"),
    Relative("REL"),
    Adjective("ADJ"),
    Verb("V"),
    Preposition("P"),
    Interrogative("INTG"),
    Vocative("VOC"),
    Negative("NEG"),
    Emphatic("EMPH"),
    Purpose("PRP"),
    Imperative("IMPV"),
    Future("FUT"),
    Conjunction("CONJ"),
    Determiner("DET"),
    Initials("INL"),
    Time("T"),
    Location("LOC"),
    Accusative("ACC"),
    Conditional("COND"),
    SubordinatingConjunction("SUB"),
    Restriction("RES"),
    Exceptive("EXP"),
    Aversion("AVR"),
    Certainty("CERT"),
    Retraction("RET"),
    Preventive("PREV"),
    Answer("ANS"),
    Inceptive("INC"),
    Surprise("SUR"),
    Supplemental("SUP"),
    Exhortation("EXH"),
    ImperativeVerbalNoun("IMPN"),
    Explanation("EXL"),
    Equalization("EQ"),
    Resumption("REM"),
    Cause("CAUS"),
    Amendment("AMD"),
    Prohibition("PRO"),
    Circumstantial("CIRC"),
    Result("RSLT"),
    Interpretation("INT"),
    Comitative("COM");

    private static final Map<String, PartOfSpeech> tagMap
            = stream(values()).collect(toMap(x -> x.tag, identity()));

    private final String tag;

    PartOfSpeech(String tag) {
        this.tag = tag;
    }

    public String tag() {
        return tag;
    }

    @Override
    public String toString() {
        return tag;
    }

    public static PartOfSpeech parse(String tag) {
        var partOfSpeech = tagMap.get(tag);
        if (partOfSpeech == null) {
            throw new UnsupportedOperationException(
                    format("Part of speech tag {0} not recognized.", tag));
        }
        return partOfSpeech;
    }
}