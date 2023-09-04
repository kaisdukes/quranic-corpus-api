package app.qurancorpus.translation;

public record Translation(String key, String name, String[] verses) {

    public String getVerse(int verseSequenceNumber) {
        return verses[verseSequenceNumber - 1];
    }
}