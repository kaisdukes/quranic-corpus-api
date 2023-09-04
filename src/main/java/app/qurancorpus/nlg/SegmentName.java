package app.qurancorpus.nlg;

import app.qurancorpus.morphology.*;

import static app.qurancorpus.morphology.PartOfSpeechCategory.Nominal;
import static app.qurancorpus.morphology.PronounType.SecondObject;
import static app.qurancorpus.morphology.SegmentType.Prefix;
import static app.qurancorpus.morphology.SegmentType.Suffix;

public class SegmentName {

    private SegmentName() {
    }

    public static String getSegmentName(Segment[] segments, Segment stem, Segment segment) {
        var derivation = segment.getDerivation();
        if (derivation != null) {
            return switch (derivation) {
                case ActiveParticiple -> "active participle";
                case PassiveParticiple -> "passive participle";
                case VerbalNoun -> "verbal noun";
            };
        }

        switch (segment.getPartOfSpeech()) {
            case Noun -> {
                return "noun";
            }
            case ProperNoun -> {
                return "proper noun";
            }
            case Pronoun -> {
                if (segment.getType() == Suffix) {
                    var stemPartOfSpeech = stem.getPartOfSpeech();
                    if (stemPartOfSpeech == PartOfSpeech.Noun
                            || stemPartOfSpeech == PartOfSpeech.Time
                            || stemPartOfSpeech == PartOfSpeech.Location) {
                        return "possessive pronoun";
                    }

                    switch (segment.getPronounType()) {
                        case Subject -> {
                            return "subject pronoun";
                        }
                        case Object -> {
                            return segments[segments.length - 1].getPronounType() == SecondObject
                                    ? "first object pronoun"
                                    : "object pronoun";
                        }
                        case SecondObject -> {
                            return "second object pronoun";
                        }
                    }
                }
                return "personal pronoun";
            }
            case Demonstrative -> {
                return "demonstrative pronoun";
            }
            case Relative -> {
                return "relative pronoun";
            }
            case Adjective -> {
                return "adjective";
            }
            case Verb -> {
                return "verb";
            }
            case Preposition -> {
                return "preposition";
            }
            case Accusative -> {
                return "accusative particle";
            }
            case Conditional -> {
                return
                        segment.getPartOfSpeechCategory() == Nominal
                                ? "conditional noun"
                                : "conditional particle";
            }
            case SubordinatingConjunction -> {
                return "subordinating conjunction";
            }
            case Restriction -> {
                return "restriction particle";
            }
            case Exceptive -> {
                return "exceptive particle";
            }
            case Aversion -> {
                return "aversion particle";
            }
            case Certainty -> {
                return "particle of certainty";
            }
            case Retraction -> {
                return "retraction particle";
            }
            case Preventive -> {
                return "preventive particle";
            }
            case Answer -> {
                return "answer particle";
            }
            case Inceptive -> {
                return "inceptive particle";
            }
            case Surprise -> {
                return "surprise particle";
            }
            case Supplemental -> {
                return "supplemental particle";
            }
            case Exhortation -> {
                return "exhortation particle";
            }
            case Result -> {
                return "result particle";
            }
            case ImperativeVerbalNoun -> {
                return "imperative verbal noun";
            }
            case Interrogative -> {
                if (segment.getType() != Prefix) {
                    return
                            segment.getPartOfSpeechCategory() == Nominal
                                    ? "interrogative noun"
                                    : "interrogative particle";
                }
                return "interrogative {alif}";
            }
            case Vocative -> {
                return segment.getType() == Suffix ? "vocative suffix" : "vocative particle";
            }
            case Negative -> {
                return "negative particle";
            }
            case Emphatic -> {
                return segment.getType() == Suffix ? "emphatic suffix" : "emphatic prefix";
            }
            case Purpose -> {
                return "particle of purpose";
            }
            case Imperative -> {
                return "imperative particle";
            }
            case Future -> {
                return "future particle";
            }
            case Conjunction -> {
                return segment.getType() == Prefix ? "conjunction" : "coordinating conjunction";
            }
            case Initials -> {
                return "Quranic initials";
            }
            case Time -> {
                return "time adverb";
            }
            case Location -> {
                return "location adverb";
            }
            case Explanation -> {
                return "explanation particle";
            }
            case Equalization -> {
                return "equalization particle";
            }
            case Resumption -> {
                return "resumption particle";
            }
            case Circumstantial -> {
                return "circumstantial particle";
            }
            case Cause -> {
                return "particle of cause";
            }
            case Amendment -> {
                return "amendment particle";
            }
            case Prohibition -> {
                return "prohibition particle";
            }
            case Interpretation -> {
                return "particle of interpretation";
            }
            case Comitative -> {
                return "comitative particle";
            }
            default -> throw new UnsupportedOperationException();
        }
    }
}