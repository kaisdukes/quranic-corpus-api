package app.qurancorpus.lexicography;

import app.qurancorpus.arabic.ArabicText;

public record Lemma(ArabicText arabicText, String key) {
}