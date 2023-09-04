package app.qurancorpus.morphology;

import app.qurancorpus.arabic.ArabicText;
import app.qurancorpus.lexicography.Lemma;

import static app.qurancorpus.morphology.PartOfSpeech.*;
import static app.qurancorpus.morphology.PartOfSpeechCategory.Nominal;
import static app.qurancorpus.morphology.PartOfSpeechCategory.Particle;

public class Segment {

    // Segment.
    private ArabicText arabicText;
    private final SegmentType type;
    private int segmentNumber;
    private int startIndex;
    private int endIndex;

    // Part-of-speech.
    private final PartOfSpeech partOfSpeech;

    // Features used to group similar words.
    private ArabicText root;
    private Lemma lemma;

    // Person, gender and number.
    private PersonType person;
    private GenderType gender;
    private NumberType number;

    // Verb features.
    private AspectType aspect;
    private MoodType mood;
    private VoiceType voice;
    private FormType form;

    // Derived nouns.
    private DerivationType derivation;

    // Nominal features.
    private StateType state;
    private CaseType caseType;
    private PronounType pronounType;

    // Special features.
    private SpecialType special;

    public Segment(SegmentType type, PartOfSpeech partOfSpeech) {
        this.type = type;
        this.partOfSpeech = partOfSpeech;
    }

    public void setArabicText(ArabicText arabicText) {
        this.arabicText = arabicText;
    }

    public ArabicText getArabicText() {
        return this.arabicText;
    }

    public SegmentType getType() {
        return type;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public int getSegmentNumber() {
        return segmentNumber;
    }

    public void setSegmentNumber(int segmentNumber) {
        this.segmentNumber = segmentNumber;
    }

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setRoot(ArabicText root) {
        this.root = root;
    }

    public ArabicText getRoot() {
        return root;
    }

    public void setLemma(Lemma lemma) {
        this.lemma = lemma;
    }

    public Lemma getLemma() {
        return lemma;
    }

    public void setPerson(PersonType person) {
        this.person = person;
    }

    public PersonType getPerson() {
        return person;
    }

    public void setGender(GenderType gender) {
        this.gender = gender;
    }

    public GenderType getGender() {
        return gender;
    }

    public void setNumber(NumberType number) {
        this.number = number;
    }

    public NumberType getNumber() {
        return number;
    }

    public void setAspect(AspectType aspect) {
        this.aspect = aspect;
    }

    public AspectType getAspect() {
        return aspect;
    }

    public void setMood(MoodType mood) {
        this.mood = mood;
    }

    public MoodType getMood() {
        return mood;
    }

    public void setVoice(VoiceType voice) {
        this.voice = voice;
    }

    public VoiceType getVoice() {
        return voice;
    }

    public void setForm(FormType form) {
        this.form = form;
    }

    public FormType getForm() {
        return form;
    }

    public void setDerivation(DerivationType derivation) {
        this.derivation = derivation;
    }

    public DerivationType getDerivation() {
        return derivation;
    }

    public void setState(StateType state) {
        this.state = state;
    }

    public StateType getState() {
        return state;
    }

    public void setCase(CaseType caseType) {
        this.caseType = caseType;
    }

    public CaseType getCase() {
        return caseType;
    }

    public void setPronounType(PronounType pronounType) {
        this.pronounType = pronounType;
    }

    public PronounType getPronounType() {
        return pronounType;
    }

    public void setSpecial(SpecialType special) {
        this.special = special;
    }

    public SpecialType getSpecial() {
        return special;
    }

    public PartOfSpeechCategory getPartOfSpeechCategory() {
        if (partOfSpeech == Verb) {
            return PartOfSpeechCategory.Verb;
        }
        if (partOfSpeech == Noun
                || partOfSpeech == ProperNoun
                || partOfSpeech == Adjective
                || partOfSpeech == Time
                || partOfSpeech == Location
                || partOfSpeech == Pronoun
                || partOfSpeech == Relative
                || partOfSpeech == Demonstrative) {
            return Nominal;
        }
        var lemma = this.lemma != null ? this.lemma.key() : null;
        if (lemma != null && lemma.equals("<i*aA")) {
            return Nominal;
        }
        if (partOfSpeech == Conditional || partOfSpeech == Interrogative) {
            if (this.lemma != null) {
                if (lemma.equals("man") || lemma.equals("maA")
                        || lemma.equals(">aY~") || lemma.equals(">ay~")
                        || lemma.equals("kayof") || lemma.equals("kam")
                        || lemma.equals(">an~aY`") || lemma.equals("maA*aA")
                        || lemma.equals("mataY`") || lemma.equals(">ayon")
                        || lemma.equals(">ay~aAn") || lemma.equals("{l~a*iY")
                        || lemma.equals("Hayov")) {
                    return Nominal;
                }
            }
        }
        return Particle;
    }
}