package app.qurancorpus.syntax;

import app.qurancorpus.orthography.TokenResponse;

public record WordResponse(
        WordType type,
        TokenResponse token,
        String elidedText,
        String elidedPosTag,
        int startNode,
        int endNode) {
}