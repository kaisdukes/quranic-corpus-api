package app.qurancorpus.morphology.segmentation;

import app.qurancorpus.morphology.PartOfSpeech;
import app.qurancorpus.morphology.PronounType;
import app.qurancorpus.morphology.Segment;

import static app.qurancorpus.arabic.encoding.buckwalter.BuckwalterEncoder.toBuckwalter;

public class MorphologyWriter {
    private final StringBuilder text = new StringBuilder();

    public String write(Segment... segments) {
        text.setLength(0);
        for (var segment : segments) {
            writeSegment(segment);
        }
        return text.toString();
    }

    private void writeSegment(Segment segment) {
        switch (segment.getType()) {
            case Prefix -> writePrefix(segment);
            case Stem -> writeStem(segment);
            case Suffix -> writeSuffix(segment);
        }
    }

    private void writePrefix(Segment segment) {
        switch (segment.getPartOfSpeech()) {
            case Interrogative -> writeValue("A:INTG+");
            case Equalization -> writeValue("A:EQ+");
            case Comitative -> writeValue("w:COM+");
            case Conjunction -> {
                switch (segment.getLemma().arabicText().getCharacterType(0)) {
                    case Waw -> writeValue("w:CONJ+");
                    case Fa -> writeValue("f:CONJ+");
                }
            }
            case Resumption -> {
                switch (segment.getLemma().arabicText().getCharacterType(0)) {
                    case Waw -> writeValue("w:REM+");
                    case Fa -> writeValue("f:REM+");
                }
            }
            case Supplemental -> {
                switch (segment.getLemma().arabicText().getCharacterType(0)) {
                    case Waw -> writeValue("w:SUP+");
                    case Fa -> writeValue("f:SUP+");
                }
            }
            case Result -> writeValue("f:RSLT+");
            case Circumstantial -> writeValue("w:CIRC+");
            case Cause -> writeValue("f:CAUS+");
            case Preposition -> {
                switch (segment.getLemma().arabicText().getCharacterType(0)) {
                    case Ba -> writeValue("bi+");
                    case Kaf -> writeValue("ka+");
                    case Ta -> writeValue("ta+");
                    case Waw -> writeValue("w:P+");
                    case Lam -> writeValue("l:P+");
                }
            }
            case Determiner -> writeValue("Al+");
            case Emphatic -> writeValue("l:EMPH+");
            case Purpose -> writeValue("l:PRP+");
            case Imperative -> writeValue("l:IMPV+");
            case Future -> writeValue("sa+");
            case Vocative -> {
                switch (segment.getLemma().arabicText().getCharacterType(0)) {
                    case Ha -> writeValue("ha+");
                    case Ya -> writeValue("ya+");
                }
            }
        }
    }

    private void writeStem(Segment segment) {
        writePartOfSpeech(segment);
        writeDerivationType(segment);

        // Verb features.
        writeAspect(segment);
        writeVoice(segment);
        writeForm(segment);

        // Lemma, root and special.
        writeLemma(segment);
        writeRoot(segment);
        writeSpecial(segment);

        // Person, gender and number.
        if (segment.getPerson() != null || segment.getGender() != null
                || segment.getNumber() != null) {
            writeSpace();
            writePersonGenderNumber(segment);
        }

        // Verb mood.
        writeMood(segment);

        // Nominal features.
        writeState(segment);
        writeCase(segment);
    }

    private void writePartOfSpeech(Segment segment) {
        var partOfSpeech = segment.getPartOfSpeech();
        if (partOfSpeech != null) {
            writeSpace();
            text.append("POS:");
            text.append(partOfSpeech);
        }
    }

    private void writeDerivationType(Segment segment) {
        var derivation = segment.getDerivation();
        if (derivation != null) {
            writeSpace();
            text.append(derivation);
        }
    }

    private void writeLemma(Segment segment) {
        var lemma = segment.getLemma();
        if (lemma != null) {
            writeSpace();
            text.append("LEM:");
            text.append(lemma.key());
        }
    }

    private void writeRoot(Segment segment) {
        var root = segment.getRoot();
        if (root != null) {
            writeSpace();
            text.append("ROOT:");
            text.append(toBuckwalter(root));
        }
    }

    private void writeSpecial(Segment segment) {
        var special = segment.getSpecial();
        if (special != null) {
            writeSpace();
            switch (special) {
                case Kaana -> text.append("SP:kaAn");
                case Kaada -> text.append("SP:kaAd");
                case Inna -> text.append("SP:<in~");
            }
        }
    }

    private void writeForm(Segment segment) {
        var form = segment.getForm();
        if (form != null) {
            writeSpace();
            text.append('(');
            text.append(form);
            text.append(')');
        }
    }

    private void writeVoice(Segment segment) {
        var voice = segment.getVoice();
        if (voice != null) {
            writeValue(voice.toString());
        }
    }

    private void writeAspect(Segment segment) {
        var aspect = segment.getAspect();
        if (aspect != null) {
            writeValue(aspect.toString());
        }
    }

    private void writeMood(Segment segment) {
        var mood = segment.getMood();
        if (mood != null) {
            writeSpace();
            text.append("MOOD:");
            text.append(mood);
        }
    }

    private void writeState(Segment segment) {
        var state = segment.getState();
        if (state != null) {
            writeValue(state.toString());
        }
    }

    private void writeCase(Segment segment) {
        var caseType = segment.getCase();
        if (caseType != null) {
            writeValue(caseType.toString());
        }
    }

    private void writeSuffix(Segment segment) {

        // Vocative suffix.
        var partOfSpeech = segment.getPartOfSpeech();
        if (partOfSpeech == PartOfSpeech.Vocative) {
            writeValue("+VOC");
            return;
        }

        // Emphatic suffix.
        if (partOfSpeech == PartOfSpeech.Emphatic) {
            writeValue("+n:EMPH");
            return;
        }

        // l:P+
        if (partOfSpeech == PartOfSpeech.Preposition) {
            writePrefix(segment);
            return;
        }

        // Suffixed object pronoun.
        if (partOfSpeech == PartOfSpeech.Pronoun && segment.getPronounType() != PronounType.Subject) {
            writeSpace();
            text.append("PRON:");
            writePersonGenderNumber(segment);
        }
    }

    private void writePersonGenderNumber(Segment segment) {

        // Write person.
        var person = segment.getPerson();
        if (person != null) {
            text.append(person);
        }

        // Write gender.
        var gender = segment.getGender();
        if (gender != null) {
            text.append(gender);
        }

        // Write number.
        var number = segment.getNumber();
        if (number != null) {
            text.append(number);
        }
    }

    private void writeSpace() {
        if (text.length() > 0) {
            text.append(' ');
        }
    }

    private void writeValue(String value) {
        writeSpace();
        text.append(value);
    }
}