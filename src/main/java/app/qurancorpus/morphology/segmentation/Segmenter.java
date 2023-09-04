package app.qurancorpus.morphology.segmentation;

import app.qurancorpus.arabic.ArabicText;
import app.qurancorpus.arabic.CharacterType;
import app.qurancorpus.lexicography.Lemma;
import app.qurancorpus.lexicography.LemmaService;
import app.qurancorpus.morphology.*;
import app.qurancorpus.orthography.Token;

import java.util.ArrayList;

import static app.qurancorpus.arabic.encoding.buckwalter.BuckwalterDecoder.fromBuckwalter;
import static app.qurancorpus.arabic.encoding.buckwalter.BuckwalterEncoder.toBuckwalter;

public class Segmenter {
    private final LemmaService lemmaService;
    private final PronounReader pronounReader = new PronounReader();
    private final ArrayList<Segment> segments = new ArrayList<>();
    private Segment segment;
    private Segment stem;
    private Segment emphaticSuffix;
    private Token token;
    private ArabicText arabicText;
    private String morphology;
    private int prefixIndex;
    private int suffixIndex;

    // Lemmas.
    private final Lemma prefixWa;
    private final Lemma prefixFa;
    private final Lemma prefixBi;
    private final Lemma prefixKa;
    private final Lemma prefixTa;
    private final Lemma prefixLa;
    private final Lemma prefixSa;
    private final Lemma prefixYa;
    private final Lemma prefixHa;
    private final Lemma suffixNoon;
    private final Lemma vocativeSuffix;

    public Segmenter(LemmaService lemmaService) {
        this.lemmaService = lemmaService;
        this.prefixWa = lemmaService.getLemma("w");
        this.prefixFa = lemmaService.getLemma("f");
        this.prefixBi = lemmaService.getLemma("b");
        this.prefixKa = lemmaService.getLemma("k");
        this.prefixTa = lemmaService.getLemma("t");
        this.prefixLa = lemmaService.getLemma("l");
        this.prefixSa = lemmaService.getLemma("s");
        this.prefixYa = lemmaService.getLemma("yaA");
        this.prefixHa = lemmaService.getLemma("haA");
        this.suffixNoon = lemmaService.getLemma("n");
        this.vocativeSuffix = lemmaService.getLemma("hum~a");
    }

    public Segment[] getSegments(Token token, String morphology) {
        segments.clear();
        segment = null;
        stem = null;
        this.token = token;
        this.arabicText = token.arabicText();
        this.morphology = morphology;
        prefixIndex = 0;

        // Skip suffixed symbols.
        suffixIndex = arabicText.getLength();
        if (!arabicText.isLetter(suffixIndex - 1)) {
            suffixIndex--;
        }

        // Read whitespace delimited items.
        readItems(morphology.split(" "));

        // Subject pronoun suffixed to POS:V
        readSubjectPronoun();

        // Split stems.
        splitStems();

        // Segments.
        var segments = new Segment[this.segments.size()];
        this.segments.toArray(segments);
        var position = 0;
        for (var segment : segments) {
            var length = segment.getEndIndex() - segment.getStartIndex();
            segment.setArabicText(arabicText.substring(position, position + length));
            position += length;
        }
        return segments;
    }

    private void readItems(String[] items) {

        // Read each item, with correct handling of participles, double
        // pronouns, multiple stems and emphatic suffixes.
        var size = items.length;
        emphaticSuffix = null;
        for (var i = 0; i < size; i++) {

            // Get item.
            String item = items[i];

            // Double item?
            if (i < size - 1) {

                // Active particple.
                String next = items[i + 1];
                if (item.equals("ACT") && next.equals("PCPL")) {
                    segment.setDerivation(DerivationType.ActiveParticiple);
                    i++;
                    continue;
                }

                // Passive participle.
                if (item.equals("PASS") && next.equals("PCPL")) {
                    segment.setDerivation(DerivationType.PassiveParticiple);
                    i++;
                    continue;
                }

                // Double object pronoun.
                if (item.startsWith("PRON:") && next.startsWith("PRON:")) {
                    readObjectPronouns(item, next);
                    i++;
                    continue;
                }
            }

            // Prefix.
            if (readPrefix(item)) {
                continue;
            }

            // Part-of-speech (stem).
            if (item.startsWith("POS:")) {
                addStem(PartOfSpeech.parse(item.substring(4)));
                continue;
            }

            // Features.
            if (readFeatures(item)) {
                continue;
            }

            // +VOC
            if (item.equals("+VOC")) {
                readVocativeSuffix();
                continue;
            }

            // Object pronoun.
            if (item.startsWith("PRON:")) {
                readObjectPronouns(item, null);
                continue;
            }

            // +n:EMPH
            if (item.equals("+n:EMPH")) {
                addSuffix(PartOfSpeech.Emphatic);
                emphaticSuffix = segment;
                continue;
            }

            // Invalid.
            throw new UnsupportedOperationException("Invalid morphological feature" + ": "
                    + item);
        }

        // +n:EMPH
        if (emphaticSuffix != null) {
            readEmphaticSuffix();
        }
    }

