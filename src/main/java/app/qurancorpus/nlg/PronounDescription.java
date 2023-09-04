package app.qurancorpus.nlg;

import app.qurancorpus.morphology.*;

import static app.qurancorpus.morphology.AspectType.*;
import static app.qurancorpus.morphology.GenderType.Feminine;
import static app.qurancorpus.morphology.GenderType.Masculine;
import static app.qurancorpus.morphology.NumberType.*;
import static app.qurancorpus.morphology.PartOfSpeech.Verb;
import static app.qurancorpus.morphology.PersonType.*;
import static app.qurancorpus.morphology.PronounType.Subject;
import static app.qurancorpus.nlg.ArabicGrammar.*;

public record PronounDescription(String description, boolean quote) {

    public PronounDescription(String description) {
        this(description, false);
    }

    public static PronounDescription getPronounDescription(Segment stem, Segment pronoun) {
        var aspect = stem.getAspect();
        var person = pronoun.getPerson();
        var gender = pronoun.getGender();
        var number = pronoun.getNumber();
        var pronounType = pronoun.getPronounType();

        // 1S
        if (person == First && number == Singular) {
            if (pronounType == PronounType.Object) {
                return new PronounDescription(PRONOUN_TYPE_YA);
            }
            return new PronounDescription(PRONOUN_TYPE_TA);
        }

        // 1P
        if (person == First && number == Plural) {
            return new PronounDescription(PRONOUN_TYPE_NA, true);
        }

        // 2D
        if (person == Second && number == Dual) {
            if (pronounType == Subject) {
                if (aspect == Imperfect || aspect == Imperative) {
                    return new PronounDescription(PRONOUN_TYPE_ALIF);
                }
                return new PronounDescription(PRONOUN_TYPE_TA);
            }
            return new PronounDescription(PRONOUN_TYPE_KAF);
        }

        // 2FS
        if (person == Second && gender == Feminine && number == Singular) {
            if (pronounType == PronounType.Object) {
                return new PronounDescription(PRONOUN_TYPE_KAF);
            }

            // POS:V PERF
            if (stem.getPartOfSpeech() == Verb && stem.getAspect() == Perfect) {
                return new PronounDescription(PRONOUN_TYPE_TA);
            }

            return new PronounDescription(PRONOUN_TYPE_YA);
        }

        // 2FP
        if (person == Second && gender == Feminine && number == Plural) {
            if (pronounType == Subject) {
                return new PronounDescription(PRONOUN_TYPE_TA);
            }
            return new PronounDescription(PRONOUN_TYPE_KAF);
        }

        // 2MS | 2MP
        if (person == Second && gender == Masculine && (number == Singular || number == Plural)) {
            if (pronounType == Subject) {
                if (aspect == Perfect) {
                    return new PronounDescription(PRONOUN_TYPE_TA);
                }
                return new PronounDescription(PRONOUN_TYPE_WAW);
            }
            return new PronounDescription(PRONOUN_TYPE_KAF);
        }

        // 3MS
        if (person == Third && gender == Masculine && number == Singular) {
            return new PronounDescription(PRONOUN_TYPE_HA);
        }

        // 3FS
        if (person == Third && gender == Feminine && number == Singular) {
            return new PronounDescription(PRONOUN_TYPE_HAA, true);
        }

        // 3D
        if (person == Third && number == Dual) {
            if (pronounType == Subject) {
                return new PronounDescription(PRONOUN_TYPE_ALIF);
            }
            return new PronounDescription(PRONOUN_TYPE_HA);
        }

        // 3FP
        if (person == Third && gender == Feminine && number == Plural) {
            if (pronounType == Subject) {
                return new PronounDescription(PRONOUN_TYPE_NOON);
            }
            return new PronounDescription(PRONOUN_TYPE_HN, true);
        }

        // 3MP
        if (person == Third && gender == Masculine && number == Plural) {
            if (pronounType == Subject) {
                return new PronounDescription(PRONOUN_TYPE_WAW);
            }
            return new PronounDescription(PRONOUN_TYPE_HM, true);
        }

        return null;
    }
}