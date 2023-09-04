package app.qurancorpus.nlg;

import app.qurancorpus.arabic.ArabicText;
import app.qurancorpus.arabic.ArabicTextBuilder;

import static app.qurancorpus.arabic.CharacterType.Alif;
import static app.qurancorpus.arabic.DiacriticType.HamzaAbove;
import static app.qurancorpus.arabic.encoding.EncodingOptions.CombineAlifWithMaddah;
import static app.qurancorpus.arabic.encoding.unicode.UnicodeEncoder.toUnicode;

public class Text {
    private final StringBuilder text = new StringBuilder();

    public Text() {
    }

    public Text(String text) {
        add(text);
    }

    public void add(String text) {
        this.text.append(text);
    }

    public void add(char ch) {
        add(Character.toString(ch));
    }

    public void add(int value) {
        add(Integer.toString(value));
    }

    public void add(Text text) {
        this.text.append(text.text);
    }

    public void addArabic(String arabic) {
        text.append('[');
        text.append(arabic);
        text.append(']');
    }

    public void addArabic(ArabicText text) {
        addArabic(toUnicode(text, CombineAlifWithMaddah));
    }

    public void addPhonetic(String phonetic) {
        text.append('{');
        text.append(phonetic);
        text.append('}');
    }

    public boolean isEmpty() {
        return text.isEmpty();
    }

    @Override
    public String toString() {
        return text.toString();
    }

    public void space() {
        if (!isEmpty()) {
            add(' ');
        }
    }

    public void endSentence() {
        add('.');
    }

    public void addArabicLetters(ArabicText letters, boolean isRoot) {

        // phonetic
        var phonetic = new StringBuilder();
        var size = letters.getLength();
        for (var i = 0; i < size; i++) {
            if (i > 0) {
                phonetic.append(' ');
            }
            var letter = letters.getCharacterType(i);
            phonetic.append(isRoot ? letter.getPhoneticRoot() : letter.getPhoneticName());
        }
        addPhonetic(phonetic.toString());
        add(' ');

        // arabic
        var builder = new ArabicTextBuilder();
        for (var i = 0; i < size; i++) {
            if (i > 0) {
                builder.addWhitespace();
            }
            var letter = letters.getCharacterType(i);
            builder.add(letter);
            if (isRoot && letter == Alif) {
                builder.add(HamzaAbove);
            }
        }
        addArabic(builder.toArabicText());
    }

    public void addIndefiniteArticle(boolean upperCase, Text next) {
        addIndefiniteArticle(upperCase, next.text);
    }

    public void addIndefiniteArticle(boolean upperCase, CharSequence next) {
        var ch = next.length() > 0 ? next.charAt(0) : 0;
        if (ch == ' ') {
            ch = next.charAt(1);
        }

        if (ch == 'a' || ch == 'e' || ch == 'i') {
            add(upperCase ? "An" : "an");
        } else {
            add(upperCase ? "A" : "a");
        }
    }
}