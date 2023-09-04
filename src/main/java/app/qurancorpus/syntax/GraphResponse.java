package app.qurancorpus.syntax;

public record GraphResponse(
        int graphNumber,
        int graphCount,
        int legacyCorpusGraphNumber,
        GraphLocationResponse prev,
        GraphLocationResponse next,
        WordResponse[] words,
        EdgeResponse[] edges,
        PhraseNodeResponse[] phraseNodes) {
}