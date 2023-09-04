package app.qurancorpus.syntax;

public record PhraseNodeResponse(
        int startNode,
        int endNode,
        String phraseTag) {
}