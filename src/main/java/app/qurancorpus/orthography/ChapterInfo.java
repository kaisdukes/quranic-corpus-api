package app.qurancorpus.orthography;

public record ChapterInfo(
        int chapterNumber,
        String phonetic,
        String translation,
        String city) {
}