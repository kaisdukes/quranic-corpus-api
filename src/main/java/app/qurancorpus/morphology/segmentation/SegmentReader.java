package app.qurancorpus.morphology.segmentation;

import app.qurancorpus.lexicography.Lemma;
import app.qurancorpus.lexicography.LemmaService;
import app.qurancorpus.morphology.*;

import static app.qurancorpus.arabic.encoding.buckwalter.BuckwalterDecoder.fromBuckwalter;

public class SegmentReader {
    private final LemmaService lemmaService;
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

    public SegmentReader(LemmaService lemmaService) {
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

    public Segment read(String morphology, boolean hasStem) {
        if (morphology.startsWith("POS:")) {
            return readStem(morphology);
        }

        if (morphology.startsWith("PRON:")) {
            var segment = new Segment(SegmentType.Suffix, PartOfSpeech.Pronoun);
            readPersonGenderNumber(segment, morphology.substring(5));
            return segment;
        }

        return switch (morphology) {
            case "+n:EMPH" -> getSuffix(PartOfSpeech.Emphatic, suffixNoon);
            case "+VOC" -> getSuffix(PartOfSpeech.Vocative, vocativeSuffix);
            case "A:INTG+" -> getPrefix(PartOfSpeech.Interrogative);
            case "A:EQ+" -> getPrefix(PartOfSpeech.Equalization);
            case "f:CONJ+" -> getPrefix(PartOfSpeech.Conjunction, prefixFa);
            case "f:REM+" -> getPrefix(PartOfSpeech.Resumption, prefixFa);
            case "f:RSLT+" -> getPrefix(PartOfSpeech.Result, prefixFa);
            case "f:CAUS+" -> getPrefix(PartOfSpeech.Cause, prefixFa);
            case "f:SUP+" -> getPrefix(PartOfSpeech.Supplemental, prefixFa);
            case "w:SUP+" -> getPrefix(PartOfSpeech.Supplemental, prefixWa);
            case "w:CONJ+" -> getPrefix(PartOfSpeech.Conjunction, prefixWa);
            case "w:COM+" -> getPrefix(PartOfSpeech.Comitative, prefixWa);
            case "w:REM+" -> getPrefix(PartOfSpeech.Resumption, prefixWa);
            case "w:CIRC+" -> getPrefix(PartOfSpeech.Circumstantial, prefixWa);
            case "w:P+" -> getPrefix(PartOfSpeech.Preposition, prefixWa);
            case "ka+" -> getPrefix(PartOfSpeech.Preposition, prefixKa);
            case "l:EMPH+" -> getPrefix(PartOfSpeech.Emphatic);
            case "bi+" -> getPrefix(PartOfSpeech.Preposition, prefixBi);
            case "ta+" -> getPrefix(PartOfSpeech.Preposition, prefixTa);
            case "l:P+" -> hasStem
                    ? getSuffix(PartOfSpeech.Preposition, prefixLa)
                    : getPrefix(PartOfSpeech.Preposition, prefixLa);
            case "l:IMPV+" -> getPrefix(PartOfSpeech.Imperative);
            case "l:PRP+" -> getPrefix(PartOfSpeech.Purpose);
            case "sa+" -> getPrefix(PartOfSpeech.Future, prefixSa);
            case "ya+" -> getPrefix(PartOfSpeech.Vocative, prefixYa);
            case "ha+" -> getPrefix(PartOfSpeech.Vocative, prefixHa);
            case "Al+" -> getPrefix(PartOfSpeech.Determiner);
            default -> throw new UnsupportedOperationException("Unknown morphology: " + morphology);
        };
    }

    private Segment readStem(String morphology) {
        var tags = morphology.split(" ");
        var partOfSpeech = PartOfSpeech.parse(tags[0].substring(4));
        var segment = new Segment(SegmentType.Stem, partOfSpeech);

        var size = tags.length;
        for (var i = 1; i < size; i++) {
            var tag = tags[i];

            if (tag.startsWith("ROOT:")) {
                segment.setRoot(fromBuckwalter(tag.substring(5)));
                continue;
            }

            if (tag.startsWith("LEM:")) {
                segment.setLemma(lemmaService.getLemma(tag.substring(4)));
                continue;
            }

            if (tag.startsWith("SP:")) {
                segment.setSpecial(SpecialType.parse(tag.substring(3)));
                continue;
            }

            if (tag.startsWith("MOOD:")) {
                segment.setMood(MoodType.parse(tag.substring(5)));
                continue;
            }

            switch (tag) {
                case "NOM" -> segment.setCase(CaseType.Nominative);
                case "GEN" -> segment.setCase(CaseType.Genitive);
                case "ACC" -> segment.setCase(CaseType.Accusative);
                case "ACT" -> {
                    if (i < size - 1 && tags[i + 1].equals("PCPL")) {
                        segment.setDerivation(DerivationType.ActiveParticiple);
                        i++;
                    } else {
                        throw new UnsupportedOperationException();
                    }
                }
                case "PASS" -> {
                    if (i < size - 1 && tags[i + 1].equals("PCPL")) {
                        segment.setDerivation(DerivationType.PassiveParticiple);
                        i++;
                    } else {
                        segment.setVoice(VoiceType.Passive);
                    }
                }
                case "VN" -> segment.setDerivation(DerivationType.VerbalNoun);
                case "PERF" -> segment.setAspect(AspectType.Perfect);
                case "IMPF" -> segment.setAspect(AspectType.Imperfect);
                case "IMPV" -> segment.setAspect(AspectType.Imperative);
                case "(I)" -> segment.setForm(FormType.First);
                case "(II)" -> segment.setForm(FormType.Second);
                case "(III)" -> segment.setForm(FormType.Third);
                case "(IV)" -> segment.setForm(FormType.Fourth);
                case "(V)" -> segment.setForm(FormType.Fifth);
                case "(VI)" -> segment.setForm(FormType.Sixth);
                case "(VII)" -> segment.setForm(FormType.Seventh);
                case "(VIII)" -> segment.setForm(FormType.Eighth);
                case "(IX)" -> segment.setForm(FormType.Ninth);
                case "(X)" -> segment.setForm(FormType.Tenth);
                case "(XI)" -> segment.setForm(FormType.Eleventh);
                case "(XII)" -> segment.setForm(FormType.Twelfth);
                case "DEF" -> segment.setState(StateType.Definite);
                case "INDEF" -> segment.setState(StateType.Indefinite);
                default -> readPersonGenderNumber(segment, tag);
            }
        }

        return segment;
    }

    private void readPersonGenderNumber(Segment segment, String tag) {
        var size = tag.length();
        for (var i = 0; i < size; i++) {
            switch (tag.charAt(i)) {
                case '1' -> segment.setPerson(PersonType.First);
                case '2' -> segment.setPerson(PersonType.Second);
                case '3' -> segment.setPerson(PersonType.Third);
                case 'M' -> segment.setGender(GenderType.Masculine);
                case 'F' -> segment.setGender(GenderType.Feminine);
                case 'S' -> segment.setNumber(NumberType.Singular);
                case 'D' -> segment.setNumber(NumberType.Dual);
                case 'P' -> segment.setNumber(NumberType.Plural);
                default -> throw new UnsupportedOperationException("Unknown tag: " + tag);
            }
        }
    }

    private Segment getPrefix(PartOfSpeech partOfSpeech) {
        return new Segment(SegmentType.Prefix, partOfSpeech);
    }

    private Segment getPrefix(PartOfSpeech partOfSpeech, Lemma lemma) {
        var segment = new Segment(SegmentType.Prefix, partOfSpeech);
        segment.setLemma(lemma);
        return segment;
    }

    private Segment getSuffix(PartOfSpeech partOfSpeech, Lemma lemma) {
        var segment = new Segment(SegmentType.Suffix, partOfSpeech);
        segment.setLemma(lemma);
        return segment;
    }
}