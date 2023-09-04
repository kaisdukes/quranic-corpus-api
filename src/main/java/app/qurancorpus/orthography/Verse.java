package app.qurancorpus.orthography;

import app.qurancorpus.arabic.ArabicText;
import memseqdb.SeqGraphNode;

public record Verse(
        Location location,
        ArabicText arabicText,
        Token[] tokens) implements SeqGraphNode<Token> {

    @Override
    public Token getChild(int tokenNumber) {
        return tokens[tokenNumber - 1];
    }

    @Override
    public Token[] children() {
        return tokens;
    }
}