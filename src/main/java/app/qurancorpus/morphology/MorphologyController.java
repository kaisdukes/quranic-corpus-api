package app.qurancorpus.morphology;

import app.qurancorpus.orthography.*;
import app.qurancorpus.translation.Translation;
import app.qurancorpus.translation.TranslationResponse;
import app.qurancorpus.translation.TranslationService;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import jakarta.inject.Inject;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static app.qurancorpus.orthography.Location.parseLocation;
import static java.util.Arrays.stream;

@Controller("/morphology")
public class MorphologyController {

    @Inject
    Document document;

    @Inject
    LocationService locationService;

    @Inject
    TranslationService translationService;

    @Inject
    OrthographyService orthographyService;

    @Inject
    TokenTransformer tokenTransformer;

    @Inject
    MorphologyGraph morphologyGraph;

    @Get
    public VerseResponse[] getMorphology(
            @QueryValue String location,
            @Min(1) @Max(10) @QueryValue("n") int count,
            @Nullable @QueryValue("translation") String translationQuery,
            @Nullable @QueryValue Boolean features) {

        // request
        var _location = parseLocation(location);
        var translations = getTranslations(translationQuery);
        var _features = features != null && features;

        // response
        var verses = document.children()[_location.chapterNumber() - 1].verses();
        var verseNumber = _location.verseNumber();
        var verseCount = Math.min(count, Math.max(0, verses.length - verseNumber + 1));
        var verseResponses = new VerseResponse[verseCount];
        for (var i = 0; i < verseCount; i++) {
            verseResponses[i] = getVerseResponse(verses[verseNumber++ - 1], translations, _features);
        }
        return verseResponses;
    }

    @Get("word")
    public WordMorphologyResponse getWordMorphology(@QueryValue String location) {
        var token = document.getToken(parseLocation(location));
        var wordMorphology = morphologyGraph.getWordMorphology(token);
        return new WordMorphologyResponse(
                tokenTransformer.getTokenResponse(token, false),
                wordMorphology.summary(),
                wordMorphology.segmentDescriptions(),
                wordMorphology.arabicGrammar());
    }

    private Translation[] getTranslations(String translationQuery) {
        if (translationQuery == null || translationQuery.length() == 0) return null;
        return stream(translationQuery.split(","))
                .map(translationService::getTranslation)
                .toArray(Translation[]::new);
    }

    private VerseResponse getVerseResponse(Verse verse, Translation[] translations, boolean features) {
        var tokens = verse.tokens();
        var tokenCount = tokens.length;
        var tokenResponses = new TokenResponse[tokenCount];
        for (var i = 0; i < tokenCount; i++) {
            tokenResponses[i] = tokenTransformer.getTokenResponse(tokens[i], features);
        }
        var location = verse.location();
        var verseSequenceNumber = locationService.getVerseSequenceNumber(location);
        return new VerseResponse(
                location.toArray(),
                tokenResponses,
                getTranslations(translations, verseSequenceNumber),
                orthographyService.getVerseMark(verseSequenceNumber));
    }

    private TranslationResponse[] getTranslations(Translation[] translations, int verseSequenceNumber) {
        if (translations == null) return null;
        var n = translations.length;
        var responses = new TranslationResponse[n];
        for (var i = 0; i < n; i++) {
            var translation = translations[i];
            responses[i] = new TranslationResponse(
                    translation.name(),
                    translation.getVerse(verseSequenceNumber));
        }
        return responses;
    }
}