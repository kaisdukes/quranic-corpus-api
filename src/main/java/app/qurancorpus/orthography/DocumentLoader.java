package app.qurancorpus.orthography;

import app.qurancorpus.orthography.tanzil.TanzilChapter;
import app.qurancorpus.orthography.tanzil.TanzilReader;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static app.qurancorpus.arabic.encoding.unicode.UnicodeDecoder.fromUnicode;
import static java.lang.System.currentTimeMillis;

@Factory
public class DocumentLoader {
    private static final Logger log = LoggerFactory.getLogger(DocumentLoader.class);

    @Singleton
    public Document load() {
        var start = currentTimeMillis();
        var document = readDocument();
        var elapsed = currentTimeMillis() - start;
        log.info("Document loaded in {} ms", elapsed);
        return document;
    }

    private Document readDocument() {
        var reader = new TanzilReader();
        var tanzilChapters = reader.readChapters();
        var chapterCount = tanzilChapters.size();
        var chapters = new Chapter[chapterCount];
        for (var i = 0; i < chapterCount; i++) {
            chapters[i] = buildChapter(i + 1, tanzilChapters.get(i));
        }
        return new Document(chapters);
    }

    private Chapter buildChapter(int chapterNumber, TanzilChapter chapter) {
        var tanzilVerses = chapter.verses();
        var verseCount = tanzilVerses.size();
        var verses = new Verse[verseCount];
        for (var i = 0; i < verseCount; i++) {
            var verseNumber = i + 1;
            var location = new Location(chapterNumber, verseNumber);
            var arabicText = fromUnicode(tanzilVerses.get(i));
            var tokens = new Tokenizer(chapterNumber, verseNumber, arabicText).getTokens();
            verses[i] = new Verse(location, arabicText, tokens.toArray(new Token[0]));
        }
        return new Chapter(chapterNumber, verses);
    }
}