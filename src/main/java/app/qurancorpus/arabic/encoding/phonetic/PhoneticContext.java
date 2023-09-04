package app.qurancorpus.arabic.encoding.phonetic;

import app.qurancorpus.morphology.MorphologyGraph;
import app.qurancorpus.orthography.Token;

public record PhoneticContext(
        MorphologyGraph morphologyGraph,
        Token token) {
}