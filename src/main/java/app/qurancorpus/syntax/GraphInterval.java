package app.qurancorpus.syntax;

import app.qurancorpus.Interval;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;

public class GraphInterval {

    public static Interval<Integer> phraseInterval(SyntaxNode phraseNode) {
        return new Interval<>(
                phraseNode.start().index(),
                phraseNode.end().index());
    }

    public static Interval<Integer> edgeInterval(Edge edge) {
        var head = edge.head();
        var dependent = edge.dependent();

        if (head.isPhrase()) {

            // phrase -> phrase
            if (dependent.isPhrase()) {
                return getInterval(
                        head.start().index(),
                        head.end().index(),
                        dependent.start().index(),
                        dependent.end().index());
            }

            // word -> phrase
            return getInterval(
                    head.start().index(),
                    head.end().index(),
                    dependent.index());
        }

        // phrase -> word
        if (dependent.isPhrase()) {
            return getInterval(
                    head.index(),
                    dependent.start().index(),
                    dependent.end().index());
        }

        // word -> word
        return getInterval(head.index(), dependent.index());
    }

    private static Interval<Integer> getInterval(int... indices) {
        var start = MAX_VALUE;
        var end = MIN_VALUE;
        for (var index : indices) {
            if (index < start) {
                start = index;
            }
            if (index > end) {
                end = index;
            }
        }
        return new Interval<>(start, end);
    }
}