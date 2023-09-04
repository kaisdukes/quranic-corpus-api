package app.qurancorpus.orthography;

import com.fasterxml.jackson.annotation.JsonValue;

public enum VerseMark {
    Section("section"),
    Sajdah("sajdah");

    @JsonValue
    private final String key;

    VerseMark(String key) {
        this.key = key;
    }
}