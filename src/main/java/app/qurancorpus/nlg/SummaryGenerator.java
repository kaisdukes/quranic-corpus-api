package app.qurancorpus.nlg;

import app.qurancorpus.arabic.CharacterType;
import app.qurancorpus.morphology.*;
import app.qurancorpus.orthography.Token;

import java.util.ArrayList;

import static app.qurancorpus.arabic.encoding.buckwalter.BuckwalterDecoder.fromBuckwalter;
import static app.qurancorpus.morphology.AspectType.Imperfect;
import static app.qurancorpus.morphology.Diptote.isDiptoteWithGenitiveFatha;
import static app.qurancorpus.morphology.MoodType.Indicative;
import static app.qurancorpus.morphology.Morphology.*;
import static app.qurancorpus.morphology.PartOfSpeech.*;
import static app.qurancorpus.morphology.PronounType.SecondObject;
import static app.qurancorpus.morphology.PronounType.Subject;
import static app.qurancorpus.morphology.SpecialType.Inna;
import static app.qurancorpus.morphology.SpecialType.Kaana;
import static app.qurancorpus.nlg.ArabicGrammar.*;
import static app.qurancorpus.nlg.Ordinal.getLongName;
import static app.qurancorpus.nlg.PronounDescription.getPronounDescription;
import static app.qurancorpus.nlg.SegmentName.getSegmentName;
import static app.qurancorpus.syntax.Syntax.*;

public class SummaryGenerator implements Generator {
    private final Text text = new Text();
    private final Token token;
    private final Segment[] segments;
    private final Segment stem;
    private boolean singleSegment;

    public SummaryGenerator(Token token, Segment[] segments, Segment stem) {
        this.token = token;
        this.segments = segments;
        this.stem = stem;
    }

    @Override
    public String generate() {
        writeLeadingText();

        var segmentCount = isDeterminerAl(segments) ? segments.length - 1 : segments.length;
        singleSegment = segmentCount == 1;
        if (!singleSegment) {
            text.add("divided into ");
            text.add(segmentCount);
            text.add(" morphological segments");
            text.endSentence();
            writeSegmentNames();
        }

        writeSegmentDescriptions();
        writeSummary();
        return text.toString();
    }

    private void writeSummary() {

        if (isPrepositionPhrase(segments)) {
            text.space();
            text.add("Together the segments form a preposition phrase known as ");
            text.addPhonetic("jār wa majrūr");
            text.add(' ');
            text.addArabic(PREPOSITION_PHRASE);
            text.endSentence();
            return;
        }

        if (isPreventivePhrase(segments)) {
            text.space();
            text.add("Together the segments are known as ");
            text.addPhonetic("kāfa wa makfūfa");
            text.add(' ');
            text.addArabic(PREVENTIVE_PHRASE);
            text.endSentence();
        }
    }

    private void writeLeadingText() {
        var location = token.location();
        if (stem.getPartOfSpeech() == Initials) {
            text.add("Verse ");
            text.add(location.verseNumber());
            text.add(" of chapter ");
            text.add(location.chapterNumber());
            text.add(" begins with ");
            return;
        }

        text.add("The ");
        text.add(getLongName(location.tokenNumber()));
        text.add(" word of verse (");
        text.add(location.chapterNumber());
        text.add(':');
        text.add(location.verseNumber());
        text.add(") is ");
    }

    private void writeSegmentNames() {
        var items = new ArrayList<String>();
        var segmentCount = segments.length;
        for (var i = 0; i < segmentCount; i++) {
            var segment = segments[i];
            if (segment.getPartOfSpeech() == Determiner) {
                continue;
            }
            if (i == segments.length - 2
                    && segment.getPronounType() == PronounType.Object
                    && segments[i + 1].getPronounType() == SecondObject) {
                items.add("two object pronouns");
                break;
            }
            items.add(getSegmentName(segments, stem, segment));
        }

        text.space();

        var itemCount = items.size();
        for (var i = 0; i < itemCount; i++) {
            var item = items.get(i);
            if (i == 0) {
                text.addIndefiniteArticle(true, item);
                text.add(' ');
            } else if (i == itemCount - 1) {
                text.add(" and ");
            } else {
                text.add(", ");
            }
            text.add(item);
        }
        text.endSentence();
    }

