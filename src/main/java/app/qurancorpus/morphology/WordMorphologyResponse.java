package app.qurancorpus.morphology;

import app.qurancorpus.orthography.TokenResponse;

public record WordMorphologyResponse(
        TokenResponse token,
        String summary,
        String[] segmentDescriptions,
        String arabicGrammar) {
}