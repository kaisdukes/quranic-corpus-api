package app.qurancorpus.nlg;

import app.qurancorpus.morphology.PronounType;
import app.qurancorpus.morphology.Segment;
import app.qurancorpus.orthography.Token;

import static app.qurancorpus.arabic.CharacterType.Kaf;
import static app.qurancorpus.arabic.encoding.buckwalter.BuckwalterDecoder.fromBuckwalter;
import static app.qurancorpus.arabic.encoding.unicode.UnicodeEncoder.toUnicode;
import static app.qurancorpus.morphology.Diptote.isDiptoteWithGenitiveFatha;
import static app.qurancorpus.morphology.Morphology.isEmphasisNoonWithTanween;
import static app.qurancorpus.morphology.Morphology.isSuffixElision;
import static app.qurancorpus.morphology.PartOfSpeech.*;
import static app.qurancorpus.morphology.PartOfSpeechCategory.Nominal;
import static app.qurancorpus.morphology.PronounType.SecondObject;
import static app.qurancorpus.morphology.PronounType.Subject;
import static app.qurancorpus.morphology.SegmentType.Stem;
import static app.qurancorpus.morphology.SpecialType.*;
import static app.qurancorpus.morphology.VoiceType.Passive;
import static app.qurancorpus.nlg.ArabicGrammar.*;
import static app.qurancorpus.nlg.PronounDescription.getPronounDescription;
import static app.qurancorpus.syntax.Syntax.*;

public class ArabicGrammarGenerator implements Generator {
    private final StringBuilder text = new StringBuilder();
    private final Token token;
    private final Segment[] segments;

    public ArabicGrammarGenerator(Token token, Segment[] segments) {
        this.token = token;
        this.segments = segments;
    }

    @Override
    public String generate() {
        Segment stem = null;
        var segmentCount = segments.length;
        for (var i = 0; i < segmentCount; i++) {

            // PP
            var segment = segments[i];
            if (isPrepositionPhrase(segments, i)) {
                writeSection(PREPOSITION_PHRASE);
                while ((stem = segments[i]).getType() != Stem) {
                    i++;
                }
                continue;
            }

            // POS:PRV
            if (isPreventivePhrase(segments, i)) {
                writeSection(PREVENTIVE_PHRASE);
                while ((stem = segments[i]).getType() != Stem) {
                    i++;
                }
                continue;
            }

            switch (segment.getType()) {
                case Prefix -> writePrefix(segment);
                case Stem -> writeStem(stem = segment);
                case Suffix -> writeSuffix(token, stem, segment);
            }
        }

        return text.toString();
    }

    private void writePrefix(Segment prefix) {
        switch (prefix.getPartOfSpeech()) {
            case Conjunction -> {
                switch (prefix.getLemma().arabicText().getCharacterType(0)) {
                    case Waw -> writeSection(CONJUNCTION_WAW);
                    case Fa -> writeSection(CONJUNCTION_FA);
                }
            }
            case Comitative -> writeSection(COMITATIVE_WAW);
            case Resumption -> {
                switch (prefix.getLemma().arabicText().getCharacterType(0)) {
                    case Waw -> writeSection(RESUMPTION_WAW);
                    case Fa -> writeSection(RESUMPTION_FA);
                }
            }
            case Circumstantial -> writeSection(CIRCUMSTANTIAL_WAW);
            case Result -> writeSection(RESULT_FA);
            case Cause -> writeSection(CAUSE_FA);
            case Supplemental -> {
                switch (prefix.getLemma().arabicText().getCharacterType(0)) {
                    case Waw -> writeSection(SUPPLEMENTAL_WAW);
                    case Fa -> writeSection(SUPPLEMENTAL_FA);
                }
            }
            case Emphatic -> writeSection(EMPHASIS_LAM);
            case Imperative -> writeSection(IMPERATIVE_LAM);
            case Purpose -> writeSection(PURPOSE_LAM);
            case Future -> writeSection(FUTURE_PARTICLE);
            case Interrogative -> writeSection(INTERROGATIVE_ALIF);
            case Equalization -> writeSection(EQUALIZATION_ALIF);
            case Vocative -> writeSection(VOCATIVE_PREFIX);
            case Preposition -> writeSection(PREPOSITION);
        }
    }

