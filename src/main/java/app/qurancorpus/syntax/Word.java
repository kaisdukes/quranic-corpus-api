package app.qurancorpus.syntax;

import app.qurancorpus.arabic.ArabicText;
import app.qurancorpus.morphology.PartOfSpeech;
import app.qurancorpus.orthography.Token;

public record Word(
        WordType type,
        Token token,
        ArabicText elidedText,
        PartOfSpeech elidedPartOfSpeech,
        int start,
        int end) {
}