package app.qurancorpus.syntax;

import com.fasterxml.jackson.annotation.JsonValue;

public enum WordType {
    Token("token"),
    Reference("reference"),
    Elided("elided");

    @JsonValue
    private final String key;

    WordType(String key) {
        this.key = key;
    }
}