    private boolean readPrefix(String item) {

        // A:INTG+
        if (item.equals("A:INTG+")) {
            if (arabicText.getCharacterType(prefixIndex) == CharacterType.Hamza
                    || arabicText.getCharacterType(prefixIndex) == CharacterType.Alif) {
                addPrefix(PartOfSpeech.Interrogative, 1);
                return true;
            }
            fail("A:INTG+");
        }

        // A:EQ+
        if (item.equals("A:EQ+")) {
            if (arabicText.getCharacterType(prefixIndex) == CharacterType.Hamza
                    || arabicText.getCharacterType(prefixIndex) == CharacterType.Alif) {
                addPrefix(PartOfSpeech.Equalization, 1);
                return true;
            }
            fail("A:EQ+");
        }

        // f:CONJ+
        if (item.equals("f:CONJ+")) {
            if (arabicText.getCharacterType(prefixIndex) == CharacterType.Fa) {
                addPrefix(PartOfSpeech.Conjunction, 1);
                segment.setLemma(prefixFa);
                return true;
            }
            fail("f:CONJ+");
        }

        // f:REM+
        if (item.equals("f:REM+")) {
            if (arabicText.getCharacterType(prefixIndex) == CharacterType.Fa) {
                addPrefix(PartOfSpeech.Resumption, 1);
                segment.setLemma(prefixFa);
                return true;
            }
            fail("f:REM+");
        }

        // f:RSLT+
        if (item.equals("f:RSLT+")) {
            if (arabicText.getCharacterType(prefixIndex) == CharacterType.Fa) {
                addPrefix(PartOfSpeech.Result, 1);
                segment.setLemma(prefixFa);
                return true;
            }
            fail("f:RSLT+");
        }

        // f:CAUS+
        if (item.equals("f:CAUS+")) {
            if (arabicText.getCharacterType(prefixIndex) == CharacterType.Fa) {
                addPrefix(PartOfSpeech.Cause, 1);
                segment.setLemma(prefixFa);
                return true;
            }
            fail("f:CAUS+");
        }

        // f:SUP+
        if (item.equals("f:SUP+")) {
            if (arabicText.getCharacterType(prefixIndex) == CharacterType.Fa) {
                addPrefix(PartOfSpeech.Supplemental, 1);
                segment.setLemma(prefixFa);
                return true;
            }
            fail("f:SUP+");
        }

        // w:SUP+
        if (item.equals("w:SUP+")) {
            if (arabicText.getCharacterType(prefixIndex) == CharacterType.Waw) {
                addPrefix(PartOfSpeech.Supplemental, 1);
                segment.setLemma(prefixWa);
                return true;
            }
            fail("w:SUP+");
        }

        // w:CONJ+
        if (item.equals("w:CONJ+")) {
            if (arabicText.getCharacterType(prefixIndex) == CharacterType.Waw) {
                addPrefix(PartOfSpeech.Conjunction, 1);
                segment.setLemma(prefixWa);
                return true;
            }
            fail("w:CONJ+");
        }

        // w:COM+
        if (item.equals("w:COM+")) {
            if (arabicText.getCharacterType(prefixIndex) == CharacterType.Waw) {
                addPrefix(PartOfSpeech.Comitative, 1);
                segment.setLemma(prefixWa);
                return true;
            }
            fail("w:COM+");
        }

        // w:REM+
        if (item.equals("w:REM+")) {
            if (arabicText.getCharacterType(prefixIndex) == CharacterType.Waw) {
                addPrefix(PartOfSpeech.Resumption, 1);
                segment.setLemma(prefixWa);
                return true;
            }
            fail("w:REM+");
        }

        // w:CIRC+
        if (item.equals("w:CIRC+")) {
            if (arabicText.getCharacterType(prefixIndex) == CharacterType.Waw) {
                addPrefix(PartOfSpeech.Circumstantial, 1);
                segment.setLemma(prefixWa);
                return true;
            }
            fail("w:CIRC+");
        }

        // w:P+
        if (item.equals("w:P+")) {
            if (arabicText.getCharacterType(prefixIndex) == CharacterType.Waw) {
                addPrefix(PartOfSpeech.Preposition, 1);
                segment.setLemma(prefixWa);
                return true;
            }
            fail("w:P+");
        }

        // ka+
        if (item.equals("ka+")) {
            if (arabicText.getCharacterType(prefixIndex) == CharacterType.Kaf) {
                addPrefix(PartOfSpeech.Preposition, 1);
                segment.setLemma(prefixKa);
                return true;
            }
            fail("ka+");
        }

        // l:EMPH+
        if (item.equals("l:EMPH+")) {
            if (arabicText.getCharacterType(prefixIndex) == CharacterType.Lam) {
                addPrefix(PartOfSpeech.Emphatic, 1);
                return true;
            }
            fail("l:EMPH+");
        }

        // bi+
        if (item.equals("bi+")) {
            if (arabicText.getCharacterType(prefixIndex) == CharacterType.Ba) {
                addPrefix(PartOfSpeech.Preposition, 1);
                segment.setLemma(prefixBi);
                return true;
            }
            fail("bi+");
        }

        // ta+
        if (item.equals("ta+")) {
            if (arabicText.getCharacterType(prefixIndex) == CharacterType.Ta) {
                addPrefix(PartOfSpeech.Preposition, 1);
                segment.setLemma(prefixTa);
                return true;
            }
            fail("ta+");
        }

        // l:P+
        if (item.equals("l:P+")) {

            // Suffix.
            if (stem != null
                    && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Lam) {
                addSuffix(PartOfSpeech.Preposition);
                setSuffixLength(1);
                segment.setLemma(prefixLa);
                return true;
            }

            // Prefix.
            if (arabicText.getCharacterType(prefixIndex) == CharacterType.Lam) {
                addPrefix(PartOfSpeech.Preposition, 1);
                segment.setLemma(prefixLa);
                return true;
            }
            fail("l:P+");
        }

        // l:IMPV+
        if (item.equals("l:IMPV+")) {
            if (arabicText.getCharacterType(prefixIndex) == CharacterType.Lam) {
                addPrefix(PartOfSpeech.Imperative, 1);
                return true;
            }
            fail("l:IMPV+");
        }

        // l:PRP+
        if (item.equals("l:PRP+")) {
            if (arabicText.getCharacterType(prefixIndex) == CharacterType.Lam) {
                addPrefix(PartOfSpeech.Purpose, 1);
                return true;
            }
            fail("l:PRP+");
        }

        // sa+
        if (item.equals("sa+")) {
            if (arabicText.getCharacterType(prefixIndex) == CharacterType.Seen) {
                addPrefix(PartOfSpeech.Future, 1);
                segment.setLemma(prefixSa);
                return true;
            }
            fail("sa+");
        }

        // ya+
        if (item.equals("ya+")) {

            // (20:94:2) yabona&um~a
            if (token.location().equals(20, 94, 2)
                    && arabicText.getCharacterType(prefixIndex) == CharacterType.Ya) {
                addPrefix(PartOfSpeech.Vocative, 1);
                segment.setLemma(prefixYa);
                return true;
            }
            if (arabicText.getCharacterType(prefixIndex) == CharacterType.Ya
                    && arabicText.getCharacterType(prefixIndex + 1) == CharacterType.Alif) {
                addPrefix(PartOfSpeech.Vocative, 2);
                segment.setLemma(prefixYa);
                return true;
            }
            fail("ya+");
        }

        // ha+
        if (item.equals("ha+")) {

            // (6:150:2), (33:18:8) halum~a
            // ha+ POS:V IMPV ROOT:lmm 2MP
            if (token.location().equals(6, 150, 2)
                    || token.location().equals(33, 18, 8)) {
                addPrefix(PartOfSpeech.Vocative, 1);
                segment.setLemma(prefixHa);
                return true;
            }
            if (arabicText.getCharacterType(prefixIndex) == CharacterType.Ha
                    && arabicText.getCharacterType(prefixIndex + 1) == CharacterType.Alif) {
                addPrefix(PartOfSpeech.Vocative, 2);
                segment.setLemma(prefixHa);
                return true;
            }
            fail("ha+");
        }

        // Al+
        if (item.equals("Al+")) {

            // Lam before Al+
            if (prefixIndex > 0
                    && arabicText.getCharacterType(prefixIndex - 1) == CharacterType.Lam) {
                if (arabicText.getCharacterType(prefixIndex) == CharacterType.Lam) {
                    addPrefix(PartOfSpeech.Determiner, 1);
                    return true;
                }
                fail("Al+");
            }

            // no preceeding Lam
            else {
                if (arabicText.getCharacterType(prefixIndex) == CharacterType.Alif
                        && arabicText.getCharacterType(prefixIndex + 1) == CharacterType.Lam) {
                    addPrefix(PartOfSpeech.Determiner, 2);
                    return true;
                }
                if (arabicText.getCharacterType(prefixIndex) == CharacterType.Lam
                        && arabicText.isSukun(prefixIndex)) {
                    addPrefix(PartOfSpeech.Determiner, 1);
                    return true;
                }
                fail("Al+");
            }
        }

        return false;
    }