    private void writeSegmentDescriptions() {
        for (var segment : segments) {
            switch (segment.getType()) {
                case Prefix -> writePrefixDescription(segment);
                case Stem -> writeStemDescription(segment);
                case Suffix -> writeSuffixDescription(segment);
            }
        }
    }

    private void writePrefixDescription(Segment prefix) {

        // A:INTG+
        var partOfSpeech = prefix.getPartOfSpeech();
        if (partOfSpeech == Interrogative) {
            text.space();
            text.add("The prefixed ");
            text.addPhonetic("alif");
            text.add(" is an interrogative particle used to form a question and is usually translated as \"is\", \"are\", or \"do\"");
            text.endSentence();
            return;
        }

        // A:EQ+
        if (partOfSpeech == Equalization) {
            text.space();
            text.add("The prefixed ");
            text.addPhonetic("alif");
            text.add(" indicates equality and is usually translated as \"whether\"");
            text.endSentence();
            return;
        }

        // l:PRP+
        if (partOfSpeech == Purpose) {
            text.space();
            text.add("The prefixed particle ");
            text.addPhonetic("lām");
            text.add(" is used to indicate the purpose of an action and makes the following verb subjunctive");
            text.endSentence();
            return;
        }

        // l:EMPH+
        if (partOfSpeech == Emphatic) {
            text.space();
            text.add("The prefixed particle ");
            text.addPhonetic("lām");
            text.add(" is usually translated as \"surely\" or \"indeed\" and is used to add emphasis");
            text.endSentence();
            return;

        }

        // l:IMPV+
        if (partOfSpeech == Imperative) {
            text.space();
            text.add("The prefixed particle ");
            text.addPhonetic("lām");
            text.add(" is usually translated as \"let\" and is used to form an imperative construction");
            text.endSentence();
            return;
        }

        // w:CONJ+ f:CONJ+
        if (partOfSpeech == Conjunction) {
            var isWa = prefix.getLemma().arabicText().getCharacterType(0) == CharacterType.Waw;
            text.space();
            text.add("The prefixed conjunction ");
            String phonetic = isWa ? "wa" : "fa";
            text.addPhonetic(phonetic);
            text.add(" is usually translated as \"and\"");
            text.endSentence();
            return;
        }

        // w:REM+ f:REM+
        if (partOfSpeech == Resumption) {
            var isWa = prefix.getLemma().arabicText().getCharacterType(0) == CharacterType.Waw;
            text.space();
            text.add("The connective particle ");
            String phonetic = isWa ? "wa" : "fa";
            text.addPhonetic(phonetic);
            text.add(" is usually translated as \"then\" or \"so\" and is used to indicate a sequence of events");
            text.endSentence();
            return;
        }

        // w:SUP+ f:SUP+
        if (partOfSpeech == Supplemental) {
            var isWa = prefix.getLemma().arabicText().getCharacterType(0) == CharacterType.Waw;
            text.space();
            text.add("The supplemental particle ");
            String phonetic = isWa ? "wa" : "fa";
            text.addPhonetic(phonetic);
            text.add(" is usually translated as \"then\" or \"so\"");
            text.endSentence();
            return;
        }

        // f:RSLT+
        if (partOfSpeech == Result) {
            text.space();
            text.add("The result particle ");
            text.addPhonetic("fa");
            text.add(" is usually translated as \"then\" or \"so\" and is used to indicate the result of a condition");
            text.endSentence();
            return;
        }

        // w:CIRC+
        if (partOfSpeech == Circumstantial) {
            text.space();
            text.add("The connective particle ");
            text.addPhonetic("wa");
            text.add(" is usually translated as \"while\" and is used to indicate the circumstance of events");
            text.endSentence();
            return;
        }

        // w:COM+
        if (partOfSpeech == Comitative) {
            text.space();
            text.add("The comitative usage of ");
            text.addPhonetic("wāw");
            text.add(' ');
            text.addArabic(COMITATIVE_PARTICLE);
            text.add(" precedes a comitative object ");
            text.addArabic(fromBuckwalter("mfEwl mEh"));
            text.add(", and is considered to be equivalent to \"with\" ");
            text.addArabic(fromBuckwalter("mE"));
            text.endSentence();
            return;
        }

        // f:CAUS+
        if (partOfSpeech == Cause) {
            text.space();
            text.add("The prefixed particle ");
            text.addPhonetic("fa");
            text.add(" is usually translated as \"then\" or \"so\"");
            text.endSentence();
            text.space();
            text.add("The particle is used to indicate cause and makes the following verb subjunctive");
            text.endSentence();
            return;
        }

        // bi+ ka+ ta+ w:P+ l:P+
        if (partOfSpeech == Preposition) {
            String phonetic = null;
            String translation = null;
            var isOath = false;
            switch (prefix.getLemma().arabicText().getCharacterType(0)) {
                case Ba -> {
                    phonetic = "bi";
                    translation = "\"with\" or \"by\"";
                }
                case Kaf -> {
                    phonetic = "ka";
                    translation = "\"like\" or \"as\"";
                }
                case Ta -> {
                    phonetic = "ta";
                    translation = "\"by\"";
                    isOath = true;
                }
                case Waw -> {
                    phonetic = "wa";
                    translation = "\"by\"";
                    isOath = true;
                }
                case Lam -> {
                    phonetic = "lām";
                    translation = "\"for\"";
                }
            }

            text.space();
            text.add("The prefixed preposition ");
            text.addPhonetic(phonetic);
            text.add(" is usually translated as ");
            text.add(translation);
            if (isOath) {
                text.add(" and is used to form an oath");
            }
            text.endSentence();
            return;
        }

        // sa+
        if (partOfSpeech == Future) {
            text.space();
            text.add("The prefixed future particle ");
            text.addPhonetic("sa");
            text.add(" is used in combination with the imperfect (present tense) verb to form the future tense");
            text.endSentence();
        }
    }

