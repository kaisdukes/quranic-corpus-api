package app.qurancorpus.orthography;

import static java.lang.Integer.parseInt;

public class LocationParser {
    private String text;
    private int index;

    public Location parse(String text) {
        this.text = text;
        var chapterNumber = readNumber();
        if (!readColon()) {
            throw new UnsupportedOperationException("Expected a colon.");
        }
        var verseNumber = readNumber();
        var tokenNumber = readColon() ? readNumber() : 0;
        return new Location(chapterNumber, verseNumber, tokenNumber);
    }

    private boolean readColon() {
        if (canRead() && peek() == ':') {
            index++;
            return true;
        }
        return false;
    }

    private int readNumber() {
        var start = this.index;
        while (canRead() && peek() >= '0' && peek() <= '9') {
            index++;
        }
        return parseInt(text.substring(start, index));
    }

    private boolean canRead() {
        return index < text.length();
    }

    private char peek() {
        return text.charAt(index);
    }
}