    private boolean readFeatures(String item) {

        // Root.
        int size = item.length();
        if (item.startsWith("ROOT:")) {
            segment.setRoot(fromBuckwalter(item.substring(5)));
            return true;
        }

        // Lemma.
        if (item.startsWith("LEM:")) {
            segment.setLemma(lemmaService.getLemma(item.substring(4)));
            return true;
        }

        // Special.
        if (item.startsWith("SP:")) {
            readSpecial(item);
            return true;
        }

        // Mood.
        if (item.startsWith("MOOD:")) {
            readMood(item);
            return true;
        }

        // Form.
        if (item.equals("(I)")) {
            segment.setForm(FormType.First);
            return true;
        }
        if (item.equals("(II)")) {
            segment.setForm(FormType.Second);
            return true;
        }
        if (item.equals("(III)")) {
            segment.setForm(FormType.Third);
            return true;
        }
        if (item.equals("(IV)")) {
            segment.setForm(FormType.Fourth);
            return true;
        }
        if (item.equals("(V)")) {
            segment.setForm(FormType.Fifth);
            return true;
        }
        if (item.equals("(VI)")) {
            segment.setForm(FormType.Sixth);
            return true;
        }
        if (item.equals("(VII)")) {
            segment.setForm(FormType.Seventh);
            return true;
        }
        if (item.equals("(VIII)")) {
            segment.setForm(FormType.Eighth);
            return true;
        }
        if (item.equals("(IX)")) {
            segment.setForm(FormType.Ninth);
            return true;
        }
        if (item.equals("(X)")) {
            segment.setForm(FormType.Tenth);
            return true;
        }
        if (item.equals("(XI)")) {
            segment.setForm(FormType.Eleventh);
            return true;
        }
        if (item.equals("(XII)")) {
            segment.setForm(FormType.Twelfth);
            return true;
        }

        // Voice.
        if (item.equals("ACT")) {
            segment.setVoice(VoiceType.Active);
            return true;
        }
        if (item.equals("PASS")) {
            segment.setVoice(VoiceType.Passive);
            return true;
        }

        // Aspect.
        if (item.equals("PERF")) {
            segment.setAspect(AspectType.Perfect);
            return true;
        }
        if (item.equals("IMPF")) {
            segment.setAspect(AspectType.Imperfect);
            return true;
        }
        if (item.equals("IMPV")) {
            segment.setAspect(AspectType.Imperative);
            return true;
        }

        // Case.
        if (item.equals("NOM")) {
            segment.setCase(CaseType.Nominative);
            return true;
        }
        if (item.equals("GEN")) {
            segment.setCase(CaseType.Genitive);
            return true;
        }
        if (item.equals("ACC")) {
            segment.setCase(CaseType.Accusative);
            return true;
        }

        // State.
        if (item.equals("DEF")) {
            segment.setState(StateType.Definite);
            return true;
        }
        if (item.equals("INDEF")) {
            segment.setState(StateType.Indefinite);
            return true;
        }

        // Derivation.
        if (item.equals("VN")) {
            segment.setDerivation(DerivationType.VerbalNoun);
            return true;
        }

        // Person, gender, number.
        if (size >= 1 && size <= 3) {
            readPersonGenderNumber(item);
            return true;
        }

        return false;
    }

