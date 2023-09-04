package app.qurancorpus.nlg;

import app.qurancorpus.morphology.*;

import static app.qurancorpus.morphology.PartOfSpeech.Emphatic;
import static app.qurancorpus.morphology.PartOfSpeech.Preventive;
import static app.qurancorpus.nlg.SegmentName.getSegmentName;

public class SegmentDescriptionGenerator implements Generator {
    private final Text text = new Text();
    private final Segment[] segments;
    private final Segment stem;
    private final Segment segment;

    public SegmentDescriptionGenerator(Segment[] segments, Segment stem, Segment segment) {
        this.segments = segments;
        this.stem = stem;
        this.segment = segment;
    }

    @Override
    public String generate() {
        switch (segment.getType()) {
            case Prefix -> writePrefixDescription();
            case Stem -> writeStemDescription();
            case Suffix -> writeSuffixDescription();
        }
        return text.toString();
    }

    private void writePrefixDescription() {
        String phonetic = null;
        String translation = null;

        switch (segment.getPartOfSpeech()) {
            case Conjunction -> {
                switch (segment.getLemma().arabicText().getCharacterType(0)) {
                    case Waw -> {
                        phonetic = "wa";
                        translation = "and";
                    }
                    case Fa -> {
                        phonetic = "fa";
                        translation = "and";
                    }
                }
            }
            case Preposition -> {
                switch (segment.getLemma().arabicText().getCharacterType(0)) {
                    case Ba -> phonetic = "bi";
                    case Kaf -> phonetic = "ka";
                    case Ta -> {
                        phonetic = "ta";
                        translation = "oath";
                    }
                    case Waw -> {
                        phonetic = "wa";
                        translation = "oath";
                    }
                    case Lam -> phonetic = "lām";
                }
            }
            case Emphatic, Purpose, Imperative -> phonetic = "lām";
            case Future -> phonetic = "sa";
            case Vocative -> phonetic = switch (segment.getLemma().arabicText().getCharacterType(0)) {
                case Ha -> "ha";
                case Ya -> "ya";
                default -> null;
            };
        }

        // l:EMPH+
        if (segment.getPartOfSpeech() == Emphatic) {
            text.add("emphatic prefix");
        } else {
            text.add("prefixed ");
            text.add(getSegmentName(segments, stem, segment));
        }

        if (phonetic != null) {
            text.space();
            text.addPhonetic(phonetic);
        }

        if (translation != null) {
            text.add(" (");
            text.add(translation);
            text.add(')');
        }
    }

    private void writeStemDescription() {
        if (segment.getCase() != null) {
            text.space();
            text.add(segment.getCase().name().toLowerCase());
        }

        writePersonGenderNumber();

        if (segment.getState() != null) {
            text.space();
            text.add(segment.getState().name().toLowerCase());
        }

        if (segment.getForm() != null) {
            text.add(" (form ");
            text.add(segment.getForm().toString());
            text.add(')');
        }

        if (segment.getVoice() != null) {
            text.space();
            text.add(segment.getVoice().name().toLowerCase());
        }

        if (segment.getAspect() != null) {
            text.space();
            text.add(segment.getAspect().name().toLowerCase());
        }

        text.space();
        text.add(getSegmentName(segments, segment, segment));

        if (segment.getPartOfSpeech() == Preventive) {
            text.space();
            text.addPhonetic("mā");
        }

        if (segment.getMood() != null) {
            text.add(", ");
            text.add(segment.getMood().name().toLowerCase());
            text.add(" mood");
        }
    }

    private void writeSuffixDescription() {
        if (segment.getPronounType() != PronounType.Subject) {
            writePersonGenderNumber();
        }
        text.space();
        text.add(getSegmentName(segments, stem, segment));
        if (segment.getPartOfSpeech() == Emphatic) {
            text.space();
            text.addPhonetic("nūn");
        }
    }

    private void writePersonGenderNumber() {
        var person = segment.getPerson();
        var gender = segment.getGender();
        var number = segment.getNumber();

        if (person != null) {
            text.space();
            switch (person) {
                case First -> text.add("1st");
                case Second -> text.add("2nd");
                case Third -> text.add("3rd");
            }
            text.add(" person");
        }

        if (gender != null) {
            text.space();
            text.add(gender.name().toLowerCase());
        }

        if (number != null) {
            text.space();
            text.add(number.name().toLowerCase());
        }
    }
}