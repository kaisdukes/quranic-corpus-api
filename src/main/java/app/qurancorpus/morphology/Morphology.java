package app.qurancorpus.morphology;

import app.qurancorpus.arabic.CharacterType;
import app.qurancorpus.orthography.Token;

import static app.qurancorpus.morphology.PartOfSpeech.Determiner;
import static app.qurancorpus.morphology.PartOfSpeech.Interrogative;
import static app.qurancorpus.morphology.SegmentType.Prefix;

public class Morphology {

    private Morphology() {
    }

    public static Segment getStem(Segment[] segments) {
        for (var segment : segments) {
            if (segment.getType() == SegmentType.Stem) {
                return segment;
            }
        }
        throw new UnsupportedOperationException("A unique stem could not be found for the token.");
    }

    public static boolean isDeterminerAl(Segment[] segments) {
        return isPrefix(segments, Determiner);
    }

    public static boolean isInterrogativeAlif(Segment[] segments) {
        return isPrefix(segments, Interrogative);
    }

    public static boolean isEmphasisNoonWithTanween(Segment segment) {
        return segment.getType() == SegmentType.Suffix
                && segment.getPartOfSpeech() == PartOfSpeech.Emphatic
                && segment.getArabicText().getCharacterType(segment.getArabicText().getLength() - 1) == CharacterType.Alif;
    }

    public static boolean isSuffixElision(Token token, Segment suffix) {

        // PRON:1S
        if (suffix.getPronounType() != PronounType.Object
                || suffix.getPerson() != PersonType.First
                || suffix.getNumber() != NumberType.Singular) {
            return false;
        }

        // If the segment's length is zero, then elision.
        if (suffix.getArabicText().getLength() == 0) {

            // Shadda on Y?
            return token.arabicText().getCharacterType(suffix.getStartIndex() - 1) != CharacterType.AlifMaksura;
        }

        // HA + YA
        if (suffix.getArabicText().getLength() == 2) {
            return false;
        }

        // If the segment does not end in a YA, then elision.
        var characterType = suffix.getArabicText().getCharacterType(suffix.getArabicText().getLength() - 1);
        return characterType != CharacterType.AlifMaksura && characterType != CharacterType.Ya;
    }

    private static boolean isPrefix(Segment[] segments, PartOfSpeech partOfSpeech) {
        for (var segment : segments) {
            if (segment.getType() != Prefix) {
                return false;
            }
            if (segment.getPartOfSpeech() == partOfSpeech) {
                return true;
            }
        }
        return false;
    }
}