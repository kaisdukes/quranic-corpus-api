package app.qurancorpus.syntax;

import java.util.Map;

import static java.text.MessageFormat.format;
import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public enum Relation {
    Possessive("poss"),
    Object("obj"),
    Subject("subj"),
    Conjunction("conj"),
    Link("link"),
    Predicate("pred"),
    Genitive("gen"),
    Apposition("app"),
    Subordinate("sub"),
    Adjective("adj"),
    PassiveSubject("pass"),
    SpecialSubject("subjx"),
    SpecialPredicate("predx"),
    Circumstantial("circ"),
    Vocative("voc"),
    Exceptive("exp"),
    CognateAccusative("cog"),
    Specification("spec"),
    Purpose("prp"),
    Future("fut"),
    Interrogative("intg"),
    Emphasis("emph"),
    Negation("neg"),
    Prohibition("pro"),
    Compound("cpnd"),
    Condition("cond"),
    Result("rslt"),
    ImperativeResult("imrs"),
    Imperative("impv"),
    Certainty("cert"),
    Answer("ans"),
    Restriction("res"),
    Surprise("sur"),
    Retraction("ret"),
    Explanation("exl"),
    Preventive("prev"),
    Aversion("avr"),
    Inceptive("inc"),
    Exhortation("exh"),
    Equalization("eq"),
    Cause("caus"),
    Amendment("amd"),
    Supplemental("sup"),
    Interpretation("int"),
    Comitative("com");

    private static final Map<String, Relation> tagMap
            = stream(values()).collect(toMap(x -> x.tag, identity()));

    private final String tag;

    Relation(String tag) {
        this.tag = tag;
    }

    public String tag() {
        return tag;
    }

    @Override
    public String toString() {
        return tag;
    }

    public static Relation parse(String tag) {
        var relation = tagMap.get(tag);
        if (relation == null) {
            throw new UnsupportedOperationException(
                    format("Relation tag {0} not recognized.", tag));
        }
        return relation;
    }
}