    private void writeStemDescription(Segment stem) {

        // POS:PRO
        var partOfSpeech = stem.getPartOfSpeech();
        if (partOfSpeech == Prohibition) {
            if (singleSegment) {
                text.add("a prohibition particle ");
                text.addArabic(PROHIBITION);
                text.add(" that");
            } else {
                text.space();
                text.add("The prohibition particle ");
                text.addArabic(PROHIBITION);
            }
            text.add(" is used to form a negative imperative and places the following verb into the jussive mood ");
            text.addArabic(JUSSIVE);
            text.endSentence();
            return;
        }

        // POS:NEG SP:kaAn
        var special = stem.getSpecial();
        if (partOfSpeech == Negative && special == Kaana) {
            if (singleSegment) {
                text.add("a negative particle that acts");
            } else {
                text.space();
                text.add("The negative particle acts");
            }
            text.add(" like the verb ");
            text.addPhonetic("laysa");
            text.add(' ');
            text.addArabic(fromBuckwalter("lys"));
            text.add(". This verb belongs to a special group of words known as ");
            text.addPhonetic("kāna");
            text.add(" and her sisters ");
            text.addArabic(KAANA_GROUP);
            text.endSentence();
            return;
        }

        // POS:NEG SP:<in~
        if (partOfSpeech == Negative && special == Inna) {
            if (singleSegment) {
                text.add("a negative particle that acts");
            } else {
                text.space();
                text.add("The negative particle acts");
            }
            text.add(" like the particle ");
            text.addPhonetic("anna");
            text.add(' ');
            text.addArabic(fromBuckwalter(">n"));
            text.add(". This particle belongs to a special group of words known as ");
            text.addPhonetic("inna");
            text.add(" and her sisters ");
            text.addArabic(INNA_GROUP);
            text.endSentence();
            return;
        }

        // Inna.
        if (special == Inna) {
            if (singleSegment) {
                text.add("an accusative particle which ");
            } else {
                text.space();
                text.add("The accusative particle ");
            }
            text.add("belongs to a special group of words known as ");
            text.addPhonetic("inna");
            text.add(" and her sisters ");
            text.addArabic(INNA_GROUP);
            text.endSentence();
            return;
        }

        // POS:INL
        if (partOfSpeech == Initials) {
            writeInitials();
            return;
        }

        // POS:PREV
        if (isPreventivePhrase(segments)) {
            text.space();
            text.add("The preventive particle ");
            text.addPhonetic("mā");
            text.add(" stops ");
            text.addPhonetic("inna");
            text.add(" from taking its normal role in the sentence");
            text.endSentence();
        }

        var isFeatureGroup2 = isFeatureGroup2(stem);
        var caseType = stem.getCase();

        var mood = stem.getMood();
        if (partOfSpeech == Verb && stem.getAspect() == Imperfect && mood == null) {
            mood = Indicative;
        }

        if (!singleSegment && !isFeatureGroup2 && mood == null && caseType == null) {
            return;
        }

        var featureGroup1 = getFeatureGroup1(stem);
        var isFeatureGroup1 = !featureGroup1.isEmpty();
        if (singleSegment) {
            if (isFeatureGroup1 || isFeatureGroup2) {
                text.addIndefiniteArticle(false, featureGroup1);
                text.add(featureGroup1);
            }
            if (partOfSpeech == Verb) {
                text.endSentence();
                text.space();
                text.add("The verb is ");
                writeFeatureGroup2(stem);
            } else {
                if (isFeatureGroup2) {
                    text.add(' ');
                    writeFeatureGroup2(stem);
                }
                var segmentName = getSegmentName(segments, stem, stem);
                if (!isFeatureGroup1 && !isFeatureGroup2) {
                    text.addIndefiniteArticle(false, segmentName);
                }
                text.add(' ');
                text.add(segmentName);
            }
        } else {
            text.space();
            text.add("The");
            text.add(featureGroup1);
            if (partOfSpeech != Verb) {
                text.add(' ');
                text.add(getSegmentName(segments, stem, stem));
            }

            if (isFeatureGroup2) {
                text.add(" is ");
                writeFeatureGroup2(stem);
            }
        }

        if (caseType != null || mood != null) {
            if (isFeatureGroup2) {
                text.add(" and");
            }

            text.add(singleSegment && !isFeatureGroup2 ? " in the " : " is in the ");

            if (caseType != null) {
                text.add(caseType.name().toLowerCase());
                text.add(" case ");
                switch (caseType) {
                    case Nominative -> text.addArabic(NOMINATIVE);
                    case Genitive -> text.addArabic(GENITIVE);
                    case Accusative -> text.addArabic(ACCUSATIVE);
                }
            } else {
                text.add(mood.name().toLowerCase());
                text.add(" mood ");
                switch (mood) {
                    case Indicative -> text.addArabic(NOMINATIVE);
                    case Subjunctive -> text.addArabic(ACCUSATIVE);
                    case Jussive -> text.addArabic(JUSSIVE);
                }
            }
        }

        text.endSentence();

        if (isDiptoteWithGenitiveFatha(stem)) {
            text.space();
            text.add("The case marker is a ");
            text.addPhonetic("fatḥah");
            text.add(" instead of a ");
            text.addPhonetic("kasrah");
            text.add(" because the ");
            text.add(getSegmentName(segments, stem, stem));
            text.add(" is a diptote ");
            text.addArabic(DIPTOTE);
            text.endSentence();
        }

        if (stem.getRoot() != null) {
            writeRoot(stem);
        }

        if (partOfSpeech == Verb && special != null) {
            text.space();
            text.add("The verb ");
            text.addArabic(getHeadName(segments, stem));
            text.add(" belongs to a special group of words known as ");

            if (special == Kaana) {
                text.addPhonetic("kāna");
                text.add(" and her sisters ");
                text.addArabic(KAANA_GROUP);
            } else {
                text.addPhonetic("kāda");
                text.add(" and her sisters ");
                text.addArabic(KAADA_GROUP);
            }

            text.endSentence();
        }
    }

