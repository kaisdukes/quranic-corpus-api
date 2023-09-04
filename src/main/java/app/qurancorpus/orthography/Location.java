package app.qurancorpus.orthography;

public record Location(int chapterNumber, int verseNumber, int tokenNumber) {

    public Location(int chapterNumber, int verseNumber) {
        this(chapterNumber, verseNumber, 0);
    }

    public int[] toArray() {
        return tokenNumber != 0
                ? new int[]{chapterNumber, verseNumber, tokenNumber}
                : new int[]{chapterNumber, verseNumber};
    }

    @Override
    public String toString() {
        var text = new StringBuilder();
        text.append(chapterNumber);
        text.append(':');
        text.append(verseNumber);
        if (tokenNumber > 0) {
            text.append(':');
            text.append(tokenNumber);
        }
        return text.toString();
    }

    public static Location parseLocation(String text) {
        return new LocationParser().parse(text);
    }

    @Override
    public boolean equals(Object value) {
        if (this == value) return true;
        if (!(value instanceof Location location)) return false;

        return location.chapterNumber == chapterNumber
                && location.verseNumber == verseNumber
                && location.tokenNumber == tokenNumber;
    }

    public boolean equals(int chapterNumber, int verseNumber, int tokenNumber) {
        return this.chapterNumber == chapterNumber
                && this.verseNumber == verseNumber
                && this.tokenNumber == tokenNumber;
    }

    @Override
    public int hashCode() {
        // A human-readable hash code, with chapter number, verse number and
        // token number each taking up 3 digits.
        return chapterNumber * 1000000 + verseNumber * 1000 + tokenNumber;
    }
}