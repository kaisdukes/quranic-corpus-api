package app.qurancorpus.syntax;

public record SyntaxNode(
        int index,
        PhraseType phraseType,
        SyntaxNode start,
        SyntaxNode end) {

    public boolean isPhrase() {
        return phraseType != null;
    }
}