    private void writeInitials() {
        text.add("the Quranic initials ");
        text.addArabicLetters(token.arabicText(), false);
        text.endSentence();

        text.space();
        text.add("These are sequences of letters that occur at the start of certain chapters in the Quran");
        text.endSentence();

        text.space();
        text.add("In Arabic these are known as the disconnected or shortened letters ");
        text.addArabic(INITIALS);
        text.endSentence();
    }

    private Text getFeatureGroup1(Segment stem) {
        var text = new Text();

        if (stem.getState() == StateType.Indefinite) {
            text.add(" indefinite");
        }

        if (stem.getVoice() == VoiceType.Passive) {
            text.add(" passive");
        }

        if (stem.getForm() != null) {
            text.add(" form ");
            text.add(stem.getForm().toString());
        }

        if (stem.getAspect() != null) {
            text.add(' ');
            text.add(stem.getAspect().name().toLowerCase());
            text.add(" verb ");
            switch (stem.getAspect()) {
                case Perfect -> text.addArabic(VERB + ' ' + PERFECT);
                case Imperfect -> text.addArabic(VERB + ' ' + IMPERFECT);
                case Imperative -> text.addArabic(VERB + ' ' + IMPERATIVE);
            }
        }

        return text;
    }

    private void writeFeatureGroup2(Segment stem) {
        writePersonGenderNumber(stem);
    }

