package app.qurancorpus.nlg;

import java.util.ArrayList;
import java.util.List;

import static app.qurancorpus.nlg.SpanType.Arabic;
import static app.qurancorpus.nlg.SpanType.Phonetic;

public class SpanReader {
    private final String text;
    private final List<Span> spans = new ArrayList<>();
    private int position;

    public SpanReader(String text) {
        this.text = text;
    }

    public List<Span> readSpans() {
        while (position < text.length()) {
            var ch = peek();
            spans.add(switch (ch) {
                case '{' -> readSpan(Phonetic);
                case '[' -> readSpan(Arabic);
                default -> readText();
            });
        }
        return spans;
    }

    private Span readText() {
        var start = position;
        while (canRead() && peek() != '{' && peek() != '[') {
            position++;
        }
        return new Span(SpanType.Text, text.substring(start, position));
    }

    private Span readSpan(SpanType spanType) {
        var start = ++position;
        var endChar = spanType == Phonetic ? '}' : ']';
        while (text.charAt(position) != endChar) {
            position++;
        }
        return new Span(spanType, text.substring(start, position++));
    }

    private boolean canRead() {
        return position < text.length();
    }

    private char peek() {
        return text.charAt(position);
    }
}