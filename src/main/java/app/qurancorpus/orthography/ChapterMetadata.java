package app.qurancorpus.orthography;

public record ChapterMetadata(
        int chapterNumber,
        int verseCount,
        String phonetic,
        String translation,
        String city) {
}