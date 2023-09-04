package app.qurancorpus.lexicography;

import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

import static app.qurancorpus.arabic.encoding.buckwalter.BuckwalterDecoder.fromBuckwalter;

@Singleton
public class LemmaService {
    private final Map<String, Lemma> lemmas = new HashMap<>();

    public Lemma getLemma(String key) {
        var lemma = lemmas.get(key);
        if (lemma == null) {
            lemma = newLemma(key);
            lemmas.put(key, lemma);
        }
        return lemma;
    }

    private Lemma newLemma(String key) {
        var arabic = key;
        var ch = key.charAt(key.length() - 1);
        if (ch >= '0' && ch <= '9') {
            arabic = key.substring(0, key.length() - 1);
        }
        return new Lemma(fromBuckwalter(arabic), key);
    }
}