    private boolean isFeatureGroup2(Segment stem) {
        return stem.getPerson() != null || stem.getGender() != null || stem.getNumber() != null;
    }

    private void writeRoot(Segment stem) {
        var root = stem.getRoot();
        text.space();
        text.add("The ");
        text.add(getSegmentName(segments, stem, stem));
        text.add("'s ");
        if (root.getLength() == 3) {
            text.add("triliteral ");
        } else {
            text.add("quadriliteral ");
        }
        text.add("root is ");
        text.addArabicLetters(root, true);
        text.endSentence();
    }

    private void writeSuffixDescription(Segment suffix) {

        // n:EMPH+
        if (suffix.getPartOfSpeech() == Emphatic) {
            text.space();
            text.add("The suffixed emphatic particle is known as the ");
            text.addPhonetic("nūn");
            text.add(" of emphasis ");
            text.addArabic(fromBuckwalter("nwn Altwkyd"));
            if (isEmphasisNoonWithTanween(suffix)) {
                text.add(", and is indicated by ");
                text.addPhonetic("tanwīn");
            }
            text.endSentence();
        }

        var pronounType = suffix.getPronounType();
        if (pronounType == Subject) {
            text.space();
            text.add("The suffix ");
            text.addArabic(getPronounDescription(stem, suffix).description());
            text.add(" is an attached subject pronoun");
            text.endSentence();
        } else if (pronounType == PronounType.Object || pronounType == SecondObject) {
            if (isSuffixElision(token, suffix)) {
                writeOmittedPronounSuffix(suffix);
                return;
            }
            text.space();
            text.add("The attached ");
            text.add(getSegmentName(segments, stem, suffix));
            text.add(" is ");
            writePersonGenderNumber(suffix);
            text.endSentence();
        }
    }

    private void writeOmittedPronounSuffix(Segment suffix) {
        text.space();
        text.add("The ");
        text.addPhonetic("yā");
        text.add(" of the ");
        writePersonGenderNumber(suffix);
        text.add(" ");
        text.add(getSegmentName(segments, stem, suffix));
        text.add(" has been omitted due to elision ");
        text.addArabic(fromBuckwalter("AlyA' mH*wfp"));
        if (token.arabicText().isKasra(token.arabicText().getLength() - 1)) {
            text.add(", and is indicated by the ");
            text.addPhonetic("kasrah");
        }
        text.endSentence();
    }

    private void writePersonGenderNumber(Segment segment) {
        var person = segment.getPerson();
        var gender = segment.getGender();
        var number = segment.getNumber();

        if (person != null) {
            text.add(person.name().toLowerCase());
            text.add(" person");
        }

        if (gender != null) {
            if (person != null) {
                text.add(' ');
            }
            text.add(gender.name().toLowerCase());
        }

        if (number != null) {
            if (person != null || gender != null) {
                text.add(' ');
            }
            text.add(number.name().toLowerCase());
        }
    }
}