    private void readSpecial(String item) {

        if (item.equals("SP:kaAn")) {
            segment.setSpecial(SpecialType.Kaana);
            return;
        }
        if (item.equals("SP:kaAd")) {
            segment.setSpecial(SpecialType.Kaada);
            return;
        }
        if (item.equals("SP:<in~")) {
            segment.setSpecial(SpecialType.Inna);
        }
    }

    private void readMood(String item) {

        if (item.equals("MOOD:IND")) {
            segment.setMood(MoodType.Indicative);
            return;
        }
        if (item.equals("MOOD:SUBJ")) {
            segment.setMood(MoodType.Subjunctive);
            return;
        }
        if (item.equals("MOOD:JUS")) {
            segment.setMood(MoodType.Jussive);
        }
    }

    private void readVocativeSuffix() {

        // Validate.
        if (arabicText.getCharacterType(suffixIndex - 1) != CharacterType.Meem) {
            fail("+VOC");
        }

        // +VOC
        addSuffix(PartOfSpeech.Vocative);
        setSuffixLength(1);
        segment.setLemma(vocativeSuffix);
    }

    private void readEmphaticSuffix() {

        // Validate.
        switch (arabicText.getCharacterType(suffixIndex - 1)) {
            case Alif: // (12:32:17), (96:15:5)
            case Noon:
                break;
            default:
                fail("+n:EMPH");
                break;
        }

        // +n:EMPH
        segment = emphaticSuffix;
        setSuffixLength(1);
        segment.setLemma(suffixNoon);
    }

