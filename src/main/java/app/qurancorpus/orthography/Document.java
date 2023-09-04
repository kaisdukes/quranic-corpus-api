package app.qurancorpus.orthography;

import memseqdb.SeqGraphNode;

public class Document implements SeqGraphNode<Chapter> {
    private final Chapter[] chapters;
    private final int verseCount;
    private final int tokenCount;

    public Document(Chapter[] chapters) {
        this.chapters = chapters;

        // counts
        var verseCount = 0;
        var tokenCount = 0;
        for (var chapter : chapters) {
            for (var verse : chapter.verses()) {
                verseCount++;
                tokenCount += verse.tokens().length;
            }
        }
        this.verseCount = verseCount;
        this.tokenCount = tokenCount;
    }

    @Override
    public Chapter getChild(int chapterNumber) {
        return chapters[chapterNumber - 1];
    }

    @Override
    public Chapter[] children() {
        return chapters;
    }

    public int verseCount() {
        return verseCount;
    }

    public int tokenCount() {
        return tokenCount;
    }

    public Verse getVerse(int chapterNumber, int verseNumber) {
        return getChild(chapterNumber).getChild(verseNumber);
    }

    public Token getToken(int chapterNumber, int verseNumber, int tokenNumber) {
        return getChild(chapterNumber).getChild(verseNumber).getChild(tokenNumber);
    }

    public Token getToken(Location location) {
        return getToken(location.chapterNumber(), location.verseNumber(), location.tokenNumber());
    }

    public Token getNextToken(Token token) {
        var location = token.location();
        var chapterNumber = location.chapterNumber();
        var verseNumber = location.verseNumber();
        var tokenNumber = location.tokenNumber();

        var chapter = getChild(chapterNumber);
        var verse = chapter.getChild(verseNumber);

        if (tokenNumber < verse.tokens().length) {
            return verse.getChild(tokenNumber + 1);
        }

        if (verseNumber < chapter.verses().length) {
            return chapter.getChild(verseNumber + 1).tokens()[0];
        }

        if (chapterNumber < chapters.length) {
            return getToken(chapterNumber + 1, 1, 1);
        }

        return null;
    }
}