package app.qurancorpus.orthography;

import app.qurancorpus.translation.TranslationResponse;

public record VerseResponse(
        int[] location,
        TokenResponse[] tokens,
        TranslationResponse[] translations,
        VerseMark verseMark) {
}