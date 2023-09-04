package app.qurancorpus.morphology;

import app.qurancorpus.arabic.encoding.phonetic.PhoneticContext;
import app.qurancorpus.morphology.segmentation.MorphologyWriter;
import app.qurancorpus.orthography.LocationService;
import app.qurancorpus.orthography.Token;
import app.qurancorpus.orthography.TokenResponse;
import app.qurancorpus.translation.TranslationService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import static app.qurancorpus.arabic.encoding.phonetic.PhoneticEncoder.toPhonetic;
import static app.qurancorpus.arabic.encoding.unicode.UnicodeEncoder.toUnicode;

@Singleton
public class TokenTransformer {

    @Inject
    LocationService locationService;

    @Inject
    TranslationService translationService;

    @Inject
    MorphologyGraph morphologyGraph;

    public TokenResponse getTokenResponse(Token token, boolean features) {
        var segments = morphologyGraph.query(token);
        var segmentCount = segments.length;
        var segmentResponses = new SegmentResponse[segmentCount];
        var morphologyWriter = features ? new MorphologyWriter() : null;

        for (var i = 0; i < segmentCount; i++) {
            var segment = segments[i];
            var pronounType = features ? null : segment.getPronounType();
            segmentResponses[i] = new SegmentResponse(
                    toUnicode(segment.getArabicText()),
                    features ? null : segment.getPartOfSpeech().toString(),
                    pronounType != null ? pronounType.tag() : null,
                    features ? morphologyWriter.write(segment) : null);
        }

        var location = token.location();
        return new TokenResponse(
                location.toArray(),
                translationService.getTokenTranslation(locationService.getTokenSequenceNumber(location)),
                toPhonetic(new PhoneticContext(morphologyGraph, token), token.arabicText()),
                segmentResponses);
    }
}