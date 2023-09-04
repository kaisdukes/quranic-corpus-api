package app.qurancorpus;

import static java.text.MessageFormat.format;

public record Interval<T>(T start, T end) {

    @Override
    public String toString() {
        return format("{0}-{1}", start, end);
    }
}