    private void readObjectPronouns(String item1, String item2) {

        // Add object pronoun segments, in ascending order.
        addSuffix(PartOfSpeech.Pronoun);
        Segment pronoun1 = segment;
        Segment pronoun2 = null;
        if (item2 != null) {
            addSuffix(PartOfSpeech.Pronoun);
            pronoun2 = segment;
        }

        // Read second object pronoun.
        if (pronoun2 != null) {
            pronoun2.setPronounType(PronounType.SecondObject);
            segment = pronoun2;
            readPersonGenderNumber(item2.substring(5));
            setSuffixLength(pronounReader.readObjectPronoun(
                    token,
                    stem,
                    pronoun2,
                    suffixIndex,
                    false,
                    emphaticSuffix != null));
        }

        // Read first object pronoun.
        pronoun1.setPronounType(PronounType.Object);
        segment = pronoun1;
        readPersonGenderNumber(item1.substring(5));
        setSuffixLength(pronounReader.readObjectPronoun(
                token,
                stem,
                pronoun1,
                suffixIndex,
                pronoun2 != null,
                emphaticSuffix != null));
    }

    private void readSubjectPronoun() {

        // Object pronoun?
        var segmentCount = segments.size();
        var isObjectAttached = segments.get(segmentCount - 1).getPartOfSpeech() == PartOfSpeech.Pronoun;

        // +n:EMPH?
        for (var value : segments) {
            if (value.getPartOfSpeech() == PartOfSpeech.Emphatic && value.getType() == SegmentType.Suffix) {
                return;
            }
        }

        // Read subject pronoun.
        var length = pronounReader.readSubjectPronoun(token, stem, suffixIndex, isObjectAttached);
        if (length == 0) {
            return;
        }

        // Create pronoun.
        var pronoun = new Segment(SegmentType.Suffix, PartOfSpeech.Pronoun);
        pronoun.setPronounType(PronounType.Subject);

        // Set suffix length.
        pronoun.setStartIndex(suffixIndex - length);
        pronoun.setEndIndex(stem.getEndIndex());
        stem.setEndIndex(pronoun.getStartIndex());

        // Copy stem features.
        pronoun.setPerson(stem.getPerson());
        pronoun.setGender(stem.getGender());
        pronoun.setNumber(stem.getNumber());

        // Insert pronoun after stem.
        var stemNumber = stem.getSegmentNumber();
        segments.add(stemNumber, pronoun);

        // Update locations.
        for (var i = stemNumber; i <= segmentCount; i++) {
            segments.get(i).setSegmentNumber(i + 1);
        }
    }

