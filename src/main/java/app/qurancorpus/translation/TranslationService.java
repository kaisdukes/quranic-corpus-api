package app.qurancorpus.translation;

import app.qurancorpus.orthography.Document;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.Map;

import static app.qurancorpus.ResourceReader.readResource;
import static java.text.MessageFormat.format;
import static java.util.Arrays.stream;

@Singleton
public class TranslationService {
    private final String[] tokenTranslation;
    private final Map<String, Translation> verseTranslations = new HashMap<>();
    private final TranslationMetadata[] metadata;

    @SneakyThrows
    public TranslationService(Document document, ObjectMapper objectMapper) {
        tokenTranslation = readTokenTranslation(document);

        var translationInfo = objectMapper.readValue(
                readResource("/data/translation/index.json"),
                TranslationInfo[].class);

        metadata = stream(translationInfo).map(x -> new TranslationMetadata(
                        x.name().toLowerCase().replace(' ', '-'),
                        x.name()))
                .toArray(TranslationMetadata[]::new);

        for (var translation : metadata) {
            verseTranslations.put(
                    translation.key(),
                    readVerseTranslation(document, translation));
        }
    }

    public TranslationMetadata[] getMetadata() {
        return metadata;
    }

    public String getTokenTranslation(int tokenSequenceNumber) {
        return tokenTranslation[tokenSequenceNumber - 1];
    }

    public Translation getTranslation(String key) {
        var translation = verseTranslations.get(key);
        if (translation == null) {
            throw new UnsupportedOperationException(format("Translation {0} not found.", key));
        }
        return translation;
    }

    @SneakyThrows
    private String[] readTokenTranslation(Document document) {
        var tokenCount = document.tokenCount();
        var translation = new String[tokenCount];
        try (var reader = readResource("/data/translation/word-by-word.txt")) {
            for (var i = 0; i < tokenCount; i++) {
                translation[i] = reader.readLine();
            }
        }
        return translation;
    }

    @SneakyThrows
    private Translation readVerseTranslation(
            Document document,
            TranslationMetadata translation) {

        var key = translation.key();
        var verseCount = document.verseCount();
        var verses = new String[verseCount];
        try (var reader = readResource(format("/data/translation/{0}.txt", key))) {
            for (var i = 0; i < verseCount; i++) {
                verses[i] = reader.readLine();
            }
        }
        return new Translation(key, translation.name(), verses);
    }
}