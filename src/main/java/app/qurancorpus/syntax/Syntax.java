package app.qurancorpus.syntax;

import app.qurancorpus.morphology.*;

import static app.qurancorpus.arabic.encoding.buckwalter.BuckwalterDecoder.fromBuckwalter;
import static app.qurancorpus.arabic.encoding.buckwalter.BuckwalterEncoder.toBuckwalter;
import static app.qurancorpus.arabic.encoding.unicode.UnicodeEncoder.toUnicode;
import static app.qurancorpus.morphology.AspectType.Imperfect;
import static app.qurancorpus.morphology.AspectType.Perfect;
import static app.qurancorpus.morphology.GenderType.Masculine;
import static app.qurancorpus.morphology.Morphology.isInterrogativeAlif;
import static app.qurancorpus.morphology.NumberType.Singular;
import static app.qurancorpus.morphology.PartOfSpeech.*;
import static app.qurancorpus.morphology.PersonType.Second;
import static app.qurancorpus.morphology.PersonType.Third;
import static app.qurancorpus.morphology.SegmentType.Stem;

public class Syntax {

    private Syntax() {
    }

    public static boolean isPrepositionPhrase(Segment[] segments) {
        var segmentCount = segments.length;
        for (var i = 0; i < segmentCount; i++) {
            if (isPrepositionPhrase(segments, i)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPrepositionPhrase(Segment[] segments, int index) {

        // POS:P
        var segment = segments[index];
        if (segment.getPartOfSpeech() != PartOfSpeech.Preposition) {
            return false;
        }

        // Preposition prefix + stem.
        var next = index < segments.length - 1 ? segments[index + 1] : null;
        if (segment.getType() == SegmentType.Prefix) {
            if (next == null) {
                return true;
            }
            var partOfSpeech = next.getPartOfSpeech();
            return partOfSpeech != Accusative && partOfSpeech != Supplemental;
        }

        // Preposition stem + pronoun suffix.
        return segment.getType() == Stem
                && next != null
                && next.getPartOfSpeech() == Pronoun;
    }

    public static boolean isPreventivePhrase(Segment[] segments) {
        var segmentCount = segments.length;
        for (var i = 0; i < segmentCount; i++) {
            if (isPreventivePhrase(segments, i)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPreventivePhrase(Segment[] segments, int index) {

        // POS:ACC
        var segment = segments[index];
        if (segment.getPartOfSpeech() != Accusative) {
            return false;
        }

        // POS:PREV
        var next = index < segments.length - 1 ? segments[index + 1] : null;
        return next != null && next.getPartOfSpeech() == Preventive;
    }

    public static String getHeadName(Segment[] segments, Segment segment) {
        var partOfSpeech = segment.getPartOfSpeech();
        var aspect = segment.getAspect();
        var person = segment.getPerson();
        var gender = segment.getGender();
        var number = segment.getNumber();

        String root = null;
        if (segment.getRoot() != null) {
            root = toBuckwalter(segment.getRoot());
        }

        var lemma = segment.getLemma();

        // POS:V IMPF ROOT:kwn 2D
        if (partOfSpeech == Verb && aspect == Imperfect && root.equals("kwn") && person == Second) {
            return toUnicode(fromBuckwalter("kAn"));
        }

        // POS:V PERF ROOT:kwn 1S
        // POS:V PERF ROOT:kwn 1P
        // POS:V PERF ROOT:kwn 2MP
        if (partOfSpeech == Verb && aspect == Perfect
                && root.equals("kwn")
                && (person == PersonType.First || person == Second)) {
            return toUnicode(fromBuckwalter("kAn"));
        }

        // POS:V PERF ROOT:lys 2MS
        // POS:V PERF ROOT:lys 3FS
        if (partOfSpeech == Verb
                && aspect == Perfect
                && root.equals("lys")
                && (person == Second || (person == Third
                && gender == GenderType.Feminine && number == Singular))) {
            return toUnicode(fromBuckwalter("lys"));
        }

        // POS:V PERF ROOT:dwm SP:kaAn 2MS
        if (partOfSpeech == Verb && aspect == Perfect
                && root.equals("dwm") && person == Second
                && gender == Masculine
                && number == Singular) {
            return toUnicode(fromBuckwalter("dAm"));
        }

        // A:INTG+ POS:PART LEM:<in~
        if (isInterrogativeAlif(segments) && lemma != null && lemma.key().equals("<in~")) {
            return toUnicode(fromBuckwalter("An"));
        }

        // LEM:ka>an~
        if (lemma != null && lemma.key().equals("ka>an~")) {
            return toUnicode(fromBuckwalter("k>n"));
        }

        // Elided ACC.
        if (segment.getArabicText().getLength() == 0 && segment.getPartOfSpeech() == Accusative) {
            return toUnicode(fromBuckwalter("An"));
        }

        // Default.
        return toUnicode(segment.getArabicText().removeDiacritics());
    }
}