package app.qurancorpus.orthography;

import app.qurancorpus.arabic.ArabicText;

import java.util.ArrayList;

public class Tokenizer {
    private final int chapterNumber;
    private final int verseNumber;
    private final ArrayList<Token> tokens;

    public Tokenizer(int chapterNumber, int verseNumber, ArabicText arabicText) {
        this.chapterNumber = chapterNumber;
        this.verseNumber = verseNumber;
        tokens = buildTokens(arabicText);
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    private ArrayList<Token> buildTokens(ArabicText arabicText) {
        var tokens = new ArrayList<Token>();
        var characterCount = arabicText.getLength();
        var startPosition = 0;

        for (var i = 0; i < characterCount; i++) {
            if (isTokenSeparator(arabicText, i)) {
                tokens.add(new Token(
                        new Location(chapterNumber, verseNumber, tokens.size() + 1),
                        arabicText.substring(startPosition, i)));
                startPosition = i + 1;
            }
        }

        tokens.add(new Token(
                new Location(chapterNumber, verseNumber, tokens.size() + 1),
                arabicText.substring(startPosition, characterCount)));

        return tokens;
    }

    private boolean isTokenSeparator(ArabicText arabicText, int i) {
        if (chapterNumber == 37 && verseNumber == 130 && i == 11) {
            return false;
        }
        return arabicText.getCharacterType(i) == null;
    }
}