    private void addPrefix(PartOfSpeech partOfSpeech, int length) {
        addSegment(SegmentType.Prefix, partOfSpeech);
        segment.setStartIndex(prefixIndex);
        prefixIndex += length;
        segment.setEndIndex(prefixIndex);
    }

    private void addStem(PartOfSpeech partOfSpeech) {
        addSegment(SegmentType.Stem, partOfSpeech);
        segment.setStartIndex(prefixIndex);
        segment.setEndIndex(arabicText.getLength());
        stem = segment;
    }

    private void addSuffix(PartOfSpeech partOfSpeech) {
        addSegment(SegmentType.Suffix, partOfSpeech);
        segment.setEndIndex(arabicText.getLength());
    }

    private void addSegment(SegmentType type, PartOfSpeech partOfSpeech) {
        segment = new Segment(type, partOfSpeech);
        segment.setSegmentNumber(segments.size() + 1);
        segments.add(segment);
    }

    private void setSuffixLength(int length) {
        suffixIndex -= length;
        segment.setStartIndex(suffixIndex);
        segments.get(segment.getSegmentNumber() - 2).setEndIndex(suffixIndex);
    }

    private void readPersonGenderNumber(String item) {
        var size = item.length();
        for (var i = 0; i < size; i++) {
            switch (item.charAt(i)) {

                // Person.
                case '1' -> segment.setPerson(PersonType.First);
                case '2' -> segment.setPerson(PersonType.Second);
                case '3' -> segment.setPerson(PersonType.Third);

                // Gender.
                case 'M' -> segment.setGender(GenderType.Masculine);
                case 'F' -> segment.setGender(GenderType.Feminine);

                // Number.
                case 'S' -> segment.setNumber(NumberType.Singular);
                case 'D' -> segment.setNumber(NumberType.Dual);
                case 'P' -> segment.setNumber(NumberType.Plural);

                default -> throw new UnsupportedOperationException("Invalid morphological feature: " + item);
            }
        }
    }

    private void splitStems() {
        Segment stem1 = null;
        Segment stem2 = null;

        // Find stems.
        for (var segment : segments) {
            if (segment.getType() == SegmentType.Stem) {
                if (stem1 == null) {
                    stem1 = segment;
                } else {
                    stem2 = segment;
                }
            }
        }

        // Double stem?
        if (stem1 == null || stem2 == null) {
            return;
        }

        // Get second stem length.
        var stemLength = readSecondStem(stem2);
        suffixIndex -= stemLength;

        // Update stems.
        stem1.setEndIndex(suffixIndex);
        stem2.setStartIndex(suffixIndex);
    }

    private int readSecondStem(Segment stem2) {

        var lemma = stem2.getLemma().key();
        if (lemma.equals("maA")) {
            if (arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Meem
                    && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Alif) {
                return 2;
            }
            if (arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Meem) {
                return 1;
            }
        }

        if (lemma.equals("man")) {
            if (arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Meem
                    && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Noon) {
                return 2;
            }
        }

        if (lemma.equals("laA")) {
            if (arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Lam
                    && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Alif) {
                return 2;
            }
        }

        if (lemma.equals("lan")) {
            if (arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Lam
                    && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Noon) {
                return 2;
            }
        }

        if (lemma.equals("law")) {
            if (arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Lam
                    && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Waw) {
                return 2;
            }
        }

        if (lemma.equals(">um~")) {
            if (arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Waw
                    && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Meem) {
                return 2;
            }
        }

        fail("second stem, LEM:" + lemma);
        return 0;
    }

    private void fail(String feature) {
        throw new UnsupportedOperationException(
                "Failed to produce segments for token: "
                        + token.location()
                        + ' ' + toBuckwalter(token.arabicText())
                        + ", morphology: " + morphology
                        + ", feature: " + feature);
    }
}