    private void writeStem(Segment stem) {
        var partOfSpeech = stem.getPartOfSpeech();
        var special = stem.getSpecial();
        switch (partOfSpeech) {

            // POS:N
            case Noun -> writeSection(NOUN);

            // POS:PN
            case ProperNoun -> {
                if (stem.getLemma().key().equals("{ll~ah")
                        || stem.getLemma().key().equals("{ll~ahum~a")) {
                    writeSection(ALLAH);
                } else {
                    writeSection(PROPER_NOUN);
                }
            }

            // POS:PRON
            case Pronoun -> writeSection(PRONOUN);

            // POS:DEM
            case Demonstrative -> writeSection(DEMONSTRATIVE);

            // POS:REL
            case Relative -> writeSection(RELATIVE);

            // POS:ADJ
            case Adjective -> writeSection(ADJECTIVE);

            // POS:V
            case Verb -> writeVerb(stem);

            // POS:P
            case Preposition -> writeSection(PREPOSITION);

            // POS:INTG
            case Interrogative -> writeSection(
                    stem.getPartOfSpeechCategory() == Nominal
                            ? INTERROGATIVE_NOUN
                            : INTERROGATIVE_PARTICLE);

            // POS:NEG
            case Negative -> {

                // SP:kaAn
                if (special == Kaana) {
                    writeSection(NEGATIVE_TYPE);
                    text.append(" «");
                    text.append(PARTICLE_LAYSA);
                    text.append("»");
                    break;
                }

                // SP:<in~
                if (special == Inna) {
                    writeSection(NEGATIVE_ROLE);
                    text.append(" «");
                    text.append(PARTICLE_ANNA);
                    text.append("»");
                    break;
                }
                writeSection(NEGATIVE);
            }

            // POS:PRO
            case Prohibition -> writeSection(PROHIBITION);

            // POS:FUT
            case Future -> writeSection(FUTURE_PARTICLE);

            // POS:CONJ
            case Conjunction -> writeSection(CONJUNCTION);

            // POS:INL
            case Initials -> writeSection(INITIALS);

            // POS:T
            case Time -> writeSection(TIME);

            // POS:LOC
            case Location -> writeSection(LOCATION);

            // POS:ACC
            case Accusative -> {
                writeSection(ACCUSATIVE_PARTICLE);
                if (!stem.getLemma().arabicText().isHamzaBelow(0)) {
                    // not LEM:<in~
                    write(GROUP_MEMBER);
                    text.append(" «");
                    text.append(PARTICLE_INNA);
                    text.append("»");
                }
            }

            // POS:COND
            case Conditional -> writeSection(
                    stem.getPartOfSpeechCategory() == Nominal
                            ? CONDITIONAL_NOUN
                            : CONDITIONAL_PARTICLE);

            // POS:SUB
            case SubordinatingConjunction -> writeSection(SUBORDINATING_CONJUNCTION);

            // POS:RES
            case Restriction -> writeSection(RESTRICTION_PARTICLE);

            // POS:EXP
            case Exceptive -> writeSection(EXCEPTIVE_PARTICLE);

            // POS:AVR
            case Aversion -> writeSection(AVERSION_PARTICLE);

            // POS:CERT
            case Certainty -> writeSection(CERTAINTY_PARTICLE);

            // POS:RET
            case Retraction -> writeSection(RETRACTION_PARTICLE);

            // POS:ANS
            case Answer -> writeSection(ANSWER_PARTICLE);

            // POS:INC
            case Inceptive -> writeSection(INCEPTIVE_PARTICLE);

            // POS:SUR
            case Surprise -> writeSection(SURPRISE_PARTICLE);

            // POS:SUP
            case Supplemental -> writeSection(SUPPLEMENTAL_PARTICLE);

            // POS:EXH
            case Exhortation -> writeSection(EXHORTATION_PARTICLE);

            // POS:IMPN
            case ImperativeVerbalNoun -> writeSection(IMPERATIVE_VERBAL_NOUN);

            // POS:EXL
            case Explanation -> writeSection(EXPLANATION_PARTICLE);

            // POS:AMD
            case Amendment -> writeSection(AMENDMENT_PARTICLE);

            // POS:INT
            case Interpretation -> writeSection(INTERPRETATION_PARTICLE);
        }

        // case
        var isMasculinePartOfSpeechName = partOfSpeech != Adjective;
        if (stem.getCase() != null) {
            switch (stem.getCase()) {
                case Nominative -> write(isMasculinePartOfSpeechName ? NOMINATIVE : NOMINATIVE_FEMININE);
                case Genitive -> write(isMasculinePartOfSpeechName ? GENITIVE : GENITIVE_FEMININE);
                case Accusative -> write(isMasculinePartOfSpeechName ? ACCUSATIVE : ACCUSATIVE_FEMININE);
            }
        }

        // case marker
        if (isDiptoteWithGenitiveFatha(stem)) {
            write(GENITIVE_FATHA_REASON);
            write(DIPTOTE);
        }
    }

    private void writeVerb(Segment stem) {
        writeSection(VERB);

        if (stem.getAspect() != null) {
            switch (stem.getAspect()) {
                case Perfect -> write(PERFECT);
                case Imperfect -> write(IMPERFECT);
                case Imperative -> write(IMPERATIVE);
            }
        }

        if (stem.getSpecial() == Kaana && stem.getRoot().getCharacterType(0) != Kaf) {
            // not ROOT:kwn
            write(GROUP_MEMBER);
            text.append(" «");
            text.append(VERB_KAANA);
            text.append("»");
        }

        if (stem.getVoice() == Passive) {
            write(PASSIVE);
        }

        if (stem.getMood() != null) {
            switch (stem.getMood()) {
                case Subjunctive -> write(ACCUSATIVE);
                case Jussive -> write(JUSSIVE);
            }
        }
    }

    private void writeSuffix(Token token, Segment stem, Segment suffix) {

        // +VOC
        if (suffix.getPartOfSpeech() == Vocative) {
            return;
        }

        // n:EMPH+
        if (suffix.getPartOfSpeech() == Emphatic) {
            write(WAW);
            if (isEmphasisNoonWithTanween(suffix)) {
                text.append(EMPHASIS_NOON_TANWEEN);
            } else {
                text.append(EMPHASIS_NOON);
            }
            return;
        }

        // POS:P
        if (stem.getPartOfSpeech() == Preposition) {
            return;
        }

        // description
        write(WAW);
        var pronounDescription = getPronounDescription(stem, suffix);
        if (pronounDescription != null) {
            if (pronounDescription.quote()) {
                text.append("«");
            }
            text.append(pronounDescription.description());
            if (pronounDescription.quote()) {
                text.append("»");
            }
        }
        if (isSuffixElision(token, suffix)) {
            write(toUnicode(fromBuckwalter("AlmH*wfp")));
        }

        write(PRONOUN_ROLE);

        var special = stem.getSpecial();
        if (suffix.getPronounType() == Subject) {
            if (special == Kaana || special == Kaada) {
                write(KAANA_PRONOUN);
                text.append(" «");
                text.append(getHeadName(segments, stem));
                text.append("»");
            } else if (stem.getVoice() == Passive) {
                write(PASSIVE_SUBJECT_PRONOUN);
            } else {
                write(SUBJECT_PRONOUN);
            }
            return;
        }

        if (special == Inna) {
            write(INNA_PRONOUN);
            text.append(" «");
            text.append(getHeadName(segments, stem));
            text.append("»");
            return;
        }

        if (stem.getPartOfSpeech() == Verb) {
            write(OBJECT_PRONOUN);

            // double pronoun?
            if (segments[segments.length - 1].getPronounType() == SecondObject) {
                write(suffix.getPronounType() == PronounType.Object ? FIRST : SECOND);
            }
            return;
        }

        write(POSSESSIVE_PRONOUN);
    }

    private void writeSection(String text) {
        if (!this.text.isEmpty()) {
            this.text.append('\n');
        }
        this.text.append(text);
    }

    private void write(String text) {
        if (!this.text.isEmpty()) {
            this.text.append(' ');
        }
        this.text.append(text);
    }
}